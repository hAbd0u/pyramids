package com.eternitas.lastwill



import org.scalajs.dom
import dom.crypto.{HashAlgorithm, KeyUsage, RsaHashedKeyAlgorithm, crypto}

import scala.scalajs.js
import scala.scalajs.js.typedarray.Uint8Array



case class Eternitas(name:String) {




def withKeys() = {

  crypto.subtle.generateKey(
    RsaHashedKeyAlgorithm.
      `RSA-OAEP`(4096,
        new Uint8Array( js.Array(1,0,1)),
          HashAlgorithm.`SHA-256`
        ),true,
    js.Array(KeyUsage.encrypt,KeyUsage.decrypt))

}}


object Encrypt {




}
