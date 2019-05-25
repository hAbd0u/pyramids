package com.lyrx.pyramids.ipfs

import com.lyrx.pyramids.pcrypto.CryptoTypes.{AllJSKeysOpt, JsonKeyPair, JsonWebKeyOptPair}
import com.lyrx.pyramids.pcrypto.{Crypto, WalletNative}
import com.lyrx.pyramids.{CanInstantiate, PimpedString, Pyramid, PyramidConfig, PyramidJSON}

import scala.scalajs.js
import js.Dynamic.{literal => l}
import scala.concurrent.{ExecutionContext, Future, Promise}
import scala.scalajs.js.Math
trait CanIpfs extends Crypto with PyramidJSON
{
  val pyramidConfig:PyramidConfig


  def initIpfs()(implicit executionContext: ExecutionContext) ={
    val promise:Promise[Ipfs] = Promise[Ipfs]
    val ipfs =new Ipfs(l("repo"  -> s"ipfs-${Math.random().toString()}" ))
    ipfs.
      onReadyOnce(()=>promise.success(ipfs)
    )
    promise.future.map(i=>new Pyramid(pyramidConfig.copy(ipfsOpt = Some(ipfs))))
  }




  def exportAllPublicKeys()(implicit ctx:ExecutionContext):Future[JsonWebKeyOptPair]
  =
   exportPublicKey(pyramidConfig.asymKeyOpt).
    flatMap(pubKeyOpt => exportPublicKey(pyramidConfig.signKeyOpt).map(signKeyOpt=>(pubKeyOpt,signKeyOpt))  )



  def publicKeyToBuffer()(implicit ctx:ExecutionContext) =
    exportAllPublicKeys().map(kp=>l(
      "asym" -> l("public" -> kp._1.getOrElse(null)),
      "sign" -> l("public" -> kp._2.getOrElse(null))

    ).asInstanceOf[WalletNative]).map(
      w=>new PimpedString(stringify(w)).toArrayBuffer().asInstanceOf[NodeBuffer]
    )

}
