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

object ETConfig{
  def empty()=ETConfig( NamedKeyPair(None,None),None,None,None, None,None,NamedKeyPair(None,None),None)
}
case class ETConfig(
                     val namedKeyPair: NamedKeyPair,
                     val allAuth: Option[AllCredentials],
                     val keyOpt: Option[CryptoKey],
                     val pinDataOpt:Option[String],
                     val signKeyOpt: Option[CryptoKey],
                     val titleOpt: Option[String],
                     val signKeyPair: NamedKeyPair,
                     val signatureOpt:Option[String]
                   )


case class NamedKeyPair(nameOpt:Option[String],keyPairOpt: Option[CryptoKeyPair])





class Eternitas(val config:ETConfig) {

  def withPinData(pinFolderOr:js.UndefOr[PinFolder] )(implicit userFeedback: UserFeedback) = {
    pinFolderOr.map(f=>f.`hash`.map(pinDataHash=>{
      new Eternitas(config.copy(pinDataOpt = Some(pinDataHash) ))})).
      flatten.
      getOrElse(new Eternitas(config.copy(pinDataOpt = None)))
  }

  def withPinDataHash(s:String) = new Eternitas(config.copy(pinDataOpt =  Some(s)))


  def withoutPinDataHash() = new Eternitas(config.copy(pinDataOpt =   None))




  def addKey(key:CryptoKey) = new Eternitas( config.copy(keyOpt = Some(key)))


  def addKeyPair(privateKey:CryptoKey,publicKey:CryptoKey) = new Eternitas(config.copy(namedKeyPair = NamedKeyPair(None,Some(js.Dictionary(
    "publicKey"->publicKey,
    "privateKey" -> privateKey
  ).asInstanceOf[CryptoKeyPair])) ))


  def addSignKeyPair(privateKey:CryptoKey,publicKey:CryptoKey,nameOpt:Option[String]) = new Eternitas(config.copy(signKeyPair = NamedKeyPair(nameOpt,Some(js.Dictionary(
    "publicKey"->publicKey,
    "privateKey" -> privateKey
  ).asInstanceOf[CryptoKeyPair]))))










  def withKeyPair()(implicit ctx: ExecutionContext) = {
    if (config.namedKeyPair.keyPairOpt.isEmpty)
      AsymCrypto
        .generateKeys()
        .map(
          key =>
            new Eternitas( config.copy(namedKeyPair = NamedKeyPair(None,Some(key)))))

    else
      Future.successful(this)
  }


  def importTitle(walletNative: WalletNative) = {
    new Eternitas(config.copy(titleOpt = Some(
      walletNative.
        title.
        map(t => t.text.map(s=>s)).
        flatten.
        getOrElse("[UNTITLED]"))))
  }

  def withSignKeyPair()(implicit ctx: ExecutionContext) = {
    if (config.signKeyPair.keyPairOpt.isEmpty)
      AsymCrypto
        .generateSignKeys()
        .map(
          keys =>
            new Eternitas(config.copy(signKeyPair = NamedKeyPair(None,Some(keys)))))
    else
      Future.successful(this)
  }

  def withSymKey()(implicit ctx: ExecutionContext) = {
    if (config.keyOpt.isEmpty){
      val kf = SymCrypto
        .generateKey()
      kf.onComplete(t => {
        t.failed.map(e=>println("Error generating symmetric key: " +e.getMessage))
        //t.map(key => println("Generated symmetric key: " + key))
      })

        kf.map(
          key => new Eternitas(config.copy(keyOpt = Some(key))))
             }
    else
      Future.successful(this)
  }

  def withAllKeys()(implicit ctx: ExecutionContext) = withKeyPair().
    map(e=>e.withSymKey().map(e2=>e2.withSignKeyPair())).flatten.flatten



  def exportKeyPair()(implicit ctx: ExecutionContext):Future[js.Dynamic] = exportKeyPairOpt(config.namedKeyPair.keyPairOpt)
  def exportSignKeyPair()(implicit ctx: ExecutionContext):Future[js.Dynamic] = exportKeyPairOpt(config.signKeyPair.keyPairOpt)


  def exportKeyPairs()(implicit ctx: ExecutionContext):Future[js.Dynamic] = exportKeyPairOpt(config.namedKeyPair.keyPairOpt).
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





  def exportKey()()(implicit ctx: ExecutionContext):Future[js.Dynamic] = config.keyOpt
    .map(
      key =>
        SymCrypto.eexportKey(key).map(_.asInstanceOf[js.Dynamic]))
    .getOrElse(Future{l()})

  def exportCredentials()()(implicit ctx: ExecutionContext):Future[js.Dynamic] = Future {config.allAuth.
    map(p => new Pinata(p).export()).getOrElse(
    l("pinataApi" ->"",
      "pinataApiSecret" ->"",
      "stampdApi" -> "",
      "stampdApiSecret" -> ""
    ))}


  def exportPinFolder()(implicit ctx: ExecutionContext):Future[js.Dynamic] = Future{
    l("hash" -> config.pinDataOpt.getOrElse("").toString)
  }

  def export()(implicit ctx: ExecutionContext) ={
    Future.sequence(
      Seq(exportKeyPair(),
        exportKey(),
        exportCredentials(),
        exportPinFolder(),
        exportSignKeyPair(),
        Future{config.titleOpt.map(t=>l("text" -> t)).getOrElse(l())}
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


