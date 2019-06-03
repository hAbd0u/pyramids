package com.lyrx.pyramids.ipfs

import com.lyrx.pyramids.pcrypto.CryptoTypes.JsonWebKeyOptPair
import com.lyrx.pyramids.pcrypto.{Crypto, WalletNative}
import com.lyrx.pyramids.{Pyramid, PyramidConfig, PyramidJSON}

import scala.concurrent.{ExecutionContext, Future}
import scala.scalajs.js.Dynamic.{literal => l}
import typings.stdLib.ReadableStream

trait CanIpfs extends Crypto with PyramidJSON {
  val pyramidConfig: PyramidConfig

  def initIpfs()(implicit executionContext: ExecutionContext) = Future {
    new Pyramid(
      pyramidConfig
        .copy(
          ipfsOpt = Some(
            IpfsHttpClient(
              l(
                "host" -> "ipfs.infura.io",
                "port" -> 5001,
                "protocol" -> "https"
              )))
        )
        .msg("Connected to IPFS network!"))
  }

  def initIpfsAndPublishPublicKeys()(
      implicit executionContext: ExecutionContext) =
    initIpfs().flatMap(_.publicKeysToIpfs())

  def exportAllPublicKeys()(
      implicit ctx: ExecutionContext): Future[JsonWebKeyOptPair] =
    exportPublicKey(pyramidConfig.asymKeyOpt).flatMap(pubKeyOpt =>
      exportPublicKey(pyramidConfig.signKeyOpt).map(signKeyOpt =>
        (pubKeyOpt, signKeyOpt)))

  def publicKeysToBuffer()(implicit ctx: ExecutionContext) =
    exportAllPublicKeys()
      .map(
        kp =>
          l(
            "asym" -> l("public" -> kp._1.getOrElse(null)),
            "sign" -> l("public" -> kp._2.getOrElse(null))
          ).asInstanceOf[WalletNative])
      .map(
        w => BufferObject.from(stringify(w))
      )

  def publicKeysToIpfs()(implicit ctx: ExecutionContext) =
    publicKeysToBuffer()
      .flatMap(
        n =>
          pyramidConfig.ipfsOpt
            .map(ipfs => ipfs.futureAdd(n).map(Some(_)))
            .getOrElse(Future { None }))
      .map(_.flatMap(_.headOption.map(_.hash)))
      .map(_.map(s =>
        new Pyramid(pyramidConfig.
          copy(pubKeysOpt = Some(s)).
          msg(s"Oh Pharao, we have published your divine signature!")))
        .getOrElse(new Pyramid(pyramidConfig)))

  def bufferToIpfs(buffer: Buffer)(implicit ctx: ExecutionContext) =
    pyramidConfig.ipfsOpt
      .map(_.futureAdd(buffer).map(l => Some(l.head.hash)))
      .getOrElse(Future { None })

}
