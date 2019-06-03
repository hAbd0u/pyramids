package com.lyrx.pyramids.jszip


import com.lyrx.pyramids.pcrypto.Encrypted
import typings.jszipLib.jszipMod
import typings.jszipLib.jszipMod.JSZip

import scala.scalajs.js
import scala.scalajs.js.annotation.JSImport
import scala.scalajs.js.typedarray.{ArrayBuffer, Uint8Array => UINT8ARRAY}




object  ZippableEncrypt{
  def fromZip(zip:JSZip) = ???
}




case class ZippableEncrypt(unencrypted: Option[ArrayBuffer],
                           encrypted:Option[ArrayBuffer],
                           random:Option[ArrayBuffer],
                           signature:Option[ArrayBuffer],
                           metaData:Option[ArrayBuffer],
                           metaRandom:Option[ArrayBuffer],
                           signer:Option[ArrayBuffer]


) extends Encrypted{



  def fromEncrypted(e:Encrypted) = ZippableEncrypt(
    e.unencrypted,
    e.encrypted,
    e.random,
    e.signature,
    e.metaData,
    e.metaRandom,
    e.signer
  )

  def zipped()=signature.map(
    s=>withMetaData().file("data.signature",convert(s))).
    getOrElse(withMetaData())

  private def convert(b:ArrayBuffer) = new UINT8ARRAY(b).asInstanceOf[typings.stdLib.Uint8Array]


  def zippedUnsigned() = orEncrypted().
    encrypted.
    map(
      b=>zipInstance().
        file("data.encr", convert(b)).
        file("data.random",convert(random.get))
        ).
    getOrElse(zipInstance().file("data.dat",convert(unencrypted.get)))


  def withMetaData()=metaData.map(
    md => zippedUnsigned().
      file("data.meta",convert(md)).
    file("meta.random",convert(metaRandom.get))

  ).getOrElse(zippedUnsigned())


  def orEncrypted() = if(encrypted.isDefined)
    this
  else
    ZippableEncrypt(this.unencrypted,None,None,signature,None,None,None)

}



