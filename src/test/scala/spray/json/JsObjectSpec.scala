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


class JsObjectSpec extends Specification {
  val objectA = JsObject(
    "a" -> JsNumber(42),
    "b" -> JsArray(JsNumber(1), JsNumber(2), JsNumber(3)))

  val objectB = JsObject(
    "b" -> JsArray(JsNumber(100), JsNumber(200), JsNumber(300)),
    "c" -> JsObject("foo" -> JsString("bar")))


  "The mergeWith" should {
    "merge JsObjects together" in {
      (objectA mergeWith objectB) mustEqual JsObject(
        "a" -> JsNumber(42),
        "b" -> JsArray(JsNumber(100), JsNumber(200), JsNumber(300)),
        "c" -> JsObject("foo" -> JsString("bar")))
    }
  }


  "The ++ operator" should {
    "be equivalent to mergeWith" in {
      (objectA mergeWith objectB) mustEqual(objectA ++ objectB)
    }
  }
}
