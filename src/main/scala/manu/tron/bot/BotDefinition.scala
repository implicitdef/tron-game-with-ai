package manu.tron.bot

import manu.tron.common.Vocabulary._

trait BotDefinition {

  def nextMove(status: GameStatus): Direction

}
