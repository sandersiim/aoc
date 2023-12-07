package aoc2022.day10

import org.assertj.core.api.Assertions
import readInput
import runSolution

fun main() {
  fun part1(input: List<String>): Int {
    var x = 1
    var cycle = 1
    var signalStrength = 0
    fun checkAndUpdateSignalStrength() {
      if ((cycle - 20) >= 0 && (cycle-20) % 40 == 0) {
        println(cycle)
        println(x)
        signalStrength += cycle * x
      }
    }

    input.forEach {
      if (it == "noop") {
        cycle += 1
        checkAndUpdateSignalStrength()
      } else {
        val change = it.substring(5).toInt()
        cycle += 1
        checkAndUpdateSignalStrength()
        cycle += 1
        x += change
        checkAndUpdateSignalStrength()
      }
    }

    return signalStrength
  }

  fun part2(input: List<String>): Int {
    var x = 1
    var cycle = 0
    val crt = (0 until 6).map {
      (0 until 40).map { '.' }.toMutableList()
    }
    fun checkPixelDraw() {
      val colPos = cycle % 40
      if (colPos == x - 1 || colPos == x || colPos == x + 1) {
        val row = cycle / 40
        crt[row][colPos] = '#'
      }
    }

    checkPixelDraw()

    input.forEach {
      if (it == "noop") {
        cycle += 1
        checkPixelDraw()
      } else {
        val change = it.substring(5).toInt()
        cycle += 1
        checkPixelDraw()
        cycle += 1
        x += change
        checkPixelDraw()
      }
    }

    crt.forEach { println(it.joinToString("")) }

    return 0
  }

  val inputDir = "src/aoc2022/day10/inputs"

  val testResult = runSolution("Part1 test") { part1(readInput("test", inputDir)) }
  Assertions.assertThat(testResult).isEqualTo(13140)

  val input = readInput("input", inputDir)
  runSolution("Part1") { part1(input) }

  val test2Result = runSolution("Part2 test") { part2(readInput("test", inputDir)) }
  Assertions.assertThat(test2Result).isEqualTo(0)

  runSolution("Part2") { part2(input) }

}
