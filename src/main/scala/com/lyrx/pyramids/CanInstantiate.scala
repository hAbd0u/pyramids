package com.lyrx.pyramids

import scala.scalajs.js

trait CanInstantiate {

  def instantiate[C <: js.Any](args:js.Any)(implicit tag: js.ConstructorTag[C]): C =
    (js.Dynamic.newInstance(tag.constructor)(args)).asInstanceOf[C]


  def instantiate[C <: js.Any](arg:String)(implicit tag: js.ConstructorTag[C]): C =
    (js.Dynamic.newInstance(tag.constructor)(arg)).asInstanceOf[C]


  def instantiate[C <: js.Any]()(implicit tag: js.ConstructorTag[C]): C =
    (js.Dynamic.newInstance(tag.constructor)()).asInstanceOf[C]

}
