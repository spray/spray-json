package spray.json

trait KeyableWriter[T] {
  def write(obj: T): String
}

trait KeyableReader[T] {
  def read(jsString: JsString): T
}

trait KeyableFormat[T] extends KeyableWriter[T] with KeyableReader[T]
