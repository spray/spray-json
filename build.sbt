name := "spray-json"

version := "1.3.2"

organization := "io.spray"

organizationHomepage := Some(new URL("http://spray.io"))

description := "A Scala library for easy and idiomatic JSON (de)serialization"

homepage := Some(new URL("https://github.com/spray/spray-json"))

startYear := Some(2011)

licenses := Seq("Apache 2" -> new URL("http://www.apache.org/licenses/LICENSE-2.0.txt"))

scalaVersion := "2.11.7"

scalacOptions ++= Seq("-feature", "-language:_", "-unchecked", "-deprecation", "-encoding", "utf8")

resolvers += Opts.resolver.sonatypeReleases

libraryDependencies ++= Seq(
  "org.specs2" %% "specs2-core" % "3.7.1" % "test",
  "org.specs2" %% "specs2-scalacheck" % "3.7.1" % "test",
  "org.scalacheck" %% "scalacheck" % "1.12.5" % "test"
)

(scalacOptions in doc) ++= Seq("-doc-title", name.value + " " + version.value)

// generate boilerplate
Boilerplate.settings

// OSGi settings
osgiSettings

OsgiKeys.exportPackage := Seq("""spray.json.*;version="${Bundle-Version}"""")

OsgiKeys.importPackage <<= scalaVersion { sv => Seq("""scala.*;version="$<range;[==,=+);%s>"""".format(sv)) }

OsgiKeys.importPackage ++= Seq("""spray.json;version="${Bundle-Version}"""", "*")

OsgiKeys.additionalHeaders := Map("-removeheaders" -> "Include-Resource,Private-Package")

///////////////
// publishing
///////////////

crossScalaVersions := Seq("2.10.5", "2.11.7", "2.12.0-M3")

scalaBinaryVersion <<= scalaVersion(sV => if (CrossVersion.isStable(sV)) CrossVersion.binaryScalaVersion(sV) else sV)

publishMavenStyle := true

useGpg := true

publishTo <<= version { v: String =>
  val nexus = "https://oss.sonatype.org/"
  if (v.trim.endsWith("SNAPSHOT")) Some("snapshots" at nexus + "content/repositories/snapshots")
  else                             Some("releases" at nexus + "service/local/staging/deploy/maven2")
}

pomIncludeRepository := { _ => false }

pomExtra :=
  <scm>
    <url>git://github.com/spray/spray.git</url>
    <connection>scm:git:git@github.com:spray/spray.git</connection>
  </scm>
  <developers>
    <developer><id>sirthias</id><name>Mathias Doenitz</name></developer>
    <developer><id>jrudolph</id><name>Johannes Rudolph</name></developer>
  </developers>