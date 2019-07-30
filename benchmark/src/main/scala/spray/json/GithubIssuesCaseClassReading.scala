package spray.json

import com.fasterxml.jackson.databind.DeserializationFeature
import org.openjdk.jmh.annotations.Benchmark
import org.openjdk.jmh.annotations.Setup
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.module.SimpleModule
import com.fasterxml.jackson.module.scala.experimental.ScalaObjectMapper
import com.fasterxml.jackson.module.scala.DefaultScalaModule

import scala.io.Source

object GithubIssuesPartialModel {
  import DefaultJsonProtocol._
  case class LabelsArrayElement(
    id:  Long,
    url: String,
    //node_id: String,
    name:    String,
    color:   String,
    default: Boolean)

  implicit val LabelsArrayElementFormat = jsonFormat5(LabelsArrayElement.apply _)

  case class User(
    login:   String,
    id:      Long,
    node_id: String /*,
                   //gists_url: String,
                   //organizations_url: String,
                   gravatar_id: String,
                   //url: String,

                   //repos_url: String,
                   //received_events_url: String,

                   //following_url: String,
                   site_admin: Boolean,
                   //subscriptions_url: String,
                   //starred_url: String,
                   //html_url: String,

                   `type`: String,
                   //events_url: String,
                   //avatar_url: String,
                   followers_url: String*/ )

  implicit val UserFormat = jsonFormat3(User.apply _)

  case class Pull_requestOptionElement(
    diff_url:  String,
    html_url:  String,
    patch_url: String,
    url:       String)

  implicit val Pull_requestOptionElementFormat = jsonFormat4(Pull_requestOptionElement.apply _)

  case class RootArrayElement(
    url:            String,
    repository_url: String,
    //labels_url: String,
    comments_url: String,
    //events_url: String,
    html_url: String,
    id:       Long,
    //node_id: String,
    number: Long,
    title:  String,
    user:   User,
    //labels: Seq[LabelsArrayElement],
    state: String,

    locked: Boolean,
    //assignee: Option[User],
    assignees:  Seq[User],
    created_at: String,

    //body: Option[String],
    //milestone: Option[String],
    //closed_at: Option[String],

    updated_at:         String,
    author_association: String

  //,
  //comments: BigDecimal//,
  //pull_request: Option[Pull_requestOptionElement]
  )

  implicit val RootArrayElementFormat = jsonFormat14(RootArrayElement.apply _)
}

class GithubIssuesCaseClassReading extends Common {
  var jsonString: String = _
  var jsonBytes: Array[Byte] = _

  implicit val playJsonRootFormat = {
    import GithubIssuesPartialModel._
    import play.api.libs.json._
    implicit val playJsonUserFormat: Format[User] = play.api.libs.json.Json.format
    implicit val playJsonLabelFormat: Format[LabelsArrayElement] = play.api.libs.json.Json.format
    play.api.libs.json.Json.format: Format[RootArrayElement]
  }

  implicit def circeRootFormat = {
    import GithubIssuesPartialModel._
    import io.circe._
    import io.circe.generic.semiauto._
    import io.circe.parser._
    implicit def circeUserFormat: Decoder[User] = deriveDecoder
    implicit def circeLabelFormat: Decoder[LabelsArrayElement] = deriveDecoder

    deriveDecoder: Decoder[RootArrayElement]
  }

  implicit def upickleDefaultFormat = {
    import GithubIssuesPartialModel._
    import upickle.default.{ ReadWriter => RW, Reader => R, Writer => W }
    implicit val userFormat: R[User] = upickle.default.macroR[User]
    implicit val labelFormat: R[LabelsArrayElement] = upickle.default.macroR[LabelsArrayElement]
    upickle.default.macroR[RootArrayElement]
  }

  val jacksonMapper: ObjectMapper with ScalaObjectMapper = new ObjectMapper with ScalaObjectMapper {
    registerModule(DefaultScalaModule)
    configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
  }

  @Setup
  def setup(): Unit = {
    jsonString = Source.fromResource("github-akka-issues.json").mkString
    jsonBytes = jsonString.getBytes("utf8") // yeah, a useless utf8 roundtrip
  }

  @Benchmark
  def readSprayJsonViaASTPartial(): AnyRef = {
    import DefaultJsonProtocol._
    JsonParser(jsonBytes).convertTo[Seq[GithubIssuesPartialModel.RootArrayElement]]
  }

  @Benchmark
  def readPlayJsonPartial(): AnyRef = {
    import play.api.libs.json.Json
    Json.fromJson[Seq[GithubIssuesPartialModel.RootArrayElement]](Json.parse(jsonBytes)).get
  }

  @Benchmark
  def readJacksonPartial(): AnyRef = {
    jacksonMapper.readValue[Array[GithubIssuesPartialModel.RootArrayElement]](jsonBytes)
    //Json.fromJson[Seq[GithubIssuesPartialModel.RootArrayElement]](Json.parse(jsonBytes)).get
  }

  /*
  Fails with "upickle.core.Abort: expected sequence got int32"
  @Benchmark
  def readUPickleDefaultBinaryPartial(): Unit =
    upickle.default.readBinary[Seq[GithubIssuesPartialModel.RootArrayElement]](jsonBytes)

  */

  @Benchmark
  def readCircePartial(): AnyRef = {
    import io.circe._
    import io.circe.generic.semiauto._
    import io.circe.parser._
    decode[Seq[GithubIssuesPartialModel.RootArrayElement]](jsonString).right.get
  }
}
