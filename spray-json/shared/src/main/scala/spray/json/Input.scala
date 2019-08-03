package spray.json

/**
 * Type-class for any kind of data that can be used as input to JSON parsing.
 *
 * Custom inputs can be supported by implementing the RandomAccessBytes trait.
 *
 * Input itself is not supposed to be implemented by third parties.
 */
sealed trait Input[T] {
  private[json] def parserInput(t: T): ParserInput
}
object Input {
  private[json] def byParserInput[T](f: T => ParserInput): Input[T] = new Input[T] { def parserInput(t: T): ParserInput = f(t) }

  implicit def forString: Input[String] = byParserInput(string => ParserInput(string))
  implicit def forChars: Input[Array[Char]] = byParserInput(chars => ParserInput(chars))
  implicit def forBytes: Input[Array[Byte]] = byParserInput(bytes => ParserInput(bytes))
  implicit def forRandomAccessBytes(bytes: RandomAccessBytes): Input[RandomAccessBytes] =
    byParserInput(bytes => bytes: ParserInput)
}
