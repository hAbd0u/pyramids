package com.lyrx.pyramids

import com.lyrx.pyramids.infura.InfuraProxy
import com.lyrx.pyramids.ipfs.{CanIpfs, TextDecoder}
import com.lyrx.pyramids.jszip._
import com.lyrx.pyramids.keyhandling._
import com.lyrx.pyramids.pcrypto.{AsymetricCrypto, DecryptedData, EncryptedData}
import com.lyrx.pyramids.stellar.Stellar
import com.lyrx.pyramids.temporal.{Temporal, TemporalProxy}
import org.scalajs.dom.crypto.{CryptoKey, JsonWebKey}
import org.scalajs.dom.raw
import org.scalajs.dom.raw.File
import typings.fileDashSaverLib.fileDashSaverMod.{^ => filesaver}
import typings.jszipLib.jszipMod
import typings.jszipLib.jszipMod.JSZip
import typings.{nodeLib, stdLib}

import scala.concurrent.{ExecutionContext, Future}
import scala.scalajs.js
import scala.scalajs.js.JSON
import scala.scalajs.js.typedarray.{ArrayBuffer, Uint8Array}


object  Pyramid{
  def apply(pharaoKeys:String,aStellarPublic:String)=new Pyramid(PyramidConfig(
    None,
    None,
    None,
    Messages(Some("Welcome to your Pyramid!"),
      None),
    None,
    IpfsData(
      None,
      None,
      pharaoData = PharaoData(
        pharaoKeys ),None,None),
    TemporalData(None,None,None),
    StellarData(stellarPublic = "GDY7YWJF6F7W7EIQP5UDWYXNBC62JUSGJOLM2VWRQGY7RZ5SDYRZOZNT",stellarServerOpt = None)
  ))
}

trait InfuraIpfs extends CanIpfs with InfuraProxy{

}

class InfuraIpfsImpl(override val pyramidConfig:PyramidConfig)  extends InfuraIpfs{

  override def initIpfsAndPublishPublicKeys()(
    implicit executionContext: ExecutionContext):Future[Pyramid]  =
    initIpfs().flatMap(p=>new InfuraIpfsImpl(p.pyramidConfig).publicKeysToIpfs())

}


trait TemporalIpfs extends CanIpfs with TemporalProxy{

}
class TemporalIpfsImpl(override val pyramidConfig:PyramidConfig)  extends TemporalIpfs{

  override def initIpfsAndPublishPublicKeys()(
    implicit executionContext: ExecutionContext):Future[Pyramid] = ???

}



class Pyramid(override val pyramidConfig: PyramidConfig)
  extends KeyCreation
    with KeyExport
    with KeyImport
  with DownloadWallet
  with UploadWallet
  with Encryption
  with AsymetricCrypto
  with Temporal
  with Stellar
{

def msg(s:String) = new Pyramid(this.pyramidConfig.msg(s))


  def infura(pyramidConfig:PyramidConfig) = new InfuraIpfsImpl(pyramidConfig)
  def temporal(pyramidConfig:PyramidConfig) = new TemporalIpfsImpl(pyramidConfig)




  def uploadZip(f:raw.File)(implicit executionContext:ExecutionContext)=
    zipEncrypt(f)
    .flatMap(_.dump())
    .flatMap(infura(pyramidConfig).bufferToIpfs(_))
    .map(_.
      map(s=>pyramidConfig.
        withUpload(s).msg(s"You encrypted and uploaded ${f.name}")
        ).getOrElse(pyramidConfig.msg("We have done nothing!")))


  def downloadZip(aHash:String)
              (implicit executionContext:ExecutionContext) =
    infura(pyramidConfig).readIpfs(aHash).flatMap(aFuture=>aFuture.
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



  def encryptSymKeys()(implicit executionContext:ExecutionContext) = infura(pyramidConfig).readPharaoKeys().
    flatMap(_.map(exportSymKeyEncrypted(_)).getOrElse(Future{None}))

  def uploadSymKeys()(implicit executionContext:ExecutionContext) =encryptSymKeys().
    flatMap(_.map((b:ArrayBuffer)=>infura(pyramidConfig).bufferToIpfs(
      nodeLib.bufferMod.Buffer.from(
        b.asInstanceOf[stdLib.ArrayBuffer]
      ))).getOrElse(Future{None}))



  def publishSymKeys()(implicit executionContext:ExecutionContext) =
    uploadSymKeys().map(_.map(s=>pyramidConfig.
      withSymKey(s).
      msg(s"Session token: $s")).
      getOrElse(
        pyramidConfig.msg("Why didn't you give your keys to the Pharao?")))



  def initKeys()(implicit executionContext:ExecutionContext) = new InfuraIpfsImpl(pyramidConfig).initIpfsAndPublishPublicKeys()
    .flatMap(_.publishSymKeys()).map(new Pyramid(_))

  def readIpfsSymKey(aHash:String,cryptoKey: CryptoKey)
                    (implicit executionContext: ExecutionContext) =
    infura(pyramidConfig).readIpfs(aHash).map(_.map(f=>f))



  def encryptAndSignWithLoaded(f:File)(implicit ctx:ExecutionContext) =
  importSymKey().
    flatMap(keyOpt => encryptAndSignFile(f,keyOpt).map(_.zipped()))

  def zipEncrypt(f:File) (implicit ctx:ExecutionContext) =
    if(pyramidConfig.isPharao())
      encryptAndSignWithLoaded(f)
      else
    encryptAndSignFileDefault(f).map(_.zipped())

  def readEncryptedSymKey()(implicit ctx:ExecutionContext) =
    pyramidConfig.ipfsData.symKeyOpt.
      flatMap(aHash=>
        pyramidConfig.
          asymKeyOpt.
          map(asymKey=>new InfuraIpfsImpl(pyramidConfig).readIpfs(aHash))).
      getOrElse(Future{None})

  def decryptSymKey()(implicit ctx:ExecutionContext) =
    readEncryptedSymKey().flatMap(
      _.flatMap(f=>pyramidConfig.
      asymKeyOpt.
      map(asymKeys=>asymDecryptBuffer(
        asymKeys.privateKey,f).
        map(Some(_)))).
      getOrElse(Future{None}))

  def decodeSymKey()(implicit ctx:ExecutionContext) =
    decryptSymKey().
      map(_.map(b=>new TextDecoder().decode(new Uint8Array(b))))

  def parseSymKey()(implicit ctx:ExecutionContext) =
    decodeSymKey().
      map(_.map(s=>JSON.parse(s).asInstanceOf[JsonWebKey]))

  def importSymKey()(implicit ctx:ExecutionContext) =
    parseSymKey().
      flatMap(
        _.map(wk=>importSymetricKey(wk).
          map(Some(_))).
          getOrElse(Future{None}))

  def withImportSymKey()(implicit ctx:ExecutionContext) = importSymKey().
    map(_.map(kryptoKey=>new Pyramid(pyramidConfig.copy(symKeyOpt = Some(kryptoKey)))))




  def uploadZip2()(implicit ctx:ExecutionContext) =
    importSymKey().map(key=>pyramidConfig.msg("I have found your keys!"))



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
