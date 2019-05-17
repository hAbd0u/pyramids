package com.lyrx.pyramids.cryptography

import org.scalajs.dom.crypto.{CryptoKey, JsonWebKey, KeyFormat, crypto}

import scala.concurrent.ExecutionContext

trait Crypto {


  def exportCryptoKey(key: CryptoKey)
                (implicit ctx:ExecutionContext) = crypto.subtle.
    exportKey(KeyFormat.jwk,key).
    toFuture.map((a:Any)=>a.asInstanceOf[JsonWebKey])








}
