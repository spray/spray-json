package spray.json

import org.specs2.mutable._

class SnakeCaseJsonSupportSpec extends Specification with SnakeCaseJsonSupport {

  "SnakeCaseJsonSupport" should {
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
          ab: Int = 0,
          AC: Int = 0,
          Ad: Int = 0,
          bA: Int = 0,
          AAA: Int = 0,
          AAb: Int = 0,
          AbA: Int = 0,
          bAA: Int = 0,
          bbA: Int = 0,
          bAb: Int = 0,
          Acc: Int = 0,
          ccc: Int = 0
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
      fields1 must haveKey ("hello_world")
      fields1 must haveKey ("a1_a_ab_bbbb_bb_c_ccc")
      fields1 must haveKey ("ab_a_abb_aa_abbb1_aa2bb1_aa2_aaa_abb1b_aa")
      fields1 must haveKey ("x")
      fields1 must haveKey ("y")
      fields1 must haveKey ("^there-ar_e_non_(alphabetic))_character!s")
      fields1 must haveKey ("t_here-are_(als_o)_no_n_(alpha_betic))_chara_cter!s")
      fields1 must haveKey ("  there are space character ")
      fields1 must haveKey (" これは　マルチバイトabcのa_bc確認です。")
      fields1 must haveKey ("ab")
      fields1 must haveKey ("ac")
      fields1 must haveKey ("ad")
      fields1 must haveKey ("b_a")
      fields1 must haveKey ("aaa")
      fields1 must haveKey ("a_ab")
      fields1 must haveKey ("ab_a")
      fields1 must haveKey ("b_aa")
      fields1 must haveKey ("bb_a")
      fields1 must haveKey ("b_ab")
      fields1 must haveKey ("acc")
      fields1 must haveKey ("ccc")
      val fields2 = sampleFields2.fields
      fields2 must haveKey("aaa123bbb")
      fields2 must haveKey("aa_a123bbb")
      fields2 must haveKey("aaa123_bbb")
      fields2 must haveKey("aa_a123_bbb")
      fields2 must haveKey("aaa123")
      fields2 must haveKey("aa_a123")
      fields2 must haveKey("a_aa123")
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
