package day14

import org.assertj.core.api.Assertions.assertThat
import readInput
import runSolution

data class ParsedInput(
  val template: String,
  val rules: Map<String, String>
)

fun main() {

  fun parseInput(input: List<String>): ParsedInput {
    val template = input[0]

    val rules = input.drop(2).associate { line ->
      line.split(" -> ").let { it[0] to it[1] }
    }

    return ParsedInput(template, rules)
  }

  fun solve(input: List<String>, steps: Int): Long {
    val (template, rules) = parseInput(input)
    rules.forEach(::println)
    println(template)

    val initialPairCounts = template
      .zipWithNext()
      .groupingBy { "${it.first}${it.second}" }
      .eachCount()
      .mapValues { it.value.toLong() }
    println(initialPairCounts)

    val finalPairCounts = (1..steps).fold(initialPairCounts) { acc, _ ->
      val newPairCounts = acc.toMutableMap()
      acc.forEach { (rulePair, num) ->
        if (rules.containsKey(rulePair)) {
          val insertedElement = rules[rulePair]!!
          val newPair1 = rulePair[0] + insertedElement
          val newPair2 = insertedElement + rulePair[1]
          if (!newPairCounts.containsKey(newPair1)) {
            newPairCounts[newPair1] = 0
          }
          if (!newPairCounts.containsKey(newPair2)) {
            newPairCounts[newPair2] = 0
          }
          newPairCounts[newPair1] = newPairCounts[newPair1]!!.plus(num)
          newPairCounts[newPair2] = newPairCounts[newPair2]!!.plus(num)
          newPairCounts[rulePair] = newPairCounts[rulePair]!!.minus(num)
        }
      }
      println(newPairCounts)
      newPairCounts
    }

    val charCountsMap = mutableMapOf(template.first() to 1L)

    finalPairCounts.forEach {
      if (!charCountsMap.containsKey(it.key[1])) {
        charCountsMap[it.key[1]] = 0
      }
      charCountsMap[it.key[1]] = charCountsMap[it.key[1]]!!.plus(it.value)
    }
    val charCountsSorted = charCountsMap.values.sorted()

    return charCountsSorted.last() - charCountsSorted.first()
  }

  fun part1(input: List<String>): Long {
    return solve(input, 10)
  }

  fun part2(input: List<String>): Long {
    return solve(input, 40)
  }

  val testInput = readInput("day14_test")
  val testResult = runSolution("Part1 test") { part1(testInput) }
  assertThat(testResult).isEqualTo(1588)

  val input = readInput("day14_input")

  runSolution("Part1") { part1(input) }

  val testResult2 = runSolution("Part2 test") { part2(testInput) }
  assertThat(testResult2).isEqualTo(2188189693529)

  runSolution("Part2") { part2(input) }
}
