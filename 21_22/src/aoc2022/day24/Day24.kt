package aoc2022.day24

import Coords
import Direction
import lcm
import org.assertj.core.api.Assertions
import readInput
import runSolution
import java.util.ArrayDeque

data class Blizzard(
  val pos: Coords,
  val direction: Direction
) {
  fun move(rows: Int, cols: Int): Blizzard {
    val newPos = when (direction) {
      Direction.UP -> Coords((pos.row - 1 + rows).mod(rows), pos.col)
      Direction.DOWN -> Coords((pos.row + 1).mod(rows), pos.col)
      Direction.RIGHT -> Coords(pos.row, (pos.col + 1).mod(cols))
      Direction.LEFT -> Coords(pos.row, (pos.col - 1 + cols).mod(cols))
    }
    return Blizzard(newPos, direction)
  }
}

data class State(
  val currentTime: Int,
  val pos: Coords,
)

fun List<Blizzard>.printBlizzardMap(rows: Int, cols: Int) {
  (0 until rows).forEach { row ->
    val blizzardsAt = this.filter { it.pos.row == row }.groupBy { it.pos.col }
    var rowStr = ""
    (0 until cols).forEach { col ->
      val colBlizzards = blizzardsAt[col] ?: emptyList()
      if (colBlizzards.isEmpty()) {
        rowStr += "."
      } else if (colBlizzards.size > 1) {
        rowStr += colBlizzards.size
      } else {
        rowStr += colBlizzards[0].direction.charString()
      }
    }
    println(rowStr)
  }
}

fun main() {
  fun parseBlizzards(input: List<String>): List<Blizzard> {
    return (1 until input.size - 1).flatMap { rowIdx ->
      val row = input[rowIdx]
      row.substring(1, row.length - 1).mapIndexedNotNull { colIdx, c ->
        val pos = Coords(row = rowIdx - 1, col = colIdx)
        val dir = when (c) {
          '>' -> Direction.RIGHT
          '<' -> Direction.LEFT
          '^' -> Direction.UP
          'v' -> Direction.DOWN
          else -> null
        }
        dir?.let { Blizzard(pos, it) }
      }
    }
  }

  fun findMinPathFromStartToEnd(
    startPos: Coords,
    endPos: Coords,
    rows: Int,
    cols: Int,
    allBlizzardPositionSets: List<Set<Coords>>
  ): Map<State, Pair<State, Int>> {
    val actualStartPos = if (startPos.row == 0) {
      Coords(-1, 0)
    } else {
      Coords(rows, cols - 1)
    }
    val actualEndPos = if (endPos.row == 0) {
      Coords(-1, 0)
    } else {
      Coords(rows, cols - 1)
    }
    println("Looking for all min paths from $actualStartPos to $actualEndPos")

    fun State.nextValidStates(): List<State> {
      val nextTime = (currentTime - 1 + allBlizzardPositionSets.size).mod(allBlizzardPositionSets.size)

      val nextStates = pos.neighbours().filter {
        it.row >= 0 && it.col >= 0 && it.row < rows && it.col < cols && it !in allBlizzardPositionSets[nextTime]
      }.map { State(nextTime, it) }.toMutableList()
      if (pos !in allBlizzardPositionSets[nextTime]) {
        nextStates.add(State(nextTime, pos))
      }

      return nextStates
    }

    val bestPaths = mutableMapOf<State, Pair<State, Int>>()
    allBlizzardPositionSets.indices.filter { endPos !in allBlizzardPositionSets[it] }.forEach { i ->
      println("Looking for min paths for time $i")
      val to = State(i, endPos)
      val currentMinPathLengths = mutableMapOf<State, Int>()
      val statesToLookThrough = ArrayDeque<Pair<State, Int>>()
      statesToLookThrough.add(Pair(to, 0))

      var current: Pair<State, Int>
      while (statesToLookThrough.isNotEmpty()) {
        current = statesToLookThrough.removeFirst()
        if (
          currentMinPathLengths.containsKey(current.first) &&
          current.second >= currentMinPathLengths.getValue(current.first)
        ) {
          continue
        } else {
          currentMinPathLengths[current.first] = current.second
        }
        if (current.first.pos == startPos) {
          continue
        }

        current.first.nextValidStates().forEach { statesToLookThrough.addLast(Pair(it, current.second + 1)) }
      }
      allBlizzardPositionSets.indices.forEach {
        val startState = State(it, actualStartPos)
        val endState = State((to.currentTime + 1).mod(allBlizzardPositionSets.size), actualEndPos)
        val firstMoveState = State((it + 1).mod(allBlizzardPositionSets.size), startPos)

        if (currentMinPathLengths.containsKey(firstMoveState)) {
          val newMin = currentMinPathLengths.getValue(firstMoveState) + 2
          if (!bestPaths.containsKey(startState) || bestPaths.getValue(startState).second > newMin) {
            bestPaths[startState] = Pair(endState, newMin)
          }
        }
      }
    }

    allBlizzardPositionSets.indices.forEach {
      val startState = State(it, actualStartPos)

      if (!bestPaths.containsKey(startState)) {
        var nextBestPathTime = (it + 1 until allBlizzardPositionSets.size).firstOrNull { time ->
          bestPaths.containsKey(State(time, actualStartPos))
        }
        val difference: Int
        if (nextBestPathTime == null) {
          nextBestPathTime = (0 until it).first { time ->
            bestPaths.containsKey(State(time, actualStartPos))
          }
          difference = nextBestPathTime + allBlizzardPositionSets.size - it
        } else {
          difference = nextBestPathTime - it
        }
        val nextBestPath = bestPaths.getValue(State(nextBestPathTime, actualStartPos))
        bestPaths[startState] = Pair(
          nextBestPath.first,
          nextBestPath.second + difference
        )
      }
    }

    return bestPaths
  }

  fun part1(input: List<String>): Int {
    val totalRows = input.size - 2
    val totalCols = input.first().length - 2
    val blizzards = parseBlizzards(input)
    val lcm = lcm(totalRows, totalCols)
    println(totalRows)
    println(totalCols)
    println(lcm)

    var currentBlizzards = blizzards
    val allBlizzardPositionSets = (0 until lcm).map {
      val newPositions = mutableSetOf<Coords>()
      currentBlizzards.mapTo(newPositions) { it.pos }
      currentBlizzards = currentBlizzards.map { it.move(totalRows, totalCols) }
      newPositions
    }

    val minPathsFromStartToEnd = findMinPathFromStartToEnd(
      Coords(0, 0),
      Coords(totalRows - 1, totalCols - 1),
      totalRows,
      totalCols,
      allBlizzardPositionSets
    )
    println(minPathsFromStartToEnd)

    return minPathsFromStartToEnd.getValue(State(0, Coords(-1, 0))).second
  }

  fun part2(input: List<String>): Int {
    val totalRows = input.size - 2
    val totalCols = input.first().length - 2
    val blizzards = parseBlizzards(input)
    val lcm = lcm(totalRows, totalCols)
    println(totalRows)
    println(totalCols)
    println(lcm)

    var currentBlizzards = blizzards
    val allBlizzardPositionSets = (0 until lcm).map {
      val newPositions = mutableSetOf<Coords>()
      currentBlizzards.mapTo(newPositions) { it.pos }
      currentBlizzards = currentBlizzards.map { it.move(totalRows, totalCols) }
      newPositions
    }

    val minPathsFromStartToEnd = findMinPathFromStartToEnd(
      Coords(0, 0),
      Coords(totalRows - 1, totalCols - 1),
      totalRows,
      totalCols,
      allBlizzardPositionSets
    )
    println(minPathsFromStartToEnd)
    val minPathsFromEndToStart = findMinPathFromStartToEnd(
      Coords(totalRows - 1, totalCols - 1),
      Coords(0, 0),
      totalRows,
      totalCols,
      allBlizzardPositionSets
    )
    println(minPathsFromEndToStart)

    var bestPathLength = Int.MAX_VALUE
    allBlizzardPositionSets.indices.forEach {
      val bestPathToEnd = minPathsFromStartToEnd.getValue(State(it, Coords(-1, 0)))
      val bestPathBack = minPathsFromEndToStart.getValue(bestPathToEnd.first)
      val bestPathAgainToEnd = minPathsFromStartToEnd.getValue(bestPathBack.first)
      val totalLength = it + bestPathToEnd.second + bestPathBack.second + bestPathAgainToEnd.second
      if (totalLength < bestPathLength) {
        bestPathLength = totalLength
        println("New best total length: $totalLength")
        println(bestPathToEnd)
        println(bestPathBack)
        println(bestPathAgainToEnd)
      }
    }

    return bestPathLength
  }

  val inputDir = "src/aoc2022/day24/inputs"

  val testResult = runSolution("Part1 test") { part1(readInput("test", inputDir)) }
  Assertions.assertThat(testResult).isEqualTo(18)

  val input = readInput("input", inputDir)
//  val part1Res = runSolution("Part1") { part1(input) }
//  Assertions.assertThat(part1Res).isEqualTo(230)

  val test2Result = runSolution("Part2 test") { part2(readInput("test", inputDir)) }
  Assertions.assertThat(test2Result).isEqualTo(54)

  runSolution("Part2") { part2(input) }
}
