package com.lyrx.pyramids.demo

import com.lyrx.pyramids.{Pyramid, PyramidConfig}
import com.lyrx.pyramids.frontend.UserFeedback
import org.scalajs.dom.raw.File

import scala.concurrent.{ExecutionContext, Future}

trait UpDownload extends UserFeedback {

  def handle(f: Future[PyramidConfig], msgOpt: Option[String] = None)(
      implicit executionContext: ExecutionContext): Unit

  def handleWithIpfs(f: Future[PyramidConfig], msgOpt: Option[String] = None)(
      implicit executionContext: ExecutionContext): Unit

  def doDownload(pyramid: Pyramid)(implicit $ : JQueryOb,
                                   executionContext: ExecutionContext): Unit = {

    def forDownload()(implicit executionContext: ExecutionContext) = {
      val av = ($("#cid").`val`())
      val uploadOpt: Option[String] = av
        .map(r => Some(r.toString()))
        .getOrElse(pyramid.pyramidConfig.ipfsData.uploadOpt)
      new Pyramid(
        uploadOpt
          .map(s => pyramid.pyramidConfig.withUpload(s))
          .getOrElse(pyramid.pyramidConfig))
    }

    def forDownloadPharao(dp: Pyramid)(
        implicit executionContext: ExecutionContext) = {
      val av: TextFieldContents = ($("#symkey").`val`())
      val symKeyOpt: Option[String] = av
        .map(r => Some(r.toString()))
        .getOrElse(pyramid.pyramidConfig.ipfsData.symKeyOpt)
      new Pyramid(
        symKeyOpt
          .map(s => dp.pyramidConfig.withSymKey(s))
          .getOrElse(dp.pyramidConfig))

    }

    def downloadAsSlave()(implicit executionContext: ExecutionContext) = {
      message("Loading/decrypting ...")
      handle(forDownload().download(), None)
    }
    def downloadAsPharao()(implicit executionContext: ExecutionContext) = {
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

  def doUpload(f: File, pyramid: Pyramid)(
      implicit $ : JQueryOb,
      executionContext: ExecutionContext): Unit = {
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

}
