package com.eternitas.lastwill

import org.scalajs.dom.crypto.CryptoKeyPair

import scala.concurrent.{ExecutionContext, Future}
import scala.scalajs.js




case class Pinnata(api:String,secretApi:String)

class Eternitas(val keysOpt:Option[CryptoKeyPair] =None) {



  def withKeys()(implicit ctx:ExecutionContext) = {
    if(keysOpt.isEmpty) Encrypt.generateKeys().map(key=>new Eternitas(keysOpt=Some(key)))
    else
      Future.successful(new Eternitas(keysOpt=Some(keysOpt.get)))

  }


  def exportKeyJWKPublic()(implicit ctx:ExecutionContext) = keysOpt.map(
    key=>Encrypt.eexportKey(key.publicKey)
  )

  def export()(implicit ctx:ExecutionContext) = keysOpt.map(
    key=>Encrypt
      .eexportKey(key.publicKey)
      .map(publicJw=>
        Encrypt.
          eexportKey(key.privateKey).
          map(privateJw=>js.Dynamic.literal(
            "private" ->privateJw,
            "public" -> publicJw)
          )).flatten
  ).
    getOrElse(Future{js.Dynamic.literal("empty" -> true)}).
    map(ee=>js.JSON.stringify(ee:js.Any,null:js.Array[js.Any],1:js.Any))

}

