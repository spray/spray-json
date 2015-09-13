package spray.json

import scala.collection.immutable.ListMap
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
