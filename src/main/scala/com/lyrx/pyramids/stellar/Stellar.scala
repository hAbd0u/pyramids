package com.lyrx.pyramids.stellar

import com.lyrx.pyramids.PyramidConfig
import typings.stellarDashSdkLib.stellarDashSdkMod
import stellarDashSdkMod.{Server, ^ => StellarBase}

import scala.concurrent.{ExecutionContext, Future}





object Stellar{

  val TESTNET = "https://horizon-testnet.stellar.org"



}

trait Stellar {
  val pyramidConfig:PyramidConfig

  def initStellar()(implicit executionContext: ExecutionContext) ={
    Future{pyramidConfig.withStellar(new Server(Stellar.TESTNET)).msg("Stellar is initialized!")}
  }

  def initKeys()(implicit executionContext: ExecutionContext)=pyramidConfig.
    stellarOpt.
    map(stellar=>{
      stellar.operations()
    })



}
