package com.eternitas.lastwill.ipfss

import scala.scalajs.js
import scala.scalajs.js.annotation.JSGlobal

import scala.scalajs.js.Dynamic.{literal => l}

@js.native
@JSGlobal
class Ipfs extends js.Object{

}


object IPFSS {

  def instantiate[C <: js.Any](args:js.Dynamic)(implicit tag: js.ConstructorTag[C]): C =
    (js.Dynamic.newInstance(tag.constructor)(args)).asInstanceOf[C]

  def instance()={
    instantiate[Ipfs](l())
   // js.Dynamic.newInstance(js.constructorOf[IPFS].constructor)().asInstanceOf[IPFS]
  }

}
