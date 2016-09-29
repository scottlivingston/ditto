val sharedSettings = Seq(
  version := "0.0.1",
  organization := "io.livingston",
  scalaVersion := "2.11.8",
  resolvers += "twttr" at "https://maven.twttr.com/",
  libraryDependencies ++= Seq(
    "net.jcazevedo" %% "moultingyaml" % "0.3.0",
    "org.scalactic" %% "scalactic" % "3.0.0",
    "org.scalatest" %% "scalatest" % "3.0.0" % "test"
  )
)

lazy val dittoName = "ditto"
lazy val ditto = Project(
  id = dittoName,
  base = file("."),
  settings = Defaults.coreDefaultSettings ++
    sharedSettings
).settings(
  name := dittoName,
  scroogeThriftSourceFolder in Compile <<= baseDirectory { base => base / "src/test/thrift" },
  libraryDependencies ++= Seq(
    "com.typesafe" % "config" % "1.3.1"
  )
).dependsOn(dittoCore, dittoHttp, dittoThrift)

lazy val dittoCoreName = s"$dittoName-core"
lazy val dittoCore = Project(
  id = dittoCoreName,
  base = file(dittoCoreName),
  settings = Defaults.coreDefaultSettings ++
    sharedSettings
).settings(
  name := dittoCoreName,
  libraryDependencies ++= Seq(
    "com.typesafe.scala-logging" %% "scala-logging" % "3.5.0",
    "ch.qos.logback" %  "logback-classic" % "1.1.7",
    "com.twitter" %% "util-app" % "6.37.0"
  )
)

lazy val dittoHttpName = s"$dittoName-http"
lazy val dittoHttp = Project(
  id = dittoHttpName,
  base = file(dittoHttpName),
  settings = Defaults.coreDefaultSettings ++
    sharedSettings
).settings(
  name := dittoHttpName,
  libraryDependencies ++= Seq(
    "com.twitter" %% "finagle-http" % "6.38.0"
  )
).dependsOn(dittoCore)

lazy val dittoThriftName = s"$dittoName-thrift"
lazy val dittoThrift = Project(
  id = dittoThriftName,
  base = file(dittoThriftName),
  settings = Defaults.coreDefaultSettings ++
    sharedSettings
).settings(
  name := dittoThriftName,
  libraryDependencies ++= Seq(
    "com.twitter" %% "finagle-thrift" % "6.38.0"
  )
).dependsOn(dittoCore)
