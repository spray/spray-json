package spray.json

import java.util.NoSuchElementException

import org.apache.commons.lang3.RandomStringUtils._
import org.specs2.mutable._

import scala.util.Random._

/**
  * Created by bathalh on 2/22/16.
  */
class TriptionSpec extends Specification
{
    def nextString = randomAlphanumeric( nextInt( 16 ) + 1 )

    "Basic monadic and helper function should work work:" should
    {
        "Undefined.isDefined is false; Null and Value are true" in {
            Undefined.isDefined mustEqual false
            Null.isDefined mustEqual true
            Value(nextString).isDefined mustEqual true
        }
        "Null.isNull is true; Null and Value are false" in {
            Undefined.isNull mustEqual false
            Null.isNull mustEqual true
            Value(nextString).isNull mustEqual false
        }
        "Value.hasValue is true; Null and Undefined are false" in {
            Undefined.hasValue mustEqual false
            Null.hasValue mustEqual false
            Value(nextString).hasValue mustEqual true
        }
        "Value.get retrieves the value" in {
            val x = nextString
            Value(x).get mustEqual x
        }
        "Null.get throws NoSuchElementException" in {
            try {
                Null.get
                "" mustEqual "Expected NoSuchElementException"
            } catch {
                case nsee: NoSuchElementException => nsee.getMessage mustEqual "Null.get"
            }
        }
        "Undefined.get throws NoSuchElementException" in {
            try {
                Undefined.get
                "" mustEqual "Expected NoSuchElementException"
            } catch {
                case nsee: NoSuchElementException => nsee.getMessage mustEqual "Undefined.get"
            }
        }
        "getOrElse gets the value if it exists; otherwise executes the else" in {
            val value = nextString
            val alt = nextString
            Undefined.getOrElse( alt ) mustEqual alt
            Null.getOrElse( alt ) mustEqual alt
            Value(value).getOrElse( alt ) mustEqual value
        }
        "map translates a value to another value or returns original Tription if value is not there" in {
            val value = nextString
            val append = nextString
            def mapFunction( s: String ) = s + append

            Undefined.map( mapFunction ) mustEqual Undefined
            Null.map( mapFunction ) mustEqual Null
            Value(value).map( mapFunction ) mustEqual Value(value + append)
        }
        "flatMap translates a value to another value or returns original Tription if value is not there" in {
            val value = nextString
            val append = nextString
            def mapFunction( s: String ) = Value(s + append)

            Undefined.flatMap( mapFunction ) mustEqual Undefined
            Null.flatMap( mapFunction ) mustEqual Null
            Value(value).flatMap( mapFunction ) mustEqual Value(value + append)
        }
        "foreach executes for value in a Value and does nothing otherwise" in {
            val sb = new StringBuilder
            def foreachFunction( x: Any ) = sb.append( x.toString )

            Undefined foreach foreachFunction
            sb.toString mustEqual ""
            Undefined foreach foreachFunction
            sb.toString mustEqual ""
            val v = nextString
            Value(v) foreach foreachFunction
            sb.toString mustEqual v
        }
    }
}
