package spray.json
package lenses

/**
 * Defines a set of operations to update Json values.
 */
trait Operations { _: ExtraImplicits =>
  /**
   * The set operation sets or creates a value.
   */
  def set[T: JsonWriter](t: => T): Operation = new Operation {
    def apply(value: SafeJsValue): SafeJsValue =
    // ignore existence of old value
      Right(t.toJson)
  }

  /**
   * A MapOperation is one that expect an old value to be available.
   */
  trait MapOperation extends Operation {
    def apply(value: JsValue): SafeJsValue

    def apply(value: SafeJsValue): SafeJsValue = value.flatMap(apply)
  }

  /**
   * The `modify` operation applies a function on the (converted) value
   */
  def modify[T: Reader : JsonWriter](f: T => T): Operation = new MapOperation {
    def apply(value: JsValue): SafeJsValue =
      value.as[T] map (v => f(v).toJson)
  }

  /**
   * The `modifyOrDeleteField` operation works together with the `optionalField` lens.
   * The passed function is called for every existing field. If the function returns
   * `Some(value)`, this will become the new value. If the function returns `None` the
   * field will be deleted.
   */
  def modifyOrDeleteField[T: Reader : JsonWriter](f: T => Option[T]): Operation = new MapOperation {
    def apply(value: JsValue): SafeJsValue =
      value.as[T] flatMap (v => f(v).map(x => Right(x.toJson)).getOrElse(OptionLenses.FieldMissing))
  }

  def append(update: Update): Operation = ???
  def update(update: Update): Operation = ???
  def extract[M[_], T](value: Lens[M])(f: M[T] => Update): Operation = ???
}

object Operations extends Operations with ExtraImplicits
