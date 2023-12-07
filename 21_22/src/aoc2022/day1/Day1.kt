package aoc2022.day1

import org.assertj.core.api.Assertions
import readInput
import runSolution

fun main() {
  fun getElfCalories(input: List<String>): List<Int> {
    val elfCalories = mutableListOf<Int>()
    var current = 0
    input.forEach {
      if (it.isEmpty()) {
        elfCalories.add(current)
        current = 0
      } else {
        current += it.toInt()
      }
    }
    elfCalories.add(current)

    return elfCalories
  }

  fun part1(input: List<String>): Int {
    return getElfCalories(input).max()
  }

  fun part2(input: List<String>): Int {
    return getElfCalories(input).sortedDescending().take(3).sum()
  }

  val inputDir = "src/aoc2022/day1/inputs"

  val testResult = runSolution("Part1 test") { part1(readInput("test", inputDir)) }
  Assertions.assertThat(testResult).isEqualTo(24000)

  val input = readInput("input", inputDir)
  runSolution("Part1") { part1(input) }

  val test2Result = runSolution("Part2 test") { part2(readInput("test", inputDir)) }
  Assertions.assertThat(test2Result).isEqualTo(45000)

  runSolution("Part2") { part2(input) }

}
