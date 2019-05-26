enablePlugins(ScalaJSPlugin,ScalaJSBundlerPlugin)




resolvers += Resolver.bintrayRepo("oyvindberg", "ScalablyTyped")



name := "pyramids"

version := "0.1"

scalaVersion := "2.12.8"





// This is an application with a main method
scalaJSUseMainModuleInitializer := true

libraryDependencies ++= Seq(
  "com.lihaoyi" % "utest_sjs1.0.0-M7_2.12" % "0.6.7" % "test",
  "org.scala-js" %%% "scalajs-dom" % "0.9.6" ,
  "org.scalablytyped" % "jquery_sjs0.6_2.12" % "3.3-dt-20190108Z-49ff4d"
)



npmDependencies in Compile ++= Seq(
   "jquery" -> "3.4.1",
   "web3"  -> "1.0.0-beta.55",
   //"ipfs" -> "0.36.2",
  "buffer" -> "3.5.5",
   "jszip"  -> "3.2.1"
)




testFrameworks += new TestFramework("utest.runner.Framework")


/*
val genDirPath = new java.io.File("src/main/webapp/js")
crossTarget in(Compile, fastOptJS) := genDirPath
crossTarget in(Compile, fullOptJS) := genDirPath
*/

