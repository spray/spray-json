package spray.json

/**
 * Allows to customize settings for the JSON parser.
 *
 * Use it like this:
 *
 * ```
 * val customSettings =
 *   JsonParserSettings.default
 *     .withMaxDepth(100)
 *     .withMaxNumberCharacters(20)
 *
 * JsonParser(jsonString, customSettings)
 * // or
 * jsonString.parseJson(customSettings)
 * ```
 */
sealed trait JsonParserSettings {
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
   * Returns a copy of this settings object with the `maxDepth` setting changed to the new value.
   */
  def withMaxDepth(newValue: Int): JsonParserSettings

  /**
   * The maximum number of characters the parser should support for numbers. This is restricted because creating
   * `BigDecimal`s with high precision can be very slow (approx. quadratic runtime per amount of characters).
   */
  def maxNumberCharacters: Int

  /**
   * Returns a copy of this settings object with the `maxNumberCharacters` setting changed to the new value.
   */
  def withMaxNumberCharacters(newValue: Int): JsonParserSettings
}
object JsonParserSettings {
  val default: JsonParserSettings = SettingsImpl()

  private case class SettingsImpl(
    maxDepth: Int = 1000,
    maxNumberCharacters: Int = 100
  ) extends JsonParserSettings {
    override def withMaxDepth(newValue: Int): JsonParserSettings = copy(maxDepth = newValue)
    override def withMaxNumberCharacters(newValue: Int): JsonParserSettings = copy(maxNumberCharacters = newValue)
  }
}