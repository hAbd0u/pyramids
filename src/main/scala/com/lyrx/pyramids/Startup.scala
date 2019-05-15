package com.lyrx.pyramids
import org.scalajs.jquery.{JQuery, jQuery => $}
import org.scalajs.dom.{Event, document}

import scala.concurrent.{ExecutionContext, Future}

// This file is same as LastWillStartup.scala of master branch. for push 1

object Startup {

  //test suggested by Alex.
  def main(args: Array[String]): Unit ={
    implicit val ec = ExecutionContext.global;
    Pyramid().generateKeys().
      onComplete(t => {
        t.failed.map(thr=>println(s"Error: ${thr.getMessage}"))
        t.map(p=>init(p.pyramidConfig))
      })
  }

  def init(pyramidConfig: PyramidConfig)(implicit executionContext: ExecutionContext):JQuery={

    val pyramid = new Pyramid(pyramidConfig)
    def handle(f:Future[PyramidConfig]) = f.map(config=>init(config))


    def action(selector:String,c:(Event)=>Future[PyramidConfig]) =
      $(selector).off().click((e:Event)=>handle(c(e)))

    action("#logo", pyramid.downloadKey _  )



  }





}
