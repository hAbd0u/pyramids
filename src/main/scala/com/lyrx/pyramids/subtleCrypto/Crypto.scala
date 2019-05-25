package com.lyrx.pyramids.subtleCrypto

import org.scalajs.dom.crypto.{CryptoKey, CryptoKeyPair, JsonWebKey, KeyFormat, crypto}

import scala.concurrent.{ExecutionContext, Future}

object CryptoTypes{
  type PyramidCryptoKey = CryptoKey
  type PyramidCryptoKeyPair = CryptoKeyPair
  type JsonKeyPair = (JsonWebKey,JsonWebKey)
  type JSKeyPairOpt = Option[JsonKeyPair]
  type JSKeyOpt = Option[JsonWebKey]
  type AllJSKeysOpt = (JSKeyOpt,JSKeyPairOpt,JSKeyPairOpt)

}



trait Crypto {



  def exportCryptoKey(key: CryptoKey)
                (implicit ctx:ExecutionContext) = crypto.subtle.
    exportKey(KeyFormat.jwk,key).
    toFuture.map((a:Any)=>a.asInstanceOf[JsonWebKey])



  def exportKeys(keyPairOpt:Option[CryptoKeyPair])(implicit ctx:ExecutionContext)=keyPairOpt.
    map(keys=>exportCryptoKey(keys.publicKey).
      flatMap(pubKeyJsonWeb=>exportCryptoKey(
        keys.privateKey
      ).flatMap(privKeyJsonWeb=>Future{Some((privKeyJsonWeb,pubKeyJsonWeb))})
      )
    ).
    getOrElse(Future{None})





}
