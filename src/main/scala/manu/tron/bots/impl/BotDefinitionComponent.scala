package manu.tron.bots.impl

import manu.tron.bots._
import manu.tron.common.Vocabulary._
import manu.tron.common.Vocabulary.GameStatus
import manu.tron.service.impl._

/**
 * Created by manu on 1/26/14.
 */
trait BotDefinitionComponent {

  this: GameBasicLogicServiceComponent
    with GameOperatorServiceComponent
    with VoronoiServiceComponent =>

  val botDefinition: BotDefinitionImpl

  class BotDefinitionImpl extends BotDefinition {

    private val MinimaxDepth = 5

    override def nextMove(status: GameStatus, selfId: PlayerId) = {
      gameBasicLogicService.validDirections(status, selfId) match {
        case Seq()     => gameBasicLogicService.anyDirection
        case Seq(dir)  => dir
        case dirs      => dirs.map( dir => {
            // map every direction to itself and the rating of the resulting status
            val resultingStatus = gameOperatorService.applyPlayerMove(status, selfId, dir)
            (
              dir,
              miniMax(resultingStatus, selfId, MinimaxDepth)
            )
          }
        )
        //pick the best rating
        .maxBy(_._2)
        ._1
      }
    }

    /**
     * Evaluates a status and rates it, in the point of view
     * of the selfId.
     *
     * The bigger the returned value, the better this status is for the selfId.
     * The lower it is, the better this status is for the other player.
     *
     * This method works recursively, where depth is the level of
     * number of recursive calls allowed, before falling back
     * to the raw evaluation method of the status.
     *
     */
    private def miniMax(status: GameStatus, selfId: PlayerId, depth: Int): Int = {
      if(depth == 0 || gameOperatorService.isGameOver(status))
        //recursion is over
        rateStatus(status, selfId)
      else {

        val playingId = status.nextPlayerToPlay.get

        val childStatus = gameBasicLogicService.allDirections.map(
          gameOperatorService.applyPlayerMove(status, playingId, _)
        )

        val rates = childStatus.map(
          //rate them by recursion
          miniMax(_, selfId, depth - 1)
        )
        //if self is the one choosing the move, he will choose the best for him
        if(playingId == selfId)
          rates.max
        //the opponent will choose the worst for us
        else
          rates.min
      }
    }

    /**
     * Evaluates a status and rates it, in the point of view of "selfId".
     *
     * The bigger the returned value, the better this status is for that player
     * The lower it is, the better this status is for the other player.
     *
     * This method works by raw evaluation of the current positions,
     * without considering the possible moves
     *
     */
    private def rateStatus(status: GameStatus, selfId: PlayerId) = {
      if(gameOperatorService.isGameOver(status))
        //who died ?
        if (status.deadPlayers.contains(selfId))
          Int.MinValue
        else
          Int.MaxValue
      else {
        //use our heuristic :
        //perform a flood-fill algorithm to separate the two players' Voronoi regions
        val regions = voronoiService.computeVoronoiRegions(status, None)
        //the score is simply the difference in the sizes
        regions(selfId).size - regions(gameBasicLogicService.otherPlayer(status, selfId)).size
      }
    }

  }


}
