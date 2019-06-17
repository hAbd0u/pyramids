package com.lyrx.pyramids.stellar

import com.lyrx.pyramids.PyramidConfig

import typings.stellarDashSdkLib.stellarDashSdkMod
import stellarDashSdkMod.{Server, ^ => StellarBase}





object Stellar{

  val TESTNET = "https://horizon-testnet.stellar.org"



}

trait Stellar {
  val pyramidConfig:PyramidConfig

  def initStellar() ={
    println("Have no Stellar!")
    new Server(Stellar.TESTNET)
    println("Have Stellar!")
    pyramidConfig
  }



}
