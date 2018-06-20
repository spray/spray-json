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

class SortedPrinterSpec extends Specification {

  "The SortedPrinter" should {
    "print a more complicated JsObject nicely aligned with fields sorted" in {
      val obj = JsonParser {
        """{
          |  "Unic\u00f8de" :  "Long string with newline\nescape",
          |  "Boolean no": false,
          |  "number": -1.2323424E-5,
          |  "key with \"quotes\"" : "string",
          |  "key with spaces": null,
          |  "simpleKey" : "some value",
          |    "zero": 0,
          |  "sub object" : {
          |    "sub key": 26.5,
          |    "a": "b",
          |    "array": [1, 2, { "yes":1, "no":0 }, ["a", "b", null], false]
          |  },
          |  "Boolean yes":true
          |}""".stripMargin
      }
      SortedPrinter(obj) mustEqual {
        """{
          |  "Boolean no": false,
          |  "Boolean yes": true,
          |  "Unic\u00f8de": "Long string with newline\nescape",
          |  "key with \"quotes\"": "string",
          |  "key with spaces": null,
          |  "number": -0.000012323424,
          |  "simpleKey": "some value",
          |  "sub object": {
          |    "a": "b",
          |    "array": [1, 2, {
          |      "no": 0,
          |      "yes": 1
          |    }, ["a", "b", null], false],
          |    "sub key": 26.5
          |  },
          |  "zero": 0
          |}""".stripMargin
      }
    }
  }

}
