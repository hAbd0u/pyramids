package com.lyrx.pyramids
import com.lyrx.pyramids.actions.DragAndDrop
import com.lyrx.pyramids.messages.UserFeedback
import org.scalajs.jquery.{JQuery, JQueryEventObject, jQuery => $}
import org.scalajs.dom.{Event, File, document}

import scala.concurrent.{ExecutionContext, Future}

// This file is same as LastWillStartup.scala of master branch. for push 1

object Startup extends DragAndDrop with UserFeedback{

  override def msgField():JQuery= $("#message")
  def timeField():JQuery = $("#time")


  //test suggested by Alex.
  def main(args: Array[String]): Unit = document.addEventListener(
      "DOMContentLoaded",
      (e: Event) =>startup())


  def startup()={
    implicit val ec = ExecutionContext.global;
    message("Welcome, Pharao! Please wait ...")
    Pyramid()
      .generateKeys()
      .onComplete(t => {
        t.failed.map(thr => error(s"Error generating keys: ${thr.getMessage}"))
        t.map(p => init(p.pyramidConfig))
      })
  }






// Newly added code by Alex
  def init(pyramidConfig: PyramidConfig)(
      implicit executionContext: ExecutionContext): PyramidConfig = {

    val pyramid = new Pyramid(pyramidConfig)
    def handle(f: Future[PyramidConfig]) = f.map(config => init(config))
    def click(selector: String, c: (Event) => Future[PyramidConfig]) =
      $(selector).off().click((e: Event) => handle(c(e)))


    //show message and error
    pyramidConfig.messages.messageOpt.map(s=>message(s))
    pyramidConfig.messages.errorOpt.map(s=>error(s))

    // prevent default for drag and droo
    onDragOverNothing($(".front-page").off()).on("drop", (e: Event) => e.preventDefault())


    //Download/upload wallet:
    pyramid
      .downloadWallet($("#logo").off())
      .map((q2: JQuery) => onDrop(q2, (f) => handle(pyramid.uploadWallet(f))))

    pyramidConfig
  }

}
