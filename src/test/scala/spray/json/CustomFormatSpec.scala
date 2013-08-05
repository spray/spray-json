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

import org.specs2.mutable.Specification

class CustomFormatSpec extends Specification with DefaultJsonProtocol {

  case class MyType(name: String, value: Int)

  val MyTypeProtocol = new RootJsonFormat[MyType] {
    def read(json: JsValue) = {
      json.asJsObject.getFields("name", "value") match {
        case Seq(JsString(name), JsNumber(value)) => MyType(name, value.toInt)
        case _ => deserializationError("Expected fields: 'name' (JSON string) and 'value' (JSON number)")
      }
    }
    def write(obj: MyType) = JsObject("name" -> JsString(obj.name), "value" -> JsNumber(obj.value))
  }

  val MyTypeProtocolWithOptionals = new RootJsonFormat[MyType] {

    def maybeJsObject(json:JsValue):Either[String,JsObject] =
      try{ Right(json.asJsObject) }
      catch{ case e:Throwable => Left(e.getMessage)}

    //this could be more elegant with a for comprehension but currently definitions are not allowed with either
    //https://issues.scala-lang.org/browse/SI-5793
    def maybeMyType(json:JsValue):Either[String,MyType] = maybeJsObject(json).right.flatMap{ obj =>
      obj.getString("name").right.map{ name =>{
        val value = obj.getInt("value").right.getOrElse(42)
        MyType(name,value)
      }}
    }

    def read(json: JsValue) = maybeMyType(json) match {
      case Right(my) => my
      case Left(error) => deserializationError(error)
    }

    def write(obj: MyType) = JsObject("name" -> JsString(obj.name), "value" -> JsNumber(obj.value))
  }

  "A custom JsonFormat built with 'asJsonObject'" should {
    val value = MyType("bob", 42)

    "correctly deserialize valid JSON content" in {
      implicit val format = MyTypeProtocol
      """{ "name": "bob", "value": 42 }""".asJson.convertTo[MyType] mustEqual value
    }

    "support full round-trip (de)serialization" in {
      implicit val format = MyTypeProtocol
      value.toJson.convertTo[MyType] mustEqual value
    }

    "support optional parameters" in {
      implicit val format = MyTypeProtocolWithOptionals
      """{ "name": "bob" }""".asJson.convertTo[MyType] mustEqual value
    }

    "allow overriding default parameters" in{
      implicit val format = MyTypeProtocolWithOptionals
      """{ "name": "mike", "value": 123 }""".asJson.convertTo[MyType] mustEqual MyType("mike", 123)
    }

    "throw an exception if a required field is mission" in{
      implicit val format = MyTypeProtocolWithOptionals
      """{ "value": 123 }""".asJson.convertTo[MyType] must throwA(new DeserializationException("Field name is missing"))
    }
  }

}