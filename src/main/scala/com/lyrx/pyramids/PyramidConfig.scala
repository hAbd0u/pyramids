package com.lyrx.pyramids

import com.lyrx.pyramids.ipfs.IpfsClient
import com.lyrx.pyramids.pcrypto.{Crypto, CryptoTypes}
import com.lyrx.pyramids.temporal.{JWTToken, TemporalCredentials}
import typings.stellarDashSdkLib.stellarDashSdkMod

import scala.scalajs.js.typedarray.ArrayBuffer





case class Messages(messageOpt:Option[String], errorOpt:Option[String]  ) {

}

case class PharaoData(pubkey:String)

case class IpfsData(uploadOpt:Option[String], //hash of an uploaded file
                         pubKeysOpt:Option[String],  //hash of the public keys (signature and encryption of the user)
                         pharaoData:PharaoData,  // Hash of the public key of the pharaoh
                        symKeyOpt:Option[String],  //Hash of the symmetric encryption key encrypted with the public key of the pharaoh
                        temporalOpt:Option[String]  // hash of the JWT Token for Temporal
                        ){
  def isPharao()= pubKeysOpt.
    map(k=> (k == pharaoData.pubkey)).getOrElse(false)
}


case class TemporalData(temporalCredentialsOpt:Option[TemporalCredentials],  // Credentials of a Temporal account
                    temporalJWTOpt:Option[ JWTToken],  //current JWTToken for Temporal
                    temporalClientOpt:Option[IpfsClient] // IPFS HTTP Client foroi Temporal
                       )

case class StellarData(stellarPublic:String,
                       stellarServerOpt:Option[stellarDashSdkMod.Server],
                       stellarInternHash:Option[String]
                      )

case class PyramidConfig( //distributedDir: DistributedDir,
                          symKeyOpt:Option[CryptoTypes.PyramidCryptoKey],
                          asymKeyOpt:Option[CryptoTypes.PyramidCryptoKeyPair],
                          signKeyOpt:Option[CryptoTypes.PyramidCryptoKeyPair],
                          messages:Messages, //current status messages
                          infuraClientOpt:Option[ IpfsClient],  // IPFS HTTP Client for Infura
                          ipfsData: IpfsData,  // List of hashes for the current state
                          temporalData: TemporalData,
                          stellarData:StellarData
                        ) {

  def pyramid() = new Pyramid(this)

  def withStellarInternHash(s:String) =this.copy(stellarData=this.stellarData.copy(stellarInternHash=Some(s)))
  def withStellar(server:stellarDashSdkMod.Server) = this.copy(stellarData=this.stellarData.copy(stellarServerOpt=Some(server)))

  def withTemporalCredentials(cr:TemporalCredentials)=this.copy(temporalData=this.temporalData.copy(temporalCredentialsOpt = Some(cr)))

  def withTemporalJWT(jwt: JWTToken)=this.copy(temporalData=this.temporalData.copy(temporalJWTOpt = Some(jwt)))

  def withTemporalClient(client: IpfsClient)=this.copy(temporalData=this.temporalData.copy(temporalClientOpt = Some(client)))


  def isPharao()=ipfsData.isPharao()

  def name()=if(isPharao()) "Pharaoh" else "Humble Tokenizer"


  def withTemporal(s:String) = this.copy(ipfsData = this.ipfsData.copy(temporalOpt=Some(s.trim())))
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
