package com.eternitas.lastwill

import com.eternitas.lastwill.axioss.Pinata
import com.eternitas.lastwill.cryptoo.{AsymCrypto, SymCrypto}
import org.scalajs.dom.crypto.{CryptoKey, CryptoKeyPair, JsonWebKey}

import scala.concurrent.{ExecutionContext, Future}
import scala.scalajs.js
import js.Dynamic.{literal => l}

case class PinataAuth(api: String, secretApi: String)


case class EncryptedPin(dataHash:String,ivHash:String)

class Eternitas(
    val keyPairOpt: Option[CryptoKeyPair],
    val pinnataOpt: Option[PinataAuth],
    val keyOpt: Option[CryptoKey],
    val pinDataOpt:Option[String]
) {

  def withPinData(pinDataHash: String) = new Eternitas(this.keyPairOpt,
    this.pinnataOpt,
    this.keyOpt,
    Some(pinDataHash) )


  def addKey(key:CryptoKey) = new Eternitas(keyPairOpt,pinnataOpt,Some(key),pinDataOpt)

  def addKeyPair(privateKey:CryptoKey,publicKey:CryptoKey) = new Eternitas(
    Some(js.Dictionary(
      "publicKey"->publicKey,
      "privateKey" -> privateKey
    ).asInstanceOf[CryptoKeyPair]),
    pinnataOpt = this.pinnataOpt,
    keyOpt=this.keyOpt,
    pinDataOpt=this.pinDataOpt
  )


  def withKeyPair()(implicit ctx: ExecutionContext) = {
    if (keyPairOpt.isEmpty)
      AsymCrypto
        .generateKeys()
        .map(
          key =>
            new Eternitas(
              keyPairOpt = Some(key),
              pinnataOpt = this.pinnataOpt,
              keyOpt = this.keyOpt,
              pinDataOpt=this.pinDataOpt))
    else
      Future.successful(this)
  }

  def withSymKey()(implicit ctx: ExecutionContext) = {
    if (keyOpt.isEmpty){
      val kf = SymCrypto
        .generateKey()
      kf.onComplete(t => {
       // t.failed.map(e=>println("Error generating symmetric key: " +e.getMessage))
       // t.map(key => println("Generated: " + key))
      })

        kf.map(
          key =>
            new Eternitas(
              keyPairOpt =this.keyPairOpt,
              pinnataOpt = this.pinnataOpt,
              keyOpt = Some(key),
              pinDataOpt=this.pinDataOpt
            ))}
    else
      Future.successful(this)
  }

  def withAllKeys()(implicit ctx: ExecutionContext) = withKeyPair().
    map(e=>e.withSymKey()).flatten



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
    map(p => new Pinata(p).export()).getOrElse(l("api" ->"","apisecret" ->""))}


  def exportPinFolder()(implicit ctx: ExecutionContext):Future[js.Dynamic] = Future{
    l("hash" -> pinDataOpt.getOrElse("").toString)
  }

  def export()(implicit ctx: ExecutionContext) ={
    Future.sequence(Seq(exportKeyPair(),exportKey(),exportPinata(),exportPinFolder())).
      map(s => l("asym" -> s(0),"sym" -> s(1),"pinata" -> s(2),"pinfolder" -> s(3)))
  }.map((aDynamic: js.Dynamic) =>
    js.JSON.stringify(aDynamic: js.Any, null: js.Array[js.Any], 1: js.Any))






}
