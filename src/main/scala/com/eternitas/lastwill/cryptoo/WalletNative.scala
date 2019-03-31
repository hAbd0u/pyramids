package com.eternitas.lastwill.cryptoo

import com.eternitas.lastwill.axioss.PinataData
import org.scalajs.dom.crypto.JsonWebKey

import scala.scalajs.js


@js.native
trait KeypairNative extends js.Object {
  val `private`: js.UndefOr[JsonWebKey] = js.native
  val `public` : js.UndefOr[JsonWebKey] = js.native

}
@js.native
trait PinFolder extends js.Object {
  val `hash`: js.UndefOr[String] = js.native
}

@js.native
trait PinataNative extends js.Object {
  val api: js.UndefOr[String] = js.native
  val apisecret: js.UndefOr[String] = js.native
}


@js.native
trait WalletNative extends js.Object {
  val pinata:js.UndefOr[PinataNative] = js.native
  val asym:js.UndefOr[KeypairNative]  = js.native
  val pinFolder:js.UndefOr[PinFolder] = js.native
}
