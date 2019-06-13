package com.lyrx.pyramids

import com.lyrx.pyramids.frontend.UserFeedback

import scala.concurrent.{ExecutionContext, Future}

trait CanStartup extends UserFeedback {

  def createPyramid(): Pyramid

  def init(pyramidConfig: PyramidConfig)(
      implicit executionContext: ExecutionContext): Future[PyramidConfig]

  def startup()(implicit executionContext: ExecutionContext) = {

    message("Generating keys ...")
    createPyramid()
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

  def initTemporal(pyramid: Pyramid)(
      implicit executionContext: ExecutionContext) =
    pyramid.jwtToken().map(_.map(t => println(s"Token: ${t.token}")))

}
