package com.eternitas.lastwill.services

import com.eternitas.lastwill.Eternitas

import scala.scalajs.js
import scala.scalajs.js.annotation.{JSGlobal, JSImport}
import Instances.{instantiate => `new`}


@js.native
@JSGlobal
class Web3 extends js.Object{


}


@js.native
@JSImport("Web3", "web3")
object Web3 extends js.Object{

  val providers:Providers = js.native

}

@js.native
trait Providers extends js.Object{
  @js.native
  class  HttpProvider extends js.Object{



  }

  val HttpProvider:HttpProvider = js.native

}





object Web3Instance {




   def instance(et:Eternitas)= et.config.allAuth.
       flatMap(_.infuraOpt.
         flatMap(_.
           project.map(
           p=> //new Web3(new Web3.providers.HttpProvider("https://www.mainnet.infura.com/ID")
             `new`[Web3](`new`[Web3.providers.HttpProvider](
                 s"https://www.mainnet.infura.com/${p}")))))



}
