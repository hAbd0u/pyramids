package com.lyrx.pyramids.ipfs

import scala.scalajs.js
import scala.scalajs.js.annotation.{JSGlobal, JSImport}
import scala.scalajs.js.typedarray.{ArrayBuffer, Uint8Array}


@js.native
@JSGlobal
class Ipfs(args:js.Dynamic) extends js.Object{

  def once(s:String,c:js.Function0[Unit]):Unit = js.native
  def isOnline():Boolean = js.native
  def version():String = js.native

  def add(content:Buffer, option:js.Dynamic, c: js.Function2[IPFSSError,js.Array[IPFSSFile],Unit]):Unit = js.native

}

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

@js.native
//@JSGlobal("Buffer")
@JSImport("buffer/","Buffer")
object BufferObject extends js.Object {

  def from(s:String):Buffer = js.native

}

@js.native
trait Buffer extends js.Object {

}