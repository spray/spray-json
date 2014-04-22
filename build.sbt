scalaVersion := "2.11.0"

resolvers += "spray" at "http://repo.spray.io"

libraryDependencies <++= scalaVersion { sv =>
  val specsVersion = sv match {
    case v if v.startsWith("2.9") => "1.12.4.1"
    case _ => "2.3.11"
  }
  Seq(
    "io.spray" %% "spray-json" % "1.2.6",
    "org.parboiled" %% "parboiled-scala" % "1.1.6" % "compile",
    "org.specs2" %% "specs2" % specsVersion % "test")
}

initialCommands in console += """
    import spray.json._
    import DefaultJsonProtocol._
    import lenses._
    import JsonLenses._
"""

crossScalaVersions := Seq("2.9.3", "2.10.4", "2.11.0")