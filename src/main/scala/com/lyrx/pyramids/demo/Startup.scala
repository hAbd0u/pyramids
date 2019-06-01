package com.lyrx.pyramids.demo

import com.lyrx.pyramids.frontend.UserFeedback
import com.lyrx.pyramids.keyhandling.DragAndDrop
import com.lyrx.pyramids.{Pyramid, PyramidConfig}

import org.scalajs.dom.{Event, File, document}
import typings.jqueryLib.{JQuery, JQueryEventObject, jqueryMod => $}

import scala.concurrent.{ExecutionContext, Future}

object Startup extends DragAndDrop with UserFeedback {
  implicit val ec = ExecutionContext.global

  override def msgField[T](): JQuery[T] = {
    $("#message")
  }

  override def timeField[T](): JQuery[T] = $("#time")

  def main(args: Array[String]): Unit =
    document.addEventListener("DOMContentLoaded", (e: Event) => startup())

  def startup() = {

    message("Generating keys ...")
    Pyramid()
      .generateKeys()
      .map(p => ipfsInit(p.pyramidConfig))

  }

  def ipfsInit(pyramidConfig: PyramidConfig)(
      implicit executionContext: ExecutionContext) = {
    message("Connecting IPFS network ...")
    val f =
      new Pyramid(
        pyramidConfig
      ).initIpfsAndPublishPublicKeys()

    f.failed.map(thr => error(s"Initialization Error: ${thr.getMessage}"))
    f.map((p: Pyramid) => init(p.pyramidConfig))

  }

  def handleWithIpfs(f: Future[PyramidConfig],
                     msgOpt: Option[String] = None) = {
    msgOpt.map(message(_))
    f.onComplete(t => t.failed.map(thr => error(thr.getMessage)))
    f.map(config => ipfsInit(config))
  }
  def handle(f: Future[PyramidConfig], msgOpt: Option[String] = None) = {
    msgOpt.map(message(_))
    f.onComplete(t => t.failed.map(thr => error(thr.getMessage)))
    f.map(config => init(config))
  }

  def init(pyramidConfig: PyramidConfig)(
      implicit executionContext: ExecutionContext): Future[PyramidConfig] = {

    val pyramid = new Pyramid(pyramidConfig)

    showMessages(pyramidConfig)

    updateFrontend(pyramid)

    pyramidConfig.
      uploadOpt.
      map(s =>
        $("#pinfolder").
          html
          (s"<a href='https://ipfs.infura.io/ipfs/$s'"+
            s" target='_blank'>Chamber</a>")
      )




    Future { pyramidConfig }

  }


  private def updateFrontend( pyramid: Pyramid) = {
    // prevent default for drag and droo
    onDragOverNothing($(".front-page").off())
      .on("drop", (e: JQueryEventObject) => e.preventDefault())

    //Download/upload wallet:
    pyramid
      .downloadWallet($("#logo").off())
      .map(
        (q2: JQuery[_]) =>
          onDrop(q2,
            (f) =>
              handleWithIpfs(pyramid.uploadWallet(f),
                Some(s"Importing keys from ${f.name}"))))

    onDrop($("#drop_zone").off(), (f) => handle({
      message("Uploading ...")
      pyramid.uploadZip(f)
    }))
  }

  def showMessages(pyramidConfig: _root_.com.lyrx.pyramids.PyramidConfig): Option[Unit] = {
    //show message and error
    pyramidConfig.messages.messageOpt.map(s => message(s))
    pyramidConfig.messages.errorOpt.map(s => error(s))
  }
}
