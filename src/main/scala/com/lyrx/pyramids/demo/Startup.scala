package com.lyrx.pyramids.demo

import com.lyrx.pyramids.keyhandling.DragAndDrop
import com.lyrx.pyramids.frontend.UserFeedback
import com.lyrx.pyramids.jszip.JSZip
import com.lyrx.pyramids.{Pyramid, PyramidConfig}
import org.scalajs.dom.{Event, document}
import org.scalajs.jquery.{JQuery, JQueryEventObject, jQuery => $}

import scala.concurrent.{ExecutionContext, Future}
import scala.util.Try

object Startup extends DragAndDrop with UserFeedback{

  override def msgField():JQuery= $("#message")
  def timeField():JQuery = $("#time")


  //test suggested by Alex.
  def main(args: Array[String]): Unit = document.addEventListener(
      "DOMContentLoaded",
      (e: Event) =>startup())


  def startup()={
    implicit val ec = ExecutionContext.global;
    message("Welcome, Pharao! Initialzing ...")

    Pyramid()
      .generateKeys().map(p=>init(p.pyramidConfig))


  }


  def init(pyramidConfig: PyramidConfig)(
    implicit executionContext: ExecutionContext)= new Pyramid(
    pyramidConfig
  ).initIpfs().onComplete( (t:Try[Pyramid]) => {
    t.failed.map(thr => error(s"Initialization Error: ${thr.getMessage}"))
    t.map((p:Pyramid) => internalInit(p.pyramidConfig))
  })

  def internalInit(pyramidConfig: PyramidConfig)(
      implicit executionContext: ExecutionContext): PyramidConfig = {

    val pyramid = new Pyramid(pyramidConfig)

    def handle(f: Future[PyramidConfig]) = {
      f.onComplete(t=>t.failed.map(thr=>error(thr.getMessage)))
      f.map(config => init(config))
    }
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
