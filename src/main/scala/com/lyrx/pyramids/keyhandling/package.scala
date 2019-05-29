package com.lyrx.pyramids



import typings.jszipLib.jszipMod.JSZip

import scala.scalajs.js
import scala.scalajs.js.annotation.{JSGlobal, JSImport}
import scala.scalajs.js.typedarray.{ArrayBuffer, Uint8Array}



@JSImport("jszip", JSImport.Namespace)
@js.native
class JJSZip extends JSZip


package object keyhandling {


  case class Encrypted(unencrypted: Option[ArrayBuffer],
                       encrypted:Option[ArrayBuffer],
                       random:Option[ArrayBuffer],
                       signature:Option[ArrayBuffer]){


    def orEncrypted() = if(encrypted.isDefined)
      this
    else
      Encrypted(this.unencrypted,None,None,signature)


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





type EncryptionResult =  Encrypted//(Option[ArrayBuffer], Option[ArrayBuffer], Option[ArrayBuffer], Option[ArrayBuffer])


  implicit class PimpedEncryptionResult(r:EncryptionResult){



    //&new JSZip {}

    def toZip() =  ??? //JJSZip.file("data.encr",data)

  }




}
