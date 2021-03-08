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

import org.specs2.ScalaCheck

import scala.collection.immutable.TreeSet

class CollectionFormatsSpec extends Specification with DefaultJsonProtocol with ScalaCheck {

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
    "convert a Map[Int, String] to a JsObject given a KeyableFormat" in {
      implicit val keyableFormat: KeyableFormat[Int] = new KeyableFormat[Int] {
        override def read(jsString: JsString) = jsString.value.toInt
        override def write(obj: Int) = obj.toString
      }

      val map = Map(1 -> "a", 2 -> "b", 3 -> "c")
      val json = JsObject("1" -> JsString("a"), "2" -> JsString("b"), "3" -> JsString("c"))

      map.toJson mustEqual json
    }
    "be able to convert a JsObject to a Map[Int, String] given a KeyableFormat" in {
      implicit val keyableFormat: KeyableFormat[Int] = new KeyableFormat[Int] {
        override def read(jsString: JsString) = jsString.value.toInt
        override def write(obj: Int) = obj.toString
      }

      val map = Map(1 -> "a", 2 -> "b", 3 -> "c")
      val json = JsObject("1" -> JsString("a"), "2" -> JsString("b"), "3" -> JsString("c"))

      json.convertTo[Map[Int, String]] mustEqual map
    }
  }
  
  "The immutableSetFormat" should {
    val set = Set(1, 2, 3)
    val numbers = Set(JsNumber(1), JsNumber(2), JsNumber(3))
    "convert a Set[Int] to a JsArray of JsNumbers" in {
      set.toJson.asInstanceOf[JsArray].elements.toSet mustEqual numbers
    }
    "convert a JsArray of JsNumbers to a Set[Int]" in {
      JsArray(numbers.toVector).convertTo[Set[Int]] mustEqual set
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

  "viaSeq" should {
    "maintain order" in prop { s: TreeSet[Long] =>
      viaSeq[TreeSet[Long], Long](TreeSet(_: _*)).write(s) must_== s.toList.toJson
    }
  }
}
