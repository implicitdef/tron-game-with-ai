package manu.tron.bots.impl

import manu.tron.bots._
import manu.tron.service.GameBasicLogicService
import manu.tron.common.Vocabulary.GameStatus
import manu.tron.common.Vocabulary

class QuickBotDefinition extends BotDefinition {

  this: GameBasicLogicService =>

  override def nextMove(status: GameStatus, selfId: Vocabulary.PlayerId) =
    validDirections(status, selfId).headOption.getOrElse(anyDirection)

}
