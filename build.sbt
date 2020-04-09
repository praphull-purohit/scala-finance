organization := "com.praphull"
name := "scala-finance"
description := "Financial function implementations in Scala"
version := "0.0.1"

scalaVersion := Versions.scala
crossScalaVersions := Versions.crossCompileVersions

libraryDependencies ++= Seq(
  "org.scalatest" %% "scalatest" % Versions.scalaTest % Test,
  "joda-time" % "joda-time" % Versions.jodaTime % Compile
)

scalacOptions ++= Seq(
  "-Xfatal-warnings",
  "-deprecation",
  "-unchecked",
  "-feature",
  "-Xlint"
)
scalacOptions in(Compile, doc) += "-no-link-warnings"
