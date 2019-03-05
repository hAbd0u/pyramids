package com.eternitas.wizard

trait ContentSeq {
  def getWizardStatusOpt(i: Int): Option[WizardStatus]
  def wizardDepth(): Int

}

class ContentSeqImpl(pages: Seq[Content]) extends ContentSeq {

  def getStati(): Seq[WizardStatus] =
    pages.zipWithIndex.map(
      (t: (Content, Int)) =>
        WizardStatus(
          current = Some(t._1),
          previous = if (t._2 > 0) Some(pages(t._2 - 1)) else None,
          next = if (t._2 < pages.size - 1) Some(pages(t._2 + 1)) else None
      ))

  val stati = getStati()

  override def getWizardStatusOpt(i: Int): Option[WizardStatus] = {
    stati.lift(i)
  }

  override def wizardDepth(): Int = stati.size





}
