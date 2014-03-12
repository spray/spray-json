name := "spray-json"

version := "1.2.5"

organization := "io.spray"

organizationHomepage := Some(new URL("http://spray.io"))

description := "A Scala library for easy and idiomatic JSON (de)serialization"

homepage := Some(new URL("https://github.com/spray/spray-json"))

startYear := Some(2011)

licenses := Seq("Apache 2" -> new URL("http://www.apache.org/licenses/LICENSE-2.0.txt"))

scalaVersion := "2.10.3"

scalacOptions <<= scalaVersion map {
  case "2.9.3"  => Seq("-unchecked", "-deprecation", "-encoding", "utf8")
  case x if x startsWith "2.10" => Seq("-feature", "-language:implicitConversions", "-unchecked", "-deprecation", "-encoding", "utf8")
}

resolvers += Opts.resolver.sonatypeReleases

libraryDependencies <++= scalaVersion { sv =>
  Seq("org.parboiled" %% "parboiled-scala" % "1.1.5" % "compile") ++
  (sv match {
    case "2.9.3"  =>
      Seq(
        "org.specs2" %% "specs2" % "1.12.4.1" % "test",
        "org.scalacheck" %% "scalacheck" % "1.10.0" % "test"
      )
    case x if x startsWith "2.10" =>
      Seq(
        "org.specs2" %% "specs2" % "2.3.10" % "test",
        "org.scalacheck" %% "scalacheck" % "1.11.3" % "test"
      )
  })
}

scaladocOptions <<= (name, version).map { (n, v) => Seq("-doc-title", n + " " + v) }

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

crossScalaVersions := Seq("2.9.3", "2.10.3")

scalaBinaryVersion <<= scalaVersion(sV => if (CrossVersion.isStable(sV)) CrossVersion.binaryScalaVersion(sV) else sV)

publishMavenStyle := true

publishTo <<= version { version =>
  Some {
    "spray repo" at {
      // public uri is repo.spray.io, we use an SSH tunnel to the nexus here
      "http://localhost:42424/content/repositories/" + {
        if (version.trim.endsWith("SNAPSHOT")) "snapshots/" else"releases/"
      }
    }
  }
}


///////////////
// ls-sbt
///////////////

seq(lsSettings:_*)

(LsKeys.tags in LsKeys.lsync) := Seq("json")

(LsKeys.docsUrl in LsKeys.lsync) := Some(new URL("http://spray.github.com/spray/api/spray-json/"))

(externalResolvers in LsKeys.lsync) := Seq("spray repo" at "http://repo.spray.io")
