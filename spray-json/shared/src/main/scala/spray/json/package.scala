/*
 * Copyright (C) 2009-2011 Mathias Doenitz
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

package spray

package object json {

  type JsField = (String, JsValue)

  def deserializationError(msg: String, cause: Throwable = null, fieldNames: List[String] = Nil) = throw new DeserializationException(msg, cause, fieldNames)
  def serializationError(msg: String) = throw new SerializationException(msg)

  def jsonReader[T](implicit reader: JsonReader[T]) = reader
  def jsonWriter[T](implicit writer: JsonWriter[T]) = writer

  implicit def enrichAny[T](any: T): RichAny[T] = new RichAny(any)
  def enrichString(string: String) = new RichString(string)
  implicit class RichWithInput[I](any: I)(implicit i: Input[I]) {
    def parseJson: JsValue = json.parseJson(any)
    def parseJson(settings: JsonParserSettings): JsValue = json.parseJson(any, settings)
    def parseJsonAs[T: JsonReader]: T = json.parseJsonAs(any)
    def parseJsonAs[T: JsonReader](settings: JsonParserSettings): T = json.parseJsonAs(any, settings)
  }

  @deprecated("use enrichAny", "1.3.4")
  def pimpAny[T](any: T) = new PimpedAny(any)
  @deprecated("use enrichString", "1.3.4")
  def pimpString(string: String) = new PimpedString(string)

  def parseJson[I](i: I, settings: JsonParserSettings = JsonParserSettings.default)(implicit input: Input[I]): JsValue =
    JsonParser(input.parserInput(i), settings)

  def parseJsonAs[I: Input, T: JsonReader](i: I, settings: JsonParserSettings = JsonParserSettings.default): T =
    parseJson(i, settings).convertTo[T]
}

package json {

  case class DeserializationException(msg: String, cause: Throwable = null, fieldNames: List[String] = Nil) extends RuntimeException(msg, cause)
  class SerializationException(msg: String) extends RuntimeException(msg)

  private[json] class RichAny[T](any: T) {
    def toJson(implicit writer: JsonWriter[T]): JsValue = writer.write(any)
  }

  private[json] class RichString(string: String) {
    @deprecated("deprecated in favor of parseJson", "1.2.6")
    def asJson: JsValue = parseJson
    def parseJson: JsValue = json.parseJson(string)
    def parseJson(settings: JsonParserSettings): JsValue = json.parseJson(string, settings)
  }

  @deprecated("use RichAny", "1.3.4")
  private[json] class PimpedAny[T](any: T) {
    def toJson(implicit writer: JsonWriter[T]): JsValue = writer.write(any)
  }

  @deprecated("use RichString", "1.3.4")
  private[json] class PimpedString(string: String) {
    @deprecated("deprecated in favor of parseJson", "1.2.6")
    def asJson: JsValue = parseJson
    def parseJson: JsValue = string.parseJson
  }
}
