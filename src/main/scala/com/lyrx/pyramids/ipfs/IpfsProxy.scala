package com.lyrx.pyramids.ipfs

import com.lyrx.pyramids.{Pyramid, PyramidConfig}

import scala.concurrent.{ExecutionContext, Future}

trait IpfsProxy {

  val pyramidConfig:PyramidConfig

  def initIpfs()(implicit executionContext: ExecutionContext):Future[Pyramid]




}
