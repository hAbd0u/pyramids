package com.eternitas.lastwill.ipfss

import scala.scalajs.js
import scala.scalajs.js.annotation.{JSGlobal, JSName}
import scala.scalajs.js.Dynamic.{literal => l}
import scala.scalajs.js.Math
import scala.scalajs.js.typedarray.ArrayBuffer

@js.native
@JSGlobal
class Ipfs extends js.Object{

  def once(s:String,c:js.Function0[Unit]):Unit = js.native
  def isOnline():Boolean = js.native
  def version():String = js.native

  def add(path:String, content:NodeBuffer, c: js.Function2[IPFSSError,js.Array[IPFSSFile],Unit]) = js.native

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
@JSGlobal
@JSName("Buffer")
object NodeBuffer extends js.Object{

}


@js.native
trait  NodeBuffer extends js.Object{

}



object IPFSS {

  implicit class PimpedIpfs(ipfs:Ipfs){

    def onReadyOnce(c:js.Function0[Unit]) = ipfs.once("ready",c)
  }

    lazy val node = instance()

  private def instantiate[C <: js.Any](args:js.Dynamic)(implicit tag: js.ConstructorTag[C]): C =
    (js.Dynamic.newInstance(tag.constructor)(args)).asInstanceOf[C]

  private def instance()={
    instantiate[Ipfs](l("repo"  -> s"ipfs-${Math.random().toString()}" ))
   // js.Dynamic.newInstance(js.constructorOf[IPFS].constructor)().asInstanceOf[IPFS]
  }

}
