package spray.json

import java.util.Locale

import org.specs2.mutable._

class SnakeCaseJsonSupportSpec extends Specification with SnakeCaseJsonSupport {

  private val PASS1 = """([A-Z]+)([A-Z][a-z])""".r
  private val PASS2 = """([a-z\d])([A-Z])""".r
  private val REPLACEMENT = "$1_$2"

  def snakify(name: String) =
    PASS2.replaceAllIn(PASS1.replaceAllIn(name, REPLACEMENT), REPLACEMENT).toLowerCase(Locale.US)

  "SnakeCaseJsonSUpport" should {
    "convert field names to snake cased fields names." in {
      // Given
      case class SampleFields(
          HelloWorld: Int = 0,
          A1AAbBBBBBbCCcc: Int = 0,
          AbAAbbAAAbbb1AA2bb1AA2AAAAbb1bAA: Int = 0,
          x: Int = 0,
          Y: Int = 0,
          `^There-arE_Non_(Alphabetic))_character!S`: Int = 0,
          `tHere-Are_(AlsO)_noN_(alphaBetic))_CharaCter!s`: Int = 0,
          `  There are space character `: Int = 0,
          ` これは　マルチバイトAbcのaBc確認です。`: Int = 0,
          aa: Int = 0,
          AA: Int = 0,
          Aa: Int = 0,
          aA: Int = 0,
          AAA: Int = 0,
          AAa: Int = 0,
          AaA: Int = 0,
          aAA: Int = 0,
          aaA: Int = 0,
          aAa: Int = 0,
          Aaa: Int = 0,
          aaa: Int = 0
      )
      case class SampleFields2(
          aaa123bbb: Int = 0,
          aaA123bbb: Int = 0,
          aaa123Bbb: Int = 0,
          aaA123Bbb: Int = 0,
          aaa123: Int = 0,
          aaA123: Int = 0,
          aAA123: Int = 0
      )
      implicit val jsonFormat1: RootJsonFormat[SampleFields] =
         jsonFormat21(SampleFields.apply)
      implicit val jsonFormat2: RootJsonFormat[SampleFields2] =
        jsonFormat7(SampleFields2.apply)
      // When
      val sampleFields1 = SampleFields().toJson.asJsObject
      val sampleFields2 = SampleFields2().toJson.asJsObject
      // Then
      val fields1 = sampleFields1.fields
      fields1.contains(snakify("HelloWorld")) mustEqual true
      fields1.contains(snakify("A1AAbBBBBBbCCcc")) mustEqual true
      fields1.contains(snakify("AbAAbbAAAbbb1AA2bb1AA2AAAAbb1bAA")) mustEqual true
      fields1.contains(snakify("x")) mustEqual true
      fields1.contains(snakify("Y")) mustEqual true
      fields1.contains(snakify("^There-arE_Non_(Alphabetic))_character!S")) mustEqual true
      fields1.contains(snakify("tHere-Are_(AlsO)_noN_(alphaBetic))_CharaCter!s")) mustEqual true
      fields1.contains(snakify("  There are space character ")) mustEqual true
      fields1.contains(snakify(" これは　マルチバイトAbcのaBc確認です。")) mustEqual true
      fields1.contains(snakify("aa")) mustEqual true
      fields1.contains(snakify("aA")) mustEqual true
      fields1.contains(snakify("Aa")) mustEqual true
      fields1.contains(snakify("AA")) mustEqual true
      fields1.contains(snakify("AAA")) mustEqual true
      fields1.contains(snakify("aaA")) mustEqual true
      fields1.contains(snakify("aAa")) mustEqual true
      fields1.contains(snakify("Aaa")) mustEqual true
      fields1.contains(snakify("AAa")) mustEqual true
      fields1.contains(snakify("AaA")) mustEqual true
      fields1.contains(snakify("aAA")) mustEqual true
      fields1.contains(snakify("aaa")) mustEqual true
      val fields2 = sampleFields2.fields
      fields2.contains(snakify("aaa123bbb")) mustEqual true
      fields2.contains(snakify("aaa123Bbb")) mustEqual true
      fields2.contains(snakify("aaA123bbb")) mustEqual true
      fields2.contains(snakify("aaa123")) mustEqual true
      fields2.contains(snakify("aaA123")) mustEqual true
      fields2.contains(snakify("aAA123")) mustEqual true
    }
    "deserialize snake cased json" in {
      // Given
      case class SampleFields(
          a: Int,
          aA: Int,
          helloWorld: Int,
          HelloTheWorld: Int
      )
      implicit val jsonFormat: RootJsonFormat[SampleFields] =
        jsonFormat4(SampleFields.apply)
      val json = JsObject(
        "hello_the_world" -> 4.toJson,
        "a_a" -> 2.toJson,
        "a" -> 1.toJson,
        "hello_world" -> 3.toJson
      )
      // When
      val result = jsonFormat.read(json)
      // Then
      result mustEqual SampleFields(
        a = 1, aA = 2, helloWorld = 3, HelloTheWorld = 4
      )
    }
  }
}
