package com.eternitas.lastwill

import com.eternitas.lastwill.axioss._
import com.eternitas.lastwill.cryptoo._
import com.eternitas.wizard.JQueryWrapper
import com.lyrx.eternitas.lastwill.LastWillStartup
import org.querki.jquery.JQuery
import org.scalajs.dom.raw.{File, FileReader}
import com.eternitas.lastwill.Buffers._
import org.scalajs.dom.crypto.{CryptoKey, JsonWebKey}

import scala.concurrent.{ExecutionContext, Future}
import scala.scalajs.js
import scala.scalajs.js.typedarray.ArrayBuffer
import scala.util.Try
import js.Dynamic.{literal => l}
import scala.scalajs.js.UndefOr

object Upload {

  val SAMPLE_CID="QmSVBM2wzJhEiSXr2acC7Bid2mCXVE3pSfWVYErrQED5Ld"

  def isCID(s:String) = s.startsWith("Qm") && (s.length == SAMPLE_CID.length)

  def cidFromFile(f:File)=if (js.isUndefined(f) || js.isUndefined(f.name)) None
    else {
      val name = f.name
      val index =name.lastIndexOf(".")
      if( index == SAMPLE_CID.length()){
        val maybeCID = name.substring(0,index)
        if(isCID(maybeCID))
         Some(maybeCID)
        else
          None
      }
      else  None
    }



  implicit class PUpload(jq: JQuery) extends PimpedJQuery.PJQuery(jq) {

    def upLoad(eternitas: Eternitas)(implicit ctx: ExecutionContext,
                                     $ : JQueryWrapper,
                                     feedback: UserFeedback) =
      onDrop(
        (file: File) =>
          handleDrop(eternitas, file)
      ).onDragOverNothing()




    def handleDrop(eternitas: Eternitas, file: File)(implicit ctx: ExecutionContext,
                                                     $ : JQueryWrapper,
                                                     feedback: UserFeedback): Unit = {

      val cidOpt =cidFromFile(file)
      if(cidOpt.isDefined){
        //feedback.message("CID found in file name: " + cidOpt.get)
        LastWillStartup.init(eternitas.withPinDataHash(cidOpt.get))
      }
      else {
        encryptAndUpload(eternitas, file)
      }
    }

    def encryptAndUpload(eternitas: Eternitas, file: File)(implicit ctx: ExecutionContext,
                                                           $ : JQueryWrapper,
                                                           feedback: UserFeedback): Unit = {
      eternitas.config.keyOpt.map(
        key =>
          new FileReader().onReadArrayBuffer(
            file,
            (arrayBuffer: ArrayBuffer) =>
              SymCrypto
                .encrypt(key, arrayBuffer)
                .onComplete((t: Try[SymEncryptionResult]) =>
                  onEncryptionResult(t, eternitas, file))
          ))
    }



    def onEncryptionResult(t: Try[SymEncryptionResult],
                           eternitas: Eternitas,
                           file: File)(implicit feedback: UserFeedback,
                                       $ : JQueryWrapper,
                                       executionContext: ExecutionContext) = {

      t.failed.map(thr =>
        feedback.error(s"Encryption failed: ${thr.getMessage()}"))
      t.map((symEncryptionResult: SymEncryptionResult) => {
        feedback.log(s"Encrypted", symEncryptionResult.result)
        feedback.log(s"IV", symEncryptionResult.iv)
        eternitas.config.allAuth.map(p =>
          withPinata(eternitas, file, symEncryptionResult, p))
      })

    }

    def withPinata(eternitas: Eternitas,
                   file: File,
                   symEncryptionResult: SymEncryptionResult,
                   p: AllCredentials)(implicit feedback: UserFeedback,
                                      executionContext: ExecutionContext,
                                      $ : JQueryWrapper): AxiosImpl = {

      feedback.message("Start pinning, please be very patient!")
      new Pinata(p)
        .pinFileToIPFS(symEncryptionResult.result, PinataMetaData(file))
        .`then`((axiosResponse) =>
          pinataUpload(file, eternitas, symEncryptionResult, p, axiosResponse))
        .`catch`((error) => feedback.message(s"Error pinning hash: ${error}"))

    }

    def pinataUpload(file: File,
                     eternitas: Eternitas,
                     symEncryptionResult: SymEncryptionResult,
                     p: AllCredentials,
                     axiosResponse: AxiosResponse)(
        implicit $ : JQueryWrapper,
        feedback: UserFeedback,
        executionContext: ExecutionContext): AxiosImpl = {

      new Pinata(p)
        .pinFileToIPFS(symEncryptionResult.iv,
                       PinataMetaData(
                         axiosResponse
                           .asInstanceOf[PinataPinResponse]
                           .data))
        .`then`((axiosResponse2) =>
          handlePinResult(file, eternitas, axiosResponse, axiosResponse2))
        .`catch`((error) => feedback.message(s"Pinning error: ${error}"))

    }

    private def handlePinResult(file: File,
                                eternitas: Eternitas,
                                axiosResponse: AxiosResponse,
                                axiosResponse2: AxiosResponse)(
        implicit $ : JQueryWrapper,
        userFeedback: UserFeedback,
        executionContext: ExecutionContext) = {

      val dataHash = axiosResponse.asInstanceOf[PinataPinResponse].data.IpfsHash
      val ivHash = axiosResponse2.asInstanceOf[PinataPinResponse].data.IpfsHash

      userFeedback.logString(s"Data uploaded: ${dataHash}")
      userFeedback.logString(s"IV uploaded: ${ivHash}")

      pinDataList(file, dataHash, ivHash, eternitas, e => {
        LastWillStartup.init(e)
      })

    }

    def pinDataList(file: File,
                    dataHash: String,
                    ivHash: String,
                    eternitas: Eternitas,
                    cb: (Eternitas) => js.Any)(
        implicit $ : JQueryWrapper,
        userFeedback: UserFeedback,
        executionContext: ExecutionContext) = {

      def havePinData() = {
        eternitas.config.pinDataOpt.isDefined &&
        eternitas.config.pinDataOpt.map(s => (s != "")).getOrElse(false)
      }

      def mLoadPinData(cb: (PinDataListNative) => Unit) =
        if (havePinData())
          eternitas.config.pinDataOpt.map((aHash: String) =>
            if(aHash.length() > 0)
              PimpedJQuery.loadHashAsText(aHash, (s: String) => {
              cb(js.JSON.parse(s).asInstanceOf[PinDataListNative])
            }))
        else
          cb(l("data" -> js.Array()).asInstanceOf[PinDataListNative])

      mLoadPinData((pinDataNative) => {
        createPinData(file, dataHash, ivHash, eternitas, pinDataNative).map(
          pinString =>
            eternitas.config.allAuth.map(auth => {
               //userFeedback.logString("Pinning: " + pinString)
              new Pinata(auth)
                .pinFileToIPFS(
                  pinString.toArrayBuffer(),
                  PinataMetaData(name = Some("Eternitas-Data"),
                                 size = None,
                                 `type` = Some("application/json"))
                )
                .`then`((r: AxiosResponse) => {
                  val eternitasHash =
                    r.asInstanceOf[PinataPinResponse].data.IpfsHash
                  userFeedback.logString(
                    s"Pinned Eternitas-Data: ${eternitasHash}")
                  cb(eternitas.withPinDataHash(eternitasHash))
                })
                .`catch`((e: AxiosError) => {
                  userFeedback.error(s"Error pinning eternitas data list: ${e}")
                  cb(eternitas)
                })
            }))
      })

    }

    def generate(webKey: JsonWebKey,
                 signKey: JsonWebKey,
                 pinDataNative: PinDataListNative,
                 d1: PinDataNative) =
      Eternitas.stringify(
        pinDataNative.data
          .map((pd: js.Array[PinDataNative]) => {
            pd.append(d1)
            l(
              "data" -> pd,
              "pubkey" -> webKey,
              "sign" -> signKey,
              "purpose" -> "box"
            )
          })
          .getOrElse(l(
            "data" -> js.Array[PinDataNative](d1)
          ))
          .asInstanceOf[PinDataListNative])

    def createPinData(
        file: File,
        dataHash: String,
        ivHash: String,
        eternitas: Eternitas,
        pinDataNative: PinDataListNative)(implicit ctx: ExecutionContext) = {
      val d1 = l("hash" -> dataHash,
                 "vc" -> ivHash,
                 //"name" -> file.name,
                 "type" -> file.`type`,
                 "timestamp" -> new js.Date()).asInstanceOf[PinDataNative]

      createFuturePindata(eternitas, pinDataNative, d1)
      //generate(null,null,pinDataNative,d1)
    }

    private def createFuturePindata(
        eternitas: Eternitas,
        pinDataNative: PinDataListNative,
        d1: PinDataNative)(implicit ctx: ExecutionContext) = {
      val t: Option[Future[String]] = eternitas.config.namedKeyPair.keyPairOpt.map(
        kp =>
          AsymCrypto
            .eexportKey(kp.publicKey)
            .map(
              webKey =>
                eternitas.config.signKeyOpt
                  .map(signKey =>
                    SymCrypto
                      .eexportKey(signKey)
                      .map(signKeyJS =>
                        generate(webKey, signKeyJS, pinDataNative, d1)))
                  .getOrElse(
                    Future { generate(webKey, null, pinDataNative, d1) }
                ))
            .flatten)

      t.getOrElse(Future {
        generate(null, null, pinDataNative, d1)
      })
    }
  }

}
