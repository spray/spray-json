package spray.json

trait KeyableFormats {

  implicit object StringKeyableFormat extends KeyableFormat[String] {
    override def write(obj: String) = obj
    override def read(json: JsString) = json.value
  }
}
