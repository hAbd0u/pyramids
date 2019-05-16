package com.lyrx.pyramids

import org.scalajs.dom.crypto.{CryptoKey, CryptoKeyPair}




case class Messages(messageOpt:Option[String], errorOpt:Option[String]  ) {

}


case class PyramidConfig(symKeyOpt:Option[CryptoKey],
                         asymKeyOpt:Option[CryptoKeyPair],
                         signKeyOpt:Option[CryptoKeyPair],
                         messages:Messages  ) {

}
