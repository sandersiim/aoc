package day15

import Coords
import org.assertj.core.api.Assertions.assertThat
import readInput
import runSolution
import java.util.PriorityQueue

fun main() {
  data class Node(
    val coords: Coords,
    val risk: Int
  ) : DijkstraNode<Coords> {
    override var visited: Boolean = false
    override val id get() = coords
  }

  fun parseInput(input: List<String>): List<List<Int>> {
    return input.map { line ->
      line.split("").mapNotNull { it.toIntOrNull() }
    }
  }

  fun part1(input: List<String>): Int {
    val parsedInput = parseInput(input)
    val graph = DijkstraGraph<Coords>()

    val distances = mutableMapOf(Coords(0, 0) to 0)
    val compareByDistance: Comparator<Coords> = compareBy { distances.getOrDefault(it, Int.MAX_VALUE) }
    val unvisitedNodes = PriorityQueue(compareByDistance)

    parsedInput.indices.forEach { row ->
      parsedInput[row].indices.forEach { col ->
        val currentCoords = Coords(row, col)
        unvisitedNodes.add(currentCoords)
        val currentNode = Node(currentCoords, parsedInput[row][col])
        if (col < parsedInput[row].size - 1) {
          val newNode = Node(currentCoords.copy(col = col + 1), parsedInput[row][col + 1])
          graph.addEdge(currentNode, newNode, newNode.risk)
        }

        if (row < parsedInput.size - 1) {
          val newNode = Node(currentCoords.copy(row = row + 1), parsedInput[row + 1][col])
          graph.addEdge(currentNode, newNode, newNode.risk)
        }
      }
    }

    var currentNodeId = unvisitedNodes.poll()
    val endNodeId = Coords(parsedInput.size - 1, parsedInput[0].size - 1)

    while (true) {
      if (currentNodeId == endNodeId) {
        break
      }
      graph.getNeighbors(currentNodeId).forEach { (neighbor, weight) ->
        if (!neighbor.visited) {
          val newDist = distances[currentNodeId]!! + weight
          if (newDist < distances.getOrDefault(neighbor.id, Int.MAX_VALUE)) {
            distances[neighbor.id] = newDist
            unvisitedNodes.remove(neighbor.id)
            unvisitedNodes.add(neighbor.id)
          }
        }
      }
      graph.getNode(currentNodeId).visited = true
      currentNodeId = unvisitedNodes.poll()
    }

    return distances[endNodeId]!!
  }

  fun part2(input: List<String>): Int {
    val parsedInput = parseInput(input)
    val graph = DijkstraGraph<Coords>()

    val distances = mutableMapOf(Coords(0, 0) to 0)
    val compareByDistance: Comparator<Coords> = compareBy { distances.getOrDefault(it, Int.MAX_VALUE) }
    val unvisitedNodes = PriorityQueue(compareByDistance)
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
        unvisitedNodes.add(currentCoords)
        val currentNode = Node(currentCoords, nodeWeight(row, col))
        if (col > 0) {
          val newNode = Node(currentCoords.copy(col = col - 1), nodeWeight(row, col - 1))
          graph.addEdge(currentNode, newNode, newNode.risk)
        }
        if (col < maxCol) {
          val newNode = Node(currentCoords.copy(col = col + 1), nodeWeight(row, col + 1))
          graph.addEdge(currentNode, newNode, newNode.risk)
        }

        if (row > 0) {
          val newNode = Node(currentCoords.copy(row = row - 1), nodeWeight(row - 1, col))
          graph.addEdge(currentNode, newNode, newNode.risk)
        }
        if (row < maxRow) {
          val newNode = Node(currentCoords.copy(row = row + 1), nodeWeight(row + 1, col))
          graph.addEdge(currentNode, newNode, newNode.risk)
        }
      }
    }

    var currentNodeId = unvisitedNodes.poll()
    val endNodeId = Coords(maxRow, maxCol)
    println(endNodeId)

    while (currentNodeId != null) {
      if (currentNodeId == endNodeId) {
        break
      }
      graph.getNeighbors(currentNodeId).forEach { (neighbor, weight) ->
        if (!neighbor.visited) {
          val newDist = distances[currentNodeId]!! + weight
          if (newDist < distances.getOrDefault(neighbor.id, Int.MAX_VALUE)) {
            distances[neighbor.id] = newDist
            unvisitedNodes.remove(neighbor.id)
            unvisitedNodes.add(neighbor.id)
          }
        }
      }
      graph.getNode(currentNodeId).visited = true
      currentNodeId = unvisitedNodes.poll()
    }

    return distances[endNodeId]!!
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
