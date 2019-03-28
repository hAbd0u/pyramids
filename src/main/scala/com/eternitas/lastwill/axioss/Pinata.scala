package com.eternitas.lastwill.axioss

import com.eternitas.lastwill.PinataAuth
import org.scalajs.dom.raw.FormData

import scala.scalajs.js
import scala.scalajs.js.Dynamic.{literal => l}
import scala.scalajs.js.annotation.JSGlobal
import scala.scalajs.js.typedarray.ArrayBuffer
@js.native
@JSGlobal
class PimpedFormData extends FormData {

  val `_boundary`: String = js.native
}

class Pinata(auth: PinataAuth) {
  val url = "https://api.pinata.cloud/data/testAuthentication";

  def export(): js.Dynamic = {
    l("api" -> auth.api, "apisecret" -> auth.secretApi)
  }

  def authenticate(c: (AxiosImpl) => Unit, ec: (AxiosError) => Unit) =
    axios
      .get(url,
           l(
             "headers" -> l(
               "pinata_api_key" -> auth.api,
               "pinata_secret_api_key" -> auth.secretApi
             )))
      .`then`(a => c(a))
      .`catch`(e => ec(e))

  def pinFileToIPFS(arrayBuffer: ArrayBuffer) = {

    var data = new PimpedFormData();
    data.append("file", arrayBuffer);

    //You'll need to make sure that the metadata is in the form of a JSON object that's been convered to a string

    data.append("pinataMetadata",
                js.JSON.stringify(
                  l("name" -> "testname",
                    "keyvalues" -> l(
                      "exampleKey" -> "exampleValue"
                    ))))

    axios.post(
      url,
      data,
      l(
        "headers" -> l(
          "Content-Type" -> s"multipart/form-data; boundary= ${data._boundary}",
          "pinata_api_key" -> auth.api,
          "pinata_secret_api_key" -> auth.secretApi
        )
      )
    )

    /*
    .then(function (response) {
      //handle response here
    }).catch(function (error) {
      //handle error here
    })

   */

  }

}
