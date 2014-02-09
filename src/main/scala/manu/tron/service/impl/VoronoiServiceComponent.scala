package manu.tron.service.impl

import manu.tron.service._
import manu.tron.common.Vocabulary._
import scala.Some

/**
 * Created by manu on 2/3/14.
 */

trait VoronoiServiceComponent {

  this: GameBasicLogicServiceComponent =>

  val voronoiService: VoronoiService

  class VoronoiServiceImpl extends VoronoiService {



    private case class FloodFillStatus(
      board: Board,
      forbiddenPos: Seq[Pos],
      regions: Map[PlayerId, Seq[Pos]]
    )

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

    //Performs one step of the flood fill, and recurse
    private def recursiveFloodFill(floodFillStatus: FloodFillStatus, recursionMaxDepth: Option[Int]): FloodFillStatus = {
      //all pos already used
      val nonFreePos = floodFillStatus.forbiddenPos ++ floodFillStatus.regions.values.flatten

      //find all the pos into which each region should expand
      val mapNewPosForRegions = floodFillStatus.regions.mapValues( posSeq =>
        //map each pos
        posSeq.flatMap(
          //to the neighbourhood
          gameBasicLogicService.neighbourhoodBySide(_)
        )
        //keep only the relevant ones
        .filter(
          gameBasicLogicService.isPosInBoard(_, floodFillStatus.board)
        )
        .filterNot(
          nonFreePos.contains(_)
        )
        //and remove the duplicates
        .distinct
      )

      //find all pos that are reachable par more than one region
      val duplicatedNewPos =
      //flatten all new pos used
        mapNewPosForRegions.values.flatten
          //group by itself
          .groupBy(pos => pos)
          //filter to keep only duplicates
          .filter( _._2.size > 1)
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

}
