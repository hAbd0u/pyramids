package com.lyrx.pyramids

import org.scalajs.dom.File
import org.scalajs.dom.crypto.{CryptoKey, CryptoKeyPair}

import scala.scalajs.js.typedarray.ArrayBuffer






case class DistributedDir(data:Seq[EitherData],name:String){

  def `:+`(distributedData: DistributedData):DistributedDir = this.
    copy(data =
      this.data :+ Left(distributedData))

  def `:+`(distributedDir: DistributedDir):DistributedDir = this.
    copy(data =
      this.data :+ Right(distributedDir))

  def withSignature(b:ArrayBuffer):DistributedDir =this.copy(data = this.data.map( (data:EitherData) => data match {
    case Right(adir:DistributedDir) => data
    case Left(adata:DistributedData) => Left(adata.withSignature(b))
  }))

}



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


case class PyramidConfig(distributedDir: DistributedDir,
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

  def withSignature(b:ArrayBuffer)=this.copy(distributedDir=distributedDir.withSignature(b))

}
