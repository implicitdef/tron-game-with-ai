package manu.tron.bots.impl

import manu.tron.bots._
import manu.tron.service._
import manu.tron.common.Vocabulary._
import manu.tron.common.Vocabulary.GameStatus

/**
 * Created by manu on 1/25/14.
 */
class TowardsOpenSpaceBotDefinition extends BotDefinition {

  this: GameBasicLogicService with GameOperatorService =>

  override def nextMove(status: GameStatus, selfId: PlayerId) = {
    validDirections(status, selfId).map(
      dir => (dir, numberOfValidMovesInDirection(status, selfId, dir))
    ).maxBy(_._2)._1
  }

  private def numberOfValidMovesInDirection(status: GameStatus, selfId: PlayerId, dir: Direction): Int = {
    val statusAfterDirection = applyPlayerMove(status, selfId, dir);
    if(isGameOver(statusAfterDirection))
      0
    else {
      //trick the status so we can play again
      val statusAfterDirectionRigged = GameStatus(
        statusAfterDirection.board,
        statusAfterDirection.playersPos,
        statusAfterDirection.playersPreviousPos,
        statusAfterDirection.deadPlayers,
        Some(selfId)
      )
      1 + numberOfValidMovesInDirection(statusAfterDirectionRigged, selfId, dir)
    }
  }

}
