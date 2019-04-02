package com.eternitas.lastwill

import com.eternitas.lastwill.axioss.Pinata
import com.eternitas.lastwill.cryptoo.{AsymCrypto, PinFolder, SymCrypto}
import org.scalajs.dom.crypto.{CryptoKey, CryptoKeyPair, JsonWebKey}

import scala.concurrent.{ExecutionContext, Future}
import scala.scalajs.js
import js.Dynamic.{literal => l}

case class PinataAuth(api: String, secretApi: String)


case class EncryptedPin(dataHash:String,ivHash:String)

class Eternitas(
                 val keyPairOpt: Option[CryptoKeyPair],
                 val pinataAuth: Option[PinataAuth],
                 val keyOpt: Option[CryptoKey],
                 val pinDataOpt:Option[String]
) {

  def withPinData(pinFolderOr:js.UndefOr[PinFolder] )(implicit userFeedback: UserFeedback) = {
    pinFolderOr.map(f=>f.`hash`.map(pinDataHash=>{
      userFeedback.logString(s"Found user data: ${pinDataHash}")
      new Eternitas(
      this.keyPairOpt,
      this.pinataAuth,
      this.keyOpt,
      Some(pinDataHash))})).
      flatten.
      getOrElse(new Eternitas(
        this.keyPairOpt,
        this.pinataAuth,
        this.keyOpt,
        None))
  }

  def withPinDataHash(s:String) = new Eternitas(
    this.keyPairOpt,
    this.pinataAuth,
    this.keyOpt,
    Some(s))



  def addKey(key:CryptoKey) = new Eternitas(keyPairOpt,pinataAuth,Some(key),pinDataOpt)

  def addKeyPair(privateKey:CryptoKey,publicKey:CryptoKey) = new Eternitas(
    Some(js.Dictionary(
      "publicKey"->publicKey,
      "privateKey" -> privateKey
    ).asInstanceOf[CryptoKeyPair]),
    pinataAuth = this.pinataAuth,
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
              pinataAuth = this.pinataAuth,
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
              pinataAuth = this.pinataAuth,
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

  def exportPinata()()(implicit ctx: ExecutionContext):Future[js.Dynamic] = Future {pinataAuth.
    map(p => new Pinata(p).export()).getOrElse(l("api" ->"","apisecret" ->""))}


  def exportPinFolder()(implicit ctx: ExecutionContext):Future[js.Dynamic] = Future{
    l("hash" -> pinDataOpt.getOrElse("").toString)
  }

  def export()(implicit ctx: ExecutionContext) ={
    Future.sequence(Seq(exportKeyPair(),exportKey(),exportPinata(),exportPinFolder())).
      map(s => l("asym" -> s(0),"sym" -> s(1),"pinata" -> s(2),"pinfolder" -> s(3)))
  }.map((aDynamic: js.Dynamic) =>
    Eternitas.stringify(aDynamic: js.Any))





}

object Eternitas{
  def stringify(aAny:js.Any) = js.JSON.stringify(aAny: js.Any, null: js.Array[js.Any], 1: js.Any)

}


