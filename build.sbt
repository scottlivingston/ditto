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
  libraryDependencies ++= Seq(
    "com.typesafe" % "config" % "1.3.1"
  )
).dependsOn(dittoCore, dittoHttp, dittoThriftGenerator)
 .aggregate(dittoCore, dittoHttp, dittoThrift)

lazy val dittoCoreDir = s"$dittoName-core"
lazy val dittoCoreName = s"${dittoName}Core"
lazy val dittoCore = Project(
  id = dittoCoreName,
  base = file(dittoCoreDir),
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

lazy val dittoHttpDir = s"$dittoName-http"
lazy val dittoHttpName = s"${dittoName}Http"
lazy val dittoHttp = Project(
  id = dittoHttpName,
  base = file(dittoHttpDir),
  settings = Defaults.coreDefaultSettings ++
    sharedSettings
).settings(
  name := dittoHttpName,
  libraryDependencies ++= Seq(
    "com.twitter" %% "finagle-http" % "6.38.0"
  )
).dependsOn(dittoCore)

lazy val dittoThriftDir = s"$dittoName-thrift"
lazy val dittoThriftName = s"${dittoName}Thrift"
lazy val dittoThrift = Project(
  id = dittoThriftName,
  base = file(dittoThriftDir),
  settings = Defaults.coreDefaultSettings ++
    sharedSettings
).settings(
  name := dittoThriftName,
  scroogeThriftSourceFolder in Compile <<= baseDirectory { base => base / "src/test/thrift" },
  libraryDependencies ++= Seq(
    "com.twitter" %% "finagle-thrift" % "6.38.0"
  )
).dependsOn(dittoCore)

lazy val dittoThriftGeneratorDir = s"$dittoName-thrift-generator"
lazy val dittoThriftGeneratorName = s"${dittoName}ThriftGenerator"
lazy val dittoThriftGenerator = Project(
  id = dittoThriftGeneratorName,
  base = file(dittoThriftGeneratorDir),
  settings = Defaults.coreDefaultSettings ++
    sharedSettings
).settings(
  name := dittoThriftGeneratorName,
  scroogeThriftSourceFolder in Compile <<= baseDirectory { base => base / "src/main/thrift" },
  libraryDependencies ++= Seq(
    "com.twitter" %% "scrooge-generator" % "4.10.0",
    "com.twitter" %% "finagle-thrift" % "6.38.0",
    "com.github.japgolly.nyaya" %% "nyaya-gen" % "0.8.1"
  )
).dependsOn(dittoCore)