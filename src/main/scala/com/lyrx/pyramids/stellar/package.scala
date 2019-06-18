package com.lyrx.pyramids
import typings.stellarDashBaseLib

import scalajs.js

package object stellar {
  type TMemo = stellarDashBaseLib.stellarDashBaseMod.Memo[
    stellarDashBaseLib.stellarDashBaseMod.MemoType]
  type TOps = js.Array[stellarDashBaseLib.stellarDashBaseMod.Operation]
}
