package day15

import Coords
import org.assertj.core.api.Assertions.assertThat
import readInput
import runSolution

fun main() {
  fun parseInput(input: List<String>): List<List<Int>> {
    return input.map { line ->
      line.split("").mapNotNull { it.toIntOrNull() }
    }
  }

  fun part1(input: List<String>): Int {
    val parsedInput = parseInput(input)
    val graph = DijkstraGraph<Coords>()

    parsedInput.indices.forEach { row ->
      parsedInput[row].indices.forEach { col ->
        val currentCoords = Coords(row, col)
        if (col < parsedInput[row].size - 1) {
          val newNode = currentCoords.copy(col = col + 1)
          graph.addEdge(currentCoords, newNode, parsedInput[row][col + 1])
        }

        if (row < parsedInput.size - 1) {
          val newNode = currentCoords.copy(row = row + 1)
          graph.addEdge(currentCoords, newNode, parsedInput[row + 1][col])
        }
      }
    }

    val endNodeId = Coords(parsedInput.size - 1, parsedInput.size - 1)
    println(endNodeId)

    return graph.shortestPath(Coords(0, 0), endNodeId)
  }

  fun part2(input: List<String>): Int {
    val parsedInput = parseInput(input)
    val graph = DijkstraGraph<Coords>()

    val boardSize = parsedInput.size
    val maxRow = boardSize * 5 - 1
    val maxCol = boardSize * 5 - 1
    println(boardSize)

    fun nodeWeight(row: Int, col: Int): Int {
      val result = (parsedInput[row.mod(boardSize)][col.mod(boardSize)] + row/boardSize + col/boardSize - 1).mod(9) + 1
      assertThat(result).isBetween(1, 9)
      return result
    }

    (0..maxRow).forEach { row ->
      (0..maxCol).forEach { col ->
        val currentCoords = Coords(row, col)
        if (col > 0) {
          val newNode = currentCoords.copy(col = col - 1)
          graph.addEdge(currentCoords, newNode, nodeWeight(row, col - 1))
        }
        if (col < maxCol) {
          val newNode = currentCoords.copy(col = col + 1)
          graph.addEdge(currentCoords, newNode, nodeWeight(row, col + 1))
        }

        if (row > 0) {
          val newNode = currentCoords.copy(row = row - 1)
          graph.addEdge(currentCoords, newNode, nodeWeight(row - 1, col))
        }
        if (row < maxRow) {
          val newNode = currentCoords.copy(row = row + 1)
          graph.addEdge(currentCoords, newNode, nodeWeight(row + 1, col))
        }
      }
    }

    val endNodeId = Coords(maxRow, maxCol)
    println(endNodeId)

    return graph.shortestPath(Coords(0, 0), endNodeId)
  }

  val testInput = readInput("day15_test")
  val testResult = runSolution("Part1 test") { part1(testInput) }
  assertThat(testResult).isEqualTo(40)

  val input = readInput("day15_input")

  runSolution("Part1") { part1(input) }

  val testResult2 = runSolution("Part2 test") { part2(testInput) }
  assertThat(testResult2).isEqualTo(315)

  runSolution("Part2") { part2(input) }
}
