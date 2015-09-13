package spray.json

trait SortedPrinter extends PrettyPrinter {

  override protected def organiseMembers(members: Map[String, JsValue]): Seq[(String, JsValue)] =
    members.toSeq.sortBy(_._1)
}

object SortedPrinter extends SortedPrinter
