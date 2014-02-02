package manu.tron.bots.impl

import manu.tron.bots._
import manu.tron.common.Vocabulary._
import manu.tron.common.Vocabulary.GameStatus
import manu.tron.service.{GameOperatorService, GameBasicLogicService}

/**
 * Created by manu on 1/26/14.
 */
class MinimaxBotDefinition extends BotDefinition {

  this: GameBasicLogicService with GameOperatorService =>

  private val MinimaxDepth = 3

  override def nextMove(status: GameStatus, selfId: PlayerId) = {
    println("- entering nextMove, status is " + status + ", selfId is " + selfId)
    validDirections(status, selfId) match {
      case Seq()     => anyDirection
      case Seq(dir)  => dir
      case dirs      => dirs.map( dir => {
          // map every direction to itself and the rating of the resulting status
          val resultingStatus = applyPlayerMove(status, selfId, dir)
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
    println("- entering miniMax, selfId is " + selfId + ", depth is " + depth)
    if(depth == 0 || isGameOver(status))
      //recursion is over
      rateStatus(status, selfId)
    else {

      val playingId = status.nextPlayerToPlay.get

      val childStatus = allDirections.map(
        applyPlayerMove(status, playingId, _)
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
    println("- entering rateStatus, selfId is " + selfId)
    if(isGameOver(status))
      //who died ?
      if (status.deadPlayers.contains(selfId))
        Int.MinValue
      else
        Int.MaxValue
    else {
      //use our heuristic :
      //perform a flood-fill algorithm to separate the two players' Voronoi regions
      val regions = floodFill(status)
      //the score is simply the difference in the sizes
      regions(selfId).size - regions(otherPlayer(status, selfId)).size
    }
  }

  /**
   * Performs a flood-fill to find the Voronoi regions of each player in the given status
   *
   * Each free cell is assigned to a player's region if he can get there
   * in less moves than the other player.
   *
   * @return
   *
   */
  private def floodFill(status: GameStatus): Map[PlayerId, Seq[Pos]] = {
    println("- entering floodFill, status is " + status)
    //describe all the needed data
    case class FloodFillStatus(
      board: Board,
      forbiddenPos: Seq[Pos],
      regions: Map[PlayerId, Seq[Pos]]
    )

    //inner recursive function, to perform one step of the flood fill
    def recursiveFloodFill(floodFillStatus: FloodFillStatus): FloodFillStatus = {
      println("- entering recursiveFloodFill, floodFillStatus is " + floodFillStatus)
      //all pos already used
      val nonFreePos = floodFillStatus.forbiddenPos ++ floodFillStatus.regions.values.flatten

      //find all the pos into which each region should expand
      val mapNewPosForRegions = floodFillStatus.regions.mapValues( posSeq =>
        //map each pos
        posSeq.map(
          //to the free neighbourhood
          neighbourhoodBySide(_).filter(
            isPosInBoard(_, floodFillStatus.board)
          ).filterNot(
            nonFreePos.contains(_)
          )
        ).flatten
      )

      //find all pos that are reachable par more than one region
      val duplicatedNewPos =
        //flatten all new pos used
        mapNewPosForRegions.values.flatten
        //group by itself
        .groupBy(pos => pos)
        //map to the number of occurences
        .mapValues(_.size)
        //filter to keep only duplicates
        .filter( _._2 > 1)
        .keySet

      //and exclude them
      val finalMapNewPosForRegions = mapNewPosForRegions.mapValues(
        _.filterNot( duplicatedNewPos.contains(_))
      )

      //build the new status
      val newFloodFillStatus = FloodFillStatus(
        floodFillStatus.board,
        floodFillStatus.forbiddenPos,
        floodFillStatus.regions.map { case (playerId, posSeq) =>
          (playerId, posSeq ++ finalMapNewPosForRegions(playerId))
        }
      )
      if (newFloodFillStatus == floodFillStatus)
        //no change => end of recursion
        floodFillStatus
      else
        //recurse
        recursiveFloodFill(newFloodFillStatus)

    }

    //launch the recursion
    recursiveFloodFill(FloodFillStatus(
        status.board,
        status.playersPreviousPos.values.flatten.toSeq,
        status.playersPos.mapValues(Seq(_))
      )
    ).regions

  }



  //TODO debug and test
  //TODO cleaner les logs



}
