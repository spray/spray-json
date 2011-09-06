resolvers += "sbt-idea-repo" at "http://mpeltonen.github.com/maven/"

libraryDependencies ++= Seq(
	"net.databinder" % "posterous-sbt" % "0.1.7",
	"com.github.mpeltonen" %% "sbt-idea" % "0.10.0"
)


