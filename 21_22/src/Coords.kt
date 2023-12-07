import kotlin.math.abs

data class Coords(val row: Int, val col: Int) {
  companion object {
    fun fromInput(line: String) = line.split(",").let {
      Coords(it[1].toInt(), it[0].toInt())
    }
  }

  fun manhattanDistFrom(other: Coords): Int {
    return abs(row - other.row) + abs(col - other.col)
  }

  fun neighbours(): List<Coords> {
    return listOf(
      Coords(row - 1, col),
      Coords(row, col - 1),
      Coords(row + 1, col),
      Coords(row, col + 1),
    )
  }

  fun moveInDirection(dir: Direction): Coords {
    return when (dir) {
      Direction.DOWN -> Coords(row - 1, col)
      Direction.UP -> Coords(row + 1, col)
      Direction.LEFT -> Coords(row, col - 1)
      Direction.RIGHT -> Coords(row, col + 1)
    }
  }

  fun touches(other: Coords): Boolean {
    return other.row in (row-1 .. row +1) &&
        other.col in (col-1 .. col +1)
  }

  val y get() = row
  val x get() = col
}
