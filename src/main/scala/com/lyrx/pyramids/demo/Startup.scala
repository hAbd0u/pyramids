package com.lyrx.pyramids.demo

import com.lyrx.pyramids.frontend.UserFeedback
import com.lyrx.pyramids.keyhandling.DragAndDrop
import com.lyrx.pyramids.temporal.Temporal
import com.lyrx.pyramids.{CanStartup, Pyramid, PyramidConfig}
import org.scalajs.dom.{Event, File, document}
import typings.jqueryLib.{
  JQuery,
  JQueryEventObject,
  JQueryStatic,
  jqueryMod => jq
}

import scala.concurrent.{ExecutionContext, Future}
import scala.scalajs.js
import scala.scalajs.js.{JSON, UndefOr, |}

object Startup
    extends DragAndDrop
    with UserFeedback
    with CanStartup
    with UpDownload {
  implicit val ec = ExecutionContext.global

  override def msgField[T](): JQuery[T] = {
    jq("#message")
  }

  override def timeField[T](): JQuery[T] = jq("#time")

  def main(args: Array[String]): Unit =
    document.addEventListener("DOMContentLoaded", (e: Event) => startup())

  override def createPyramid(): Pyramid =
    Pyramid(
      "QmUK2hhKzDfEtnetu41AZUjc7CU8EtLn135EVKyHprVVyn",
      "GDY7YWJF6F7W7EIQP5UDWYXNBC62JUSGJOLM2VWRQGY7RZ5SDYRZOZNT")

  override def init(pyramidConfig: PyramidConfig)(
      implicit executionContext: ExecutionContext): Future[PyramidConfig] = {
    val pyramid = new Pyramid(pyramidConfig)
    implicit val $ = jq

    showMessages(pyramidConfig)

    updateActions(pyramid)

    updateFrontend(pyramidConfig)

    Future { pyramidConfig }

  }

  private def updateFrontend(pyramidConfig: PyramidConfig)(
      implicit $ : JQueryOb) = {
    val infura = "https://ipfs.infura.io/ipfs"

    val atts = "target='_blank' class='bottom-line'"

    $("#pinfolder").html(
      pyramidConfig.ipfsData.uploadOpt
        .map(s => s"<a href='$infura/$s' $atts >Chamber</a>")
        .getOrElse(""))

    $("#signature").html(
      pyramidConfig.ipfsData.pubKeysOpt
        .map(s => s"<a href='$infura/$s' $atts >Signature</a>")
        .getOrElse(""))
    $("#temporal").html(
      pyramidConfig.ipfsData.temporalOpt
        .map(s => s"<a href='$infura/$s' $atts >Temporal</a>")
        .getOrElse(""))



    $("#symkey").`val`(s"${pyramidConfig.ipfsData.symKeyOpt.getOrElse("")}")
    $("#cid").`val`(s"${pyramidConfig.ipfsData.uploadOpt.getOrElse("")}")

    pyramidConfig.ipfsData.symKeyOpt.map(aHash => {
      val m: UndefOr[String] = $("#mail").attr("href")
      m.map(s => {
        val href = s.replaceFirst("TOKENIZER", aHash)
        $("#mail").attr("href", href)
      })
    })

    if (pyramidConfig.isPharao()) {

      $("#stampd").show()
      $("#send").show()

      $("#cid").show()
      $("#symkey").show()
    } else {

      $("#stampd").hide()
      $("#send").hide()

      $("#cid").show()
      $("#symkey").hide()

    }
  }

  def updateActions(pyramid: Pyramid)(implicit $ : JQueryOb) = {
    // prevent default for drag and droo
    onDragOverNothing($(".front-page").off())
      .on("drop", (e: JQueryEventObject) => e.preventDefault())

    //Download/upload wallet:

    onDrop($("#logo").off(),
           f => handleWithIpfs(createPyramid().uploadWallet(f), None))
      .on("click",
          (e: JQueryEventObject) => handle(pyramid.downloadWallet(), None))

    onDrop($("#drop_zone").off(), (f) => doUpload(f, pyramid))
      .on("click", (e: JQueryEventObject) => doDownload(pyramid))

    $("#stampd")
      .off()
      .on("click",
          (e: JQueryEventObject) => handle(pyramid.initStellarKeys($("#cid").`val`().toString())))


  }

  def showMessages(
      pyramidConfig: _root_.com.lyrx.pyramids.PyramidConfig): Option[Unit] = {
    //show message and error
    pyramidConfig.messages.messageOpt.map(s => message(s))
    pyramidConfig.messages.errorOpt.map(s => error(s))
  }
}
