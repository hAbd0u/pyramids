package com.lyrx.pyramids

import com.lyrx.pyramids.cryptography._


object  Pyramid{
  def apply()=new Pyramid(PyramidConfig(None,None,None,Messages(Some("Welcome to your Pyramid!"),None)))
}

class Pyramid(override val pyramidConfig: PyramidConfig)
  extends KeyCreation[Pyramid]
    with KeyExport
    with KeyImport[Pyramid]
  with DownloadWallet[Pyramid]
  with UploadWallet
{





  override def createInstance(config: PyramidConfig): Pyramid = new Pyramid(config)
}
