package com.lyrx.pyramids

import com.lyrx.pyramids.ipfs.{ IpfsClient}
import com.lyrx.pyramids.pcrypto.{Crypto, CryptoTypes}

import scala.scalajs.js.typedarray.ArrayBuffer





case class Messages(messageOpt:Option[String], errorOpt:Option[String]  ) {

}


case class IpfsData(uploadOpt:Option[String],
                         pubKeysOpt:Option[String]
                        )

case class PyramidConfig(//distributedDir: DistributedDir,
                          symKeyOpt:Option[CryptoTypes.PyramidCryptoKey],
                         asymKeyOpt:Option[CryptoTypes.PyramidCryptoKeyPair],
                         signKeyOpt:Option[CryptoTypes.PyramidCryptoKeyPair],
                         messages:Messages ,
                         ipfsOpt:Option[ IpfsClient],
                         ipfsData: IpfsData
                        ) {


  def withUpload(s:String) =this.
    copy(ipfsData = ipfsData.
      copy(uploadOpt = Some(s)))

  def withPubKeys(s:String) =this.
    copy(ipfsData = ipfsData.
      copy(pubKeysOpt = Some(s)))



  def msg(s:String) = this.
    copy(
      messages=
        this.
          messages.
          copy(messageOpt = Some(s)))


  def error(s:String) = copy(
    messages=
      this.
        messages.
        copy(errorOpt = Some(s)))


}
