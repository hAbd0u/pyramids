package com.lyrx.pyramids

import com.lyrx.pyramids.ipfs.BufferObject
import typings.jszipLib.jszipMod.JSZipGeneratorOptions
import typings.jszipLib.jszipLibStrings.uint8array
import typings.stdLib.{ArrayBuffer, Uint8Array,Blob}

import scala.concurrent.ExecutionContext

import scala.scalajs.js.|

package object jszip {
  type JSData = String  | ArrayBuffer | Blob | Uint8Array



  implicit class PimpedZip(zip: JJSZip){

    def dump()(implicit executionContext: ExecutionContext) =
      zip.
        generateAsync_uint8array(
          JSZipGeneratorOptions(`type` = uint8array)) .
        toFuture.map(BufferObject.from(_))


  }


}
