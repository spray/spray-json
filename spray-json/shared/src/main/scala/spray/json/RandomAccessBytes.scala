package spray.json

import spray.json.ParserInput.IndexedBytesParserInput

/**
 * Interface for random accessible data to be used as input for JSON parsing.
 *
 * Implement to allow parsing from other data structures than the predefined ones.
 */
trait RandomAccessBytes {
  def byteAt(offset: Long): Byte
  def length: Long
}
object RandomAccessBytes {
  private[json] implicit def toParserInput(bytes: RandomAccessBytes): ParserInput = new IndexedBytesParserInput {
    override protected def byteAt(offset: Int): Byte = bytes.byteAt(offset)
    override def length: Int = bytes.length.toInt

    // inefficent default implementations
    override def sliceString(start: Int, end: Int): String =
      new String((start until end).map(i => bytes.byteAt(i)).toArray, "utf8")
    override def sliceCharArray(start: Int, end: Int): Array[Char] =
      sliceString(start, end).toCharArray
  }
}
