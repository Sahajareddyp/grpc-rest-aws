name := "grpc-rest-aws"

version := "0.1"

scalaVersion := "2.13.7"

val logbackVersion = "1.3.0-alpha10"
val sfl4sVersion = "2.0.0-alpha5"
val typesafeConfigVersion = "1.4.1"
val apacheCommonIOVersion = "2.11.0"
val scalacticVersion = "3.2.9"
val generexVersion = "1.0.2"

resolvers += Resolver.jcenterRepo

Compile / PB.targets := Seq(
  scalapb.gen() -> (Compile / sourceManaged).value / "scalapb"
)

libraryDependencies ++= Seq(
  "io.grpc" % "grpc-netty" % scalapb.compiler.Version.grpcJavaVersion,
  "com.thesamet.scalapb" %% "scalapb-runtime-grpc" % scalapb.compiler.Version.scalapbVersion,
  "com.thesamet.scalapb" %% "scalapb-runtime" % scalapb.compiler.Version.scalapbVersion % "protobuf",
)
val akkaVersion = "2.6.17"
val akkaHttpVersion = "10.2.6"
libraryDependencies ++= Seq(
  // Akka Streams
  "com.typesafe.akka" %% "akka-stream" % akkaVersion,
  // Akka HTTP
  "com.typesafe.akka" %% "akka-http" % akkaHttpVersion,
  "com.typesafe.akka" %% "akka-http-spray-json" % akkaHttpVersion,
  //  "com.typesafe.akka" %% "akka-http-spray-json" % "10.1.11"
)

libraryDependencies ++= Seq(
  "ch.qos.logback" % "logback-core" % logbackVersion,
  "ch.qos.logback" % "logback-classic" % logbackVersion,
  "org.slf4j" % "slf4j-api" % sfl4sVersion,
  "com.typesafe" % "config" % typesafeConfigVersion,
  "commons-io" % "commons-io" % apacheCommonIOVersion,
  "org.scalactic" %% "scalactic" % scalacticVersion,
  "org.scalatest" %% "scalatest" % scalacticVersion % Test,
  "org.scalatest" %% "scalatest-featurespec" % scalacticVersion % Test,
  "com.typesafe" % "config" % typesafeConfigVersion,
  "com.github.mifmif" % "generex" % generexVersion,
  "net.liftweb" %% "lift-json" % "3.5.0"
  //  "com.typesafe.akka" %% "akka-http" % "10.4.0",
  //  "com.typesafe.akka" %% "akka-stream" % "2.7.0",
  //  "com.typesafe.akka" %% "akka-http-spray-json" % "10.4.0",
  //    "org.scalaj" %% "scalaj-http" % "2.4.2"
  //    "org.scalaj" %% "scalaj-http" % "2.3.0"
)
//assembly / assemblyMergeStrategy:= {
//  case PathList("META-INF", _*) => MergeStrategy.discard
//  case _ => MergeStrategy.first


