/*
 * Copyright (C) 2014 Mathias Doenitz
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package spray.json

import scala.annotation.{ switch, tailrec }
import java.nio.ByteBuffer
import java.nio.charset.Charset

import scala.collection.immutable.TreeMap
import scala.util.control.NonFatal

/**
 * Fast, no-dependency parser for JSON as defined by http://tools.ietf.org/html/rfc4627.
 */
object JsonParser {
  def apply(input: ParserInput): JsValue = new JsonParser(input).parseJsValue()
  def apply(input: ParserInput, settings: JsonParserSettings): JsValue = new JsonParser(input, settings).parseJsValue()

  class ParsingException(val summary: String, val detail: String = "")
    extends RuntimeException(if (summary.isEmpty) detail else if (detail.isEmpty) summary else summary + ":" + detail)

  private[JsonParser] val charBufferCache: ThreadLocal[Array[Char]] = new ThreadLocal[Array[Char]] {
    override def initialValue(): Array[Char] = new Array[Char](1024)
  }
}

class JsonParser(input: ParserInput, settings: JsonParserSettings = JsonParserSettings.default) {
  def this(input: ParserInput) = this(input, JsonParserSettings.default)

  import JsonParser.ParsingException

  private[this] var cursorChar: Char = input.nextChar()
  private[this] var jsValue: JsValue = _

  def parseJsValue(): JsValue =
    parseJsValue(false)

  def parseJsValue(allowTrailingInput: Boolean): JsValue = try {
    ws()
    `value`(settings.maxDepth)
    if (!allowTrailingInput && !input.isAtEOI)
      fail("expected end-of-input")
    jsValue
  } catch {
    case _: ArrayIndexOutOfBoundsException => fail("unexpected end of input")
  }

  ////////////////////// GRAMMAR ////////////////////////

  private final val EOI = '\uFFFF' // compile-time constant

  // http://tools.ietf.org/html/rfc4627#section-2.1
  private def `value`(remainingNesting: Int): Unit =
    if (remainingNesting == 0)
      throw new ParsingException(
        "JSON input nested too deeply",
        s"JSON input was nested more deeply than the configured limit of maxDepth = ${settings.maxDepth}"
      )
    else {
      val mark = input.cursor
      def simpleValue(matched: Boolean, value: JsValue) = if (matched) jsValue = value else fail("JSON Value", mark)
      (cursorChar: @switch) match {
        case 'f' => simpleValue(`false`(), JsFalse)
        case 'n' => simpleValue(`null`(), JsNull)
        case 't' => simpleValue(`true`(), JsTrue)
        case '{' =>
          advance(); `object`(remainingNesting)
        case '[' =>
          advance(); `array`(remainingNesting)
        case '0' | '1' | '2' | '3' | '4' | '5' | '6' | '7' | '8' | '9' | '-' => `number`()
        case '"' =>
          val str = `string`()
          jsValue = if (str.length == 0) JsString.empty else JsString(str)
        case _ => fail("JSON Value")
      }
    }

  private def `false`() = advance() && ch('a') && ch('l') && ch('s') && ws('e')
  private def `null`() = advance() && ch('u') && ch('l') && ws('l')
  private def `true`() = advance() && ch('r') && ch('u') && ws('e')

  // http://tools.ietf.org/html/rfc4627#section-2.2
  private def `object`(remainingNesting: Int): Unit = {
    ws()
    jsValue = if (cursorChar != '}') {
      @tailrec def members(map: Map[String, JsValue]): Map[String, JsValue] = {
        val key = `string`()
        require(':')
        ws()
        `value`(remainingNesting - 1)
        val nextMap = map.updated(key, jsValue)
        if (ws(',')) members(nextMap) else nextMap
      }
      val map = members(TreeMap.empty[String, JsValue])
      require('}')
      JsObject(map)
    } else {
      advance()
      JsObject.empty
    }
    ws()
  }

  // http://tools.ietf.org/html/rfc4627#section-2.3
  private def `array`(remainingNesting: Int): Unit = {
    ws()
    jsValue = if (cursorChar != ']') {
      val list = Vector.newBuilder[JsValue]
      @tailrec def values(): Unit = {
        `value`(remainingNesting - 1)
        list += jsValue
        if (ws(',')) values()
      }
      values()
      require(']')
      JsArray(list.result())
    } else {
      advance()
      JsArray.empty
    }
    ws()
  }

  // http://tools.ietf.org/html/rfc4627#section-2.4
  private def `number`() = {
    val start = input.cursor
    val startChar = cursorChar
    ch('-')
    `int`()
    `frac`()
    `exp`()
    val numberLength = input.cursor - start

    jsValue =
      if (startChar == '0' && input.cursor - start == 1) JsNumber.zero
      else if (numberLength <= settings.maxNumberCharacters) JsNumber(input.sliceCharArray(start, input.cursor))
      else {
        val numberSnippet = new String(input.sliceCharArray(start, math.min(input.cursor, start + 20)))
        throw new ParsingException(
          "Number too long",
          s"The number starting with '$numberSnippet' had " +
            s"$numberLength characters which is more than the allowed limit maxNumberCharacters = ${settings.maxNumberCharacters}. If this is legit input " +
            s"consider increasing the limit."
        )
      }
    ws()
  }

  private def `int`(): Unit = if (!ch('0')) oneOrMoreDigits()
  private def `frac`(): Unit = if (ch('.')) oneOrMoreDigits()
  private def `exp`(): Unit = if (ch('e') || ch('E')) { ch('-') || ch('+'); oneOrMoreDigits() }

  private def oneOrMoreDigits(): Unit = if (DIGIT()) zeroOrMoreDigits() else fail("DIGIT")
  @tailrec private def zeroOrMoreDigits(): Unit = if (DIGIT()) zeroOrMoreDigits()

  private def DIGIT(): Boolean = cursorChar >= '0' && cursorChar <= '9' && advance()

  // http://tools.ietf.org/html/rfc4627#section-2.5
  private def `string`(): String = {
    if (cursorChar != '"') fail("'\"'")
    val res = stringFast(input.cursor)
    ws()
    res
  }

  var charBuffer: Array[Char] = JsonParser.charBufferCache.get()

  private def stringFast(start: Int): String = {
    /* Scan to the end of a simple string as fast as possible, no escapes, no unicode */
    // so far unclear results if that is really helpful
    @tailrec def parseOneFast(cursor: Int, charsRead: Int): String = {
      val b = input.charAt(cursor)
      charBuffer(charsRead) = b

      if (b == '"') {
        input.setCursor(cursor) // now at end '"'
        advance()
        new String(charBuffer, 0, charsRead)
      } else if (((b - 32) ^ 60) <= 0) // trick from jsoniter-scala
        parseOneSlow(cursor, charsRead) // fall back to slower parsing
      else
        parseOneFast(cursor + 1, charsRead + 1)
    }

    /*
     * Do full string parsing. This subsumes the functionality from parseOneFast, however, just using
     * this one for all parsing is significantly slower (> 10 %). A possible reason might be that using
     * this function for all string parsing has worse branch prediction than the simple and fast variant?
     */
    @tailrec def parseOneSlow(cursor: Int, charsRead: Int): String = {
      val b = input.charAt(cursor)
      charBuffer(charsRead) = b

      if (b == '"') {
        input.setCursor(cursor) // now at end '"'
        advance()
        new String(charBuffer, 0, charsRead)
      } else if (b == '\\') {
        val skip = escaped(cursor, charsRead)
        parseOneSlow(cursor + skip, charsRead + 1)
      } else if (input.needsDecoding && b >= 128) {
        val res = unicodeChar(b, cursor, charsRead)
        val skip = res & 0x7
        val read = res >> 4
        parseOneSlow(cursor + skip, charsRead + read + 1)
      } else
        parseOneSlow(cursor + 1, charsRead + 1)
    }

    def escaped(cursor: Int, charsRead: Int): Int =
      input.byteAt(cursor + 1) match {
        case c @ ('"' | '/' | '\\') =>
          charBuffer(charsRead) = c.toChar
          2
        case 'b' =>
          charBuffer(charsRead) = '\b'
          2
        case 'f' =>
          charBuffer(charsRead) = '\f'
          2
        case 'n' =>
          charBuffer(charsRead) = '\n'
          2
        case 'r' =>
          charBuffer(charsRead) = '\r'
          2
        case 't' =>
          charBuffer(charsRead) = '\t'
          2
        case 'u' =>
          def hexValue(c: Byte): Int =
            if ('0' <= c && c <= '9') c - '0'
            else if ('a' <= c && c <= 'f') c - 87
            else if ('A' <= c && c <= 'F') c - 55
            else fail("hex digit")

          charBuffer(charsRead) =
            ((hexValue(input.byteAt(cursor + 2)) << 12) |
              (hexValue(input.byteAt(cursor + 3)) << 8) |
              (hexValue(input.byteAt(cursor + 4)) << 4) |
              hexValue(input.byteAt(cursor + 5))).toChar

          6
        case _ => fail("JSON escape sequence")
      }
    def unicodeChar(b: Char, cursor: Int, charsRead: Int): Int = {
      if ((b & 0xE0) == 0xC0) { // two byte sequence
        charBuffer(charsRead) = (((b & 0x1f) << 6) |
          (input.byteAt(cursor + 1) & 0x3f)).toChar
        2
      } else if ((b & 0xF0) == 0xE0) { // 3-byte UTF-8 sequence
        val codePoint =
          ((b & 0x0f) << 12) |
            ((input.byteAt(cursor + 1) & 0x3f) << 6) |
            (input.byteAt(cursor + 2) & 0x3f)

        if (codePoint < 0xffff) {
          charBuffer(charsRead) = codePoint.toChar
          3
        } else {
          charBuffer(charsRead) = ((0xd7C0 + (codePoint >> 10)).toChar)
          charBuffer(charsRead + 1) = ((0xdc00 + (codePoint & 0x3ff)).toChar)
          3
        }
      } else if ((b & 0xF8) == 0xF0) { // 4-byte UTF-8 sequence
        val codePoint =
          ((b & 0x07) << 18) |
            ((input.byteAt(cursor + 1) & 0x3f) << 12) |
            ((input.byteAt(cursor + 2) & 0x3f) << 6) |
            (input.byteAt(cursor + 3) & 0x3f)

        if (codePoint < 0xffff) {
          charBuffer(charsRead) = codePoint.toChar
          4
        } else {
          charBuffer(charsRead) = ((0xd7C0 + (codePoint >> 10)).toChar)
          charBuffer(charsRead + 1) = ((0xdc00 + (codePoint & 0x3ff)).toChar)
          0x14
        }
      } else
        fail("utf-8 sequence")
    }

    try
      if (input.byteAt(start + 1) == '"') { // really fast path for empty strings
        input.setCursor(start + 1)
        advance()
        ""
      } else
        parseOneFast(start + 1, 0)
    catch {
      case NonFatal(e: ArrayIndexOutOfBoundsException) =>
        val newSize = (charBuffer.size * 2).min(settings.maxStringCharacters)
        if (charBuffer.size < newSize) {
          charBuffer = new Array[Char](newSize)
          JsonParser.charBufferCache.set(charBuffer)
          stringFast(start)
        } else
          throw e
    }
  }

  @tailrec private def ws(): Unit =
    // fast test whether cursorChar is one of " \n\r\t"
    if (((1L << cursorChar) & ((cursorChar - 64) >> 31) & 0x100002600L) != 0L) { advance(); ws() }

  ////////////////////////// HELPERS //////////////////////////

  private def ch(c: Char): Boolean = if (cursorChar == c) { advance(); true } else false
  private def ws(c: Char): Boolean = if (ch(c)) { ws(); true } else false
  private def advance(): Boolean = { cursorChar = input.nextChar(); true }
  private def require(c: Char): Unit = if (!ch(c)) fail(s"'$c'")

  private def fail(target: String, cursor: Int = input.cursor, errorChar: Char = cursorChar): Nothing = {
    val ParserInput.Line(lineNr, col, text) = input.getLine(cursor)
    val summary = {
      val unexpected =
        if (errorChar != EOI) {
          val c = if (Character.isISOControl(errorChar)) "\\u%04x" format errorChar.toInt else errorChar.toString
          s"character '$c'"
        } else "end-of-input"
      val expected = if (target != "'\uFFFF'") target else "end-of-input"
      s"Unexpected $unexpected at input index $cursor (line $lineNr, position $col), expected $expected"
    }
    val detail = {
      val sanitizedText = text.map(c => if (Character.isISOControl(c)) '?' else c)
      s"\n$sanitizedText\n${" " * (col - 1)}^\n"
    }
    throw new ParsingException(summary, detail)
  }
}

trait ParserInput {
  /**
   * Advance the cursor and get the next char.
   * Since the char is required to be a 7-Bit ASCII char no decoding is required.
   */
  def nextChar(): Char

  def cursor: Int
  def setCursor(newCursor: Int): Unit
  def length: Int
  def sliceString(start: Int, end: Int): String
  def sliceCharArray(start: Int, end: Int): Array[Char]
  def getLine(index: Int): ParserInput.Line
  def byteAt(offset: Int): Byte
  def charAt(offset: Int): Char
  def needsDecoding: Boolean
  def isAtEOI: Boolean = cursor == length
}

object ParserInput {
  private final val EOI = '\uFFFF' // compile-time constant
  private final val ErrorChar = '\uFFFD' // compile-time constant, universal UTF-8 replacement character '�'

  implicit def apply(string: String): StringBasedParserInput = new StringBasedParserInput(string)
  implicit def apply(chars: Array[Char]): CharArrayBasedParserInput = new CharArrayBasedParserInput(chars)
  implicit def apply(bytes: Array[Byte]): ByteArrayBasedParserInput = new ByteArrayBasedParserInput(bytes)

  case class Line(lineNr: Int, column: Int, text: String)

  abstract class DefaultParserInput extends ParserInput {
    protected var _cursor: Int = -1
    def cursor = _cursor
    override def setCursor(newCursor: Int): Unit = _cursor = newCursor

    def getLine(index: Int): Line = {
      val sb = new java.lang.StringBuilder
      @tailrec def rec(ix: Int, lineStartIx: Int, lineNr: Int): Line =
        nextChar() /* FIXME */ match {
          case '\n' if index > ix =>
            sb.setLength(0); rec(ix + 1, ix + 1, lineNr + 1)
          case '\n' | EOI => Line(lineNr, index - lineStartIx + 1, sb.toString)
          case c          => sb.append(c); rec(ix + 1, lineStartIx, lineNr)
        }
      val savedCursor = _cursor
      _cursor = -1
      val line = rec(ix = 0, lineStartIx = 0, lineNr = 1)
      _cursor = savedCursor
      line
    }
  }

  private val UTF8 = Charset.forName("UTF-8")

  /**
   * ParserInput that allows to create a ParserInput from any randomly accessible indexed byte storage.
   */
  abstract class IndexedBytesParserInput extends DefaultParserInput {
    def length: Int
    def byteAt(offset: Int): Byte

    def nextChar() = {
      _cursor += 1
      if (_cursor < length) (byteAt(_cursor) & 0xFF).toChar else EOI
    }
  }

  /**
   * ParserInput reading directly off a byte array which is assumed to contain the UTF-8 encoded representation
   * of the JSON input, without requiring a separate decoding step.
   */
  class ByteArrayBasedParserInput(bytes: Array[Byte]) extends IndexedBytesParserInput {
    def byteAt(offset: Int): Byte = bytes(offset)
    def length: Int = bytes.length
    def charAt(offset: Int): Char = (byteAt(offset) & 0xff).toChar
    def needsDecoding: Boolean = true

    def sliceString(start: Int, end: Int) = new String(bytes, start, end - start, UTF8)
    def sliceCharArray(start: Int, end: Int) =
      UTF8.decode(ByteBuffer.wrap(java.util.Arrays.copyOfRange(bytes, start, end))).array()
  }

  class StringBasedParserInput(string: String) extends DefaultParserInput {
    def byteAt(offset: Int): Byte = string.charAt(offset).toByte
    def charAt(offset: Int): Char = string.charAt(offset)
    def needsDecoding: Boolean = false

    def nextChar(): Char = {
      _cursor += 1
      if (_cursor < string.length) string.charAt(_cursor) else EOI
    }
    def length = string.length
    def sliceString(start: Int, end: Int) = string.substring(start, end)
    def sliceCharArray(start: Int, end: Int) = {
      val chars = new Array[Char](end - start)
      string.getChars(start, end, chars, 0)
      chars
    }
  }

  class CharArrayBasedParserInput(chars: Array[Char]) extends DefaultParserInput {
    def byteAt(offset: Int): Byte = chars(offset).toByte
    def charAt(offset: Int): Char = chars(offset)
    def needsDecoding: Boolean = false

    def nextChar(): Char = {
      _cursor += 1
      if (_cursor < chars.length) chars(_cursor) else EOI
    }
    def length = chars.length
    def sliceString(start: Int, end: Int) = new String(chars, start, end - start)
    def sliceCharArray(start: Int, end: Int) = java.util.Arrays.copyOfRange(chars, start, end)
  }
}
