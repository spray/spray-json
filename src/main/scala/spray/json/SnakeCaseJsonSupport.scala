package spray.json

import scala.reflect.ClassTag
import scala.collection.mutable

/**
  * Provides snake cased JsonFormats
  */
trait SnakeCaseJsonSupport extends DefaultJsonProtocol {

  override protected def extractFieldNames(classTag: ClassTag[_]): Array[String] = {
    def snakify(name: String) = {
      val chars = name.toCharArray
      val len = name.length
      val sb = new mutable.StringBuilder(len + len / 5)

      def isAlphabetic(char: Char): Boolean = (char >= 'a' && char <= 'z') || (char >= 'A' && char <= 'Z')

      def go(i: Int, rest: Int, processedUpper: Boolean, processedAlphaNumeric: Boolean): String =
        if (rest == 0) {
          sb.toString()
        } else if (rest > 1 && chars(i).isUpper && chars(i + 1).isLower) {
          if (processedAlphaNumeric) {
            sb.append('_')
          }
          sb.append(chars(i).toLower).append(chars(i + 1))
          go(i + 2, rest - 2, false, true)
        } else if (chars(i).isUpper) {
          if (!processedUpper && processedAlphaNumeric) {
            sb.append('_')
          }
          sb.append(chars(i).toLower)
          go(i + 1, rest - 1, true, chars(i).isDigit || isAlphabetic(chars(i)))
        } else if (!isAlphabetic(chars(i))) {
          sb.append(chars(i))
          go(i + 1, rest - 1, false, chars(i).isDigit)
        } else {
          sb.append(chars(i).toLower)
          go(i + 1, rest - 1, chars(i).isUpper, chars(i).isDigit || isAlphabetic(chars(i)))
        }

      go(0, len, true, false)
    }

    super.extractFieldNames(classTag).map {
      snakify(_)
    }
  }

}

object SnakeCaseJsonSupport extends SnakeCaseJsonSupport
