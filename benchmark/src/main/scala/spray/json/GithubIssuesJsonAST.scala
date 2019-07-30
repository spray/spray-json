package spray.json

import org.openjdk.jmh.annotations.Benchmark
import org.openjdk.jmh.annotations.Param
import org.openjdk.jmh.annotations.Setup

import scala.io.Source
import play.api.libs.json.Json

class GithubIssuesJsonAST extends Common {
  @Param(Array("github-akka-issues.json", "big-string-array.json"))
  var res: String = null

  var jsonString: String = _
  var jsonBytes: Array[Byte] = _
  var sprayJsonAST: JsValue = _
  var playJsonAST: play.api.libs.json.JsValue = _
  var upickleAST: ujson.Js.Value = _
  var circeAST: io.circe.Json = _

  @Setup
  def setup(): Unit = {
    jsonString = Source.fromResource(res).mkString
    jsonBytes = jsonString.getBytes("utf8") // yeah, a useless utf8 roundtrip
    sprayJsonAST = JsonParser(jsonString)
    playJsonAST = Json.parse(jsonString)
    upickleAST = ujson.read(jsonString)
    circeAST = io.circe.parser.parse(jsonString).right.get
  }

  @Benchmark
  def readSprayJsonFromString(): Unit = JsonParser(jsonString)

  @Benchmark
  def readSprayJsonFromBytes(): Unit = JsonParser(jsonBytes)

  @Benchmark
  def readPlayJson(): Unit = Json.parse(jsonString)

  @Benchmark
  def readUPickle(): Unit = ujson.read(jsonString)

  @Benchmark
  def readCirce(): Unit = io.circe.parser.parse(jsonString)

  @Benchmark
  def writeSprayJson(): Unit = sprayJsonAST.compactPrint

  @Benchmark
  def writePlayJson(): Unit = playJsonAST.toString()

  @Benchmark
  def writeUPickle(): Unit = ujson.write(upickleAST)

  @Benchmark
  def writeCirce(): Unit = circeAST.toString()
}
