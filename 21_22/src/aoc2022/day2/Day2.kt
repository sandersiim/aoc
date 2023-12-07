package aoc2022.day2

import org.assertj.core.api.Assertions
import readInput
import runSolution

enum class RockPaperScissors(val score: Int) {
  ROCK(1),
  PAPER(2),
  SCISSORS(3)
}

fun main() {
  fun part1(input: List<String>): Int {
    return input.sumOf {
      val opponent = when (it[0]) {
        'A' -> RockPaperScissors.ROCK
        'B' -> RockPaperScissors.PAPER
        'C' -> RockPaperScissors.SCISSORS
        else -> throw IllegalArgumentException(it)
      }
      val me = when (it[2]) {
        'X' -> RockPaperScissors.ROCK
        'Y' -> RockPaperScissors.PAPER
        'Z' -> RockPaperScissors.SCISSORS
        else -> throw IllegalArgumentException(it)
      }
      val outcome = if (opponent == me) {
        3
      } else if (
        opponent == RockPaperScissors.ROCK && me == RockPaperScissors.PAPER ||
        opponent == RockPaperScissors.PAPER && me == RockPaperScissors.SCISSORS ||
        opponent == RockPaperScissors.SCISSORS && me == RockPaperScissors.ROCK
      ) {
        6
      } else {
        0
      }

      me.score + outcome
    }
  }

  fun part2(input: List<String>): Int {
    return input.sumOf {
      val opponent = when (it[0]) {
        'A' -> RockPaperScissors.ROCK
        'B' -> RockPaperScissors.PAPER
        'C' -> RockPaperScissors.SCISSORS
        else -> throw IllegalArgumentException(it)
      }
      val me = when (it[2]) {
        'X' -> when (opponent) {
          RockPaperScissors.ROCK -> RockPaperScissors.SCISSORS
          RockPaperScissors.PAPER -> RockPaperScissors.ROCK
          RockPaperScissors.SCISSORS -> RockPaperScissors.PAPER
        }
        'Y' -> opponent
        'Z' -> when (opponent) {
          RockPaperScissors.ROCK -> RockPaperScissors.PAPER
          RockPaperScissors.PAPER -> RockPaperScissors.SCISSORS
          RockPaperScissors.SCISSORS -> RockPaperScissors.ROCK
        }
        else -> throw IllegalArgumentException(it)
      }
      val outcome = if (opponent == me) {
        3
      } else if (
        opponent == RockPaperScissors.ROCK && me == RockPaperScissors.PAPER ||
        opponent == RockPaperScissors.PAPER && me == RockPaperScissors.SCISSORS ||
        opponent == RockPaperScissors.SCISSORS && me == RockPaperScissors.ROCK
      ) {
        6
      } else {
        0
      }

      me.score + outcome
    }
  }

  val inputDir = "src/aoc2022/day2/inputs"

  val testResult = runSolution("Part1 test") { part1(readInput("test", inputDir)) }
  Assertions.assertThat(testResult).isEqualTo(15)

  val input = readInput("input", inputDir)
  runSolution("Part1") { part1(input) }

  val test2Result = runSolution("Part2 test") { part2(readInput("test", inputDir)) }
  Assertions.assertThat(test2Result).isEqualTo(12)

  runSolution("Part2") { part2(input) }

}
