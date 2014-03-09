/*
 * Copyright (C) 2011 Ruud Diterwich, BusyMachines
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

import java.lang.reflect.{ParameterizedType, Modifier}
import scala.reflect.ClassTag
import scala.reflect.classTag
import scala.collection.mutable
import scala.language.existentials

/**
 * A field format provides a more generic way to translate a field value to json and visa versa.
 * It allows for field renaming, multi-field mappings etc.
 */
trait ProductFieldFormat[F] {
  def write(field: ProductField, value: F, rest: List[JsField]) : List[JsField]
  def read(field: ProductField, obj: JsObject) : F
  def withJsonName(jsonName: String) = this
  def withJsonFormat(format: JsonFormat[F]) = this
  def withDefault(default: Option[() => Any]) = this
}

/**
 * Implicitly converts a JsonFormat to a ProductFieldFormat. This implicit
 * makes the new product formats drop-in compatible with the original ones.
 */
object ProductFieldFormat {
  implicit def of[F](implicit jsonFormat: JsonFormat[F]) =
    DefaultProductFieldFormat[F](None, None, jsonFormat)
}

/**
 * Do not serialize the field to and from json. Should only be used when the
 * field has a default value. Instead of using this class directly, one can also
 * use ProductFormat#excludeField.
 */
class NullProductFieldFormat[F] extends ProductFieldFormat[F] {
  def write(field: ProductField, value: F, rest: List[JsField])  = rest
  def read(field: ProductField, obj: JsObject) : F =
    field.default.getOrElse(throw new IllegalStateException(s"Field ${field.name} should have a default value")).apply().asInstanceOf[F]
}

object NullProductFieldFormat extends NullProductFieldFormat[Any]

/**
 * A normal field format that will serialize a product field one to one to a json field.
 */
case class DefaultProductFieldFormat[F](jsonName: Option[String], default: Option[() => Any], jsonFormat: JsonFormat[F]) extends ProductFieldFormat[F] {
  def write(field: ProductField, value: F, rest: List[JsField]) =
    if (field.isOption && value == None) rest
    else if (field.isSeq && field.default.isDefined && value == Seq.empty) rest
    else if (field.isMap && field.default.isDefined && value == Map.empty) rest
    else jsonName.getOrElse(field.name) -> jsonFormat.write(value) :: rest

  def read(field: ProductField, obj: JsObject) =
    obj.fields.get(jsonName.getOrElse(field.name)) match {
      case Some(value) => jsonFormat.read(value)
      case None => default.orElse(field.default) match {
        case Some(defarg) => defarg().asInstanceOf[F]
        case None =>
          if (field.isOption) None.asInstanceOf[F]
          else deserializationError("Object is missing required member '" + field.name + "'")
      }
    }
  override def withJsonName(jsonName: String) = this.copy(jsonName = Some(jsonName))
  override def withDefault(default: Option[() => Any]) = this.copy(default = default)
  override def withJsonFormat(format: JsonFormat[F]) = this.copy(jsonFormat = format)
}

class EmbeddedProductFieldFormat[F](originalFormat: ProductFieldFormat[F]) extends ProductFieldFormat[F] {
  def write(field: ProductField, value: F, rest: List[JsField]) =
    originalFormat.write(field, value, Nil).map(_._2).collect{case JsObject(fields) => fields}.flatten ++ rest
  def read(field: ProductField, obj: JsObject) : F =
    originalFormat match {
      case DefaultProductFieldFormat(jsonName, default, jsonFormat) =>
        jsonFormat.read(obj)
      case _ => throw new Exception(s"Can't embed a non-default product field ${field.name}")
    }
}

/**
 * Represents a case-class field.
 */
case class ProductField(
   name: String,
   default: Option[() => Any] = None,
   isOption: Boolean = false,
   isSeq: Boolean = false,
   isMap: Boolean = false,
   fieldType: Class[_],
   genericParameterTypes: Array[Class[_]],
   format: ProductFieldFormat[_])

/**
 * Base class for product formats, mainly used as json formats for case-classes.
 */
abstract class ProductFormat[P :ClassTag] extends RootJsonFormat[P] { outer =>

  /**
   * Discovered fields of the product class.
   */
  val fields: Array[ProductField]
  protected val delegate: ProductFormat[P]

  def write(p: P) = delegate.write(fields, p)
  def read(value: JsValue) = delegate.read(fields, value)
  protected def write(fields: Seq[ProductField], p: P) : JsValue
  protected def read(fields: Seq[ProductField], value: JsValue) : P

  /**
   * Returns a new format that overrides the json name for given fields (identified by name).
   */
  def withJsonNames(jsonNames: (String, String)*) = decorate(
    fields.map(f => jsonNames.find(_._1 == f.name).map(s => f.copy(format = f.format.withJsonName(s._2))).getOrElse(f)))

  /**
   * Returns a new format that overrides the json formats for given fields (identified by name).
   */
  def withJsonFormats(jsonFormats: (String, JsonFormat[_])*) = decorate(
    fields.map(f => jsonFormats.find(_._1 == f.name).map(s => f.copy(format = f.format.asInstanceOf[ProductFieldFormat[Any]].withJsonFormat(s._2.asInstanceOf[JsonFormat[Any]]))).getOrElse(f)))

  /**
   * Returns a new format that overrides the default value for given fields (identified by name).
   */
  def withDefaults(defaults: (String, () => Any)*) = decorate(
    fields.map(f => f.copy(default = defaults.find(_._1 == f.name).map(_._2).orElse(f.default))))

  /**
   * Returns a new format that overrides the field formats for given fields (identified by name).
   */
  def withFieldFormats(formats: (String, ProductFieldFormat[_])*) = decorate(
    fields.map(f => f.copy(format = formats.find(_._1 == f.name).map(_._2).getOrElse(f.format))))

  /**
   * Returns a new format with transformed product fields.
   */
  def mapFields(cp: ProductField => ProductField) = decorate(
    fields.map(cp))

  /**
   * Returns a new format that excludes given fields when serializing to json.
   */
  def excludeFields(fields: String*) =
    withFieldFormats(fields.map(_ -> NullProductFieldFormat):_*)

  /**
   * Returns a new format that embeds nested fields in the root json object.
   */
  def embed(fields: String*) = mapFields {
    case field if !fields.contains(field.name) => field
    case field => field.copy(format = new EmbeddedProductFieldFormat[Any](field.format.asInstanceOf[ProductFieldFormat[Any]]))
  }

  private def decorate(_fields: Array[ProductField]) = new ProductFormat[P] {
    val fields = _fields
    val delegate = outer.delegate
    def write(fields: Seq[ProductField], p: P) : JsValue = throw new IllegalStateException
    def read(fields: Seq[ProductField], value: JsValue) : P = throw new IllegalStateException
  }
}

abstract private[spray] class ProductFormatImpl[P <: Product :ClassTag, F0 :ProductFieldFormat, F1 :ProductFieldFormat, F2 :ProductFieldFormat,F3 :ProductFieldFormat,F4 :ProductFieldFormat,F5 :ProductFieldFormat,F6 :ProductFieldFormat,F7 :ProductFieldFormat,F8 :ProductFieldFormat,F9 :ProductFieldFormat,F10 :ProductFieldFormat, F11 :ProductFieldFormat, F12 :ProductFieldFormat, F13 :ProductFieldFormat, F14 :ProductFieldFormat, F15 :ProductFieldFormat, F16 :ProductFieldFormat, F17 :ProductFieldFormat, F18 :ProductFieldFormat, F19 :ProductFieldFormat, F20 :ProductFieldFormat, F21 :ProductFieldFormat] extends ProductFormat[P] {

  protected val delegate = this

  protected def write[F :ProductFieldFormat](field: ProductField, p: P, fieldIndex: Int, rest: List[JsField]): List[JsField] =
    field.format.asInstanceOf[ProductFieldFormat[Any]].write(field, p.productElement(fieldIndex), rest)

  protected def read[F :ProductFieldFormat](field: ProductField, value: JsValue) : F = {
    value match {
      case obj: JsObject => field.format.read(field, obj).asInstanceOf[F]
      case _ => deserializationError("Object expected")
    }
  }

  protected def jsObject(fields: Iterable[JsField]): JsObject = {
    JsObject(fields.toMap match {
      case map if map.size == fields.size => map
      case map =>
        val builder = mutable.Map[String, JsValue]()
        for ((name, value) <- fields) {
          (builder.get(name), value) match {
            case (Some(JsObject(oldFields)), JsObject(fields)) =>
              builder += (name -> jsObject(oldFields ++ fields))
            case _ => builder += (name -> value)
          }
        }
        builder.toMap
    })
  }

  private def fmt[F](implicit f: ProductFieldFormat[F]) = f.asInstanceOf[ProductFieldFormat[Any]]

  val fields = {
    val formats = Array(fmt[F0], fmt[F1],
      fmt[F2], fmt[F3], fmt[F4], fmt[F5], fmt[F6],
      fmt[F7], fmt[F8], fmt[F9], fmt[F10], fmt[F11],
      fmt[F12], fmt[F13], fmt[F14], fmt[F15], fmt[F16],
      fmt[F17], fmt[F18], fmt[F19], fmt[F20], fmt[F21])

    val runtimeClass = classTag[P].runtimeClass
    try {
      // Need companion class for default arguments.
      lazy val companionClass = Class.forName(runtimeClass.getName + "$")
      lazy val moduleField =
        try { companionClass.getField("MODULE$") }
        catch { case e : Throwable => throw new RuntimeException("Can't deserialize default arguments of nested case classes", e) }
      lazy val companionObj = moduleField.get(null)
      // copy methods have the form copy$default$N(), we need to sort them in order, but must account for the fact
      // that lexical sorting of ...8(), ...9(), ...10() is not correct, so we extract N and sort by N.toInt
      val copyDefaultMethods = runtimeClass.getMethods.filter(_.getName.startsWith("copy$default$")).sortBy(
        _.getName.drop("copy$default$".length).takeWhile(_ != '(').toInt)
      val fields = runtimeClass.getDeclaredFields.filterNot(f => f.getName.startsWith("$") || Modifier.isTransient(f.getModifiers) || Modifier.isStatic(f.getModifiers))
      if (copyDefaultMethods.length != fields.length)
        sys.error("Case class " + runtimeClass.getName + " declares additional fields")
      val applyDefaultMethods = copyDefaultMethods.map { method =>
        try {
          val defmeth = companionClass.getMethod("apply" + method.getName.drop("copy".size))
          Some(() => defmeth.invoke(companionObj))}
        catch { case e : Throwable => None }
      }
      if (fields.zip(copyDefaultMethods).exists { case (f, m) => f.getType != m.getReturnType })
        sys.error("Cannot determine field order of case class " + runtimeClass.getName)
      fields.zip(applyDefaultMethods).zipWithIndex.map { case ((f, m), index) =>
        val typeArgs: Array[Class[_]] = f.getGenericType match {
          case pType: ParameterizedType => pType.getActualTypeArguments.collect {
            case argClass: Class[_] => argClass
          }
          case _ => Array.empty
        }
        ProductField(f.getName, default = m, classOf[Option[_]].isAssignableFrom(f.getType), classOf[Seq[_]].isAssignableFrom(f.getType), classOf[Map[_, _]].isAssignableFrom(f.getType), f.getType, typeArgs, format = formats(index))
      }
    } catch {
      case ex : Throwable => throw new RuntimeException("Cannot automatically determine case class field names and order " +
        "for '" + runtimeClass.getName + "', please use the 'jsonFormat' overload with explicit field name specification", ex)
    }
  }
}
