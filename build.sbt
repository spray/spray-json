name := "spray-json"

version := "1.3.2"

organization := "io.spray"

organizationHomepage := Some(new URL("http://spray.io"))

description := "A Scala library for easy and idiomatic JSON (de)serialization"

homepage := Some(new URL("https://github.com/spray/spray-json"))

startYear := Some(2011)

licenses := Seq("Apache 2" -> new URL("http://www.apache.org/licenses/LICENSE-2.0.txt"))

scalaVersion := "2.11.6"

scalacOptions ++= Seq("-feature", "-language:_", "-unchecked", "-deprecation", "-encoding", "utf8")

resolvers += Opts.resolver.sonatypeReleases

libraryDependencies ++= Seq(
  "org.specs2" %% "specs2-core" % "2.4.16" % "test",
  "org.specs2" %% "specs2-scalacheck" % "2.4.16" % "test",
  "org.scalacheck" %% "scalacheck" % "1.12.2" % "test"
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

crossScalaVersions := Seq("2.10.5", "2.11.6")

scalaBinaryVersion <<= scalaVersion(sV => if (CrossVersion.isStable(sV)) CrossVersion.binaryScalaVersion(sV) else sV)

publishMavenStyle := true

publishTo := Some {
    "spray repo" at {
      // public uri is repo.spray.io, we use an SSH tunnel to the nexus here
      "http://localhost:42424/content/repositories/" + {
        if (version.value.trim.endsWith("SNAPSHOT")) "snapshots/" else "releases/"
      }
    }
  }