package com.lyrx.pyramids.stellar

import com.lyrx.pyramids.{Pyramid, PyramidConfig}
import typings.stellarDashSdkLib

import stellarDashSdkLib.stellarDashSdkMod
import stellarDashSdkMod.{Keypair, Network, Server,Transaction}


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


  def loadAccount()(implicit executionContext: ExecutionContext) = {
    val sourceKeypair = Keypair.fromSecret(pyramidConfig.stellarData.stellarIntern);
    val sourcePublicKey = sourceKeypair.publicKey();
    val receiverPublicKey = pyramidConfig.stellarData.stellarPublic;
    pyramidConfig.
      stellarData.stellarServerOpt.
      map(_.loadAccount(sourcePublicKey).
        toFuture.
        map(Some(_))).getOrElse(Future{None})
  }

  def sendKeys(symKeyHash:String)(implicit executionContext: ExecutionContext) = loadAccount().
    map(r=>{




    // new Transaction[TMemo, TOps]("")

      pyramidConfig.msg(s"Oh ${pyramidConfig.name()}, welcome to your Pyramid!")
    })





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
