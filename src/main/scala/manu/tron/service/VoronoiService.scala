package manu.tron.service

import manu.tron.common.Vocabulary._

/**
 * Created by manu on 2/3/14.
 */
trait VoronoiService {

  /**
   * Performs a flood-fill-like algorithm to find the Voronoi regions of each player in the given status
   *
   * In other words each free cell is assigned to a player's region if he can get there
   * in less moves than the other player.  *
   *
   * @return the map of the pos belonging to each player's Voronoi region
   *
   */
  def computeVoronoiRegions(status: GameStatus, maxRecursionDepth: Option[Int]): Map[PlayerId, Seq[Pos]]

}
