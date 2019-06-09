package com.lyrx.pyramids.ethereum

import typings.web3Lib.web3Mod.{^ => Web3}

object IPFSEthereum {

  val PROJECTID="caa8ec7e1aa74463ab6ec85cee26d091"
  val MAINNET="mainnet.infura.io/v3"
  val ROPSTEN = "opsten.infura.io/v3"
  val KOVAN = "kovan.infura.io"
  val RINKEBY = "rinkeby.infura.io/v3"
  val GOERLI = "goerli.infura.io/v3"


  def mainNet():Web3 = new Web3(s"https://${MAINNET}/s{PROJECTID}")
  def ropsten():Web3 = new Web3(s"https://${ROPSTEN}/s{PROJECTID}")
  def kovan():Web3 = new Web3(  s"https://${KOVAN}/s{PROJECTID}")
  def rinkeby():Web3 = new Web3(s"https://${RINKEBY}/s{PROJECTID}")
  def goerly():Web3 = new Web3( s"https://${GOERLI}/s{PROJECTID}")



}
