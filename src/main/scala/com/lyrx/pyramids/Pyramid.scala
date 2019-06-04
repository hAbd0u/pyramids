package com.lyrx.pyramids

import com.lyrx.pyramids.keyhandling._
import com.lyrx.pyramids.ipfs.CanIpfs
import org.scalajs.dom.raw.File
import com.lyrx.pyramids.jszip._
import com.lyrx.pyramids.pcrypto.{Encrypted, EncryptedData}
import typings.jszipLib.jszipMod.JSZip

import scala.concurrent.{ExecutionContext, Future}
import typings.stdLib.Blob


object  Pyramid{
  def apply()=new Pyramid(PyramidConfig(
    None,
    None,
    None,
    Messages(Some("Welcome to your Pyramid!"),
      None),
    None,
    IpfsData(None,None)))
}

class Pyramid(override val pyramidConfig: PyramidConfig)
  extends KeyCreation
    with KeyExport
    with KeyImport
  with DownloadWallet
  with UploadWallet
  with Encryption
  with CanIpfs {

def msg(s:String) = new Pyramid(this.pyramidConfig.msg(s))



  def uploadZip(f:File)(implicit executionContext:ExecutionContext)=
    zipEncrypt(f)
    .flatMap(_.dump())
    .flatMap(b => bufferToIpfs(b))
    .map(
      os =>
        os.map(s => pyramidConfig.
        withUpload(s).
          msg(s"Uploaded ${f.name}")
        ).getOrElse(pyramidConfig))


  def downloadZip(aHash:String)
              (implicit executionContext:ExecutionContext) =
    readIpfs(aHash).flatMap(aFuture=>aFuture.
      map(aFile=>zipInstance().
        loadAsync(aFile.asInstanceOf[Blob]).
      toFuture.map(Some(_))
      ).getOrElse(Future{None}))


  def downloadEncrypted(aHash:String)
                 (implicit executionContext:ExecutionContext) =
    downloadZip(aHash).flatMap(
      (o:Option[JSZip])=> o.map(z=>z.toEncrypted()
      ).getOrElse(Future{EncryptedData()}))




  def download()
                       (implicit executionContext:ExecutionContext) =
    pyramidConfig.
      ipfsData.uploadOpt.
      map(downloadEncrypted(_)).
      getOrElse(Future{EncryptedData()}).
      map(e=>pyramidConfig.msg("Oh Pharao, we have data for you: " +e))

}
