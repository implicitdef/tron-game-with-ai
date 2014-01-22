package manu.tron.bots.impl

import manu.tron.bots.BotDefinition
import manu.tron.common.Vocabulary._
import manu.tron.service.GameBasicLogicService

class WallHuggingBotDefinition extends BotDefinition {

  this: GameBasicLogicService =>

  def nextMove(status: GameStatus, selfId: PlayerId) = {
    val p = status.playersPos.get(selfId).get

    validDirections(status, selfId) match {
      case Seq() => anyDirection
      case dirs  => dirs.map(dir =>
                      dir -> numberOfWallsInNeighbourhood(applyDirection(p, dir), status)
                    ).toMap.maxBy(_._2)._1
    }
  }




  private def numberOfWallsInNeighbourhood(p: Pos, status: GameStatus) =
    neighbourhoodFull(p).count(! isPosValid(_, status))





}
