// shadow sbt-scalajs' crossProject and CrossType until Scala.js 1.0.0 is released
import sbtcrossproject.{crossProject, CrossType}
import com.typesafe.tools.mima.core.{ProblemFilters, ReversedMissingMethodProblem}

lazy val sprayJson =
  crossProject(JVMPlatform, JSPlatform, NativePlatform)
    .crossType(CrossType.Full)
    .in(file("."))
    .settings(
      name := "spray-json",
      version := "1.3.4",
      organization := "io.spray",
      organizationHomepage := Some(new URL("http://spray.io")),
      description := "A Scala library for easy and idiomatic JSON (de)serialization",
      homepage := Some(new URL("https://github.com/spray/spray-json")),
      startYear := Some(2011),
      licenses := Seq("Apache 2" -> new URL("http://www.apache.org/licenses/LICENSE-2.0.txt")),
      crossScalaVersions := Seq("2.10.7", "2.11.12", "2.12.4", "2.13.0-M3"),
      scalaVersion := "2.11.12",
      scalacOptions ++= Seq("-feature", "-language:_", "-unchecked", "-deprecation", "-Xlint", "-encoding", "utf8"),
      (scalacOptions in doc) ++= Seq("-doc-title", name.value + " " + version.value),
      libraryDependencies ++=
        (CrossVersion.partialVersion(scalaVersion.value) match {
          case Some((2, n)) if n >= 13 =>
            Seq("org.scala-lang.modules" %%% "scala-parallel-collections" % "0.1.2")
          case _ =>
            Nil
        }),
      scalaBinaryVersion := {
        val sV = scalaVersion.value
        if (CrossVersion.isScalaApiCompatible(sV))
          CrossVersion.binaryScalaVersion(sV)
        else
          sV
      },
      publishMavenStyle := true,
      useGpg := true,
      publishTo := {
        val nexus = "https://oss.sonatype.org/"
        if (version.value.trim.endsWith("SNAPSHOT"))
          Some("snapshots" at nexus + "content/repositories/snapshots")
        else
          Some("releases" at nexus + "service/local/staging/deploy/maven2")
      },
      pomIncludeRepository := { _ => false },
      pomExtra :=
        <scm>
          <url>git://github.com/spray/spray.git</url>
          <connection>scm:git:git@github.com:spray/spray.git</connection>
        </scm>
          <developers>
            <developer><id>sirthias</id><name>Mathias Doenitz</name></developer>
            <developer><id>jrudolph</id><name>Johannes Rudolph</name></developer>
          </developers>
    )
    .configurePlatforms(JVMPlatform)( _
      .enablePlugins(spray.boilerplate.BoilerplatePlugin)
      .enablePlugins(SbtOsgi)
    )
    .jvmSettings(
      libraryDependencies ++= (CrossVersion.partialVersion(scalaVersion.value) match {
        case Some((2, 10)) => Seq(
          "org.specs2" %% "specs2-core" % "3.8.9" % "test",
          "org.specs2" %% "specs2-scalacheck" % "3.8.9" % "test",
          "org.scalacheck" %% "scalacheck" % "1.13.4" % "test"
        )
        case Some((2, n)) if n >= 11 => Seq(
          "org.specs2" %% "specs2-core" % "4.0.2" % "test",
          "org.specs2" %% "specs2-scalacheck" % "4.0.2" % "test",
          "org.scalacheck" %% "scalacheck" % "1.13.5" % "test"
        )
        case _ => Nil
      }),
      OsgiKeys.exportPackage := Seq("""spray.json.*;version="${Bundle-Version}""""),
      OsgiKeys.importPackage := Seq("""scala.*;version="$<range;[==,=+);%s>"""".format(scalaVersion.value)),
      OsgiKeys.importPackage ++= Seq("""spray.json;version="${Bundle-Version}"""", "*"),
      OsgiKeys.additionalHeaders := Map("-removeheaders" -> "Include-Resource,Private-Package"),
      mimaPreviousArtifacts := (CrossVersion.partialVersion(scalaVersion.value) match {
        case Some((2, 13)) => Set.empty
        case _ => Set("io.spray" %% "spray-json" % "1.3.3")
      }),
      mimaBinaryIssueFilters := Seq(
        ProblemFilters.exclude[ReversedMissingMethodProblem]("spray.json.PrettyPrinter.organiseMembers")
      )
    )
    .jsSettings()
    .nativeSettings() // defined in sbt-scala-native

lazy val sprayJsonJVM = sprayJson.jvm
lazy val sprayJsonJS = sprayJson.js
lazy val sprayJsonNative = sprayJson.native