package com.eternitas.wizard

case class WizardStatus(
                         previous:Option[Content]=None,
                         current:Option[Content]=None,
                         next:Option[Content]=None
                       )
