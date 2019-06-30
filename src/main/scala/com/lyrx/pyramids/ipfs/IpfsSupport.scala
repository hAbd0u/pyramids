package com.lyrx.pyramids.ipfs

import typings.nodeLib
import typings.nodeLib.bufferMod

import scala.concurrent.{ExecutionContext, Future}
import scala.scalajs.js.typedarray.ArrayBuffer

trait IpfsSupport {


  def ipfsClientOpt():Option[IpfsClient]


  def saveBufferToIpfs(f: Future[nodeLib.Buffer])(
    implicit ctx: ExecutionContext) =
    f.flatMap(
      b => ipfsClientOpt().map(_.futureAdd(b).map(Some(_))).getOrElse(Future{None}))


  def saveArrayBufferToIpfs(f: Future[ArrayBuffer])(
    implicit ctx: ExecutionContext) =
    saveBufferToIpfs(f.map(b => bufferMod.Buffer.from(b)))

  def saveStringToIpfs(s: String)(implicit ctx: ExecutionContext) =
    saveBufferToIpfs(
      Future { bufferMod.Buffer.from(s) }
    )



  def bufferToIpfs(buffer: nodeLib.Buffer)(implicit ctx: ExecutionContext) =
    ipfsClientOpt().map(_.futureAdd(buffer).map(l => Some(l.head.hash))).getOrElse(Future{None})

  def readIpfs(aHash: String)(implicit executionContext: ExecutionContext) =
   ipfsClientOpt().map(_.futureCat(aHash).map(Some(_))).getOrElse(Future{None})

  def readIpfsString(aHash: String)(
    implicit executionContext: ExecutionContext) =
    ipfsClientOpt().map(_.futureCatString(aHash).map(Some(_))).getOrElse(Future{None})


}
