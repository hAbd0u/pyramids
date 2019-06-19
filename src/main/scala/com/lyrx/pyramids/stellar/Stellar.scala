package com.lyrx.pyramids.stellar

import com.lyrx.pyramids.{Pyramid, PyramidConfig}
import typings.stellarDashSdkLib
import typings.stellarDashBaseLib
import stellarDashSdkLib.{stellarDashSdkMod}
//import stellarDashSdkLib.stellarDashB
import stellarDashSdkMod.{Account, Keypair, Network, Server, ServerNs, TransactionBuilder }
import stellarDashBaseLib.stellarDashBaseMod //.Asset
import stellarDashBaseMod.OperationOptionsNs//.Payment
import stellarDashBaseLib.stellarDashBaseMod.OperationOptionsNs.Payment
import scala.concurrent.{ExecutionContext, Future}

import stellarDashBaseLib.stellarDashBaseMod.Operation



object Stellar{

  val TESTNET = "https://horizon-testnet.stellar.org"

implicit class PimpedServer(server: Server){
  def futureLoadAccount(publicKey:String)(implicit executionContext: ExecutionContext) =
    server.
      loadAccount(publicKey).
      toFuture.
      map(r=>new Account(r.accountId(),r.sequenceNumber()))
}

}

trait Stellar {
  val pyramidConfig:PyramidConfig
  import Stellar._

  def initStellar()(implicit executionContext: ExecutionContext) ={
    // Uncomment the following line to build transactions for the live network. Be
    // sure to also change the horizon hostname.
    // StellarSdk.Network.usePublicNetwork();
    Network.useTestNetwork()
    Future{
      new Pyramid(
      pyramidConfig.
        withStellar(new Server(Stellar.TESTNET)).msg(s"Oh ${pyramidConfig.name()}, the Stellar network is initialized!"))
    }.flatMap(p=>p.initStellarKeys())
  }


  def loadAccount()(implicit executionContext: ExecutionContext) = {
    val sourceKeypair = Keypair.fromSecret(pyramidConfig.stellarData.stellarInternHash.get);
    val sourcePublicKey = sourceKeypair.publicKey();
    val receiverPublicKey = pyramidConfig.stellarData.stellarPublic;
    pyramidConfig.
      stellarData.
      stellarServerOpt.
      map(_.futureLoadAccount(sourcePublicKey).
        map(Some(_))).getOrElse(Future{None})
  }

  def sendKeys(symKeyHash:String)(implicit executionContext: ExecutionContext) = loadAccount().
    map(_.map( (account) => {


      val p = Payment("0",stellarDashBaseMod.Asset.native(),"","")

  //stellarDashBaseLib.


     new TransactionBuilder(account).addOperation(p.asInstanceOf[Operation])

      pyramidConfig.msg(s"Oh ${pyramidConfig.name()}, welcome to your Pyramid!")
    }).getOrElse(pyramidConfig))





  def initStellarKeys()
              (implicit executionContext: ExecutionContext)
  =pyramidConfig.
    ipfsData.
    symKeyOpt.
    map(
      sendKeys(_)).
    getOrElse(Future{pyramidConfig.
      msg(s"I have no encryption keys to send by the stellar network, oh ${pyramidConfig.name()}!")})








}
