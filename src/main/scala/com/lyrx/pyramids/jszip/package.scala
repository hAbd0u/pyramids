package com.lyrx.pyramids

import com.lyrx.pyramids.ipfs.BufferObject
import com.lyrx.pyramids.pcrypto.EncryptedData
import typings.jszipLib.jszipMod.{JSZip, JSZipGeneratorOptions}
import typings.jszipLib.jszipLibStrings.uint8array
import typings.jszipLib.jszipMod
import typings.stdLib//.{ Blob, Uint8Array}

import scala.scalajs.js.typedarray
import scala.concurrent.{ExecutionContext, Future}
import scala.scalajs.js.|

package object jszip {
  type JSData = String  | stdLib.ArrayBuffer | stdLib.Blob | stdLib.Uint8Array


  def zipInstance() =new jszipMod.Class()






  implicit class PimpedZip(zip: JSZip){

    def dump()(implicit executionContext: ExecutionContext) =
      zip.
        generateAsync_uint8array(
          JSZipGeneratorOptions(`type` = uint8array)) .
        toFuture.map(BufferObject.from(_))




    def toArrayBuffer(fileName:String)
                   (implicit executionContext: ExecutionContext)=  zip.
      file(fileName)// TODO: perform null check
      .generateAsync_uint8array(
      JSZipGeneratorOptions(`type` = uint8array)).
    toFuture.map( r=> if(r !=null)
      Some(r.buffer.asInstanceOf[typedarray.ArrayBuffer])
    else
      None
    )


    def toEncrypted()
                   (implicit executionContext: ExecutionContext)=
      Future.sequence(Seq(
      "data.dat",
      "data.encrypted",
      "data.random",
      "data.signature",
      "data.meta",
      "meta.random",
      "signer.json"
    ).map(toArrayBuffer(_))).
        map(aSequence =>
          EncryptedData(
            unencrypted=  aSequence(0),
            encrypted = aSequence(1),
            random = aSequence(2),
            signature = aSequence(3),
          metaData = aSequence(4),
          metaRandom = aSequence(5),
          signer = aSequence(6)
        ))








  }


}
