package com.lyrx.pyramids.ipfs


import typings.nodeLib
import scala.scalajs.js
import js.annotation.{JSGlobal, JSImport, JSName}
import js.typedarray






@js.native
trait IPFSMetaData extends js.Object{
  val name:String = js.native
  val `type`:String = js.native
  val size:Int = js.native

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
class TextEncoder(utfLabel: js.UndefOr[String]= "utf-8" ) extends js.Object {
  def encode(buffer: String): typedarray.Uint8Array = js.native
}

@js.native
@JSGlobal
class TextDecoder(utfLabel: js.UndefOr[String] = "utf-8") extends js.Object {
  def decode(buffer: typedarray.Uint8Array): String = js.native
}



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


  @JSName("cat")
  def cat(s:String):js.Promise[nodeLib.Buffer] = js.native

  @JSName("cat")
  def catString(s:String):js.Promise[String] = js.native



}
