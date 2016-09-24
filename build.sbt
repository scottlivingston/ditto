name := "ditto"
version := "0.0.1"
scalaVersion := "2.11.8"

resolvers += "twttr" at "https://maven.twttr.com/"

libraryDependencies ++= Seq(
  "net.jcazevedo" %% "moultingyaml" % "0.3.0",
  "com.twitter" %% "finagle-http" % "6.38.0",
  "com.twitter" %% "twitter-server" % "1.23.0",
  "org.scalactic" %% "scalactic" % "3.0.0",
  "org.scalatest" %% "scalatest" % "3.0.0" % "test"
)

