name := "ScalaRTMP"

version := "0.1-SNAPSHOT"

scalaVersion := "2.10.0-RC2"

resolvers ++= Seq(
  "snapshots"             at "http://oss.sonatype.org/content/repositories/snapshots",
  "releases"              at "http://oss.sonatype.org/content/repositories/releases",
  "Typesafe Repository" at "http://repo.typesafe.com/typesafe/releases/",
  "Typesafe Snapshots Repository" at "http://repo.typesafe.com/typesafe/snapshots/"
)
 
libraryDependencies += "com.typesafe.akka" %% "akka-actor" % "2.1.0-RC2" cross CrossVersion.full
