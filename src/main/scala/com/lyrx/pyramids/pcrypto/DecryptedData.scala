package com.lyrx.pyramids.pcrypto

import com.lyrx.pyramids.ipfs.IPFSMetaData

import scala.scalajs.js.typedarray.ArrayBuffer


case class DecryptedData(unencrypted: Option[ArrayBuffer],
                         metaData:Option[IPFSMetaData]
                        ){
  def isEmpty() =(
    unencrypted.isEmpty  &&
      metaData.isEmpty)

  def descr()=if(isEmpty()) "no decrypted data" else "decrypted data"

}
