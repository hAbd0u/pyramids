enablePlugins(ScalaJSPlugin,ScalaJSBundlerPlugin)


name := "pyramids"

version := "0.1"

scalaVersion := "2.12.8"





// This is an application with a main method
scalaJSUseMainModuleInitializer := true

libraryDependencies ++= Seq(
  "com.lihaoyi" % "utest_sjs1.0.0-M7_2.12" % "0.6.7" % "test",
  "org.scala-js" %%% "scalajs-dom" % "0.9.6" ,
  "be.doeraene" %%% "scalajs-jquery" % "0.9.4"
)



npmDependencies in Compile ++= Seq(
   "jquery" -> "2.2.1",
     "web3"  -> "1.0.0-beta.55",
  "ipfs" -> "0.36.2",
  "jszip"  -> "3.2.1"
  /* 
  ProvidedJS / "js/web3/web3.min.js",
  ProvidedJS / "js/ipfs/index.js",
  ProvidedJS / "js/jszip/jszip.min.js"
  */


)




testFrameworks += new TestFramework("utest.runner.Framework")


val genDirPath = new java.io.File("src/main/webapp/js")


crossTarget in(Compile, fastOptJS) := genDirPath
crossTarget in(Compile, fullOptJS) := genDirPath

