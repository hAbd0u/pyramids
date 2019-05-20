package com.lyrx.pyramids.cryptography

import com.lyrx.pyramids.PyramidConfig
import org.scalajs.dom.crypto.{CryptoKeyPair, JsonWebKey}

import scala.concurrent.{ExecutionContext, Future}
import scala.scalajs.js
import scala.scalajs.js.Dynamic.{literal => l}



trait KeyExport extends  SymetricCrypto with AsymetricCrypto {

  val pyramidConfig: PyramidConfig





  type JSKeyPairOpt = Option[(JsonWebKey,JsonWebKey)]
  type JSKeyOpt = Option[JsonWebKey]
  type AllJSKeysOpt = (JSKeyOpt,JSKeyPairOpt,JSKeyPairOpt)


  def exportSymKey()(implicit ctx:ExecutionContext)=pyramidConfig.
    symKeyOpt.
    map(k=>exportCryptoKey(k).
      map(jw=>Some(jw))).
    getOrElse(Future{None})


  def exportASymKeys()(implicit ctx:ExecutionContext)=exportKeys(pyramidConfig.
    asymKeyOpt)

  def exportSignKeys()(implicit ctx:ExecutionContext)=exportKeys(pyramidConfig.
    signKeyOpt)


  def exportAllKeys()(implicit ctx:ExecutionContext)
  = exportSymKey().
    flatMap(symKeyOpt=>exportASymKeys().map(keysOpt=>(symKeyOpt,keysOpt))).
    flatMap(keysOpt=>exportSignKeys().map(keysOpt2=>(keysOpt._1,keysOpt._2,keysOpt2))).
    map( (ko:AllJSKeysOpt)=>l(
      "sym"->ko._1.getOrElse(null),
      "asym" -> ko._2.map((kp:(JsonWebKey,JsonWebKey))=>l("private" -> kp._1,"public" -> kp._2)).getOrElse(null),
      "sign" -> ko._3.map((kp:(JsonWebKey,JsonWebKey))=>l("private" -> kp._1,"public" -> kp._2)).getOrElse(null)
    ).asInstanceOf[WalletNative])

  def exportKeys(keyPairOpt:Option[CryptoKeyPair])(implicit ctx:ExecutionContext)=keyPairOpt.
    map(keys=>exportCryptoKey(keys.publicKey).
      flatMap(pubKeyJsonWeb=>exportCryptoKey(
        keys.privateKey
      ).flatMap(privKeyJsonWeb=>Future{Some((privKeyJsonWeb,pubKeyJsonWeb))})
      )
    ).
    getOrElse(Future{None})



}
