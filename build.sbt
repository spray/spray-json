scalaVersion := "2.10.1"

resolvers += "spray" at "http://repo.spray.io"

libraryDependencies <++= scalaVersion { sv =>
  val specsVersion = if (sv.startsWith("2.9")) "1.12.1" else "1.13"
  Seq(
    "io.spray" %% "spray-json" % "1.2.3",
    "org.parboiled" %% "parboiled-scala" % "1.1.4" % "compile",
    "org.specs2" %% "specs2" % specsVersion % "test")
}

initialCommands in console += """
    import spray.json._
    import DefaultJsonProtocol._
    import lenses._
    import JsonLenses._
"""
