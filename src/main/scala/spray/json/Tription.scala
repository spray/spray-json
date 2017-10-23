package spray.json

/**
  * A Triple-Option
  *
  * JavaScript (and JSON), unlike Java/Scala, allow undefined values, which are distinct from null values.
  * For example, a PUT request may have a payload like this:
  * <code>
  *     { "id":"234565434567898789098765",
  *       "field1": 7,
  *       "field3: null }
  * </code>
  * which would tell the server to update field1 to 7, set field3 to null, and leave field2 alone.
  * With a standard scala `Option`, it is impossible to tell whether the payload of the request had field2 and field3
  * null or undefined since any missing values translate to `None`.
  *
  * The Tription solves that problem by defining `Value` for present values, `Null` for null values, and
  * `Undefined` for values which are missing or explicitly marked as undefined.
  *
  * Created by bathalh on 2/19/16.
  */
sealed abstract class Tription[+T] extends Product
{
    def isDefined: Boolean
    def isNull: Boolean
    def hasValue = isDefined && !isNull
    def get: T

    final def getOrElse[N >: T](default: => N): N =
        if( !hasValue ) default else this.get

    final def map[N]( f: T => N ): Tription[N] =
        if( !isDefined ) Undefined
        else if( isNull ) Null
        else Value( f( get ) )

    final def flatMap[N](f: T => Tription[N]): Tription[N] =
        if( !isDefined ) Undefined
        else if( isNull ) Null
        else f( get )

    /** return `Null` (not `Undefined`) if filter criteria don't match **/
    final def filter(p: T => Boolean): Tription[T] =
        if (!hasValue || p(this.get)) this else Null

    final def foreach[U](f: T => U): Unit =
        if( hasValue ) f( this.get )

    // not sure whether to return Null or Undefined if the filter criteria are not met
//    final def filter(p: T => Boolean): Tription[T] =
//        if (!hasValue || p(this.get)) this else (Undefined/Null)
}

case class Value[+T](x: T) extends Tription[T] {
    override def isDefined: Boolean = true
    override def isNull: Boolean = false
    override def get: T = x
}

case object Null extends Tription[Nothing] {
    override def isDefined: Boolean = true
    override def isNull: Boolean = true
    override def get = throw new NoSuchElementException("Null.get")
}

case object Undefined extends Tription[Nothing] {
    override def isDefined: Boolean = false
    override def isNull: Boolean = false
    override def get = throw new NoSuchElementException("Undefined.get")
}

