package com.lyrx.pyramids

import com.lyrx.pyramids.ipfs.BufferObject
import org.scalajs.dom.raw.Blob
import typings.jszipLib.jszipMod.JSZipGeneratorOptions

import scala.concurrent.ExecutionContext
import scala.scalajs.js.typedarray.{ArrayBuffer, Uint8Array}
import scala.scalajs.js.|

package object jszip {
  type JSData = String  | ArrayBuffer | Blob | Uint8Array



  implicit class PimpedZip(zip: JJSZip){

    def dump()(implicit executionContext: ExecutionContext) = {
      zip.generateAsync_uint8array(JSZipGeneratorOptions()).toFuture.map(BufferObject.from(_))

    }
  }


}
