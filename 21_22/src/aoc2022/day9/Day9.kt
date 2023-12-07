package aoc2022.day9

import Coords
import Direction
import org.assertj.core.api.Assertions
import readInput
import runSolution
import kotlin.math.sign

fun main() {
  fun parseDir(c: Char): Direction {
    return when (c) {
      'R' -> Direction.RIGHT
      'L' -> Direction.LEFT
      'D' -> Direction.DOWN
      'U' -> Direction.UP
      else -> throw IllegalArgumentException("$c")
    }
  }

  fun part1(input: List<String>): Int {
    val visitedCoords = mutableSetOf(Coords(0, 0))
    var head = Coords(0, 0)
    var tail = Coords(0, 0)

    input.forEach { line ->
      val direction = parseDir(line[0])
      val moveNum = line.substring(2).toInt()
      repeat(moveNum) {
        head = head.moveInDirection(direction)
        if (!tail.touches(head)) {
          tail = Coords(tail.row + (head.row - tail.row).sign, tail.col + (head.col - tail.col).sign)
          visitedCoords.add(tail)
        }
      }
    }

    return visitedCoords.size
  }

  fun part2(input: List<String>): Int {
    val visitedCoords = mutableSetOf(Coords(0, 0))
    val rope = MutableList(10) { Coords(0, 0) }

    input.forEach { line ->
      val direction = parseDir(line[0])
      val moveNum = line.substring(2).toInt()
      repeat(moveNum) {
        rope[0] = rope[0].moveInDirection(direction)
        (1 until rope.size).forEach { i ->
          if (!rope[i].touches(rope[i-1])) {
            rope[i] = Coords(
              rope[i].row + (rope[i-1].row - rope[i].row).sign,
              rope[i].col + (rope[i-1].col - rope[i].col).sign,
            )
          }
        }
        visitedCoords.add(rope.last())
      }
    }

    return visitedCoords.size
  }

  val inputDir = "src/aoc2022/day9/inputs"

  val testResult = runSolution("Part1 test") { part1(readInput("test", inputDir)) }
  Assertions.assertThat(testResult).isEqualTo(13)

  val input = readInput("input", inputDir)
  runSolution("Part1") { part1(input) }

  val test2Result = runSolution("Part2 test") { part2(readInput("test2", inputDir)) }
  Assertions.assertThat(test2Result).isEqualTo(36)

  runSolution("Part2") { part2(input) }

}
