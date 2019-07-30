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

  /**
   * The initial number of characters the parser should use while collecting strings. If you know the maximum length
   * of strings in input documents, you can directly set it higher than that. Otherwise, the buffer will be increased
   * up to the maximum size as given by `maxStringCharacters`.
   *
   * The default is 1024 characters.
   */
  def initialMaxStringCharacters: Int

  /**
   * Returns a copy of this settings object with the `initialMaxStringCharacters` setting changed to the new value.
   */
  def withInitialMaxStringCharacters(newValue: Int): JsonParserSettings

  /**
   * The maximum number of characters the parser should support for a string.
   *
   * The default is 1MiB characters.
   */
  def maxStringCharacters: Int

  /**
   * Returns a copy of this settings object with the `maxStringCharacters` setting changed to the new value.
   */
  def withMaxStringCharacters(newValue: Int): JsonParserSettings
}
object JsonParserSettings {
  val default: JsonParserSettings = SettingsImpl()

  private case class SettingsImpl(
    maxDepth:                   Int = 1000,
    maxNumberCharacters:        Int = 100,
    initialMaxStringCharacters: Int = 1024,
    maxStringCharacters:        Int = 1024 * 1024
  ) extends JsonParserSettings {
    override def withMaxDepth(newValue: Int): JsonParserSettings = copy(maxDepth = newValue)
    override def withMaxNumberCharacters(newValue: Int): JsonParserSettings = copy(maxNumberCharacters = newValue)
    override def withInitialMaxStringCharacters(newValue: Int): JsonParserSettings = copy(initialMaxStringCharacters = newValue)
    override def withMaxStringCharacters(newValue: Int): JsonParserSettings = copy(maxStringCharacters = newValue)
  }
}
