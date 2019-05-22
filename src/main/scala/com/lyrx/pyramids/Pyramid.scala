package com.lyrx.pyramids

import com.lyrx.pyramids.cryptography._


object  Pyramid{
  def apply()=new Pyramid(PyramidConfig(DistributedData(None,None,None,Nil),
    None,
    None,
    None,
    Messages(Some("Welcome to your Pyramid!"),
      None)))
}

class Pyramid(override val pyramidConfig: PyramidConfig)
  extends KeyCreation
    with KeyExport
    with KeyImport
  with DownloadWallet
  with UploadWallet
{

def msg(s:String) = new Pyramid(this.pyramidConfig.msg(s))


}
