package com.eternitas.wizard

import org.querki.jquery.{JQuery, JQueryEventObject}

trait Wizard{
   implicit val $:JQueryWrapper

  def postShow(index:Int,j:JQuery): JQuery = j
  def setWizardIndex(index: Int): Any
  def showBackward(index: Int,w:Wizard=this) = {
    $(".backward")
      .show()
      .off("click")
      .click((e: JQueryEventObject) => w.setWizardIndex(index - 1))
  }

  def showForward(index: Int,w:Wizard=this): JQuery = {
    $(".forward")
      .show()
      .off("click")
      .click((e: JQueryEventObject) => setWizardIndex(index + 1))
  }

  def hideBackward(w:Wizard=this) = {
    $(".backward").hide()
  }

  def hideForward(w:Wizard=this): JQuery = {
    $(".forward").hide()
  }


}


abstract class WizardImpl(
                           jquery: JQueryWrapper,
                           wizardSeq: ContentSeq) extends Wizard {

  override implicit val $:JQueryWrapper = jquery


  override def setWizardIndex(index: Int): Any = {
    val wizardStatusOpt = wizardSeq.getWizardStatusOpt(index)

    wizardStatusOpt.map(_.current.map((c:Content)=>postShow(index,c.show((ajquery)=>postShow(index,ajquery)))))

    if (index > 0) {
      showBackward(index)
    } else hideBackward()

    if (index < wizardSeq.wizardDepth() - 1) {
      showForward(index)
    } else {
      hideForward()
    }



  }



}
