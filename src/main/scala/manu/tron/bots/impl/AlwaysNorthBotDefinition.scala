package manu.tron.bots.impl

import manu.tron.common.Vocabulary._
import manu.tron.bots.BotDefinition

class AlwaysNorthBotDefinition extends BotDefinition {

  override def nextMove(status: GameStatus, selfId: PlayerId) =
    North

}
