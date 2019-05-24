package com.lyrx.pyramids.ipfs

import scala.scalajs.js
import scala.scalajs.js.annotation.JSGlobal


@js.native
@JSGlobal
class Ipfs(args:js.Dynamic) extends js.Object{

  def once(s:String,c:js.Function0[Unit]):Unit = js.native
  def isOnline():Boolean = js.native
  def version():String = js.native

  def add(path:String, content:NodeBuffer, c: js.Function2[IPFSSError,js.Array[IPFSSFile],Unit]):Unit = js.native

}

@js.native
trait IPFSSFile extends js.Object{

  val content:NodeBuffer = js.native
  val path:String = js.native
  val size:js.UndefOr[Int] = js.native
}
@js.native
trait IPFSSError extends js.Object{

}

@js.native
@JSGlobal("Buffer")
object NodeBuffer extends js.Object{

}


@js.native
trait  NodeBuffer extends js.Object{

}




object IpfsWrappers {

}
