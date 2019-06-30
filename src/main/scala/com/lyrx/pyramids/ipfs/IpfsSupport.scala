package com.lyrx.pyramids.ipfs

import typings.nodeLib
import typings.nodeLib.bufferMod

import scala.concurrent.{ExecutionContext, Future}
import scala.scalajs.js.typedarray.ArrayBuffer

trait IpfsSupport {


  def ipfsClient():IpfsClient


  def saveBufferToIpfs(f: Future[nodeLib.Buffer])(
    implicit ctx: ExecutionContext) =
    f.flatMap(
      b => ipfsClient().futureAdd(b))


  def saveArrayBufferToIpfs(f: Future[ArrayBuffer])(
    implicit ctx: ExecutionContext) =
    saveBufferToIpfs(f.map(b => bufferMod.Buffer.from(b)))

  def saveStringToIpfs(s: String)(implicit ctx: ExecutionContext) =
    saveBufferToIpfs(
      Future { bufferMod.Buffer.from(s) }
    )



  def bufferToIpfs(buffer: nodeLib.Buffer)(implicit ctx: ExecutionContext) =
    ipfsClient().futureAdd(buffer)

  def readIpfs(aHash: String)(implicit executionContext: ExecutionContext) =
   ipfsClient().futureCat(aHash)

  def readIpfsString(aHash: String)(
    implicit executionContext: ExecutionContext) =
    ipfsClient().futureCatString(aHash)


}
