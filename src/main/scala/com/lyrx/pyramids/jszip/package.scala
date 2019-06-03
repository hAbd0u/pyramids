package com.lyrx.pyramids

import com.lyrx.pyramids.ipfs.BufferObject
import com.lyrx.pyramids.pcrypto.EncryptedData
import typings.jszipLib.jszipMod.{JSZip, JSZipGeneratorOptions}
import typings.jszipLib.jszipLibStrings.uint8array
import typings.jszipLib.jszipMod
import typings.stdLib.{ArrayBuffer, Blob, Uint8Array}

import scala.concurrent.ExecutionContext
import scala.scalajs.js.|

package object jszip {
  type JSData = String  | ArrayBuffer | Blob | Uint8Array


  def zipInstance() =new jszipMod.Class()






  implicit class PimpedZip(zip: JSZip){

    def dump()(implicit executionContext: ExecutionContext) =
      zip.
        generateAsync_uint8array(
          JSZipGeneratorOptions(`type` = uint8array)) .
        toFuture.map(BufferObject.from(_))




    def toArrayBuffer(fileName:String)
                   (implicit executionContext: ExecutionContext)=  zip.
      file("data.dat")
      .generateAsync_uint8array(
      JSZipGeneratorOptions(`type` = uint8array)).
    toFuture.map( r=> if(r !=null)
      Some(r.buffer.asInstanceOf[ArrayBuffer])
    else
      None
    )


    def toEncrypted() = Seq(
      "data.dat",
      "data.encrypted",
      "data.random",
      "data.signature",
      "data.meta",
      "meta.random",
      "signer.json"
    )


    /* EncryptedData(
      unencrypted= zip.file("data.dat").generateNodeStream(),
      encrypted = None,
      random = None,)
      signature = None,
      metaData = None,
      metaRandom = None,
      signer = None
    )
    */





  }


}
