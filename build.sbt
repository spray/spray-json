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
      }
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