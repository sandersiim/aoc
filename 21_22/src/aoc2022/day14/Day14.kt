package aoc2022.day14

import Coords
import org.assertj.core.api.Assertions
import readInput
import runSolution
import kotlin.math.max
import kotlin.math.min

fun main() {

  fun parseBoard(input: List<String>): Array<CharArray> {
    val board = Array(200) { CharArray(1000) { '.' } }
    input.forEach { line ->
      val lineCoords = line.split(" -> ").map { Coords(it.split(",")[1].toInt(), it.split(",")[0].toInt()) }
      var current = lineCoords[0]
      (1 until lineCoords.size).forEach {
        val next = lineCoords[it]
        if (next.row == current.row) {
          IntRange(min(current.col, next.col), max(current.col, next.col)).forEach { col ->
            board[next.row][col] = '#'
          }
        } else {
          IntRange(min(current.row, next.row), max(current.row, next.row)).forEach { row ->
            board[row][current.col] = '#'
          }
        }
        current = next
      }
    }
    return board
  }

  fun part1(input: List<String>): Int {
    val board = parseBoard(input)

    var sand = 0
    var currentPos = Coords(0, 500)

    while (true) {
      if (currentPos.row >= 199) {
        break
      } else if (board[currentPos.row+1][currentPos.col] == '.') {
        currentPos = currentPos.moveInDirection(Direction.UP)
      } else if (board[currentPos.row+1][currentPos.col-1] == '.') {
        currentPos = currentPos.moveInDirection(Direction.LEFT).moveInDirection(Direction.UP)
      } else if (board[currentPos.row+1][currentPos.col+1] == '.') {
        currentPos = currentPos.moveInDirection(Direction.RIGHT).moveInDirection(Direction.UP)
      } else {
        board[currentPos.row][currentPos.col] = 'o'
        sand++
        currentPos = Coords(0, 500)
      }
    }

    return sand
  }

  fun part2(input: List<String>): Int {
    val board = parseBoard(input)
    val floorRow = board.indexOfLast { it.any { c -> c == '#' } } + 2
    println(floorRow)
    var sand = 0
    var currentPos = Coords(0, 500)

    while (true) {
      if (currentPos.row == floorRow - 1) {
        board[currentPos.row][currentPos.col] = 'o'
        sand++
        currentPos = Coords(0, 500)
      } else if (board[currentPos.row + 1][currentPos.col] == '.') {
        currentPos = currentPos.moveInDirection(Direction.UP)
      } else if (board[currentPos.row + 1][currentPos.col - 1] == '.') {
        currentPos = currentPos.moveInDirection(Direction.LEFT).moveInDirection(Direction.UP)
      } else if (board[currentPos.row + 1][currentPos.col + 1] == '.') {
        currentPos = currentPos.moveInDirection(Direction.RIGHT).moveInDirection(Direction.UP)
      } else {
        board[currentPos.row][currentPos.col] = 'o'
        sand++
        if (currentPos == Coords(0, 500)) {
          break
        }
        currentPos = Coords(0, 500)
      }
    }

    return sand
  }

  val inputDir = "src/aoc2022/day14/inputs"

  val testResult = runSolution("Part1 test") { part1(readInput("test", inputDir)) }
  Assertions.assertThat(testResult).isEqualTo(24)

  val input = readInput("input", inputDir)
  runSolution("Part1") { part1(input) }

  val test2Result = runSolution("Part2 test") { part2(readInput("test", inputDir)) }
  Assertions.assertThat(test2Result).isEqualTo(93)

  runSolution("Part2") { part2(input) }
}
