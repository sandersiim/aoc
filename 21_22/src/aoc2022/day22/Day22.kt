package aoc2022.day22

import Coords
import org.assertj.core.api.Assertions
import readInput
import runSolution

data class Position(
  val coords: Coords,
  val facing: Int,
) {
  fun rotateRight(): Position {
    return Position(coords, (facing + 1).mod(4))
  }

  fun rotateLeft(): Position {
    return Position(coords, (facing + 3).mod(4))
  }

  fun move(move: Int, rows: List<List<Tile>>): Position {
    var newRow = coords.row
    var newCol = coords.col
    if (facing == 0 || facing == 2) {
      val currentRow = rows[coords.row-1]
      var indexInCurrentRow = coords.col - currentRow.first().col
      // move in row
      val moveModifier = if (facing == 0) {
        1
      } else {
        currentRow.size - 1
      }
      repeat(move) {
        val nextColIndexInRow = (indexInCurrentRow + moveModifier).mod(currentRow.size)
        if (currentRow[nextColIndexInRow].type == Tile.Type.EMPTY) {
          indexInCurrentRow = nextColIndexInRow
        }
      }
      newCol = currentRow[indexInCurrentRow].col
    } else {
      var currentRowIndex = coords.row-1
      val currentColRowIndexRange = IntRange(
        rows.indexOfFirst { it.find { tile -> tile.col == coords.col } != null },
        rows.indexOfLast { it.find { tile -> tile.col == coords.col } != null },
      )
      repeat(move) {
        val nextRowIndex = if (facing == 1) {
          if (currentRowIndex == currentColRowIndexRange.last) {
            currentColRowIndexRange.first
          } else {
            currentRowIndex + 1
          }
        } else {
          if (currentRowIndex == currentColRowIndexRange.first) {
            currentColRowIndexRange.last
          } else {
            currentRowIndex - 1
          }
        }
        val nextRow = rows[nextRowIndex]
        assert(nextRow[coords.col - nextRow.first().col].col == coords.col)
        if (nextRow[coords.col - nextRow.first().col].type == Tile.Type.EMPTY) {
          currentRowIndex = nextRowIndex
        }
      }
      newRow = currentRowIndex+1
    }

    return Position(Coords(row = newRow, col = newCol), facing)
  }

  fun movePart2(move: Int, rows: List<List<Tile>>): Position {
    var newRow = coords.row
    var newCol = coords.col
    if (facing == 0 || facing == 2) {
      val currentRow = rows[coords.row-1]
      var indexInCurrentRow = coords.col - currentRow.first().col
      // move in row
      val moveModifier = if (facing == 0) {
        1
      } else {
        currentRow.size - 1
      }
      repeat(move) {
        val nextColIndexInRow = (indexInCurrentRow + moveModifier).mod(currentRow.size)
        if (currentRow[nextColIndexInRow].type == Tile.Type.EMPTY) {
          indexInCurrentRow = nextColIndexInRow
        }
      }
      newCol = currentRow[indexInCurrentRow].col
    } else {
      var currentRowIndex = coords.row-1
      val currentColRowIndexRange = IntRange(
        rows.indexOfFirst { it.find { tile -> tile.col == coords.col } != null },
        rows.indexOfLast { it.find { tile -> tile.col == coords.col } != null },
      )
      repeat(move) {
        val nextRowIndex = if (facing == 1) {
          if (currentRowIndex == currentColRowIndexRange.last) {
            currentColRowIndexRange.first
          } else {
            currentRowIndex + 1
          }
        } else {
          if (currentRowIndex == currentColRowIndexRange.first) {
            currentColRowIndexRange.last
          } else {
            currentRowIndex - 1
          }
        }
        val nextRow = rows[nextRowIndex]
        assert(nextRow[coords.col - nextRow.first().col].col == coords.col)
        if (nextRow[coords.col - nextRow.first().col].type == Tile.Type.EMPTY) {
          currentRowIndex = nextRowIndex
        }
      }
      newRow = currentRowIndex+1
    }

    return Position(Coords(row = newRow, col = newCol), facing)
  }
}

typealias Row = List<Tile>

data class Tile(
  val type: Type,
  val col: Int,
) {
  enum class Type {
    EMPTY,
    WALL,
  }
}

fun main() {
  fun parseRow(input: String): Row {
    val row = mutableListOf<Tile>()
    val firstNonEmpty = input.indexOfFirst { it != ' ' }
    (firstNonEmpty until input.length).forEach { i ->
      if (input[i] == ' ') {
        return row
      }
      if (input[i] == '.') {
        row.add(Tile(type = Tile.Type.EMPTY, col = i + 1))
      } else {
        row.add(Tile(type = Tile.Type.WALL, col = i + 1))
      }
    }
    return row
  }

  fun parseInput(input: List<String>): Pair<List<Row>, CharIterator> {
    val iter = input.iterator()
    var next = iter.next()
    val rows = mutableListOf<Row>()

    while (next != "") {
      rows.add(parseRow(next))
      next = iter.next()
    }

    val commands = iter.next().toCharArray().iterator()

    return Pair(rows, commands)
  }

  fun part1(input: List<String>): Int {
    val (rows, commands) = parseInput(input)

    var position = Position(Coords(row = 1, col = rows[1][0].col), 0)

    var nextIntStr = ""
    while (commands.hasNext()) {
      val next = commands.next()

      if (!next.isDigit()) {
        val move = nextIntStr.toInt()
        nextIntStr = ""
        position = position.move(move, rows)

        position = if (next == 'R') {
          position.rotateRight()
        } else {
          position.rotateLeft()
        }
      } else {
        nextIntStr += next
      }
    }
    if (nextIntStr != "") {
      val move = nextIntStr.toInt()
      position = position.move(move, rows)
    }

    return 1000*position.coords.row + 4*position.coords.col + position.facing
  }

  fun part2(input: List<String>): Int {
    val (rows, commands) = parseInput(input)

    println(rows)
    var position = Position(Coords(row = 1, col = rows[1][0].col), 0)
    println(position)

    var nextIntStr = ""
    while (commands.hasNext()) {
      val next = commands.next()

      if (!next.isDigit()) {
        val move = nextIntStr.toInt()
        nextIntStr = ""
        println("move $move")
        position = position.movePart2(move, rows)
        println(position)

        position = if (next == 'R') {
          println("rotate right")
          position.rotateRight()
        } else {
          println("rotate left")
          position.rotateLeft()
        }
        println(position)
      } else {
        nextIntStr += next
      }
    }
    if (nextIntStr != "") {
      val move = nextIntStr.toInt()
      println("move $move")
      position = position.movePart2(move, rows)
      println(position)
    }

    return 1000*position.coords.row + 4*position.coords.col + position.facing
  }

  val inputDir = "src/aoc2022/day22/inputs"

  val testResult = runSolution("Part1 test") { part1(readInput("test", inputDir)) }
  Assertions.assertThat(testResult).isEqualTo(6032)

  val input = readInput("input", inputDir)
  runSolution("Part1") { part1(input) }

  val test2Result = runSolution("Part2 test") { part2(readInput("test", inputDir)) }
  Assertions.assertThat(test2Result).isEqualTo(5031)

  runSolution("Part2") { part2(input) }

}
