package com.lyrx.pyramids

import com.lyrx.pyramids.ipfs.IpfsClient
import com.lyrx.pyramids.pcrypto.{Crypto, CryptoTypes}
import com.lyrx.pyramids.temporal.{JWTToken, TemporalCredentials}

import scala.scalajs.js.typedarray.ArrayBuffer





case class Messages(messageOpt:Option[String], errorOpt:Option[String]  ) {

}


case class IpfsData(uploadOpt:Option[String], //hash of an uploaded file
                         pubKeysOpt:Option[String],  //hash of the public keys (signature and encryption of the user)
                         pharao:String,  // Hash of the public key of the pharaoh
                        symKeyOpt:Option[String],  //Hash of the symmetric encryption key encrypted with the public key of the pharaoh
                        temporalOpt:Option[String]  // hash of the JWT Token for Temporal
                        ){
  def isPharao()= pubKeysOpt.
    map(k=> (k == pharao)).getOrElse(false)
}

case class PyramidConfig( //distributedDir: DistributedDir,
                          symKeyOpt:Option[CryptoTypes.PyramidCryptoKey],
                          asymKeyOpt:Option[CryptoTypes.PyramidCryptoKeyPair],
                          signKeyOpt:Option[CryptoTypes.PyramidCryptoKeyPair],
                          messages:Messages, //current status messages
                          infuraClientOpt:Option[ IpfsClient],  // IFS HTTP Client for Infura
                          ipfsData: IpfsData,  // List of hashes for the current state
                          temporalCredentialsOpt:Option[TemporalCredentials],  // Credentials of a Temporal account
                          temporalJWTOpt:Option[ JWTToken]  //current JWTkToken for Temporal
                        ) {

  def isPharao()=ipfsData.isPharao()


  def withTemporal(s:String) = this.copy(ipfsData = this.ipfsData.copy(temporalOpt=Some(s)))
  def withUpload(s:String) =this.
    copy(ipfsData = ipfsData.
      copy(uploadOpt = Some(s)))

  def withPubKeys(s:String) =this.
    copy(ipfsData = ipfsData.
      copy(pubKeysOpt = Some(s)))

  def withSymKey(s:String) =this.
    copy(ipfsData = ipfsData.
      copy(symKeyOpt = Some(s)))





  def msg(s:String) = this.
    copy(
      messages=
        this.
          messages.
          copy(messageOpt = Some(s)))


  def error(s:String) = copy(
    messages=
      this.
        messages.
        copy(errorOpt = Some(s)))


}
