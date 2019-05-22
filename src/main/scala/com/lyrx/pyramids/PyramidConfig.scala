package com.lyrx.pyramids

import org.scalajs.dom.File
import org.scalajs.dom.crypto.{CryptoKey, CryptoKeyPair}

import scala.scalajs.js.typedarray.ArrayBuffer



object PyramidConfig{

  type EitherData = Either[DistributedData,DistributedDir]

}





case class DistributedDir(data:Seq[PyramidConfig.EitherData],name:String)

case class DistributedData(
                            unencryptedOpt:Option[ArrayBuffer],
                           bufferOpt:Option[ArrayBuffer],
                            ivOpt:Option[ArrayBuffer],
                          signatures: Seq[ArrayBuffer]
                          ){
  def withSignature(b:ArrayBuffer) = this.copy(signatures = (signatures :+ b))
}


case class Messages(messageOpt:Option[String], errorOpt:Option[String]  ) {

}


case class PyramidConfig(distributedData: DistributedData,
                          symKeyOpt:Option[CryptoKey],
                         asymKeyOpt:Option[CryptoKeyPair],
                         signKeyOpt:Option[CryptoKeyPair],
                         messages:Messages  ) {

  def msg(s:String) = this.
    copy(
      messages=
        this.
          messages.
          copy(messageOpt = Some(s)))

  def withSignature(b:ArrayBuffer)=this.copy(distributedData=distributedData.withSignature(b))

}
