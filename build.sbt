organization := "com.praphull"
name := "scala-finance"
description := "Financial function implementations in Scala"
version := "0.0.1"

scalaVersion := "2.13.1"

libraryDependencies ++= Seq(
  "org.scalatest" %% "scalatest" % Versions.scalaTest,
  "joda-time" % "joda-time" % "2.10.5" % Compile
)

scalacOptions ++= Seq(
  "-Xfatal-warnings",
  "-deprecation",
  "-unchecked",
  "-feature",
  "-Xlint"
)
scalacOptions in(Compile, doc) += "-no-link-warnings"
