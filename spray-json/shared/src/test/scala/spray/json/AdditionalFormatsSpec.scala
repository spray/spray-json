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

import org.specs2.mutable._

class AdditionalFormatsSpec extends Specification with RoundTripSpecBase {

  case class Container[A](inner: Option[A])

  object ReaderProtocol extends DefaultJsonProtocol {
    implicit def containerReader[T: JsonFormat]: JsonFormat[Container[T]] = lift {
      new JsonReader[Container[T]] {
        def read(value: JsValue) = value match {
          case JsObject(fields) if fields.contains("content") => Container(Some(jsonReader[T].read(fields("content"))))
          case _ => deserializationError("Unexpected format: " + value.toString)
        }
      }
    }
  }

  object WriterProtocol extends DefaultJsonProtocol {
    implicit def containerWriter[T: JsonFormat]: JsonFormat[Container[T]] = lift {
      new JsonWriter[Container[T]] {
        def write(obj: Container[T]) = JsObject("content" -> obj.inner.toJson)
      }
    }
  }

  "The liftJsonWriter" should {
    val obj = Container(Some(Container(Some(List(1, 2, 3)))))

    "properly write a Container[Container[List[Int]]] to JSON" in {
      import WriterProtocol._
      obj.toJson.toString mustEqual """{"content":{"content":[1,2,3]}}"""
    }

    "properly read a Container[Container[List[Int]]] from JSON" in {
      import ReaderProtocol._
      """{"content":{"content":[1,2,3]}}""".parseJson.convertTo[Container[Container[List[Int]]]] mustEqual obj
    }
  }

  case class Foo(id: Long, name: String, foos: Option[List[Foo]] = None)

  object FooProtocol extends DefaultJsonProtocol {
    implicit val fooProtocol: JsonFormat[Foo] = lazyFormat(jsonFormat(Foo.apply, "id", "name", "foos"))
  }

  "The lazyFormat wrapper" should {
    "enable recursive format definitions" in {
      import FooProtocol._
      val json = Foo(1, "a", Some(Foo(2, "b", Some(Foo(3, "c") :: Nil)) :: Foo(4, "d") :: Nil)).toJson

      json mustEqual
        """{"id":1,"name":"a","foos":[{"id":2,"name":"b","foos":[{"id":3,"name":"c"}]},{"id":4,"name":"d"}]}""".parseJson
    }
  }

  "AdditionalFormats" should {
    "provide implicits (with DefaultJsonProtocol import) for" in {
      "JsValue" in {
        import DefaultJsonProtocol._ // check that no implicits conflict
        safeReader[JsValue] // dummy to silence unused warning for above import

        roundTrip[JsValue]("53", JsNumber(53))
        roundTrip[JsValue]("[]", JsArray())
        roundTrip[JsValue]("{}", JsObject())
        roundTrip[JsValue]("\"test\"", JsString("test"))
        roundTrip[JsValue]("[{}]", JsArray(JsObject()))
      }
      "JsObject" in {
        import DefaultJsonProtocol._ // check that no implicits conflict
        safeReader[JsObject] // dummy to silence unused warning for above import

        roundTrip[JsObject]("{}", JsObject())
        roundTrip[JsObject]("""{"a":42}""", JsObject("a" -> JsNumber(42)))
      }
      "JsArray" in {
        import DefaultJsonProtocol._ // check that no implicits conflict
        safeReader[JsArray] // dummy to silence unused warning for above import

        roundTrip[JsArray]("[]", JsArray())
        roundTrip[JsArray]("[1,2,3]", JsArray(Seq(1, 2, 3).map(JsNumber(_): JsValue): _*))
      }
    }
    "provide implicits (without any import) for" in {
      "JsValue" in {
        roundTrip[JsValue]("53", JsNumber(53))
        roundTrip[JsValue]("[]", JsArray())
        roundTrip[JsValue]("{}", JsObject())
        roundTrip[JsValue]("\"test\"", JsString("test"))
        roundTrip[JsValue]("[{}]", JsArray(JsObject()))
      }
      "JsObject" in {
        roundTrip[JsObject]("{}", JsObject())
        roundTrip[JsObject]("""{"a":42}""", JsObject("a" -> JsNumber(42)))
      }
      "JsArray" in {
        roundTrip[JsArray]("[]", JsArray())
        roundTrip[JsArray]("[1,2,3]", JsArray(Seq(1, 2, 3).map(JsNumber(_): JsValue): _*))
      }
    }
  }
}
