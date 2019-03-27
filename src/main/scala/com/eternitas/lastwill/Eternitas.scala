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



  def exportKeyPair()(implicit ctx: ExecutionContext):Future[js.Dynamic] = keyPairOpt
      .map(
        keyPair =>
          AsymCrypto
            .eexportKey(keyPair.publicKey)
            .map(publicJw =>
              AsymCrypto
                .eexportKey(keyPair.privateKey)
                .map(privateJw => l("private" -> privateJw, "public" -> publicJw))
                )
            .flatten
      ).getOrElse(Future{l()})


  def exportKey()()(implicit ctx: ExecutionContext):Future[js.Dynamic] = keyOpt
    .map(
      key =>
        SymCrypto.eexportKey(key).map(_.asInstanceOf[js.Dynamic]))
    .getOrElse(Future{l()})

  def exportPinata()()(implicit ctx: ExecutionContext):Future[js.Dynamic] = Future {pinnataOpt.
    map(p => new Pinata(p).export()).getOrElse(l())}



  def export2()(implicit ctx: ExecutionContext) ={
    Future.sequence(Seq(exportKeyPair(),exportKey(),exportPinata())).
      map(s => l("asym" -> s(0)))
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
