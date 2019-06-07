package com.lyrx.pyramids.demo

import com.lyrx.pyramids.frontend.UserFeedback
import com.lyrx.pyramids.keyhandling.DragAndDrop
import com.lyrx.pyramids.{Pyramid, PyramidConfig}
import org.scalajs.dom.{Event, File, document}
import typings.jqueryLib.{JQuery, JQueryEventObject, JQueryStatic, jqueryMod => jq}

import scala.concurrent.{ExecutionContext, Future}

object Startup extends DragAndDrop with UserFeedback {
  implicit val ec = ExecutionContext.global

  type JQueryOb = org.scalablytyped.runtime.TopLevel[JQueryStatic]


  override def msgField[T](): JQuery[T] = {
    jq("#message")
  }

  override def timeField[T](): JQuery[T] = jq("#time")

  def main(args: Array[String]): Unit =
    document.addEventListener("DOMContentLoaded", (e: Event) => startup())

  def startup() = {

    message("Generating keys ...")
    Pyramid("QmUK2hhKzDfEtnetu41AZUjc7CU8EtLn135EVKyHprVVyn")
      .generateKeys()
      .map(p => ipfsInit(p.pyramidConfig))

  }

  def ipfsInit(pyramidConfig: PyramidConfig)(
      implicit executionContext: ExecutionContext) = {
    message("Connecting IPFS network ...")
    val f =
      new Pyramid(
        pyramidConfig
      ).initKeys()

    f.failed.map(thr => {
      error(s"Initialization Error: ${thr.getMessage()}")

    })
    f.map((p: Pyramid) => init(p.pyramidConfig))

  }

  def handleWithIpfs(f: Future[PyramidConfig],
                     msgOpt: Option[String] = None) = {
    msgOpt.map(message(_))
    f.onComplete(t => t.failed.map(thr => error(thr.getMessage)))
    f.map(config => ipfsInit(config))
    ()
  }
  def handle(f: Future[PyramidConfig], msgOpt: Option[String] = None) = {
    msgOpt.map(message(_))
    f.onComplete(t => t.failed.map(thr => error(thr.getMessage)))
    f.map(config => init(config))
    ()
  }

  def init(pyramidConfig: PyramidConfig)(
      implicit executionContext: ExecutionContext): Future[PyramidConfig] = {

    val pyramid = new Pyramid(pyramidConfig)
    implicit val $ = jq

    showMessages(pyramidConfig)

    updateActions(pyramid)

    updateFrontend(pyramidConfig)


    Future { pyramidConfig }

  }

  private def updateFrontend( pyramidConfig: PyramidConfig)(implicit $: JQueryOb) = {
    val infura = "https://ipfs.infura.io/ipfs"

    val atts = "target='_blank' class='bottom-line'"

    pyramidConfig.ipfsData.uploadOpt.map(s => {
      $("#pinfolder").html(s"<a href='$infura/$s' $atts >Chamber</a>")
    })

    pyramidConfig.ipfsData.pubKeysOpt.map(s => {
      $("#signature").html(s"<a href='$infura/$s' $atts >Signature</a>")

    })

    pyramidConfig.ipfsData.symKeyOpt.map(s => {
      $("#symkey").`val`(s"$s")
    })

    $("#title").html(if (pyramidConfig.isPharao())
      " Pharao!"
    else
     s"${pyramidConfig.ipfsData.symKeyOpt.getOrElse(
       " ... sorry, you cannot encrypt anything!")}"



    )


    if (pyramidConfig.isPharao()) {

      $("#stampd").show()
      $("#send").show()


      $("#cid").hide()
      $("#symkey").show()
    }
    else {
      $("#stampd").hide()
      $("#send").hide()

      $("#cid").show()
      $("#symkey").hide()

    }
  }

  private def updateActions(pyramid: Pyramid)(implicit $: JQueryOb) = {
    // prevent default for drag and droo
    onDragOverNothing($(".front-page").off())
      .on("drop", (e: JQueryEventObject) => e.preventDefault())

    //Download/upload wallet:

    onDrop($("#logo").off(), f => handleWithIpfs(pyramid.uploadWallet(f), None))
      .on("click",
          (e: JQueryEventObject) => handle(pyramid.downloadWallet(), None))

    onDrop($("#drop_zone").off(),
           (f) =>
             handle({
               message("Uploading ...")
               pyramid.uploadZip(f)
             })).on("click", (e: JQueryEventObject) => {
      message("Loading/decrypting ...")
      handle(pyramid.download(), None)
      ()
    })

    $("#stampd")
      .off()
      .on("click", (e: JQueryEventObject) => // handle(pyramid.testAsym())
         handle(pyramid.uploadZip2())
      )

  }

  def showMessages(
      pyramidConfig: _root_.com.lyrx.pyramids.PyramidConfig): Option[Unit] = {
    //show message and error
    pyramidConfig.messages.messageOpt.map(s => message(s))
    pyramidConfig.messages.errorOpt.map(s => error(s))
  }
}
