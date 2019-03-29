package com.eternitas.lastwill

import scala.scalajs.js
import scala.scalajs.js.UndefOr

object PinData {

  @js.native
  trait Pinned extends js.Object {
    val `hash`:UndefOr[String] = js.native
    val vc:UndefOr[String] = js.native
    val name:UndefOr[String] = js.native
    val `type`:UndefOr[String] = js.native
  }

    @js.native
  trait PinningData extends js.Object {

    val data:js.Array[Pinned] = js.native

  }



}
