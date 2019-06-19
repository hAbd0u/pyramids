package com.lyrx.pyramids.pcrypto

import com.lyrx.pyramids.temporal.TemporalCredentials
import org.scalajs.dom.crypto.JsonWebKey

import scala.scalajs.js


@js.native
trait KeypairNative extends js.Object {
  val `private`: js.UndefOr[JsonWebKey] = js.native
  val `public` : js.UndefOr[JsonWebKey] = js.native
}

@js.native
trait Wallets extends js.Object {

  val stellar:js.UndefOr[String] = js.native
}

@js.native
trait WalletNative extends js.Object {
  val sym:js.UndefOr[JsonWebKey] = js.native
  val asym:js.UndefOr[KeypairNative] = js.native
  val sign:js.UndefOr[KeypairNative] = js.native
  val temporal:js.UndefOr[TemporalCredentials] = js.native
  val wallets:js.UndefOr[Wallets] = js.native
}
