package spray.json

trait JsonParserSettings {
  /**
   * The JsonParser uses recursive decent parsing that keeps intermediate values on the stack. To prevent
   * StackOverflowExceptions a limit is enforced on the depth of the parsed JSON structure.
   *
   * As a guideline we tested that one level of depth needs about 300 bytes of stack space.
   *
   * The default is a depth of 1000.
   */
  def maxDepth: Int

  /**
   * Return a copy of this settings object with the `maxDepth` setting changed to the new value.
   */
  def withMaxDepth(newValue: Int): JsonParserSettings
}
object JsonParserSettings {
  val default: JsonParserSettings = SettingsImpl()

  private case class SettingsImpl(
    maxDepth: Int = 1000
  ) extends JsonParserSettings {
    override def withMaxDepth(newValue: Int): JsonParserSettings = copy(maxDepth = newValue)
  }
}