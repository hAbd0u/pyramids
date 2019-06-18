package com.lyrx.pyramids.stellar

import com.lyrx.pyramids.PyramidConfig
import typings.stellarDashSdkLib.stellarDashSdkMod.{Keypair, Network, Server, ^}

import scala.concurrent.{ExecutionContext, Future}





object Stellar{

  val TESTNET = "https://horizon-testnet.stellar.org"



}

trait Stellar {
  val pyramidConfig:PyramidConfig

  def initStellar()(implicit executionContext: ExecutionContext) ={
    Future{pyramidConfig.withStellar(new Server(Stellar.TESTNET)).msg("Stellar is initialized!")}
  }

  def initStellarKeys()
              (implicit executionContext: ExecutionContext)
  ={

    val sourceKeypair = Keypair.fromSecret(???);
    val sourcePublicKey = sourceKeypair.publicKey();

    val receiverPublicKey = pyramidConfig.stellarData.stellarPublic;

    // Uncomment the following line to build transactions for the live network. Be
    // sure to also change the horizon hostname.
    // StellarSdk.Network.usePublicNetwork();
    Network.useTestNetwork();




    //

    pyramidConfig.
      stellarData.stellarServerOpt.
      map(_.
        loadAccount(sourcePublicKey).
        toFuture.
        map(r=>r)
      )
    Future{pyramidConfig}
  }



}
