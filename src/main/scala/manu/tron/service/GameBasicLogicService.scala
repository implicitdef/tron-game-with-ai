package manu.tron.service

import manu.tron.common.Vocabulary._

trait GameBasicLogicService {

  def allDirections: Seq[Direction]

  def anyDirection: Direction

  def validDirections(status: GameStatus, selfId: PlayerId): Seq[Direction]

  def isPosValid(p: Pos, status: GameStatus): Boolean

  def isPosInBoard(p: Pos, board: Board): Boolean

  def applyDirection(p: Pos, d: Direction): Pos

  def applyDirections(p: Pos, dirs: Seq[Direction]): Pos

  def neighbourhoodBySide(p: Pos): Seq[Pos]

  def neighbourhoodByDiag(p: Pos): Seq[Pos]

  def neighbourhoodFull(p: Pos): Seq[Pos]

}
