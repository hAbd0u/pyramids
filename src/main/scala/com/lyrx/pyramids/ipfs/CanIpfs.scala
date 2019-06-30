package com.lyrx.pyramids.ipfs

import com.lyrx.pyramids.pcrypto.WalletNative
import com.lyrx.pyramids.{Pyramid, PyramidConfig, PyramidJSON, pcrypto}
import typings.nodeLib
import typings.nodeLib.bufferMod

import scala.concurrent.{ExecutionContext, Future}
import scala.scalajs.js.JSON
import scala.scalajs.js.typedarray.ArrayBuffer

trait CanIpfs extends IpfsSupport {
  val pyramidConfig: PyramidConfig

  def initIpfs()(implicit executionContext: ExecutionContext): Future[Pyramid]

  def initIpfsAndPublishPublicKeys()(
      implicit executionContext: ExecutionContext): Future[Pyramid]

  def ipfsClientOpt():Option[IpfsClient] = pyramidConfig.infuraClientOpt






}
