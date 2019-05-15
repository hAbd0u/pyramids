package com.lyrx.pyramids
import org.scalajs.jquery.{jQuery => $}


import org.scalajs.dom.document
import scala.concurrent.ExecutionContext

// This file is same as LastWillStartup.scala of master branch. for push 1

object Startup {

  //test suggested by Alex.
  def main(args: Array[String]): Unit ={
    implicit val ec = ExecutionContext.global;
    Pyramid().generateKeys().
      onComplete(t => {
        t.failed.map(thr=>println(s"Error: ${thr.getMessage}"))
        t.map(p=>init(p))
      })
  }

  def init(pyramid: Pyramid)={
    //document.getElementById("#time").addEventListener()
    println(s"Pyramid: ${pyramid.pyramidConfig}: ${$("#time")}")
  }

}
