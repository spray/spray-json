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
