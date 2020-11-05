/*
 * Copyright (C) 2011 Mathias Doenitz
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package spray.json

import scala.reflect.ClassTag

/**
 * User: cvrabie
 * Date: 05/08/2013
 * Wrapper for [[spray.json.JsObject]] class that offers specialised getters for json fields. <br />
 * All getters return Either[String, T] which allows writing a [[spray.json.JsonReader]] with a for comprehension by
 * using [[scala.util.Right]] projections. At the same time if the conversion fails the error message
 * will be stored in [[scala.util.Left]]. <br />
 * This is especially important in case of JSON objects with optional fields. See RichJsObject for examples.
 */
class RichJsObject(val obj:JsObject){
  val fields = obj.fields

  def field(fieldName: String):Either[String, JsValue] =
    fields.get(fieldName).toRight("Field '%s' is missing".format(fieldName))

  def field[T](fieldName: String, f:JsValue=>T)(implicit ct:ClassTag[T]):Either[String, T] =
    field(fieldName).right.flatMap(value => try{ Right(f(value)) }catch {
      case e:DeserializationException => Left("Error while converting field '%s' to %ct: \nCause: %s - %s".format(
        fieldName, ct.getClass.getName, e.getClass.getName, e.getMessage))
    })

  def field[T](fieldName: String, f:JsValue=>T, default:T)(implicit ct:ClassTag[T]):Right[String,T] =
    Right(field(fieldName, f).right.getOrElse(default))

  def t[T](fieldName: String)(implicit ct:ClassTag[T], reader:JsonReader[T]):Either[String, T] =
    field(fieldName, reader.read(_))

  def t[T](fieldName: String, default: T)(implicit ct:ClassTag[T], reader:JsonReader[T]):Right[String, T] =
    field(fieldName, reader.read(_), default)

  def string(fieldName: String):Either[String, String] = field(fieldName).right.flatMap{
    case JsString(str) => Right(str)
    case x => Left("Expecting JsString in field '%s' but got %s".format(fieldName, x))
  }

  def string(fieldName: String, default: String):Right[String,String] =
    Right(string(fieldName).right.getOrElse(default))

  def int(fieldName: String):Either[String,Int] = field(fieldName).right.flatMap{
    case JsNumber(num) => try { Right(num.toIntExact) }catch {
      case e:ArithmeticException => Left("The value in field '%s' is too big to fit an Int".format(fieldName))
    }
    case x => Left("Expecting JsNumber in field '%s' but got %s".format(fieldName, x))
  }

  def int(fieldName: String, default: Int):Right[String,Int] =
    Right(int(fieldName).right.getOrElse(default))

  def long(fieldName: String):Either[String,Long] = field(fieldName).right.flatMap{
    case JsNumber(num) => try { Right(num.toLongExact) } catch {
      case e:ArithmeticException => Left("The value in field '%s' is too big to fit a Long".format(fieldName))
    }
    case x => Left("Expecting JsNumber in field '%s' but got %s".format(fieldName, x))
  }

  def long(fieldName: String, default: Long):Right[String,Long] =
    Right(long(fieldName).right.getOrElse(default))

  def float(fieldName: String):Either[String,Float] = field(fieldName).right.flatMap{
    case JsNumber(num) => Right(num.toFloat)
    case x => Left("Expecting JsNumber in field '%s' but got %s".format(fieldName, x))
  }

  def float(fieldName: String, default: Float):Right[String,Float] =
    Right(float(fieldName).right.getOrElse(default))

  def double(fieldName: String):Either[String,Double] = field(fieldName).right.flatMap{
    case JsNumber(num) => Right(num.toDouble)
    case x => Left("Expecting JsNumber in field '%s' but got %s".format(fieldName, x))
  }

  def double(fieldName: String, default: Double):Right[String,Double] =
    Right(double(fieldName).right.getOrElse(default))

  def number(fieldName: String):Either[String,BigDecimal] = field(fieldName).right.flatMap{
    case JsNumber(num) => Right(num)
    case x => Left("Expecting JsNumber in field '%s' but got %s".format(fieldName, x))
  }

  def number(fieldName: String, default: BigDecimal):Right[String,BigDecimal] =
    Right(number(fieldName).right.getOrElse(default))

  def boolean(fieldName: String):Either[String,Boolean] = field(fieldName).right.flatMap({
    case JsBoolean(bool) => Right(bool)
    case x => Left("Expecting JsBoolean in field '%s' but got %s".format(fieldName, x))
  })

  def boolean(fieldName: String, default: Boolean):Right[String,Boolean] =
    Right(boolean(fieldName).right.getOrElse(default))
}

object RichJsObject{
  def apply(obj:JsObject):RichJsObject = new RichJsObject(obj)
  def unapply(jval:JsValue):Option[RichJsObject] = jval match {
    case obj:JsObject => Some(obj)
    case _ => None
  }
  implicit def jsValueAsRich(obj:JsValue):RichJsObject = new RichJsObject(
    obj.asJsObject("Expecting JsObject but got %s: %s".format(obj.getClass.getSimpleName,obj))
  )
  implicit def richAsJsValue(obj:RichJsObject):JsObject = obj.obj
  implicit def jsObjectAsRich(obj:JsObject):RichJsObject = new RichJsObject(obj)
  implicit def richAsJsObject(obj:RichJsObject):JsObject = obj.obj
}