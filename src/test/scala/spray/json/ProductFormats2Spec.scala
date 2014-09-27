package spray.json

import org.specs2.mutable._

object ProductFormatTestsFixture {

  case class Box(size: Double, things: List[Thing])
  case class Thing(name: String, properties: Map[String, String] = Map.empty)
}

class ProductFormats2Spec extends Specification {
  import ProductFormatTestsFixture._
  import DefaultJsonProtocol._

  {
    implicit val thingFormat = format2(Thing)
    implicit val boxFormat = format2(Box)

    "A case class" should {
      "be correctly serialized to json" in {
        val thing = Thing("egg", Map("color" -> "red"))
        val json = """{"name" : "egg", "properties" : {"color" : "red"}}"""
        thing.toJson mustEqual json.parseJson
        json.parseJson.convertTo[Thing] mustEqual thing
      }
    }

    "Default values" should {
      "be correctly deserialized from json" in {
        val json = """{"name" : "egg"}"""
        val thing = Thing("egg")
        json.parseJson.convertTo[Thing] mustEqual thing
        json.parseJson.convertTo[Thing].properties mustEqual Map.empty
      }
    }

    "Empty collections with a default value" should {
      "not be serialized to json" in {
        val thing = Thing("egg")
        val json = """{"name" : "egg"}"""
        thing.toJson mustEqual json.parseJson
      }
    }

    "Empty collections without a default value" should {
      "be serialized to json" in {
        val thing = Box(10, Nil)
        val json = """{"size" : 10, "things" : [] }"""
        thing.toJson mustEqual json.parseJson
      }
    }
  }

  {
    implicit val thingFormat = format2(Thing).withJsonNames("name" -> "description")
    implicit val boxFormat = format2(Box)

    "A renamed field" should {
      "be correctly serialized to json" in {
        val thing = Thing("egg", Map("color" -> "red"))
        val json = """{"description" : "egg", "properties" : {"color" : "red"}}"""
        thing.toJson mustEqual json.parseJson
        json.parseJson.convertTo[Thing] mustEqual thing
      }
      "be correctly serialized in a nested object" in {
        val box = Box(10, Thing("egg", Map("color" -> "red")) :: Nil)
        val json = """{"size":10, "things":[{"description" : "egg", "properties" : {"color" : "red"}}]}"""
        box.toJson mustEqual json.parseJson
        json.parseJson.convertTo[Box] mustEqual box
      }
    }
  }

  {
    implicit val thingFormat = format2(Thing)
      .excludeFields("properties")

    "An excluded field" should {
      "not be serialized to json" in {
        val thing = Thing("egg", Map("color" -> "red"))
        val json = """{"name":"egg"}"""
        thing.toJson mustEqual json.parseJson
        json.parseJson.convertTo[Thing] mustEqual Thing("egg")
      }
      "not be deserialized from json" in {
        val json = """{"name":"egg", "properties" : {"color" : "red"}}"""
        val thing = Thing("egg")
        json.parseJson.convertTo[Thing] mustEqual thing
      }
    }
  }

  {
    implicit val thingFormat = format2(Thing)
      .excludeFields("name")

    "An excluded field without a default" should {
      "not be deserialized from json" in {
        val thing = Thing("egg", Map("color" -> "red"))
        val json = """{"properties" : {"color" : "red"}}"""
        assert(thing.toJson === json.parseJson)
        json.parseJson.convertTo[Thing] must throwA[IllegalStateException]
      }
    }
  }

  {
    implicit val thingFormat = format2(Thing)
      .excludeFields("name")
      .withDefaults("name" -> (() => "ball"))

    "An excluded field with an explicit default" should {
      "not be serialized to json" in {
        val thing = Thing("ball", Map("color" -> "red"))
        val json = """{"properties" : {"color" : "red"}}"""
        thing.toJson mustEqual json.parseJson
        json.parseJson.convertTo[Thing] mustEqual thing
      }
    }
  }
}
