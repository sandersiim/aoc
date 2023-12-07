package aoc2022.day12

import Coords
import org.assertj.core.api.Assertions
import readInput
import runSolution

fun main() {
  fun findAllMinPathsTo(board: List<CharArray>, endPos: Coords): Map<Coords, Int> {
    val minPaths = mutableMapOf<Coords, Int>()

    fun Coords.positiveNeighbours(totalRows: Int, totalCols: Int): List<Coords> {
      return neighbours().filter { it.row >= 0 && it.col >= 0 && it.row < totalRows && it.col < totalCols }
    }

    fun solveRec(from: Coords, currentPathLength: Int) {
      if (minPaths.containsKey(from) && currentPathLength >= minPaths.getValue(from)) {
        return
      } else {
        minPaths[from] = currentPathLength
      }

      val nextCoords = from.positiveNeighbours(board.size, board.first().size).filter {
        board[it.row][it.col] >= board[from.row][from.col] - 1
      }
      val nextPathLength = 1 + currentPathLength

      nextCoords.forEach { solveRec(it, nextPathLength) }
    }
    solveRec(endPos, 0)

    return minPaths
  }

  fun part1(input: List<String>): Int {
    val board = input.map { it.toCharArray() }

    val startRowIdx = input.indexOfFirst { it.contains('S') }
    val startRowCol = board[startRowIdx].indexOf('S')
    val startPos = Coords(row = startRowIdx, col = startRowCol)
    board[startRowIdx][startRowCol] = 'a'
    val endRowIdx = input.indexOfFirst { it.contains('E') }
    val endRowCol = board[endRowIdx].indexOf('E')
    val endPos = Coords(row = endRowIdx, col = endRowCol)
    board[endRowIdx][endRowCol] = 'z'

    val minPaths = findAllMinPathsTo(board, endPos)

    return minPaths.getValue(startPos)
  }

  fun part2(input: List<String>): Int {
    val board = input.map { it.toCharArray() }

    val startRowIdx = input.indexOfFirst { it.contains('S') }
    val startRowCol = board[startRowIdx].indexOf('S')
    board[startRowIdx][startRowCol] = 'a'
    val endRowIdx = input.indexOfFirst { it.contains('E') }
    val endRowCol = board[endRowIdx].indexOf('E')
    val endPos = Coords(row = endRowIdx, col = endRowCol)
    board[endRowIdx][endRowCol] = 'z'

    val minPaths = findAllMinPathsTo(board, endPos)
    val aPositions = board.indices.flatMap { row ->
      board[row].indices
        .filter { col -> board[row][col] == 'a' }
        .map { col -> Coords(row, col) }
        .filter { minPaths.containsKey(it) }
    }

    return aPositions.minOf { minPaths.getValue(it) }
  }

  val inputDir = "src/aoc2022/day12/inputs"

  val testResult = runSolution("Part1 test") { part1(readInput("test", inputDir)) }
  Assertions.assertThat(testResult).isEqualTo(31)

  val input = readInput("input", inputDir)
  runSolution("Part1") { part1(input) }

  val test2Result = runSolution("Part2 test") { part2(readInput("test", inputDir)) }
  Assertions.assertThat(test2Result).isEqualTo(29)

  runSolution("Part2") { part2(input) }
}
