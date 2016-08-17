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

import java.io.{NotSerializableException, ObjectOutputStream}

import org.specs2.mutable._
import org.specs2.reporter.NullOutputStream

class AdditionalFormatsSpec extends Specification with Serializable {

  case class Container[A](inner: Option[A])

  object ReaderProtocol extends DefaultJsonProtocol {
    implicit def containerReader[T :JsonFormat] = lift {
      new JsonReader[Container[T]] {
        def read(value: JsValue) = value match {
          case JsObject(fields) if fields.contains("content") => Container(Some(jsonReader[T].read(fields("content"))))
          case _ => deserializationError("Unexpected format: " + value.toString)
        }
      }
    }
  }

  object WriterProtocol extends DefaultJsonProtocol {
    implicit def containerWriter[T :JsonFormat] = lift {
      new JsonWriter[Container[T]] {
        def write(obj: Container[T]) = JsObject("content" -> obj.inner.toJson)
      }
    }
  }

  def verifySerialization[T](obj: T) =
    new ObjectOutputStream(NullOutputStream).writeObject(obj) must not(throwA[NotSerializableException])

  "The liftJsonWriter" should {
    import WriterProtocol._
    val obj = Container(Some(Container(Some(List(1, 2, 3)))))

    "properly write a Container[Container[List[Int]]] to JSON" in {
      obj.toJson.toString mustEqual """{"content":{"content":[1,2,3]}}"""
    }

    "be serializable" in {
      verifySerialization(implicitly[JsonFormat[Container[Int]]])
    }
  }

  "The liftJsonReader" should {
    import ReaderProtocol._
    val obj = Container(Some(Container(Some(List(1, 2, 3)))))

    "properly read a Container[Container[List[Int]]] from JSON" in {
      """{"content":{"content":[1,2,3]}}""".parseJson.convertTo[Container[Container[List[Int]]]] mustEqual obj
    }

    "be serializable" in {
      verifySerialization(implicitly[JsonFormat[Container[Int]]])
    }
  }

  case class Foo(id: Long, name: String, foos: Option[List[Foo]] = None)

  object FooProtocol extends DefaultJsonProtocol {
    implicit val fooProtocol: JsonFormat[Foo] = lazyFormat(jsonFormat(Foo, "id", "name", "foos"))
  }

  "The lazyFormat wrapper" should {
    import FooProtocol._
    "enable recursive format definitions" in {
      Foo(1, "a", Some(Foo(2, "b", Some(Foo(3, "c") :: Nil)) :: Foo(4, "d") :: Nil)).toJson.toString mustEqual
        """{"id":1,"name":"a","foos":[{"id":2,"name":"b","foos":[{"id":3,"name":"c"}]},{"id":4,"name":"d"}]}"""
    }
    "be serializable" in {
      verifySerialization(fooProtocol)
    }
  }
}