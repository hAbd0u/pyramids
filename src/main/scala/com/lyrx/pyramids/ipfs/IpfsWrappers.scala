package com.lyrx.pyramids.ipfs

import org.scalajs.dom.raw.File
import typings.nodeLib

import scala.scalajs.js
import scala.scalajs.js.annotation.{JSGlobal, JSImport}
import scala.scalajs.js.typedarray.{ArrayBuffer, Uint8Array}





@js.native
trait IPFSSFile extends js.Object{
  val path:String = js.native
  val hash:String = js.native
  val size:Int = js.native
}
@js.native
trait IPFSSError extends js.Object{

}

@js.native
@JSGlobal
class TextEncoder(utfLabel: js.UndefOr[String]) extends js.Object {
  def encode(buffer: String): Uint8Array = js.native
}

@js.native
@JSGlobal
class TextDecoder(utfLabel: js.UndefOr[String]) extends js.Object {
  def decode(buffer: Uint8Array): String = js.native
}




/*
@js.native
@JSImport("buffer/","Buffer")
object BufferObject extends js.Object {

  def from(s:CanBuffer):Buffer = js.native

}

 */

@js.native
@JSImport("ipfs-http-client",JSImport.Namespace)
object IpfsHttpClient extends js.Object {

  def apply(lit:js.Dynamic):IpfsClient = js.native

}


@js.native
trait IpfsClient extends js.Object {

  def add(content:nodeLib.Buffer,
          option:js.Dynamic,
          c: js.Function2[IPFSSError,js.Array[IPFSSFile],Unit]):Unit =
    js.native

  def cat(s:String):js.Promise[File] = js.native
}
