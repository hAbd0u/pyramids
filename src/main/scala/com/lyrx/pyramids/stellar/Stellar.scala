package com.lyrx.pyramids.stellar

import com.lyrx.pyramids.PyramidConfig
import typings.stellarDashSdkLib.stellarDashSdkMod
import typings.stellarDashSdkLib.stellarDashSdkMod.Server

import scala.concurrent.{ExecutionContext, Future}





object Stellar{

  val TESTNET = "https://horizon-testnet.stellar.org"



}

trait Stellar {
  val pyramidConfig:PyramidConfig

  def initStellar()(implicit executionContext: ExecutionContext) ={
    Future{pyramidConfig.withStellar(new Server(Stellar.TESTNET)).msg("Stellar is initialized!")}
  }

  def initStellarKeys(pw:String)
              (implicit executionContext: ExecutionContext)
  ={

    println("Secret: " +pw)

    pyramidConfig.
      stellarOpt.
      map(stellar=>{
        stellar.operations()
      })
    Future{pyramidConfig}
  }



}
