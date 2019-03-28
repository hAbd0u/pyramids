package com.eternitas.lastwill.axioss

import com.eternitas.lastwill.PinataAuth
import org.scalajs.dom.raw.{Blob, FormData, HTMLFormElement}

import scala.scalajs.js
import scala.scalajs.js.Dynamic.{literal => l}
import scala.scalajs.js.annotation.JSGlobal
import scala.scalajs.js.typedarray.ArrayBuffer

@js.native
trait PinataData extends AxiosData {
  val IpfsHash:String = js.native
  val PinSize:String =js.native
  val Timestamp:String = js.native

}


@js.native
trait PinataPinResponse extends AxiosResponse {
  override val data:PinataData = js.native

}




@js.native
@JSGlobal
class PimpedFormData(form: HTMLFormElement = js.native) extends FormData(form) {

  val `_boundary`: String = js.native
}

class Pinata(auth: PinataAuth) {
  val url = "https://api.pinata.cloud";

  def export(): js.Dynamic = {
    l("api" -> auth.api, "apisecret" -> auth.secretApi)
  }

  def authenticate(c: (AxiosResponse) => Unit, ec: (AxiosError) => Unit) =
    axios
      .get(url+"/data/testAuthentication",
           l(
             "headers" -> l(
               "pinata_api_key" -> auth.api,
               "pinata_secret_api_key" -> auth.secretApi
             )))
      .`then`(a => c(a))
      .`catch`(e => ec(e))

  def pinFileToIPFS(arrayBuffer: ArrayBuffer) = {
    println("pinFileToIPFS")
    val data = new FormData()
    println("Have form data")
    data.append("file", new Blob(js.Array[js.Any](arrayBuffer)))
    data.append("pinataMetadata",
                js.JSON.stringify(
                  l("name" -> "testname",
                    "keyvalues" -> l(
                      "exampleKey" -> "exampleValue"
                    ))))


    println("Posting data")
    axios.post(
      url +"/pinning/pinFileToIPFS",
      data,
      l(
        "headers" -> l(
          //"Content-Type" -> s"multipart/form-data; boundary= ${data._boundary}",
          "Content-Type" -> s"multipart/form-data",
          "pinata_api_key" -> auth.api,
          "pinata_secret_api_key" -> auth.secretApi
        )
      )
    )

  }

}
