package manu.tron.bots.impl

import manu.tron.bots._
import manu.tron.common.Vocabulary._
import manu.tron.service.impl._

/**
 * Created by manu on 1/26/14.
 */
trait BotDefinitionComponent {

  this: GameBasicLogicServiceComponent
    with GameOperatorServiceComponent
    with VoronoiServiceComponent
    with MinimaxServiceComponent =>

  val botDefinition: BotDefinitionImpl

  class BotDefinitionImpl extends BotDefinition {

    private val MinimaxDepth = 5

    override def nextMove(status: GameStatus) =
      // launch the minimax algorithm
      minimaxService.minimax(status, childsFinder, voronoiHeuristic, MinimaxDepth)


    /**
     * Function to find the 4 childs status
     */
    private val childsFinder: GameStatus => Map[Direction, GameStatus] =
      status =>
        gameBasicLogicService.allDirections.map( dir =>
          (dir, gameOperatorService.applyPlayerMove(status, status.nextPlayerToPlay.head, dir))
        ).toMap



    /**
     * Heuristic function to rate a status
     */
    private val voronoiHeuristic: (GameStatus, PlayerId) => Int =
      (status, playerId) =>
        if(gameOperatorService.isGameOver(status))
          //who died ?
          if (status.deadPlayers.contains(playerId))
            Int.MinValue
          else
            Int.MaxValue
        else {
          //perform a flood-fill algorithm to separate the two players' Voronoi regions
          val regions = voronoiService.computeVoronoiRegions(status, None)
          //the score is simply the difference in the sizes
          regions(playerId).size - regions(gameBasicLogicService.otherPlayer(status, playerId)).size
        }

  }


}
