package com.lyrx.pyramids

import com.lyrx.pyramids.ipfs.{ IpfsClient}
import com.lyrx.pyramids.pcrypto.{Crypto, CryptoTypes}

import scala.scalajs.js.typedarray.ArrayBuffer






case class DistributedDir(data:Seq[EitherData],name:String){

  def `:+`(distributedData: DistributedData):DistributedDir = this.
    copy(data =
      this.data :+ Left(distributedData))

  def `:+`(distributedDir: DistributedDir):DistributedDir = this.
    copy(data =
      this.data :+ Right(distributedDir))



}



case class DistributedData(
                            unencryptedOpt:Option[ArrayBuffer],
                           bufferOpt:Option[ArrayBuffer],
                            ivOpt:Option[ArrayBuffer],
                          signatures: Seq[ArrayBuffer]
                          ) {
}


case class Messages(messageOpt:Option[String], errorOpt:Option[String]  ) {

}


case class PyramidConfig(distributedDir: DistributedDir,
                          symKeyOpt:Option[CryptoTypes.PyramidCryptoKey],
                         asymKeyOpt:Option[CryptoTypes.PyramidCryptoKeyPair],
                         signKeyOpt:Option[CryptoTypes.PyramidCryptoKeyPair],
                         messages:Messages ,
                         ipfsOpt:Option[ IpfsClient]
                        ) {

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
