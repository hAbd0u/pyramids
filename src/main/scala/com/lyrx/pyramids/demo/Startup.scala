package com.lyrx.pyramids.demo

import com.lyrx.pyramids.frontend.UserFeedback
import com.lyrx.pyramids.keyhandling.DragAndDrop
import com.lyrx.pyramids.temporal.Temporal
import com.lyrx.pyramids.{CanStartup, Pyramid, PyramidConfig}
import org.scalajs.dom.{Event, File, document}
import typings.jqueryLib.{JQuery, JQueryEventObject, JQueryStatic, jqueryMod => jq}

import scala.concurrent.{ExecutionContext, Future}
import scala.scalajs.js
import scala.scalajs.js.{JSON, UndefOr, |}

object Startup extends DragAndDrop with UserFeedback with CanStartup{
  implicit val ec = ExecutionContext.global

  type JQueryOb = org.scalablytyped.runtime.TopLevel[JQueryStatic]
  type TextFieldContents =
    js.UndefOr[java.lang.String | scala.Double | js.Array[java.lang.String]]

  override def msgField[T](): JQuery[T] = {
    jq("#message")
  }

  override def timeField[T](): JQuery[T] = jq("#time")

  def main(args: Array[String]): Unit =
    document.addEventListener("DOMContentLoaded", (e: Event) => startup())

  override def createPyramid():Pyramid =
    Pyramid("QmUK2hhKzDfEtnetu41AZUjc7CU8EtLn135EVKyHprVVyn")





  override def init(pyramidConfig: PyramidConfig)(
      implicit executionContext: ExecutionContext): Future[PyramidConfig] = {
    val pyramid = new Pyramid(pyramidConfig)
    implicit val $ = jq

    initTemporal(pyramid)

    showMessages(pyramidConfig)

    updateActions(pyramid)

    updateFrontend(pyramidConfig)

    Future { pyramidConfig }

  }

  private def updateFrontend(pyramidConfig: PyramidConfig)(
      implicit $ : JQueryOb) = {
    val infura = "https://ipfs.infura.io/ipfs"

    val atts = "target='_blank' class='bottom-line'"


    $("#pinfolder").html(pyramidConfig.ipfsData.uploadOpt.map(s =>
      s"<a href='$infura/$s' $atts >Chamber</a>"
    ).getOrElse("") )


    $("#signature").html(pyramidConfig.ipfsData.pubKeysOpt.map(s =>
      s"<a href='$infura/$s' $atts >Signature</a>").getOrElse(""))


    $("#symkey").`val`(s"${pyramidConfig.ipfsData.symKeyOpt.getOrElse("")}")
    $("#cid").`val`(s"${pyramidConfig.ipfsData.uploadOpt.getOrElse("")}")





    pyramidConfig.ipfsData.symKeyOpt.map(aHash=>{
      val m: UndefOr[String] = $("#mail").attr("href")
      m.map(s=>{
        val href = s.replaceFirst("TOKENIZER",aHash)
        $("#mail").attr("href",href)
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
  def doDownload(pyramid: Pyramid)(implicit $:JQueryOb):Unit = {

    def forDownload()={
      val av = ($("#cid").`val`())
      val uploadOpt: Option[String] = av
        .map(r => Some(r.toString()))
        .getOrElse(pyramid.pyramidConfig.ipfsData.uploadOpt)
      new Pyramid(
        uploadOpt.map(s => pyramid.pyramidConfig.withUpload(s))
          .getOrElse(pyramid.pyramidConfig))
    }

    def forDownloadPharao(dp:Pyramid) = {
      val av: TextFieldContents = ($("#symkey").`val`())
      val symKeyOpt: Option[String] = av
        .map(r => Some(r.toString()))
        .getOrElse(pyramid.pyramidConfig.ipfsData.symKeyOpt)
      new Pyramid(
        symKeyOpt.map(s => dp.pyramidConfig.withSymKey(s))
          .getOrElse(dp.pyramidConfig))

    }

    def downloadAsSlave() = {
      message("Loading/decrypting ...")
      handle(forDownload().download(), None)
    }
    def downloadAsPharao() = {
      message("Loading/decrypting for the Pharao ...")

      val p: Pyramid = forDownloadPharao(forDownload())

      val f: Future[Option[Pyramid]] = p.withImportSymKey()
      f.failed.map(thr => error(thr.getMessage))
      f.map(_.map(p2 => handle(p2.download(), None)))

    }

    if (pyramid.pyramidConfig.isPharao())
      downloadAsPharao()
    else
      downloadAsSlave()

    ()

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



    def doUpload(f: File) = {
      handle({

        def uploadAsPharao() = {
          message("Oh Pharao, you are donating ...")
          val av = ($("#symkey").`val`())

          val ao: Option[String] = av
            .map(r => Some(r.toString()))
            .getOrElse(pyramid.pyramidConfig.ipfsData.symKeyOpt)

          val p: Pyramid = new Pyramid(
            ao.map(s => pyramid.pyramidConfig.withSymKey(s))
              .getOrElse(pyramid.pyramidConfig))

          p.uploadZip(f)
        }

        if (pyramid.pyramidConfig.isPharao())
          uploadAsPharao()
        else {
          message("Humble Tokenizer, you are trying to save ...")
          pyramid.uploadZip(f)
        }

      })
    }

    onDrop($("#drop_zone").off(), (f) => doUpload(f))
      .on("click", (e: JQueryEventObject) => doDownload(pyramid))

    $("#stampd")
      .off()
      .on("click",
          (e: JQueryEventObject) => // handle(pyramid.testAsym())
            handle(pyramid.uploadZip2()))

  }

  def showMessages(
      pyramidConfig: _root_.com.lyrx.pyramids.PyramidConfig): Option[Unit] = {
    //show message and error
    pyramidConfig.messages.messageOpt.map(s => message(s))
    pyramidConfig.messages.errorOpt.map(s => error(s))
  }
}
