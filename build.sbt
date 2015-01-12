name := """landingpage"""

version := "1.0-SNAPSHOT"

lazy val root = (project in file(".")).enablePlugins(PlayScala)

scalaVersion := "2.11.4"

val playVersion = "2.3.0-2"

libraryDependencies ++= Seq(
  jdbc,
  anorm,
  cache,
  ws,
  "com.typesafe.play" %% "play" % playVersion,
  "org.webjars" %% "webjars-play" % playVersion,
  "org.postgresql" % "postgresql" % "9.3-1102-jdbc41",
  "org.seleniumhq.selenium" % "selenium-java" % "2.44.0",
  "org.webjars" % "bootstrap" % "3.3.1",
  "com.datastax.cassandra" % "cassandra-driver-core" % "2.1.4"
)

scalacOptions ++= Seq(
    "-feature",
    "-language:postfixOps" //This allows the postfix operator * to be enabled
)
