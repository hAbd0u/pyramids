package com.lyrx.pyramids

import com.lyrx.pyramids.actions.{DownloadKey, KeyCreation, KeyExport, KeyImport}


object  Pyramid{
  def apply()=new Pyramid(PyramidConfig(None,None,None))
}

class Pyramid(override val pyramidConfig: PyramidConfig)
  extends KeyCreation
    with KeyExport
    with KeyImport
  with DownloadKey  {



}
