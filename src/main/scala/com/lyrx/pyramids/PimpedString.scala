import scala.scalajs.js
import scala.scalajs.js.typedarray.Uint8Array
import scala.scalajs.js.JSConverters._

class PimpedString(s:String){
  def toUInt8Array():Uint8Array = new Uint8Array(s.getBytes().toJSArray)

  def toArrayBuffer() = toUInt8Array().buffer

  def toDynamic[T <: js.Dynamic]():T = js.JSON.parse(s).asInstanceOf[T]
}