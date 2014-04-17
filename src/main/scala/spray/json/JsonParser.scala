/*
 * Copyright (C) 2014 Ruud Diterwich
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

import java.lang.StringBuilder
import scala.collection.mutable.ListBuffer
import org.parboiled.errors.ParsingException

/**
 * This JSON parser is the almost direct implementation of the JSON grammar.
 * It has been optimized for speed, and is about 17 times faster than the original scala-parsing based
 * implementation.
 */
object JsonParser {
  def apply(s: String): JsValue =
    (new JsonParser).parse(s)

  def apply(s: Array[Char]): JsValue =
    (new JsonParser).parse(s)
}

/**
 * One can re-use a parser instance instead of using the object's apply methods. Performance will be slightly
 * better. This class, however, is NOT thread-safe.
 */
class JsonParser {

  private var s: Array[Char] = Array.empty
  private var i: Int = 0
  private var c: Char = 0
  private val sb = new StringBuilder

  def parse(s: String): JsValue =
    parse(s.toCharArray)

  def parse(s: Array[Char]): JsValue = {
    this.s = s
    this.i = -1
    next()
    whitespace()
    val result = jsonValue()
    whitespace()
    if (c != 0)
      exception("expected end of document")
    result
  }

  private def jsonObject(): Option[JsObject] = {
    if (c == '{') {
      next()
      val ab = new ListBuffer[(String, JsValue)]
      whitespace()
      while (c != '}') {
        whitespace()
        string() match {
          case Some(name) =>
            whitespace()
            if (c != ':')
              exception("Expected ':'")
            next()
            whitespace()
            val v = jsonValue()
            ab += name -> v
          case None =>
            exception("Expected name")
        }
        whitespace()
        if (c == ',') next()
        else if (c != '}')
          exception("expected '}'")
      }
      next()
      Some(JsObject(ab.toList))
    }
    else None
  }

  private def jsonArray(): Option[JsArray] = {
    if (c == '[') {
      next()
      val lb = new ListBuffer[JsValue]
      whitespace()
      while (c != ']') {
        whitespace()
        lb += jsonValue()
        whitespace()
        if (c == ',')
          next()
        else if (c != ']')
          exception("expected ']'")
      }
      next()
      Some(new JsArray(lb.toList))
    }
    else None
  }

  private def jsonValue(): JsValue =
    jsonString() orElse
      jsonNumber() orElse
      jsonObject() orElse
      jsonArray() orElse
      jsonConstant() getOrElse exception("value expected")

  private def jsonString(): Option[JsString] =
    string().map(JsString(_))

  private def jsonConstant(): Option[JsValue] = {
    if (c == 't') {
      for (cc <- "true")
        if (c == cc) next() else exception("expected 'true'")
      Some(JsTrue)
    }
    else if (c == 'f') {
      for (cc <- "false")
        if (c == cc) next() else exception("expected 'false'")
      Some(JsFalse)
    }
    else if (c == 'n') {
      for (cc <- "null")
        if (c == cc) next() else exception("expected 'null'")
      Some(JsNull)
    }
    else None
  }

  private def string(): Option[String] = {
    if (c == '"') {
      next()
      sb.setLength(0)
      while (c != 0 && c != '"') {
        if (c == '\\') {
          next()
          c match {
            case '"' => sb.append('"'); next()
            case '\\' => sb.append('\\'); next()
            case '/' => sb.append('/'); next()
            case 'b' => sb.append('\b'); next()
            case 'f' => sb.append('\f'); next()
            case 'n' => sb.append('\n'); next()
            case 'r' => sb.append('\r'); next()
            case 't' => sb.append('\t'); next()
            case 'u' =>
              next()
              val code =
                (hexDigit() << 12) +
                  (hexDigit() << 8) +
                  (hexDigit() << 4) +
                  hexDigit()
              sb.append(code.asInstanceOf[Char])
            case _ => exception("expected escape char")
          }
        } else {
          sb.append(c)
          next()
        }
      }
      if (c != '"')
        exception("expected '\"'")
      next()
      Some(sb.toString)
    }
    else None
  }

  private def jsonNumber(): Option[JsNumber] = {
    sb.setLength(0)
    if (c == '-') {
      sb.append(c)
      next()
    }
    while (c >= '0' && c <= '9') {
      sb.append(c)
      next()
    }
    if (c == '.') {
      sb.append(c)
      next()
      while (c >= '0' && c <= '9') {
        sb.append(c)
        next()
      }
    }
    if (c == 'e' || c == 'E') {
      sb.append(c)
      next()
      if (c == '-' || c == '+') {
        sb.append(c)
        next()
      }
      while (c >= '0' && c <= '9') {
        sb.append(c)
        next()
      }
    }
    if (sb.length != 0) Some(JsNumber(sb.toString))
    else None
  }

  @inline
  private def whitespace() =
    while (Character.isWhitespace(c))
      next()

  @inline
  private def hexDigit(): Int = {
    val m = c
    if (c >= 'a' && c <= 'f') {
      next()
      m - 'a' + 10
    }
    else if (c >= 'A' && c <= 'F') {
      next()
      m - 'A' + 10
    }
    else if (c >= '0' && c <= '9') {
      next()
      m - '0'
    }
    else exception("Hex digit expected")
  }

  @inline
  private def next() {
    i += 1
    if (i < s.length) {
      c = s(i)
    } else {
      c = 0
    }
  }

  def exception(message: String) =
    throw new JsonParseException(new String(s), i, message)
}

/**
 * ParsingException super class added for compatibility reasons.
 */
class JsonParseException(val s: String, val pos: Int, val msg: String)
  extends ParsingException("Json parse exception: " + msg + " at position " + pos + ": " + s.substring(pos, Math.min(s.length, pos + 20)))
