package com.lyrx.pyramids

import com.lyrx.pyramids.actions.{DownloadWallet, KeyCreation, KeyExport, KeyImport, UploadWallet}


object  Pyramid{
  def apply()=new Pyramid(PyramidConfig(None,None,None))
}

class Pyramid(override val pyramidConfig: PyramidConfig)
  extends KeyCreation
    with KeyExport
    with KeyImport
  with DownloadWallet
  with UploadWallet
{



}
