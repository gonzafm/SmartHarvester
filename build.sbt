import play.Project._

name := "SmartHarvester"

version := "1.0"

playScalaSettings

libraryDependencies ++= Seq(jdbc,anorm)