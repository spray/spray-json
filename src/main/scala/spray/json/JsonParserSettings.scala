package spray.json

trait JsonParserSettings {

}
object JsonParserSettings {
  val default: JsonParserSettings = SettingsImpl()

  private case class SettingsImpl() extends JsonParserSettings
}