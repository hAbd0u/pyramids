package com.lyrx.pyramids.demo

import com.lyrx.pyramids.frontend.UserFeedback
import com.lyrx.pyramids.keyhandling.DragAndDrop
import com.lyrx.pyramids.{Pyramid, PyramidConfig}
import org.scalajs.dom.{Event, document}
import typings.jqueryLib.{JQuery, JQueryEventObject, jqueryMod => $}

import scala.concurrent.{ExecutionContext, Future}


object  Startup extends DragAndDrop with UserFeedback{
  implicit val ec = ExecutionContext.global

   override def msgField[T]():JQuery[T] = {
     $("#message")
   }
  override  def timeField[T]():JQuery[T] = $("#time")


  def main(args: Array[String]): Unit = document.addEventListener(
    "DOMContentLoaded",
    (e: Event) =>startup())


  def startup()={


    message("Generating keys ...")
    Pyramid()
      .generateKeys().map(p=>ipfsInit(p.pyramidConfig))


  }


  def ipfsInit(pyramidConfig: PyramidConfig)(
    implicit executionContext: ExecutionContext)= {
    message("Connecting IPFS network ...")
    val f =
    new Pyramid(
      pyramidConfig
    ).initIpfsAndPublishPublicKeys()

    f.failed.map(thr => error(s"Initialization Error: ${thr.getMessage}"))
    f.map((p:Pyramid) => init(p.pyramidConfig))

  }

  def handle(f: Future[PyramidConfig],msgOpt:Option[String]=None) = {
    msgOpt.map(message(_))
    f.onComplete(t => t.failed.map(thr => error(thr.getMessage)))
    f.map(config => ipfsInit(config))
  }



  def init(pyramidConfig: PyramidConfig)(
      implicit executionContext: ExecutionContext): Future[PyramidConfig] = {

    val pyramid = new Pyramid(pyramidConfig)

    //show message and error
    pyramidConfig.messages.messageOpt.map(s => message(s))
    pyramidConfig.messages.errorOpt.map(s => error(s))

    // prevent default for drag and droo
    onDragOverNothing($(".front-page").off()).on("drop", (e: JQueryEventObject) => e.preventDefault())

    //Download/upload wallet:
    pyramid
      .downloadWallet($("#logo").off())
      .map((q2:JQuery[_]) => onDrop(q2, (f) => handle(
        pyramid.uploadWallet(f),
        Some(s"Importing keys from ${f.name}"))))


       onDrop($("#drop_zone").off(), (f) => {
         message(s"Have file: ${f.name}")
         Future{Nil}
       })


    Future {
      pyramidConfig
    }
  }


}
