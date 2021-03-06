name := """intermediatepage"""

version := "1.0-SNAPSHOT"

lazy val root = (project in file(".")).enablePlugins(PlayScala)

scalaVersion := "2.11.4"

val playVersion = "2.3.0"


libraryDependencies ++= Seq(
  jdbc,
  anorm,
  cache,
  ws,
  "com.typesafe.play" %% "play" % playVersion,
  "org.postgresql" % "postgresql" % "9.3-1102-jdbc41",
  "org.seleniumhq.selenium" % "selenium-java" % "2.44.0",
  "com.datastax.cassandra" % "cassandra-driver-core" % "2.1.4",
"jp.t2v" %% "play2-auth"      % "0.13.0",
"org.mindrot" % "jbcrypt" % "0.3m",
"org.scalikejdbc" %% "scalikejdbc"       % "2.2.0",
"org.scalikejdbc" %% "scalikejdbc-play-plugin"           % "2.3.0",
"io.prismic" %% "scala-kit" % "1.3.3"
)

scalacOptions ++= Seq(
    "-feature",
    "-language:postfixOps" //This allows the postfix operator * to be enabled
)
