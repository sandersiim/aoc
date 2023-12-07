package aoc2022.day6

import org.assertj.core.api.Assertions
import readInput
import runSolution

fun main() {
  fun solve(input: String, packetSize: Int): Int {
    input.windowed(packetSize).forEachIndexed { index, s ->
      if (s.toCharArray().toSet().size == packetSize) {
        return@solve index + packetSize
      }
    }
    return -1
  }

  fun part1(inputs: List<String>): Int {
    return solve(inputs[0], 4)
  }

  fun part2(inputs: List<String>): Int {
    return solve(inputs[0], 14)
  }

  val inputDir = "src/aoc2022/day6/inputs"

  val testResult = runSolution("Part1 test") { part1(readInput("test", inputDir)) }
  Assertions.assertThat(testResult).isEqualTo(11)

  val input = readInput("input", inputDir)
  runSolution("Part1") { part1(input) }

  val test2Result = runSolution("Part2 test") { part2(readInput("test", inputDir)) }
  Assertions.assertThat(test2Result).isEqualTo(26)

  runSolution("Part2") { part2(input) }

}
