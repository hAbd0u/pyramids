package com.lyrx.pyramids

import com.lyrx.pyramids.keyhandling._
import com.lyrx.pyramids.ipfs.CanIpfs
import org.scalajs.dom.raw.File
import com.lyrx.pyramids.jszip._
import scala.concurrent.ExecutionContext


object  Pyramid{
  def apply()=new Pyramid(PyramidConfig(//DistributedDir(Nil,""),
    None,
    None,
    None,
    Messages(Some("Welcome to your Pyramid!"),
      None),
    None,
    None,
    None))
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
          copy(uploadOpt=Some(s)).
          msg(s"Uploaded ${f.name}")
        ).getOrElse(pyramidConfig))





}
