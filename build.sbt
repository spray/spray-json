name := "spray-json"

version := "1.0.0"

organization := "cc.spray.json"

scalaVersion := "2.9.0-1"

// -------------------------------------------------------------------------
// Dependencies
// -------------------------------------------------------------------------
libraryDependencies ++= Seq(
	"org.parboiled" % "parboiled-core" % "1.0.1" % "compile" withSources(),
	"org.parboiled" % "parboiled-scala" % "1.0.1" % "compile" withSources()
)

libraryDependencies += "org.specs2" %% "specs2" % "1.5" % "test" withSources()

// -------------------------------------------------------------------------
// Options
// -------------------------------------------------------------------------
scalacOptions ++= Seq(
	"-deprecation",
	"-encoding",
	"utf8"
)

// -------------------------------------------------------------------------
// Publishing
// -------------------------------------------------------------------------
publishTo := Some("Scala Tools Releases" at "http://nexus.scala-tools.org/content/repositories/releases/")

credentials += Credentials(Path.userHome / ".ivy2" / ".credentials")

pomExtra := 
 <name>spray JSON</name>
    <url>http://spray.cc/</url>
    <inceptionYear>2011</inceptionYear>
    <description>A Scala library for easy and idiomatic JSON (de)serialization</description>
    <licenses>
      <license>
        <name>Apache 2</name>
        <url>http://www.apache.org/licenses/LICENSE-2.0.txt</url>
        <distribution>repo</distribution>
      </license>
    </licenses>
    <developers>
      <developer>
        <id>sirthias</id>
        <name>Mathias Doenitz</name>
        <timezone>+1</timezone>
        <email>mathias [at] spray.cc</email>
      </developer>
    </developers>
    <scm>
      <url>http://github.com/spray/spray-json/</url>
    </scm>
 


