inThisBuild(Seq(
  organization := "io.spray",
  organizationHomepage := Some(new URL("http://spray.io")),
  description := "A Scala library for easy and idiomatic JSON (de)serialization",
  homepage := Some(new URL("https://github.com/spray/spray-json")),
  startYear := Some(2011),
  licenses := Seq("Apache 2" -> new URL("http://www.apache.org/licenses/LICENSE-2.0.txt")),
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
))
