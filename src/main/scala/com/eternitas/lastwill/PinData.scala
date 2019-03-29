package com.eternitas.lastwill

import scala.scalajs.js

object PinData {

  @js.native
  trait Pinned extends js.Object {
    val `hash`:String = js.native
    val vc:String = js.native
  }

    @js.native
  trait PinningData extends js.Object {

    val data:js.Array[Pinned] = js.native

  }



}
