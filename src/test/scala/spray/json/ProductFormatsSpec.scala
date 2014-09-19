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

class ProductFormatsSpec extends Specification {

  case class Test3Map(a: Int, b: Option[Map[String,String]], c: Int)
  case class Test10Map(a: Int, b: Option[Map[String,String]], c: Int,
                       d: Int, e: Int, f: Int, g:Int,h:Int,i:Int,j:Int)

  case class Test2(a: Int, b: Option[Double])
  case class Test3[A, B](as: List[A], bs: List[B])
  case class TestTransient(a: Int, b: Option[Double]) {
    @transient var c = false
  }
  @SerialVersionUID(1L) // SerialVersionUID adds a static field to the case class
  case class TestStatic(a: Int, b: Option[Double])

  trait TestProtocol {
    this: DefaultJsonProtocol =>
    implicit val test2Format = jsonFormat2(Test2)
    implicit def test3Format[A: JsonFormat, B: JsonFormat] = jsonFormat2(Test3.apply[A, B])
    implicit def testTransientFormat = jsonFormat2(TestTransient)
    implicit def testStaticFormat = jsonFormat2(TestStatic)
    implicit def test3Map = jsonUnorderedFormat3(Test3Map)
    implicit def test10Map = jsonUnorderedFormat10(Test10Map)
  }
  object TestProtocol1 extends DefaultJsonProtocol with TestProtocol
  object TestProtocol2 extends DefaultJsonProtocol with TestProtocol with NullOptions

  "A JsonFormat created with `jsonFormat`, for a case class with 2 elements," should {
    import TestProtocol1._
    val obj = Test2(42, Some(4.2))
    val json = JsObject("a" -> JsNumber(42), "b" -> JsNumber(4.2))
    "convert to a respective JsObject" in {
      obj.toJson mustEqual json
    }
    "convert a JsObject to the respective case class instance" in {
      json.convertTo[Test2] mustEqual obj
    }
    "throw a DeserializationException if the JsObject does not all required members" in (
      JsObject("b" -> JsNumber(4.2)).convertTo[Test2] must
              throwA(new DeserializationException("Object is missing required member 'a'"))
    )
    "not require the presence of optional fields for deserialization" in {
      JsObject("a" -> JsNumber(42)).convertTo[Test2] mustEqual Test2(42, None)
    }
    "not render `None` members during serialization" in {
      Test2(42, None).toJson mustEqual JsObject("a" -> JsNumber(42))
    }
    "ignore additional members during deserialization" in {
      JsObject("a" -> JsNumber(42), "b" -> JsNumber(4.2), "c" -> JsString('no)).convertTo[Test2] mustEqual obj
    }
    "not depend on any specific member order for deserialization" in {
      JsObject("b" -> JsNumber(4.2), "a" -> JsNumber(42)).convertTo[Test2] mustEqual obj
    }
    "throw a DeserializationException if the JsValue is not a JsObject" in (
      JsNull.convertTo[Test2] must throwA(new DeserializationException("Object expected in field 'a'"))
    )
  }

  "A JsonProtocol mixing in NullOptions" should {
    "render `None` members to `null`" in {
      import TestProtocol2._
      Test2(42, None).toJson mustEqual JsObject("a" -> JsNumber(42), "b" -> JsNull)
    }
  }

  "A JsonProtocol mixing in NullOptions with unordered keys" should {
    "render `None` members to `null`" in {
      import TestProtocol2._
      val jsonObject : JsValue = Test3Map(42, None, 43).toJson

      jsonObject.asJsObject().getFields("a").size mustEqual 1
      jsonObject.asJsObject().getFields("a")(0) mustEqual JsNumber(42)

      jsonObject.asJsObject().getFields("b").size mustEqual 1
      jsonObject.asJsObject().getFields("b")(0) mustEqual JsNull

      jsonObject.asJsObject().getFields("c").size mustEqual 1
      jsonObject.asJsObject().getFields("c")(0) mustEqual JsNumber(43)
    }
  }

  "A JsonProtocol mixing in Map with unordered keys" should {
    "render `None` members to `null`" in {
      import TestProtocol2._
      val jsonObject : JsValue = Test3Map(42, Some(Map("red" -> "#FF0000", "azure" -> "#F0FFFF")), 43).toJson

      jsonObject.asJsObject().getFields("a").size mustEqual 1
      jsonObject.asJsObject().getFields("a")(0) mustEqual JsNumber(42)

      jsonObject.asJsObject().getFields("b").size mustEqual 1
      jsonObject.asJsObject().getFields("b")(0) must beAnInstanceOf[JsObject]

      jsonObject.asJsObject().getFields("c").size mustEqual 1
      jsonObject.asJsObject().getFields("c")(0) mustEqual JsNumber(43)

    }
  }

  "A JsonProtocol mixing in Map with unordered keys returns json string" should {
    "render `None` members to `null`" in {
      import TestProtocol2._
      val jsonObject : JsValue = Test3Map(42, Some(Map("red" -> "#FF0000",
        "azure" -> "#F0FFFF",
        "blue" -> "#0000FF",
        "black" -> "#000000",
        "white" -> "#FFFFFF"
      )), 43).toJson

      val jsonObjectString = jsonObject.compactPrint
      jsonObjectString must contain("\"a\":42")
      jsonObjectString must contain("\"c\":43")
      jsonObjectString must contain("\"red\":\"#FF0000\"")
      jsonObjectString must contain("\"azure\":\"#F0FFFF\"")
      jsonObjectString must contain("\"black\":\"#000000\"")
      jsonObjectString must contain("\"blue\":\"#0000FF\"")
      jsonObjectString must contain("\"white\":\"#FFFFFF\"")


    }
  }

  "A JsonProtocol mixing in Map with unordered keys, and 10 fields, returns json string" should {
    "render `None` members to `null`" in {
      import TestProtocol2._
      val jsonObject : JsValue = Test10Map(42, Some(Map("red" -> "#FF0000",
        "azure" -> "#F0FFFF",
        "blue" -> "#0000FF",
        "black" -> "#000000",
        "white" -> "#FFFFFF"
      )), 43,44,45,46,47,48,49,50).toJson

      val jsonObjectString = jsonObject.compactPrint
      jsonObjectString must contain("\"a\":42")
      jsonObjectString must contain("\"c\":43")
      jsonObjectString must contain("\"d\":44")
      jsonObjectString must contain("\"e\":45")
      jsonObjectString must contain("\"f\":46")
      jsonObjectString must contain("\"g\":47")
      jsonObjectString must contain("\"h\":48")
      jsonObjectString must contain("\"i\":49")
      jsonObjectString must contain("\"j\":50")
      jsonObjectString must contain("\"red\":\"#FF0000\"")
      jsonObjectString must contain("\"azure\":\"#F0FFFF\"")
      jsonObjectString must contain("\"black\":\"#000000\"")
      jsonObjectString must contain("\"blue\":\"#0000FF\"")
      jsonObjectString must contain("\"white\":\"#FFFFFF\"")


    }
  }


  "A JsonFormat for a generic case class and created with `jsonFormat`" should {
    import TestProtocol1._
    val obj = Test3(42 :: 43 :: Nil, "x" :: "y" :: "z" :: Nil)
    val json = JsObject(
      "as" -> JsArray(JsNumber(42), JsNumber(43)),
      "bs" -> JsArray(JsString("x"), JsString("y"), JsString("z"))
    )
    "convert to a respective JsObject" in {
      obj.toJson mustEqual json
    }
    "convert a JsObject to the respective case class instance" in {
      json.convertTo[Test3[Int, String]] mustEqual obj
    }
  }
  "A JsonFormat for a case class with 18 parameters and created with `jsonFormat`" should {
    object Test18Protocol extends DefaultJsonProtocol {
      implicit val test18Format = jsonFormat18(Test18)
    }
    case class Test18(
      a1: String,
      a2: String,
      a3: String,
      a4: String,
      a5: Int,
      a6: String,
      a7: String,
      a8: String,
      a9: String,
      a10: String,
      a11: String,
      a12: Double,
      a13: String,
      a14: String,
      a15: String,
      a16: String,
      a17: String,
      a18: String)

    import Test18Protocol._
    val obj = Test18("a1", "a2", "a3", "a4", 5, "a6", "a7", "a8", "a9",
                     "a10", "a11", 12d, "a13", "a14", "a15", "a16", "a17", "a18")

    val json = JsonParser("""{"a1":"a1","a2":"a2","a3":"a3","a4":"a4","a5":5,"a6":"a6","a7":"a7","a8":"a8","a9":"a9","a10":"a10","a11":"a11","a12":12.0,"a13":"a13","a14":"a14","a15":"a15","a16":"a16","a17":"a17","a18":"a18"}""")
    "convert to a respective JsObject" in {
      obj.toJson mustEqual json
    }
    "convert a JsObject to the respective case class instance" in {
      json.convertTo[Test18] mustEqual obj
    }
  }

  "A JsonFormat for a generic case class with an explicitly provided type parameter" should {
    "support the jsonFormat1 syntax" in {
      case class Box[A](a: A)
      object BoxProtocol extends DefaultJsonProtocol {
        implicit val boxFormat = jsonFormat1(Box[Int])
      }
      import BoxProtocol._
      Box(42).toJson === JsObject(Map("a" -> JsNumber(42)))
    }
  }

  "A JsonFormat for a case class with transient fields and created with `jsonFormat`" should {
    import TestProtocol1._
    val obj = TestTransient(42, Some(4.2))
    val json = JsObject("a" -> JsNumber(42), "b" -> JsNumber(4.2))
    "convert to a respective JsObject" in {
      obj.toJson mustEqual json
    }
    "convert a JsObject to the respective case class instance" in {
      json.convertTo[TestTransient] mustEqual obj
    }
  }

  "A JsonFormat for a case class with static fields and created with `jsonFormat`" should {
    import TestProtocol1._
    val obj = TestStatic(42, Some(4.2))
    val json = JsObject("a" -> JsNumber(42), "b" -> JsNumber(4.2))
    "convert to a respective JsObject" in {
      obj.toJson mustEqual json
    }
    "convert a JsObject to the respective case class instance" in {
      json.convertTo[TestStatic] mustEqual obj
    }
  }

}
