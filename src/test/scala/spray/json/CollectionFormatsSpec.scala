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
import java.util.Arrays

class CollectionFormatsSpec extends Specification with DefaultJsonProtocol {

  "The listFormat" should {
    val list = List(1, 2, 3)
    val json = JsArray(JsNumber(1), JsNumber(2), JsNumber(3))
    "convert a List[Int] to a JsArray of JsNumbers" in {
      list.toJson mustEqual json
    }
    "convert a JsArray of JsNumbers to a List[Int]" in {
      json.convertTo[List[Int]] mustEqual list
    }
  }
  
  "The arrayFormat" should {
    val array = Array(1, 2, 3)
    val json = JsArray(JsNumber(1), JsNumber(2), JsNumber(3))
    "convert an Array[Int] to a JsArray of JsNumbers" in {
      array.toJson mustEqual json
    }
    "convert a JsArray of JsNumbers to an Array[Int]" in {
      Arrays.equals(json.convertTo[Array[Int]], array) must beTrue
    }
  }
  
  "The mapFormat" should {
    val map = Map("a" -> 1, "b" -> 2, "c" -> 3)
    val json = JsObject("a" -> JsNumber(1), "b" -> JsNumber(2), "c" -> JsNumber(3))
    "convert a Map[String, Long] to a JsObject" in {
      map.toJson mustEqual json
    }
    "be able to convert a JsObject to a Map[String, Long]" in {
      json.convertTo[Map[String, Long]] mustEqual map
    }
    "convert a Map[Int, String] to a JsObject" in {
      Map(1 -> "a").toJson mustEqual JsObject("1" -> JsString("a"))
    }
    "be able to convert a JsObject to a Map[Long, Int]" in {
      val jsn = JsObject("1" -> JsNumber(1), "2" -> JsNumber(2), "3" -> JsNumber(3))
      val mp: Map[Long, Int] = Map(1L -> 1, 2L -> 2, 3L -> 3)

      jsn.convertTo[Map[Long, Int]] mustEqual Map(1L -> 1, 2L -> 2, 3L -> 3)
      jsn.convertTo[Map[Int, Long]] mustEqual Map(1 -> 1L, 2 -> 2L, 3 -> 3L)
    }
    "throw an Exception when trying to deserialize a map whose key are not deserialized to JsNumber" in {
      JsObject("a" -> JsString("1")).convertTo[Map[Int, String]] must throwA(new DeserializationException("Expected Map key to be deserializable to JsNumber, but got 'a'"))
    }
    "throw an Exception when trying to serialize a map whose key are not serialized to JsNumber" in {
      Map(1.5 -> "a").toJson must throwA(new SerializationException("Map key must be convertible to JsNumber, not '1.5'"))
    }
  }
  
  "The immutableSetFormat" should {
    val set = Set(1, 2, 3)
    val json = JsArray(JsNumber(1), JsNumber(2), JsNumber(3))
    "convert a Set[Int] to a JsArray of JsNumbers" in {
      set.toJson mustEqual json
    }
    "convert a JsArray of JsNumbers to a Set[Int]" in {
      json.convertTo[Set[Int]] mustEqual set
    }
  }

  "The indexedSeqFormat" should {
    val seq = collection.IndexedSeq(1, 2, 3)
    val json = JsArray(JsNumber(1), JsNumber(2), JsNumber(3))
    "convert a Set[Int] to a JsArray of JsNumbers" in {
      seq.toJson mustEqual json
    }
    "convert a JsArray of JsNumbers to a IndexedSeq[Int]" in {
      json.convertTo[collection.IndexedSeq[Int]] mustEqual seq
    }
  }
  
}
