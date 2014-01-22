package manu.tron.common

import manu.tron.common.Vocabulary._

object Direction2String {

  val directions2StringMap = Map(
    West  -> "w",
    North -> "n",
    East  -> "e",
    South -> "s"
  )

  implicit def direction2String(d: Direction): String =
    directions2StringMap.get(d).get

  implicit def string2Direction(s: String): Direction =
    directions2StringMap.map(_.swap).get(s).get


}
