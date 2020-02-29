package spray.json

import org.specs2.execute.Success
import org.specs2.mutable.Specification

trait RoundTripSpecBase extends Specification {
  def roundTrip[T](json: String, expected: T)(implicit format: JsonFormat[T]): Success = {
    val converted: T = json.parseJson.convertTo[T]
    converted mustEqual expected
    converted.toJson.compactPrint mustEqual json
    success
  }
}
