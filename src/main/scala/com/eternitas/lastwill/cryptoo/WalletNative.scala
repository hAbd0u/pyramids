package com.eternitas.lastwill.cryptoo

import com.eternitas.lastwill.axioss.PinataData
import org.scalajs.dom.crypto.JsonWebKey

import scala.scalajs.js
import js.Dynamic.{literal => l}


@js.native
trait KeypairNative extends js.Object {
  val `private`: js.UndefOr[JsonWebKey] = js.native
  val `public` : js.UndefOr[JsonWebKey] = js.native

}
@js.native
trait SignkeyNative extends js.Object {
  val `sign`: js.UndefOr[JsonWebKey] = js.native

}


@js.native
trait PinFolder extends js.Object {
  val `hash`: js.UndefOr[String] = js.native
}

@js.native
trait PinDataListNative extends js.Object {
  val data: js.UndefOr[js.Array[PinDataNative]] = js.native
  val pubkey: js.UndefOr[JsonWebKey] = js.native
  val purpose: js.UndefOr[String] = js.native

}

@js.native
trait PinDataNative extends js.Object {
  val hash: js.UndefOr[String]  = js.native
  val vc: js.UndefOr[String] = js.native
  //val name :js.UndefOr[String] = js.native
  val `type` :js.UndefOr[String] = js.native

  val timeStamp : js.UndefOr[js.Date] = js.native

}

object WalletNative{
  implicit class PimpedPinDataListNative(p:PinDataListNative){

    def withPinData(n:PinDataNative) = {
           l("data" -> (p.data.getOrElse(js.Array[PinDataNative]()) :+ n)).asInstanceOf[PinDataListNative]
    }

  }
}


@js.native
trait PinataNative extends js.Object {
  val api: js.UndefOr[String] = js.native
  val apisecret: js.UndefOr[String] = js.native
}

@js.native
trait TitleNative extends js.Object {
  val text:js.UndefOr[String] = js.native
}


@js.native
trait WalletNative extends js.Object {
  val pinata:js.UndefOr[PinataNative] = js.native
  val asym:js.UndefOr[KeypairNative]  = js.native
  val sign:js.UndefOr[SignkeyNative]  = js.native
  val pinfolder:js.UndefOr[PinFolder] = js.native
  val sym:js.UndefOr[JsonWebKey] = js.native
  val title:js.UndefOr[TitleNative] = js.native
}
