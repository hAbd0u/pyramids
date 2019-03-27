package com.eternitas.lastwill

import com.eternitas.lastwill.cryptoo.{AsymCrypto, SymCrypto}
import org.scalajs.dom.crypto.{CryptoKey, CryptoKeyPair, JsonWebKey}

import scala.concurrent.{ExecutionContext, Future}
import scala.scalajs.js
import js.Dynamic.{literal => l}

case class PinataAuth(api: String, secretApi: String)

class Eternitas(
    val keyPairOpt: Option[CryptoKeyPair],
    val pinnataOpt: Option[PinataAuth],
    val keyOpt: Option[CryptoKey]
) {

  def withKeys()(implicit ctx: ExecutionContext) = {
    if (keyPairOpt.isEmpty)
      AsymCrypto
        .generateKeys()
        .map(
          key =>
            new Eternitas(
              keyPairOpt = Some(key),
              pinnataOpt = this.pinnataOpt,
              keyOpt = this.keyOpt
          ))
    else
      Future.successful(this)

  }

  def exportKeyPair()(implicit ctx: ExecutionContext) = keyPairOpt.map(
    key => AsymCrypto.eexportKey(key.publicKey)
  )

  def export2()()(implicit ctx: ExecutionContext) = {
    type FutureKeyExport = (Future[JsonWebKey],String)

    val fs:Seq[Option[FutureKeyExport]]
    = Seq(
      keyPairOpt.map(keyPair =>(AsymCrypto.eexportKey(keyPair.privateKey),"private")),
      keyPairOpt.map(keyPair =>  (AsymCrypto.eexportKey(keyPair.publicKey),"public")),
      keyOpt.map(key =>  (SymCrypto.eexportKey(key),"sym"))
    )

    Future {
      l()
    }.map((aDynamic: js.Dynamic) =>
      js.JSON.stringify(aDynamic: js.Any, null: js.Array[js.Any], 1: js.Any))
  }

  def export()(implicit ctx: ExecutionContext) =
    keyPairOpt
      .map(
        key =>
          AsymCrypto
            .eexportKey(key.publicKey)
            .map(publicJw =>
              AsymCrypto
                .eexportKey(key.privateKey)
                .map(privateJw => {
                  pinnataOpt
                    .map(
                      p =>
                        l(
                          "pinata" -> new Pinata(p).export(),
                          "asym" -> l("private" -> privateJw,
                                      "public" -> publicJw)
                      ))
                    .getOrElse(l(
                      "pinata" -> l(),
                      "asym" -> l("private" -> privateJw, "public" -> publicJw)
                    ))
                }))
            .flatten
      )
      .getOrElse(Future {
        pinnataOpt
          .map(
            p =>
              l(
                "pinata" -> new Pinata(p).export()
            ))
          .getOrElse(l())
      })
      .map((aDynamic: js.Dynamic) =>
        js.JSON.stringify(aDynamic: js.Any, null: js.Array[js.Any], 1: js.Any))

}
