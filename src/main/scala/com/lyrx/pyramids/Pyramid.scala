package com.lyrx.pyramids

import com.lyrx.pyramids.ipfs.{CanIpfs, TextDecoder}
import com.lyrx.pyramids.jszip._
import com.lyrx.pyramids.keyhandling._
import com.lyrx.pyramids.pcrypto.{AsymetricCrypto, DecryptedData, EncryptedData}
import org.scalajs.dom.crypto.{CryptoKey, JsonWebKey}
import org.scalajs.dom.raw
import org.scalajs.dom.raw.File
import typings.fileDashSaverLib.fileDashSaverMod.{^ => filesaver}
import typings.jszipLib.jszipMod.JSZip
import typings.{nodeLib, stdLib}

import scala.concurrent.{ExecutionContext, Future}
import scala.scalajs.js
import scala.scalajs.js.JSON
import scala.scalajs.js.typedarray.{ArrayBuffer, Uint8Array}


object  Pyramid{
  def apply(pharaoKeys:String)=new Pyramid(PyramidConfig(
    None,
    None,
    None,
    Messages(Some("Welcome to your Pyramid!"),
      None),
    None,
    IpfsData(None,None,pharao = pharaoKeys,None)))
}

class Pyramid(override val pyramidConfig: PyramidConfig)
  extends KeyCreation
    with KeyExport
    with KeyImport
  with DownloadWallet
  with UploadWallet
  with Encryption
  with CanIpfs
  with AsymetricCrypto
{

def msg(s:String) = new Pyramid(this.pyramidConfig.msg(s))



  def uploadZip(f:raw.File)(implicit executionContext:ExecutionContext)=
    zipEncrypt(f)
    .flatMap(_.dump())
    .flatMap((b) => bufferToIpfs(b))
    .map(
      os =>
        os.map(s => pyramidConfig.
        withUpload(s).
          msg(s"Uploaded ${f.name}")
        ).getOrElse(pyramidConfig))


  def downloadZip(aHash:String)
              (implicit executionContext:ExecutionContext) =
    readIpfs(aHash).flatMap(aFuture=>aFuture.
      map(aFile=>zipInstance().
        loadAsync(aFile.asInstanceOf[stdLib.Blob]).
      toFuture.map(Some(_))
      ).getOrElse(Future{None}))


  def downloadHashEncrypted(aHash:String)
                           (implicit executionContext:ExecutionContext) =
    downloadZip(aHash).flatMap(
      (o:Option[JSZip])=> o.map(z=>z.toEncrypted()
      ).getOrElse(Future{EncryptedData()}))




  def downloadEncrypted()
                       (implicit executionContext:ExecutionContext) =
    pyramidConfig.
      ipfsData.uploadOpt.
      map(downloadHashEncrypted(_)).
      getOrElse(Future{EncryptedData()})

  def downloadDecrypted()
                       (implicit executionContext:ExecutionContext) =
    downloadEncrypted().flatMap(encr=>pyramidConfig.
      symKeyOpt.
    map(symKey=>encr.decrypt(symKey)).getOrElse(
      Future{DecryptedData(None,None)}
    ))

  def download()(implicit executionContext:ExecutionContext) =
    downloadDecrypted().map(d=>{
      d.unencrypted.map(unencr=>d.metaData.map(meta=>{

        filesaver.saveAs(
          new raw.Blob(js.Array(unencr)).asInstanceOf[stdLib.Blob],
          meta.name
        )

      }))
      pyramidConfig.
        msg(s"Oh Pharao, you have downloaded ${d.descr()}")
    })



  def encryptSymKeys()(implicit executionContext:ExecutionContext) = readPharaoKeys().
    flatMap(_.map(exportSymKeyEncrypted(_)).getOrElse(Future{None}))

  def uploadSymKeys()(implicit executionContext:ExecutionContext) =encryptSymKeys().
    flatMap(_.map((b:ArrayBuffer)=>bufferToIpfs(
      nodeLib.bufferMod.Buffer.from(
        b.asInstanceOf[stdLib.ArrayBuffer]
      ))).getOrElse(Future{None}))



  def publishSymKeys()(implicit executionContext:ExecutionContext) =
    uploadSymKeys().map(_.map(s=>pyramidConfig.
      withSymKey(s).
      msg(s"You have given your keys to the Pharao!")).
      getOrElse(
        pyramidConfig.msg("Why didn't you give your keys to the Pharao?")))



  def initKeys()(implicit executionContext:ExecutionContext) = initIpfsAndPublishPublicKeys()
    .flatMap(_.publishSymKeys()).map(new Pyramid(_))

  def readIpfsSymKey(aHash:String,cryptoKey: CryptoKey)
                    (implicit executionContext: ExecutionContext) =
    readIpfs(aHash).map(_.map(f=>f))



  def zipEncrypt(f:File) (implicit ctx:ExecutionContext) = encryptAndSignFileDefault(f).map(_.zipped())

  def fileForSymKey() (implicit ctx:ExecutionContext) =
    pyramidConfig.ipfsData.symKeyOpt.
      flatMap(aHash=>
        pyramidConfig.
          asymKeyOpt.
          map(asymKey=>readIpfs(aHash))).
      getOrElse(Future{None})

  def decryptFileForSymKey() (implicit ctx:ExecutionContext) =
    fileForSymKey().flatMap(
      _.flatMap(f=>pyramidConfig.
      asymKeyOpt.
      map(asymKeys=>asymDecryptBuffer(
        asymKeys.privateKey,f).
        map(Some(_)))).
      getOrElse(Future{None}))

  def decodeFileForSymKey() (implicit ctx:ExecutionContext) =
    decryptFileForSymKey().
      map(_.map(b=>new TextDecoder().decode(new Uint8Array(b))))

  def parseFileForSymKey() (implicit ctx:ExecutionContext) =
    decodeFileForSymKey().
      map(_.map(s=>JSON.parse(s).asInstanceOf[JsonWebKey]))

  def importFileForSymKey() (implicit ctx:ExecutionContext) =
    parseFileForSymKey().
      flatMap(
        _.map(wk=>importSymetricKey(wk).
          map(Some(_))).
          getOrElse(Future{None}))

  def uploadZip2()(implicit ctx:ExecutionContext) =
    importFileForSymKey().map(key=>pyramidConfig.msg("I have found your keys!"))



  def testAsymEncr()(implicit ctx:ExecutionContext) =
    pyramidConfig.
      asymKeyOpt.map(
      k=>asymEncryptString(k.publicKey,"This is a test").map(Some(_))
    ).getOrElse(Future{None})

  def testAsymDecr()(implicit ctx:ExecutionContext) =
    testAsymEncr().flatMap(_.flatMap(b=>
      pyramidConfig.asymKeyOpt.map(k=>asymDecryptArrayBuffer(k.privateKey,b).map(Some(_)))).getOrElse(Future{None}))






  def testAsym()(implicit ctx:ExecutionContext) =
    testAsymDecr()
      .map(r=>pyramidConfig.msg(s"Test successfull: $r"))




}
