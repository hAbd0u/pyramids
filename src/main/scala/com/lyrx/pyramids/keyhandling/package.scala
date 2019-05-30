package com.lyrx.pyramids



import com.lyrx.pyramids.ipfs.BufferObject
import com.lyrx.pyramids.pcrypto.Encrypted
import typings.jszipLib.jszipMod.{JSZip, JSZipGeneratorOptions}

import scala.concurrent.ExecutionContext
import scala.scalajs.js
import scala.scalajs.js.annotation.{JSGlobal, JSImport}
import scala.scalajs.js.typedarray.{ArrayBuffer, Uint8Array}



@JSImport("jszip", JSImport.Namespace)
@js.native
class JJSZip extends JSZip


package object keyhandling {


  case class ZippableEncrypt(unencrypted: Option[ArrayBuffer],
                       encrypted:Option[ArrayBuffer],
                       random:Option[ArrayBuffer],
                       signature:Option[ArrayBuffer]) extends Encrypted{


    def orEncrypted() = if(encrypted.isDefined)
      this
    else
      ZippableEncrypt(this.unencrypted,None,None,signature)


    private def convert(b:ArrayBuffer) = new Uint8Array(b).asInstanceOf[typings.stdLib.Uint8Array]


    def zippedUnsigned() = orEncrypted().
      encrypted.
      map(
        b=>new JJSZip().
          file("data.encr", convert(b)).
          file("data.random",convert(random.get))).
      getOrElse(new JJSZip().file("data.dat",convert(unencrypted.get)))


    def zipped()=signature.map(
      s=>zippedUnsigned().file("data.signature",convert(s))).
      getOrElse(zippedUnsigned())

  }




  $$
implicit class PimpedZip(zip: JJSZip){

  def dump()(implicit executionContext: ExecutionContext) = {
    zip.generateAsync_uint8array(JSZipGeneratorOptions()).toFuture.map(BufferObject.from(_))

  }
}









}
