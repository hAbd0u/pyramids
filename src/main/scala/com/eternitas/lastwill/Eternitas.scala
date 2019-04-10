package com.eternitas.lastwill

import com.eternitas.lastwill.axioss.Pinata
import com.eternitas.lastwill.cryptoo.{AsymCrypto, PinFolder, SymCrypto, WalletNative}
import org.scalajs.dom.crypto.{CryptoKey, CryptoKeyPair, JsonWebKey}

import scala.concurrent.{ExecutionContext, Future}
import scala.scalajs.js
import js.Dynamic.{literal => l}

case class AllCredentials(
                           pinataApi: Option[String],
                           pinataApiSecret: Option[String],
                           stampdApi:Option[String],
                           stampdApiSecret:Option[String])


case class EncryptedPin(dataHash:String,ivHash:String)

class Eternitas(
                 val keyPairOpt: Option[CryptoKeyPair],
                 val allAuth: Option[AllCredentials],
                 val keyOpt: Option[CryptoKey],
                 val pinDataOpt:Option[String],
                 val signKeyOpt: Option[CryptoKey],
                 val titleOpt: Option[String],
                 val signKeyPairOpt: Option[CryptoKeyPair]
) {

  def withPinData(pinFolderOr:js.UndefOr[PinFolder] )(implicit userFeedback: UserFeedback) = {
    pinFolderOr.map(f=>f.`hash`.map(pinDataHash=>{
      new Eternitas(
      this.keyPairOpt,
      this.allAuth,
      this.keyOpt,
      Some(pinDataHash),
        this.signKeyOpt,
        this.titleOpt,this.signKeyPairOpt)})).
      flatten.
      getOrElse(new Eternitas(
        this.keyPairOpt,
        this.allAuth,
        this.keyOpt,
        None,
        this.signKeyOpt,
        this.titleOpt,this.signKeyPairOpt
    ))
  }

  def withPinDataHash(s:String) = new Eternitas(
    this.keyPairOpt,
    this.allAuth,
    this.keyOpt,
    Some(s),
    this.signKeyOpt,
    this.titleOpt,this.signKeyPairOpt)

  def withoutPinDataHash() = new Eternitas(
    this.keyPairOpt,
    this.allAuth,
    this.keyOpt,
    None,
    this.signKeyOpt,
    this.titleOpt,this.signKeyPairOpt)




  def addKey(key:CryptoKey) = new Eternitas(
    keyPairOpt,
    allAuth,Some(key),
    pinDataOpt,
    this.signKeyOpt,
    this.titleOpt,this.signKeyPairOpt)

  def addKeyPair(privateKey:CryptoKey,publicKey:CryptoKey) = new Eternitas(
    Some(js.Dictionary(
      "publicKey"->publicKey,
      "privateKey" -> privateKey
    ).asInstanceOf[CryptoKeyPair]),
    allAuth = this.allAuth,
    keyOpt=this.keyOpt,
    pinDataOpt=this.pinDataOpt,
    this.signKeyOpt,
    this.titleOpt,this.signKeyPairOpt
  )
  def addSignKeyPair(privateKey:CryptoKey,publicKey:CryptoKey) = {

    //println("Adding sign key pair: " + privateKey)
    new Eternitas(

   keyPairOpt=this.keyPairOpt,
    allAuth = this.allAuth,
    keyOpt=this.keyOpt,
    pinDataOpt=this.pinDataOpt,
    this.signKeyOpt,
    this.titleOpt,
    Some(js.Dictionary(
      "publicKey"->publicKey,
      "privateKey" -> privateKey
    ).asInstanceOf[CryptoKeyPair])
  )}






  def withKeyPair()(implicit ctx: ExecutionContext) = {
    if (keyPairOpt.isEmpty){
      AsymCrypto
        .generateKeys()
        .map(
          key =>
            new Eternitas(
              keyPairOpt = Some(key),
              allAuth = this.allAuth,
              keyOpt = this.keyOpt,
              pinDataOpt=this.pinDataOpt,
              this.signKeyOpt,
              this.titleOpt,this.signKeyPairOpt),
        )}
    else
      Future.successful(this)
  }


  def importTitle(walletNative: WalletNative) = {
    new Eternitas(
      keyPairOpt =this.keyPairOpt,
      allAuth = this.allAuth,
      keyOpt = this.keyOpt,
      pinDataOpt=this.pinDataOpt,
      this.signKeyOpt,
      Some(
        walletNative.
          title.
          map(t => t.text.map(s=>s)).
          flatten.
          getOrElse("[UNTITLED]")),this.signKeyPairOpt
     )
  }

  def withSignKeyPair()(implicit ctx: ExecutionContext) = {
    if (signKeyPairOpt.isEmpty)
      AsymCrypto
        .generateSignKeys()
        .map(
          keys =>
            new Eternitas(
              keyPairOpt =this.keyPairOpt,
              allAuth = this.allAuth,
              keyOpt = this.keyOpt,
              pinDataOpt=this.pinDataOpt,
              this.signKeyOpt,
              this.titleOpt,
              Some(keys)))
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
              allAuth = this.allAuth,
              keyOpt = Some(key),
              pinDataOpt=this.pinDataOpt,
              this.signKeyOpt,
              this.titleOpt,this.signKeyPairOpt
            ))}
    else
      Future.successful(this)
  }

  def withAllKeys()(implicit ctx: ExecutionContext) = withKeyPair().
    map(e=>e.withSymKey().map(e2=>e2.withSignKeyPair().map(e3=>e.withSignKeyPair()))).flatten.flatten.flatten



  def exportKeyPair()(implicit ctx: ExecutionContext):Future[js.Dynamic] = exportKeyPairOpt(keyPairOpt)
  def exportSignKeyPair()(implicit ctx: ExecutionContext):Future[js.Dynamic] = exportKeyPairOpt(signKeyPairOpt)


  def exportKeyPairs()(implicit ctx: ExecutionContext):Future[js.Dynamic] = exportKeyPairOpt(keyPairOpt).
    map(d=>{
      exportSignKeyPair()
      d
    })

  def exportKeyPairOpt(aKeyPairOpt:Option[CryptoKeyPair])(implicit ctx: ExecutionContext):Future[js.Dynamic] = aKeyPairOpt
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

  def exportCredentials()()(implicit ctx: ExecutionContext):Future[js.Dynamic] = Future {allAuth.
    map(p => new Pinata(p).export()).getOrElse(
    l("pinataApi" ->"",
      "pinataApiSecret" ->"",
      "stampdApi" -> "",
      "stampdApiSecret" -> ""
    ))}


  def exportPinFolder()(implicit ctx: ExecutionContext):Future[js.Dynamic] = Future{
    l("hash" -> pinDataOpt.getOrElse("").toString)
  }

  def export()(implicit ctx: ExecutionContext) ={
    Future.sequence(
      Seq(exportKeyPair(),
        exportKey(),
        exportCredentials(),
        exportPinFolder(),
        exportSignKeyPair(),
        Future{titleOpt.map(t=>l("text" -> t)).getOrElse(l())}
      )).
      map(s => l("asym" -> s(0),
        "sym" -> s(1),
        "credentials" -> s(2),
        "pinfolder" -> s(3),
        "sign" ->s(4),
        "title" -> s(5)
      ))
  }.map((aDynamic: js.Dynamic) =>
    Eternitas.stringify(aDynamic: js.Any))





}

object Eternitas{
  def stringify(aAny:js.Any) = js.JSON.stringify(aAny: js.Any, null: js.Array[js.Any], 1: js.Any)

}


