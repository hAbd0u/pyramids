package com.lyrx.pyramids.ipfs

import com.lyrx.pyramids.pcrypto.WalletNative
import com.lyrx.pyramids.{Pyramid, PyramidConfig, PyramidJSON, pcrypto}
import typings.nodeLib
import typings.nodeLib.bufferMod

import scala.concurrent.{ExecutionContext, Future}
import scala.scalajs.js.JSON
import scala.scalajs.js.typedarray.ArrayBuffer

trait CanIpfs extends  PyramidJSON {
  val pyramidConfig: PyramidConfig

  def initIpfs()(implicit executionContext: ExecutionContext): Future[Pyramid]

  def initIpfsAndPublishPublicKeys()(
      implicit executionContext: ExecutionContext): Future[Pyramid]




  def saveBufferToIpfs(f: Future[nodeLib.Buffer])(
      implicit ctx: ExecutionContext) =
    f.flatMap(
      b =>
        pyramidConfig.infuraClientOpt
          .map(ipfs => ipfs.futureAdd(b).map(Some(_)))
          .getOrElse(Future { None }))

  def saveArrayBufferToIpfs(f: Future[ArrayBuffer])(
      implicit ctx: ExecutionContext) =
    saveBufferToIpfs(f.map(b => bufferMod.Buffer.from(b)))

  def saveStringToIpfs(s: String)(implicit ctx: ExecutionContext) =
    saveBufferToIpfs(
      Future { bufferMod.Buffer.from(s) }
    )



  def bufferToIpfs(buffer: nodeLib.Buffer)(implicit ctx: ExecutionContext) =
    pyramidConfig.infuraClientOpt
      .map(_.futureAdd(buffer).map(l => Some(l.head.hash)))
      .getOrElse(Future { None })

  def readIpfs(aHash: String)(implicit executionContext: ExecutionContext) =
    pyramidConfig.infuraClientOpt
      .map(ipfsClient => ipfsClient.futureCat(aHash).map(Some(_)))
      .getOrElse(Future { None })

  def readIpfsString(aHash: String)(
      implicit executionContext: ExecutionContext) =
    pyramidConfig.infuraClientOpt
      .map(ipfsClient => ipfsClient.futureCatString(aHash).map(Some(_)))
      .getOrElse(Future { None })



  def readIpfsNativeWallet()(implicit executionContext: ExecutionContext) =
    readIpfsString(pyramidConfig.ipfsData.pharaoData.pubkey).map(_.map(s =>
      JSON.parse(s).asInstanceOf[WalletNative]))


}
