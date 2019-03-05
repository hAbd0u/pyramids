enablePlugins(ScalaJSPlugin)

name := "eternitas-wordpress"

version := "0.1"

scalaVersion := "2.12.8"

// This is an application with a main method
scalaJSUseMainModuleInitializer := true

libraryDependencies ++= Seq(
  "com.lihaoyi" %%% "utest" % "0.6.3" % "test"
  ,"org.scala-js" %%% "scalajs-dom" % "0.9.6"
  ,"org.querki" %%% "jquery-facade" % "1.2"
)
testFrameworks += new TestFramework("utest.runner.Framework")
jsEnv := new org.scalajs.jsenv.jsdomnodejs.JSDOMNodeJSEnv()


