package com.lyrx.pyramids.ipfs

import typings.nodeLib
import typings.nodeLib.bufferMod

import scala.concurrent.{ExecutionContext, Future}
import scala.scalajs.js.typedarray.ArrayBuffer

trait IpfsSupport {


  def ipfsClientOpt():Option[IpfsClient]


  def iSaveBufferToIpfs(f: Future[nodeLib.Buffer])(
    implicit ctx: ExecutionContext) =
    f.flatMap(
      b => ipfsClientOpt().map(_.futureAdd(b).map(Some(_))).getOrElse(Future{None}))


  def iSaveArrayBufferToIpfs(f: Future[ArrayBuffer])(
    implicit ctx: ExecutionContext) =
    iSaveBufferToIpfs(f.map(b => bufferMod.Buffer.from(b)))

  def iSaveStringToIpfs(s: String)(implicit ctx: ExecutionContext) =
    iSaveBufferToIpfs(
      Future { bufferMod.Buffer.from(s) }
    )



  def iBufferToIpfs(buffer: nodeLib.Buffer)(implicit ctx: ExecutionContext) =
    ipfsClientOpt().map(_.futureAdd(buffer).map(Some(_))).getOrElse(Future{None})

  def iReadIpfs(aHash: String)(implicit executionContext: ExecutionContext) =
   ipfsClientOpt().map(_.futureCat(aHash).map(Some(_))).getOrElse(Future{None})

  def iReadIpfsString(aHash: String)(
    implicit executionContext: ExecutionContext) =
    ipfsClientOpt().map(_.futureCatString(aHash).map(Some(_))).getOrElse(Future{None})


}
