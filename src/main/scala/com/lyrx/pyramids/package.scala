package com.lyrx

import org.scalajs.dom.raw.{Blob, File}
import typings.nodeLib.bufferMod.Buffer

import scala.scalajs.js.typedarray.ArrayBuffer
import scala.scalajs.js.|

package object pyramids {
  type EitherData = Either[DistributedData,DistributedDir]


  implicit class PimpedFile(f:File) extends PyramidJSON {

    def toBuffer()=Buffer.
      from(stringify(f)).
      buffer
  }



}
