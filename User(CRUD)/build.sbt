name := "User(CRUD)"

version := "0.1"

scalaVersion := "2.12.8"

libraryDependencies ++= Seq("org.mockito" %% "mockito-scala-scalatest" % "1.15.0" % Test,

  "org.scalatest" %% "scalatest" % "3.2.6" ,
  "org.mockito" % "mockito-core" % "1.9.5" % "test",
  "com.typesafe.slick" %% "slick" % "3.3.2",
  "org.slf4j" % "slf4j-nop" % "1.6.4",

  "com.github.dnvriend" %% "akka-persistence-jdbc" % "3.5.2",


  "mysql" % "mysql-connector-java" % "6.0.6",


"com.typesafe.akka" %% "akka-stream" % "2.5.20" ,
  "com.typesafe.akka" %% "akka-http" % "10.1.7",
  "com.typesafe.akka" %% "akka-http-spray-json" % "10.1.7" ,
  "com.typesafe.akka" %% "akka-testkit" % "2.5.20",
  "com.typesafe.akka" %% "akka-http-testkit" % "10.1.7"
)
