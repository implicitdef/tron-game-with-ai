package manu.tron.bots

import manu.tron.common.Vocabulary._

trait BotDefinition {

  def nextMove(status: GameStatus, selfId: PlayerId): Direction

}
