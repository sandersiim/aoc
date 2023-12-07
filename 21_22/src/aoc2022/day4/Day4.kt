package aoc2022.day4

import rangeIntersect
import org.assertj.core.api.Assertions
import readInput
import runSolution
import size

fun main() {
  fun parseInput(input: List<String>): List<Pair<IntRange, IntRange>> {
    val regex = Regex("""(\d+)-(\d+),(\d+)-(\d+)""")
    return input.map {
      val nums = regex.matchEntire(it)!!.groupValues.drop(1).map { it.toInt() }
      Pair(IntRange(nums[0], nums[1]), IntRange(nums[2], nums[3]))
    }
  }

  fun part1(input: List<String>): Int {
    return parseInput(input).count { pair ->
      val intersectSize = pair.first.rangeIntersect(pair.second).size()
      intersectSize == pair.first.size()  || intersectSize == pair.second.size()
    }
  }

  fun part2(input: List<String>): Int {
    return parseInput(input).count { pair ->
      !pair.first.rangeIntersect(pair.second).isEmpty()
    }
  }

  val inputDir = "src/aoc2022/day4/inputs"

  val testResult = runSolution("Part1 test") { part1(readInput("test", inputDir)) }
  Assertions.assertThat(testResult).isEqualTo(2)

  val input = readInput("input", inputDir)
  runSolution("Part1") { part1(input) }

  val test2Result = runSolution("Part2 test") { part2(readInput("test", inputDir)) }
  Assertions.assertThat(test2Result).isEqualTo(4)

  runSolution("Part2") { part2(input) }

}
