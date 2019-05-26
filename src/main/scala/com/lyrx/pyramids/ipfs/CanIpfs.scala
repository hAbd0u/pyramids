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
    promise.future.map(i=>new Pyramid(
      pyramidConfig.
        copy(
          ipfsOpt = Some(ipfs)
        ).msg("Connected to IPFS network!")))
  }

  def initIpfsAndPublishPublicKeys ()(implicit executionContext: ExecutionContext) = initIpfs().
    flatMap(_.publicKeysToIpfs())




  def exportAllPublicKeys()(implicit ctx:ExecutionContext):Future[JsonWebKeyOptPair]
  =
   exportPublicKey(pyramidConfig.asymKeyOpt).
    flatMap(pubKeyOpt => exportPublicKey(pyramidConfig.signKeyOpt).map(signKeyOpt=>(pubKeyOpt,signKeyOpt))  )



  def publicKeysToBuffer()(implicit ctx:ExecutionContext) =
    exportAllPublicKeys().map(kp=>l(
      "asym" -> l("public" -> kp._1.getOrElse(null)),
      "sign" -> l("public" -> kp._2.getOrElse(null))
    ).asInstanceOf[WalletNative]).map(
      w=>  BufferObject.from(stringify(w))
    )

  def publicKeysToIpfs()(implicit ctx:ExecutionContext) = publicKeysToBuffer().
    flatMap(n=>
      pyramidConfig.
        ipfsOpt.
        map(ipfs=>ipfs.
          futureAdd(n).map(Some(_)
        )).
        getOrElse(Future{None})).
    map(_.flatMap(_.headOption.map(_.hash))).
    map(_.map(s=>new Pyramid(pyramidConfig.msg(s"Published public keys: ${s}"))).
      getOrElse(new Pyramid(pyramidConfig)))

}
