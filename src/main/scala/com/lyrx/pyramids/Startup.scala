package com.lyrx.pyramids
import com.lyrx.pyramids.actions.DragAndDrop
import org.scalajs.jquery.{JQuery, JQueryEventObject, jQuery => $}
import org.scalajs.dom.{Event, File, document}

import scala.concurrent.{ExecutionContext, Future}

// This file is same as LastWillStartup.scala of master branch. for push 1

object Startup extends DragAndDrop {

  //test suggested by Alex.
  def main(args: Array[String]): Unit = {
    implicit val ec = ExecutionContext.global;
    Pyramid()
      .generateKeys()
      .onComplete(t => {
        t.failed.map(thr => println(s"Error: ${thr.getMessage}"))
        t.map(p => init(p.pyramidConfig))
      })
  }
// Newly added code by Alex
  def init(pyramidConfig: PyramidConfig)(
      implicit executionContext: ExecutionContext): Any = {

    val pyramid = new Pyramid(pyramidConfig)
    def handle(f: Future[PyramidConfig]) = f.map(config => init(config))

    def click(selector: String, c: (Event) => Future[PyramidConfig]) =
      $(selector).off().click((e: Event) => handle(c(e)))

    onDragOverNothing($(".front-page").off()).on("drop", (e: Event) => e.preventDefault())


    pyramid
      .downloadWallet($("#logo").off())
      .map((q2: JQuery) => onDrop(q2, (f) => handle(pyramid.uploadWallet(f))))

  }

}
