package spray.json
package lenses

trait OptionLenses {
  /**
   * Operates on the first element of an JsArray which matches the predicate.
   */
  def find(pred: JsPred): OptLens = new LensImpl[Option] {
    def updated(f: SafeJsValue => SafeJsValue)(parent: JsValue): SafeJsValue = parent match {
      case JsArray(elements) =>
        elements.span(x => !pred(x)) match {
          case (prefix, element :: suffix) =>
            f(Right(element)) map (v => JsArray(prefix ::: v :: suffix))

          // element not found, do nothing
          case _ =>
            Right(parent)
        }
      case e@_ => unexpected("Not a json array: " + e)
    }

    def retr: JsValue => Validated[Option[JsValue]] = {
      case JsArray(elements) => Right(elements.find(pred))
      case e@_ => unexpected("Not a json array: " + e)
    }
  }

  val FieldMissing = unexpected("Field missing")

  /**
   * Accesses a maybe missing field of a JsObject.
   */
  def optionalField(name: String): OptLens = new LensImpl[Option] {
    def updated(f: SafeJsValue => SafeJsValue)(parent: JsValue): SafeJsValue =
      retr(parent).flatMap { oldValueO =>
        f(oldValueO.map(Right(_)).getOrElse(FieldMissing)) match {
          case FieldMissing => Right(parent)
          case x => x.map(newVal => JsObject(fields = parent.asJsObject.fields + (name -> newVal)))
        }
      }

    def retr: JsValue => Validated[Option[JsValue]] = {
      case o: JsObject => Right(o.fields.get(name))
      case e@_ => unexpected("Not a json object: " + e)
    }
  }
}

object OptionLenses extends OptionLenses
