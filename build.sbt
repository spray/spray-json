import com.typesafe.tools.mima.core.{ProblemFilters, DirectMissingMethodProblem, ReversedMissingMethodProblem, IncompatibleSignatureProblem}

name := "spray-json"

version := "1.3.7-SNAPSHOT"

organization := "io.spray"

organizationHomepage := Some(new URL("http://spray.io"))

description := "A Scala library for easy and idiomatic JSON (de)serialization"

homepage := Some(new URL("https://github.com/spray/spray-json"))

startYear := Some(2011)

licenses := Seq("Apache 2" -> new URL("http://www.apache.org/licenses/LICENSE-2.0.txt"))

scalacOptions ++= Seq("-feature", "-language:_", "-unchecked", "-deprecation", "-Xlint", "-encoding", "utf8")

resolvers += Opts.resolver.sonatypeReleases

libraryDependencies ++= (CrossVersion.partialVersion(scalaVersion.value) match {
  case Some((3, n)) => Seq(
    ("org.specs2" %% "specs2-core" % "4.5.1" % "test").cross(CrossVersion.for3Use2_13),
    ("org.specs2" %% "specs2-scalacheck" % "4.5.1" % "test").cross(CrossVersion.for3Use2_13),
    ("org.scalacheck" %% "scalacheck" % "1.15.4" % "test").cross(CrossVersion.for3Use2_13)
  )
  case Some((2, 10)) => Seq(
    "org.specs2" %% "specs2-core" % "3.8.9" % "test",
    "org.specs2" %% "specs2-scalacheck" % "3.8.9" % "test",
    "org.scalacheck" %% "scalacheck" % "1.13.4" % "test"
  )
  case Some((2, n)) if n >= 11 => Seq(
    "org.specs2" %% "specs2-core" % "4.5.1" % "test",
    "org.specs2" %% "specs2-scalacheck" % "4.5.1" % "test",
    "org.scalacheck" %% "scalacheck" % "1.14.0" % "test"
  )
  case _ => Nil
})

(scalacOptions in doc) ++= Seq("-doc-title", name.value + " " + version.value)

// generate boilerplate
enablePlugins(BoilerplatePlugin)

// OSGi settings
enablePlugins(SbtOsgi)

OsgiKeys.exportPackage := Seq("""spray.json.*;version="${Bundle-Version}"""")

OsgiKeys.importPackage := Seq("""scala.*;version="$<range;[==,=+);%s>"""".format(scalaVersion.value))

OsgiKeys.importPackage ++= Seq("""spray.json;version="${Bundle-Version}"""", "*")

OsgiKeys.additionalHeaders := Map("-removeheaders" -> "Include-Resource,Private-Package")

// Migration Manager
ThisBuild / mimaReportSignatureProblems := true
mimaPreviousArtifacts := (CrossVersion.partialVersion(scalaVersion.value) match {
  case Some((3, _)) => Set.empty
  case Some((2, 13)) => Set("io.spray" %% "spray-json" % "1.3.5")
  case Some((2, 10)) => Set.empty
  case _ =>
    Set("1.3.2", "1.3.3", "1.3.4", "1.3.5").map { v =>
      "io.spray" %% "spray-json" % v
    }
})

mimaBinaryIssueFilters := Seq(
  ProblemFilters.exclude[ReversedMissingMethodProblem]("spray.json.PrettyPrinter.organiseMembers"),
  // Some signatures have become more specific, which is harmless/good:
  ProblemFilters.exclude[IncompatibleSignatureProblem]("spray.json.DefaultJsonProtocol.*Format"),
  // Scala 2.12 used to create a strange static forwarder that is now gone
  ProblemFilters.exclude[DirectMissingMethodProblem]("spray.json.DeserializationException.apply")
)

///////////////
// publishing
///////////////

crossScalaVersions := Seq("2.12.12", "2.10.7", "2.11.12", "2.13.3", "3.1.0")

publishMavenStyle := true

useGpg := true

publishTo := {
  val nexus = "https://oss.sonatype.org/"
  if (version.value.trim.endsWith("SNAPSHOT"))
    Some("snapshots" at nexus + "content/repositories/snapshots")
  else
    Some("releases" at nexus + "service/local/staging/deploy/maven2")
}

pomIncludeRepository := { _ => false }

pomExtra :=
  <scm>
    <url>git://github.com/spray/spray-json.git</url>
    <connection>scm:git:git@github.com:spray/spray-json.git</connection>
  </scm>
  <developers>
    <developer><id>sirthias</id><name>Mathias Doenitz</name></developer>
    <developer><id>jrudolph</id><name>Johannes Rudolph</name></developer>
  </developers>

