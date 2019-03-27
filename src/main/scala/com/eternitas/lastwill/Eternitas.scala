package com.eternitas.lastwill

import com.eternitas.lastwill.cryptoo.AsymCrypto
import org.scalajs.dom.crypto.CryptoKeyPair

import scala.concurrent.{ExecutionContext, Future}
import scala.scalajs.js
import js.Dynamic.{literal => l}



case class PinataAuth(api:String,secretApi:String)

class Eternitas(
                 val keysOpt:Option[CryptoKeyPair],
                 val pinnataOpt:Option[PinataAuth]

               ) {



  def withKeys()(implicit ctx:ExecutionContext) = {
    if(keysOpt.isEmpty) AsymCrypto.generateKeys().map(key=>new Eternitas(
      keysOpt=Some(key),
      pinnataOpt = this.pinnataOpt))
    else
      Future.successful(this)

  }


  def exportKeyJWKPublic()(implicit ctx:ExecutionContext) = keysOpt.map(
    key=>AsymCrypto.eexportKey(key.publicKey)
  )

  def export()(implicit ctx:ExecutionContext) = keysOpt.map(
    key=>AsymCrypto
      .eexportKey(key.publicKey)
      .map(publicJw=> AsymCrypto.
          eexportKey(key.privateKey).
          map(privateJw=>{
            pinnataOpt.map(p=> l(
                "pinata" -> new Pinata(p).export(),
                "asym" -> l("private" -> privateJw , "public" -> publicJw)
            )).getOrElse(l(
              "pinata" -> l(),
              "asym" -> l("private" -> privateJw , "public" -> publicJw)
            ))
          }
          )
      ).flatten
  ).
    getOrElse( Future{pinnataOpt.map(p=> l(
          "pinata" -> new Pinata(p).export()

      )).getOrElse(l())}
    ).map((aDynamic:js.Dynamic)=>js.JSON.stringify(aDynamic:js.Any,null:js.Array[js.Any],1:js.Any))



}

