package com.lyrx.pyramids

import com.lyrx.pyramids.frontend.UserFeedback
import com.lyrx.pyramids.ipfs.PinResult

import scala.concurrent.{ExecutionContext, Future}
import scala.scalajs.js

trait CanStartup extends UserFeedback {

  def createPyramid(): Pyramid

  def init(pyramidConfig: PyramidConfig)(
      implicit executionContext: ExecutionContext): Future[PyramidConfig]

  def startup()(implicit executionContext: ExecutionContext) = {

    message("Generating keys ...")
    createPyramid()
      .generateKeys()
      .flatMap(p => ipfsInit(p.pyramidConfig))

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
    f.flatMap(p => init(p.pyramidConfig))


  }

  def handleWithIpfs(f: Future[PyramidConfig], msgOpt: Option[String] = None)(
      implicit executionContext: ExecutionContext) = {
    msgOpt.map(message(_))
    f.onComplete(t => t.failed.map(thr => error(thr.getMessage)))
    f.map(config => ipfsInit(config))
    ()
  }
  def handle(f: Future[PyramidConfig], msgOpt: Option[String] = None)(
      implicit executionContext: ExecutionContext) = {
    msgOpt.map(message(_))
    f.onComplete(t => t.failed.map(thr => error(thr.getMessage)))
    f.map(config => init(config))
    ()
  }


  def initTemporalLyrx(pyramid: Pyramid)(
  implicit executionContext: ExecutionContext) =
    pyramid.configFromLyrx().
      map(new Pyramid(_)).flatMap(_.initTemporal())


  def initTemporalAll(pyramid: Pyramid)(implicit executionContext: ExecutionContext) =
    if(pyramid.pyramidConfig.temporalData.temporalCredentialsOpt.isEmpty)
      initTemporalLyrx(pyramid)
    else
      initTemporal(pyramid)



  def initTemporalHash(pyramid: Pyramid)(
      implicit executionContext: ExecutionContext) =
    pyramid
      .pinJWTToken()
      .map((l: Option[js.Array[PinResult]]) =>
        l.headOption.flatMap((p: js.Array[PinResult]) =>
          p.headOption.map(p2 => new Pyramid(
            pyramid.
            pyramidConfig.
            withTemporal(p2.hash))))).
      map(_.getOrElse(pyramid))

  def initTemporal(pyramid: Pyramid)(
  implicit executionContext: ExecutionContext) =
    initTemporalHash(pyramid).flatMap(_.initTemporal())

}
