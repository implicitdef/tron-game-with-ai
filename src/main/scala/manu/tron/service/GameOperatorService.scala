package manu.tron.service

import manu.tron.common.Vocabulary._

trait GameOperatorService {

  def buildInitialStatus(board: Board, playersPos: Map[PlayerId, Pos]): GameStatus

  def applyPlayerMove(status: GameStatus, Id: PlayerId, d: Direction): GameStatus

  def isGameOver(status: GameStatus): Boolean

}
