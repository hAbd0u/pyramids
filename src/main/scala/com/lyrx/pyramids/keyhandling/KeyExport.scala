package com.lyrx.pyramids.keyhandling

import com.lyrx.pyramids.PyramidConfig
import com.lyrx.pyramids.pcrypto.CryptoTypes.{AllJSKeysOpt, JsonKeyPair}
import com.lyrx.pyramids.pcrypto.{AsymetricCrypto, Crypto, SymetricCrypto, WalletNative}
import org.scalajs.dom.crypto.CryptoKey

import scala.concurrent.{ExecutionContext, Future}
import scala.scalajs.js.Dynamic.{literal => l}



trait KeyExport extends  SymetricCrypto with AsymetricCrypto {

  val pyramidConfig: PyramidConfig



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
      "asym" -> ko._2.map((kp:JsonKeyPair)=>l("private" -> kp._1,"public" -> kp._2)).getOrElse(null),
      "sign" -> ko._3.map((kp:JsonKeyPair)=>l("private" -> kp._1,"public" -> kp._2)).getOrElse(null)
    ).asInstanceOf[WalletNative])



  def exportSymKeyEncrypted(key:CryptoKey)(implicit ctx:ExecutionContext) = exportSymKey().
    flatMap(_.map(jk=>
      encryptString(key,stringify(jk)).map(Some(_))).getOrElse(Future{None}))


  def exportSymKeyDefault()(implicit ctx:ExecutionContext) =
    pyramidConfig.asymKeyOpt.map(kp=>exportSymKeyEncrypted(kp.publicKey)).getOrElse(Future{None})






}
