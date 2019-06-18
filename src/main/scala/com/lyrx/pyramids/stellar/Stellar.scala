package com.lyrx.pyramids.stellar

import com.lyrx.pyramids.{Pyramid, PyramidConfig}
import typings.stellarDashSdkLib.stellarDashSdkMod.{Keypair, Network, Server, ^}

import scala.concurrent.{ExecutionContext, Future}





object Stellar{

  val TESTNET = "https://horizon-testnet.stellar.org"



}

trait Stellar {
  val pyramidConfig:PyramidConfig

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


  def sendSymKey(symKeyHash:String) (implicit executionContext: ExecutionContext) = {
    val sourceKeypair = Keypair.fromSecret(pyramidConfig.stellarData.stellarIntern);
    val sourcePublicKey = sourceKeypair.publicKey();

    val receiverPublicKey = pyramidConfig.stellarData.stellarPublic;

    pyramidConfig.
      stellarData.stellarServerOpt.
      map(_.
        loadAccount(sourcePublicKey).
        toFuture.
        map(r=>pyramidConfig.msg(s"Oh ${pyramidConfig.name()}, welcome to this pyramid!"))
      ).getOrElse(Future{pyramidConfig.msg(s"The stellar server is not configured, oh ${pyramidConfig.name()}!")})

  }



  def initStellarKeys()
              (implicit executionContext: ExecutionContext)
  =pyramidConfig.
    ipfsData.
    symKeyOpt.
    map(
      sendSymKey(_)).
    getOrElse(Future{pyramidConfig.
      msg(s"I have no encryption keys to send by the stellar network, oh ${pyramidConfig.name()}!")})








}
