package manu.tron.service.impl

import manu.tron.service._
import manu.tron.common.Vocabulary._
import scala.Some

/**
 * Created by manu on 2/3/14.
 */
trait VoronoiServiceImpl extends VoronoiService {

  this: GameBasicLogicService =>

  override def computeVoronoiRegions(status: GameStatus, maxRecursionDepth: Option[Int]): Map[PlayerId, Seq[Pos]] = {
    //launch the recursion
    recursiveFloodFill(FloodFillStatus(
        status.board,
        status.playersPreviousPos.values.flatten.toSeq,
        status.playersPos.mapValues(Seq(_))
      ),
      maxRecursionDepth
    ).regions
  }

  //TODO remettre les private

  case class FloodFillStatus(
    board: Board,
    forbiddenPos: Seq[Pos],
    regions: Map[PlayerId, Seq[Pos]]
  )

  //Performs one step of the flood fill, and recurse
  def recursiveFloodFill(floodFillStatus: FloodFillStatus, recursionMaxDepth: Option[Int]): FloodFillStatus = {
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
      ).flatten.distinct
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
    else if (recursionMaxDepth == Some(0))
      //force stop recursion
      newFloodFillStatus
    else
      //recurse
      recursiveFloodFill(newFloodFillStatus, recursionMaxDepth.map( _ - 1))

  }



}
