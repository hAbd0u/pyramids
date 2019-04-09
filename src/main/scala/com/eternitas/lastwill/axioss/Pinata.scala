package com.eternitas.lastwill.axioss

import com.eternitas.lastwill.PinataAuth
import org.scalajs.dom.raw.{Blob, File, FormData, HTMLFormElement}

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


//type; application/octet-stream'
object PinataMetaData{

  def apply(f:File):PinataMetaData = PinataMetaData(Some(f.name),Some(f.size),Some("application/octet-stream"))

  def apply(p:PinataData):PinataMetaData = PinataMetaData(Some(p.IpfsHash),None,Some("application/octet-stream"))

}


case class PinataMetaData(name:Option[String],size:Option[Double],`type`:Option[String])




class Pinata(auth: PinataAuth) {
  val url = "https://api.pinata.cloud";

  def export(): js.Dynamic = {
    l("pinataApi" -> auth.api,
      "pinataApiSecret" -> auth.secretApi)
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

  def pinFileToIPFS(arrayBuffer: ArrayBuffer,pn:PinataMetaData) = {
    val data = new FormData()
    data.append("file", new Blob(js.Array[js.Any](arrayBuffer)))
    data.append("pinataMetadata",
                js.JSON.stringify(
                  l(
                    "name" -> pn.name.getOrElse("NOT SPECIFIED").toString(),
                    "keyvalues" -> l(
                      "size" -> pn.size.getOrElse("???").toString(),
                      "type" -> pn.`type`.getOrElse("NONE").toString()
                    ))))
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
