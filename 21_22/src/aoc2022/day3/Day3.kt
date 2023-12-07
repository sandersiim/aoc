package aoc2022.day3

import org.assertj.core.api.Assertions
import readInput
import runSolution

fun main() {
  fun stringToCharSet(s: String): Set<Char> {
    return s.toCharArray().toSet()
  }

  fun commonItemInRucksack(sack: String): Char {
    val s1 = sack.take(sack.length / 2)
    val s2 = sack.takeLast(sack.length / 2)
    return stringToCharSet(s1).intersect(stringToCharSet(s2)).first()
  }

  fun Char.priority(): Int {
    return if (this.isLowerCase()) {
      this.code - 'a'.code + 1
    } else {
      this.code - 'A'.code + 27
    }
  }
  fun part1(input: List<String>): Int {
    return input.map(::commonItemInRucksack).sumOf { it.priority() }
  }

  fun part2(input: List<String>): Int {
    return input.windowed(3, 3).sumOf {
      val commons = it.fold(stringToCharSet(it.first())) { acc, sack -> acc.intersect(stringToCharSet(sack)) }
      commons.first().priority()
    }
  }

  val inputDir = "src/aoc2022/day3/inputs"

  val testResult = runSolution("Part1 test") { part1(readInput("test", inputDir)) }
  Assertions.assertThat(testResult).isEqualTo(157)

  val input = readInput("input", inputDir)
  runSolution("Part1") { part1(input) }

  val test2Result = runSolution("Part2 test") { part2(readInput("test", inputDir)) }
  Assertions.assertThat(test2Result).isEqualTo(70)

  runSolution("Part2") { part2(input) }

}
