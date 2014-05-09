/*
 * Copyright (C) 2009-2011 Mathias Doenitz
 * Inspired by a similar implementation by Nathan Hamblen
 * (https://github.com/n8han/Databinder-Dispatch)
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

import collection.immutable.ListMap


/**
  * The general type of a JSON AST node.
 */
sealed abstract class JsValue {
  override def toString = compactPrint
  def toString(printer: (JsValue => String)) = printer(this)
  def compactPrint = CompactPrinter(this)
  def prettyPrint = PrettyPrinter(this)
  def convertTo[T :JsonReader]: T = jsonReader[T].read(this)

  /**
   * Returns `this` if this JsValue is a JsObject, otherwise throws a DeserializationException with the given error msg.
   */
  def asJsObject(errorMsg: String = "JSON object expected"): JsObject = deserializationError(errorMsg)

  /**
   * Returns `this` if this JsValue is a JsObject, otherwise throws a DeserializationException.
   */
  def asJsObject: JsObject = asJsObject()

  @deprecated("Superceded by 'convertTo'", "1.1.0")
  def fromJson[T :JsonReader]: T = convertTo
}

/**
  * A JSON object.
 */
case class JsObject(fields: Map[String, JsValue]) extends JsValue {
  override def asJsObject(errorMsg: String) = this
  def getFields(fieldNames: String*): Seq[JsValue] = fieldNames.flatMap(fields.get)
}
object JsObject {
  // we use a ListMap in order to preserve the field order
  def apply(members: JsField*) = new JsObject(ListMap(members: _*))
  def apply(ordered: Boolean, members: JsField*) = {
    if(ordered) {
      new JsObject(ListMap(members: _*))
    }
    else {
      new JsObject(Map(members: _*))
    }
  }
  def apply(members: List[JsField]) = new JsObject(ListMap(members: _*))
  def apply(ordered: Boolean, members: List[JsField]) = {
    if(ordered) {
      new JsObject(ListMap(members: _*))
    } else {
      new JsObject(Map(members: _*))
    }
  }

}

/**
  * A JSON array.
 */
case class JsArray(elements: List[JsValue]) extends JsValue
object JsArray {
  def apply(elements: JsValue*) = new JsArray(elements.toList)
}

/**
  * A JSON string.
 */
case class JsString(value: String) extends JsValue

object JsString {
  def apply(value: Symbol) = new JsString(value.name)
}

/**
  * A JSON number.
 */
case class JsNumber(value: BigDecimal) extends JsValue
object JsNumber {
  def apply(n: Int) = new JsNumber(BigDecimal(n))
  def apply(n: Long) = new JsNumber(BigDecimal(n))
  def apply(n: Double) = n match {
    case n if n.isNaN      => JsNull
    case n if n.isInfinity => JsNull
    case _                 => new JsNumber(BigDecimal(n))
  }
  def apply(n: BigInt) = new JsNumber(BigDecimal(n))
  def apply(n: String) = new JsNumber(BigDecimal(n))
}

/**
  * JSON Booleans.
 */
sealed abstract class JsBoolean extends JsValue {
  def value: Boolean
}
object JsBoolean {
  def apply(x: Boolean): JsBoolean = if (x) JsTrue else JsFalse
  def unapply(x: JsBoolean): Option[Boolean] = Some(x.value)
}
case object JsTrue extends JsBoolean {
  def value = true
}
case object JsFalse extends JsBoolean {
  def value = false
}

/**
  * The representation for JSON null.
 */
case object JsNull extends JsValue
