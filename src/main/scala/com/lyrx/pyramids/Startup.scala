package com.lyrx.pyramids



import org.scalajs.jquery.{jQuery => $}

import scala.concurrent.ExecutionContext


object Startup {


  def main(args: Array[String]): Unit ={
    implicit val ec = ExecutionContext.global;
    Pyramid().generateKeys().
      onComplete(t=>{
        t.failed.map(thr=>println(s"Error: ${thr.getMessage}"))
        t.map(p=>init(p))
      })
  }

  def init(pyramid: Pyramid)={
    println(s"Pyramid: ${pyramid.pyramidConfig}")
  }





}
