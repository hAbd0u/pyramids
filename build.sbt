enablePlugins(ScalaJSPlugin)

name := "eternitas-wordpress"

version := "0.1"

scalaVersion := "2.12.8"





// This is an application with a main method
scalaJSUseMainModuleInitializer := true

libraryDependencies ++= Seq(
  "com.lihaoyi" % "utest_sjs1.0.0-M7_2.12" % "0.6.7" % "test",
  "org.scala-js" %%% "scalajs-dom" % "0.9.6",
  "be.doeraene" %%% "scalajs-jquery" % "0.9.4"
)
testFrameworks += new TestFramework("utest.runner.Framework")
jsEnv := new org.scalajs.jsenv.jsdomnodejs.JSDOMNodeJSEnv()




val genDirPath = "src/main/webapp/js"

lazy val copyjs = TaskKey[Unit]("copyjs", 
  "Copy scala-js files to websapp")
copyjs := {
  val outDir = new File("src/main/webapp/js")
  val inDir = new File("target/scala-2.12")
  val files = Seq(
    "eternitas-wordpress-opt.js",
    "eternitas-wordpress-opt.js.map",
    "eternitas-wordpress-jsdeps.js"
  ) map { p =>   (inDir / p, outDir / p) }
  IO.copy(files, true)
}
addCommandAlias("myFullOptJS",";fullOptJS;copyjs")

