package com.lyrx.pyramids.jszip


import com.lyrx.pyramids.pcrypto.Encrypted
import typings.jszipLib.jszipMod.JSZip
import typings.stdLib.Uint8Array

import scala.scalajs.js
import scala.scalajs.js.annotation.JSImport
import scala.scalajs.js.typedarray.{ArrayBuffer, Uint8Array => UINT8ARRAY}



@JSImport("jszip", JSImport.Namespace)
@js.native
class JJSZip extends JSZip




case class ZippableEncrypt(unencrypted: Option[ArrayBuffer],
                           encrypted:Option[ArrayBuffer],
                           random:Option[ArrayBuffer],
                           signature:Option[ArrayBuffer],
                           metaData:Option[ArrayBuffer],
                          ) extends Encrypted{


  def fromEncrypted(e:Encrypted) = ZippableEncrypt(
    e.unencrypted,
    e.encrypted,
    e.random,
    e.signature,
    e.metaData
  )

  def zipped()=signature.map(
    s=>zippedUnsigned().file("data.signature",convert(s))).
    getOrElse(zippedUnsigned())

  private def convert(b:ArrayBuffer) = new UINT8ARRAY(b).asInstanceOf[typings.stdLib.Uint8Array]


  def zippedUnsigned() = orEncrypted().
    encrypted.
    map(
      b=>new JJSZip().
        file("data.encr", convert(b)).
        file("data.random",convert(random.get))).
    getOrElse(new JJSZip().file("data.dat",convert(unencrypted.get)))

  def orEncrypted() = if(encrypted.isDefined)
    this
  else
    ZippableEncrypt(this.unencrypted,None,None,signature,None)

}





trait Zipping{


  def zip(data:Uint8Array)={



    //val r = JSZip.f


   // new Uint8Array {}
    //JJSZip.file("data.encr",data)


  }



}