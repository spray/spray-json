package spray.json

/**
  * A Triple-Option for JSON values: `Undefined`, `Null`, or `Value(x)` See readme for more details
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

