package com.lyrx.pyramids

import com.lyrx.pyramids.ipfs.BufferObject
import com.lyrx.pyramids.pcrypto.EncryptedData
import typings.jszipLib.jszipMod.{JSZip, JSZipGeneratorOptions, JSZipObject}
import typings.jszipLib.jszipLibStrings.uint8array
import typings.jszipLib.jszipMod
import typings.stdLib

import scala.scalajs.js.typedarray
import scala.concurrent.{ExecutionContext, Future}
import scala.scalajs.js.|

package object jszip {
  type JSData = String | stdLib.ArrayBuffer | stdLib.Blob | stdLib.Uint8Array

  val DATA = "data.dat"
  val ENCRYPTED = "data.encr"
  val RANDOM = "data.random"
  val SIGNER = "signature.json"
  val SIGNATURE  = "data.signature"
  val META = "data.meta"
  val METARANDOM = "meta.random"


  def zipInstance() = new jszipMod.Class()

  implicit class PimpedZip(zip: JSZip) {

    def dump()(implicit executionContext: ExecutionContext) =
      zip
        .generateAsync_uint8array(JSZipGeneratorOptions(`type` = uint8array))
        .toFuture
        .map(BufferObject.from(_))

    def toArrayBuffer(fileName: String)(
        implicit executionContext: ExecutionContext) = {
      val aFile = zip.file(fileName).asInstanceOf[JSZipObject]
      if (aFile == null)
        Future{None}
      else
        aFile
          .async_uint8array(uint8array)
          .toFuture
          .map(
            r =>
              if (r != null)
                Some(r.buffer.asInstanceOf[typedarray.ArrayBuffer])
              else
              None)
    }

    def toEncrypted()(implicit executionContext: ExecutionContext) =
      Future
        .sequence(
          Seq(
            DATA,
            ENCRYPTED,
            RANDOM,
            SIGNATURE,
            META,
            METARANDOM,
            SIGNER
          ).map(toArrayBuffer(_)))
        .map(aSequence =>
          EncryptedData(
            unencrypted = aSequence(0),
            encrypted = aSequence(1),
            random = aSequence(2),
            signature = aSequence(3),
            metaData = aSequence(4),
            metaRandom = aSequence(5),
            signer = aSequence(6)
        ))

  }

}
