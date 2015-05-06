/*
 * Copyright (C) 2011 Mathias Doenitz
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

import java.lang.reflect.Modifier
import scala.annotation.tailrec
import scala.util.control.NonFatal

/**
 * Provides the helpers for constructing custom JsonFormat implementations for types implementing the Product trait
 * (especially case classes)
 */
trait ProductFormats extends ProductFormatsInstances {
  this: StandardFormats =>

  def jsonFormat0[T](construct: () => T): RootJsonFormat[T] =
    new RootJsonFormat[T] {
      def write(p: T) = JsObject()
      def read(value: JsValue) = value match {
        case JsObject(_) => construct()
        case _ => throw new DeserializationException("Object expected")
      }
    }

  // helpers
  
  protected def productElement2Field[T](fieldName: String, p: Product, ix: Int, rest: List[JsField] = Nil)
                                       (implicit writer: JsonWriter[T]): List[JsField] = {
    val value = p.productElement(ix).asInstanceOf[T]
    writer match {
      case _: OptionFormat[_] if (value == None) => rest
      case _ => (fieldName, writer.write(value)) :: rest
    }
  }

  protected def fromField[T](value: JsValue, fieldName: String)
                                     (implicit reader: JsonReader[T]) = value match {
    case x: JsObject if
      (reader.isInstanceOf[OptionFormat[_]] &
        !x.fields.contains(fieldName)) =>
      None.asInstanceOf[T]
    case x: JsObject =>
      try reader.read(x.fields(fieldName))
      catch {
        case e: NoSuchElementException =>
          deserializationError("Object is missing required member '" + fieldName + "'", e, fieldName :: Nil)
        case DeserializationException(msg, cause, fieldNames) =>
          deserializationError(msg, cause, fieldName :: fieldNames)
      }
    case _ => deserializationError("Object expected in field '" + fieldName + "'", fieldNames = fieldName :: Nil)
  }

  protected def extractFieldNames(classManifest: ClassManifest[_]): Array[String] = {
    val clazz = classManifest.erasure
    try {
      // copy methods have the form copy$default$N(), we need to sort them in order, but must account for the fact
      // that lexical sorting of ...8(), ...9(), ...10() is not correct, so we extract N and sort by N.toInt
      val copyDefaultMethods = clazz.getMethods.filter(_.getName.startsWith("copy$default$")).sortBy(
        _.getName.drop("copy$default$".length).takeWhile(_ != '(').toInt)
      val fields = clazz.getDeclaredFields.filterNot { f =>
        import Modifier._
        (f.getModifiers & (TRANSIENT | STATIC | 0x1000 /* SYNTHETIC*/)) > 0
      }
      if (copyDefaultMethods.length != fields.length)
        sys.error("Case class " + clazz.getName + " declares additional fields")
      if (fields.zip(copyDefaultMethods).exists { case (f, m) => f.getType != m.getReturnType })
        sys.error("Cannot determine field order of case class " + clazz.getName)
      fields.map(f => ProductFormats.unmangle(f.getName))
    } catch {
      case NonFatal(ex) => throw new RuntimeException("Cannot automatically determine case class field names and order " +
        "for '" + clazz.getName + "', please use the 'jsonFormat' overload with explicit field name specification", ex)
    }
  }

}

object ProductFormats {

  private def unmangle(name: String) = {
    import java.lang.{StringBuilder => JStringBuilder}
    @tailrec def rec(ix: Int, builder: JStringBuilder): String = {
      val rem = name.length - ix
      if (rem > 0) {
        var ch = name.charAt(ix)
        var ni = ix + 1
        val sb = if (ch == '$' && rem > 1) {
          def c(offset: Int, ch: Char) = name.charAt(ix + offset) == ch
          ni = name.charAt(ix + 1) match {
            case 'a' if rem > 3 && c(2, 'm') && c(3, 'p') => { ch = '&'; ix + 4 }
            case 'a' if rem > 2 && c(2, 't') => { ch = '@'; ix + 3 }
            case 'b' if rem > 4 && c(2, 'a') && c(3, 'n') && c(4, 'g') => { ch = '!'; ix + 5 }
            case 'b' if rem > 3 && c(2, 'a') && c(3, 'r') => { ch = '|'; ix + 4 }
            case 'd' if rem > 3 && c(2, 'i') && c(3, 'v') => { ch = '/'; ix + 4 }
            case 'e' if rem > 2 && c(2, 'q') => { ch = '='; ix + 3 }
            case 'g' if rem > 7 && c(2, 'r') && c(3, 'e') && c(4, 'a') && c(5, 't') && c(6, 'e') && c(7, 'r') => { ch = '>'; ix + 8 }
            case 'h' if rem > 4 && c(2, 'a') && c(3, 's') && c(4, 'h') => { ch = '#'; ix + 5 }
            case 'l' if rem > 4 && c(2, 'e') && c(3, 's') && c(4, 's') => { ch = '<'; ix + 5 }
            case 'm' if rem > 5 && c(2, 'i') && c(3, 'n') && c(4, 'u') && c(5, 's') => { ch = '-'; ix + 6 }
            case 'p' if rem > 7 && c(2, 'e') && c(3, 'r') && c(4, 'c') && c(5, 'e') && c(6, 'n') && c(7, 't') => { ch = '%'; ix + 8 }
            case 'p' if rem > 4 && c(2, 'l') && c(3, 'u') && c(4, 's') => { ch = '+'; ix + 5 }
            case 'q' if rem > 5 && c(2, 'm') && c(3, 'a') && c(4, 'r') && c(5, 'k') => { ch = '?'; ix + 6 }
            case 't' if rem > 5 && c(2, 'i') && c(3, 'l') && c(4, 'd') && c(5, 'e') => { ch = '~'; ix + 6 }
            case 't' if rem > 5 && c(2, 'i') && c(3, 'm') && c(4, 'e') && c(5, 's') => { ch = '*'; ix + 6 }
            case 'u' if rem > 2 && c(2, 'p') => { ch = '^'; ix + 3 }
            case 'u' if rem > 5 =>
              def hexValue(offset: Int): Int = {
                val c = name.charAt(ix + offset)
                if ('0' <= c && c <= '9') c - '0'
                else if ('a' <= c && c <= 'f') c - 87
                else if ('A' <= c && c <= 'F') c - 55 else -0xFFFF
              }
              val ci = (hexValue(2) << 12) + (hexValue(3) << 8) + (hexValue(4) << 4) + hexValue(5)
              if (ci >= 0) { ch = ci.toChar; ix + 6 } else ni
            case _ => ni
          }
          if (ni > ix + 1 && builder == null) new JStringBuilder(name.substring(0, ix)) else builder
        } else builder
        rec(ni, if (sb != null) sb.append(ch) else null)
      } else if (builder != null) builder.toString else name
    }
    rec(0, null)
  }
}

/**
 * This trait supplies an alternative rendering mode for optional case class members.
 * Normally optional members that are undefined (`None`) are not rendered at all.
 * By mixing in this trait into your custom JsonProtocol you can enforce the rendering of undefined members as `null`.
 * (Note that this only affect JSON writing, spray-json will always read missing optional members as well as `null`
 * optional members as `None`.)
 */
trait NullOptions extends ProductFormats {
  this: StandardFormats =>

  override protected def productElement2Field[T](fieldName: String, p: Product, ix: Int, rest: List[JsField])
                                                (implicit writer: JsonWriter[T]) = {
    val value = p.productElement(ix).asInstanceOf[T]
    (fieldName, writer.write(value)) :: rest
  }
}
