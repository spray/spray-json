// shadow sbt-scalajs' crossProject and CrossType until Scala.js 1.0.0 is released
import sbtcrossproject.CrossPlugin.autoImport.{crossProject, CrossType}
import com.typesafe.tools.mima.core.{ProblemFilters, ReversedMissingMethodProblem}

lazy val scala210 = "2.10.7"
lazy val scala211 = "2.11.12"
lazy val scala212 = "2.12.8"
lazy val scala213 = "2.13.0"

lazy val sprayJson =
  crossProject(JVMPlatform, JSPlatform, NativePlatform)
    .crossType(CrossType.Full)
    .in(file("spray-json"))
    .settings(
      name := "spray-json",
      version := "1.3.6-SNAPSHOT",
      scalaVersion := crossScalaVersions.value.head,
      scalacOptions ++= Seq("-feature", "-language:_", "-unchecked", "-deprecation", "-Xlint", "-encoding", "utf8", "-target:jvm-1.8"),
      scalacOptions ++= {
        if (scalaMinorVersion.value >= 12) Seq("-release", "8")
        else Nil
      },
      (scalacOptions in doc) ++= Seq("-doc-title", name.value + " " + version.value),
      scalaBinaryVersion := {
        val sV = scalaVersion.value
        if (CrossVersion.isScalaApiCompatible(sV))
          CrossVersion.binaryScalaVersion(sV)
        else
          sV
      },
      // Workaround for "Shared resource directory is ignored"
      // https://github.com/portable-scala/sbt-crossproject/issues/74
      unmanagedResourceDirectories in Test += (baseDirectory in ThisBuild).value / "spray-json/shared/src/test/resources"
    )
    .enablePlugins(spray.boilerplate.BoilerplatePlugin)
    .platformsSettings(JVMPlatform, JSPlatform)(
      libraryDependencies ++= {
        if (scalaMinorVersion.value >= 11)
          Seq(
            "org.specs2" %%% "specs2-core" % "4.5.1" % "test",
            "org.specs2" %%% "specs2-scalacheck" % "4.5.1" % "test",
            "org.scalacheck" %%% "scalacheck" % "1.14.0" % "test"
          )
        else // 2.10
          Seq(
            "org.specs2" %%% "specs2-core" % "3.8.9" % "test",
            "org.specs2" %%% "specs2-scalacheck" % "3.8.9" % "test",
            "org.scalacheck" %%% "scalacheck" % "1.13.4" % "test"
          )
      }
    )
    .configurePlatforms(JVMPlatform)(_.enablePlugins(SbtOsgi))
    .jvmSettings(
      crossScalaVersions := Seq(scala213, scala212, scala211, scala210),
      OsgiKeys.exportPackage := Seq("""spray.json.*;version="${Bundle-Version}""""),
      OsgiKeys.importPackage := Seq("""scala.*;version="$<range;[==,=+);%s>"""".format(scalaVersion.value)),
      OsgiKeys.importPackage ++= Seq("""spray.json;version="${Bundle-Version}"""", "*"),
      OsgiKeys.additionalHeaders := Map("-removeheaders" -> "Include-Resource,Private-Package"),
      mimaPreviousArtifacts := {
        if (scalaMinorVersion.value == 13) Set("io.spray" %% "spray-json" % "1.3.5")
        else Set("1.3.2", "1.3.3", "1.3.4", "1.3.5").map { v => "io.spray" %% "spray-json" % v }
      },
      mimaBinaryIssueFilters := Seq(
        ProblemFilters.exclude[ReversedMissingMethodProblem]("spray.json.PrettyPrinter.organiseMembers")
      )
    )
    .jsSettings(
      crossScalaVersions := Seq(scala212, scala211)
    )
    .nativeSettings(
      crossScalaVersions := Seq(scala211),
      // Disable tests in Scala Native until testing frameworks for it become available
      unmanagedSourceDirectories in Test := Seq.empty
    )

lazy val sprayJsonJVM = sprayJson.jvm
lazy val sprayJsonJS = sprayJson.js
lazy val sprayJsonNative = sprayJson.native

lazy val benchmark = Project("benchmark", file("benchmark"))
  .settings(
    scalaVersion := scala212
  )
  .settings(noPublishSettings: _*)
  .enablePlugins(JmhPlugin)
  .dependsOn(sprayJsonJVM % "compile->test")
  .settings(
    libraryDependencies ++= Seq(
      "com.lihaoyi" %% "upickle" % "0.7.5",
      "io.circe" %% "circe-parser" % "0.11.1",
      "io.circe" %% "circe-core" % "0.11.1",
      "io.circe" %% "circe-generic" % "0.11.1",
      "com.typesafe.play" %% "play-json" % "2.7.1"
    )
  )

lazy val noPublishSettings = Seq(
  skip in publish := true
)

lazy val root = (project in file("."))
  .aggregate(sprayJsonJVM, sprayJsonJS, sprayJsonNative)
  .settings(
    publish := {},
    publishLocal := {},
    scalaVersion := scala212,
  )

def scalaMinorVersion: Def.Initialize[Long] = Def.setting { CrossVersion.partialVersion(scalaVersion.value).get._2 }
