package com.lyrx.pyramids

import com.lyrx.pyramids.keyhandling._
import com.lyrx.pyramids.ipfs.CanIpfs


object  Pyramid{
  def apply()=new Pyramid(PyramidConfig(DistributedDir(Nil,""),
    None,
    None,
    None,
    Messages(Some("Welcome to your Pyramid!"),
      None),
    None))
}

class Pyramid(override val pyramidConfig: PyramidConfig)
  extends KeyCreation
    with KeyExport
    with KeyImport
  with DownloadWallet
  with UploadWallet
  with CanIpfs
{

def msg(s:String) = new Pyramid(this.pyramidConfig.msg(s))


}
