package aoc2022.day20

import org.assertj.core.api.Assertions
import readInput
import runSolution
import java.util.LinkedList
import kotlin.math.absoluteValue

fun main() {
  fun parseInput(input: List<String>): List<Long> {
    return input.map { it.toLong() }
  }

  fun part1(input: List<String>): Long {
    val numbers = parseInput(input)
    println(numbers)
    val indexList = LinkedList((numbers.indices).toList())

    numbers.indices.forEach { i ->
      val value = numbers[i]
      val currentIndex = indexList.indexOf(i)
      var newIndex = currentIndex + value
      if (newIndex < 0) {
        val numOfAdditions = newIndex.absoluteValue / (numbers.size - 1)
        newIndex += (numbers.size - 1) * (numOfAdditions + 1)
      } else if (newIndex >= numbers.size) {
        val numOfSubtractions = newIndex / (numbers.size - 1)
        newIndex -= (numbers.size - 1) * numOfSubtractions
      }

      indexList.removeAt(currentIndex)
      indexList.add(newIndex.toInt(), i)
    }

    val mixedNumbers = indexList.map { numbers[it] }
    println(mixedNumbers)

    val zeroIndex = mixedNumbers.indexOf(0)
    val c1 = mixedNumbers[(zeroIndex + 1000).mod(numbers.size)]
    val c2 = mixedNumbers[(zeroIndex + 2000).mod(numbers.size)]
    val c3 = mixedNumbers[(zeroIndex + 3000).mod(numbers.size)]

    return c1 + c2 + c3
  }

  fun part2(input: List<String>): Long {
    val decKey = 811589153L
    val numbers = parseInput(input).map { it * decKey }
    println(numbers)
    val indexList = LinkedList((numbers.indices).toList())

    repeat(10) {
      numbers.indices.forEach { i ->
        val value = numbers[i]
        val currentIndex = indexList.indexOf(i)
        var newIndex = currentIndex + value
        if (newIndex < 0) {
          val numOfAdditions = newIndex.absoluteValue / (numbers.size - 1)
          newIndex += (numbers.size - 1) * (numOfAdditions + 1)
        } else if (newIndex >= numbers.size) {
          val numOfSubtractions = newIndex / (numbers.size - 1)
          newIndex -= (numbers.size - 1) * numOfSubtractions
        }

        indexList.removeAt(currentIndex)
        indexList.add(newIndex.toInt(), i)
      }
    }

    val mixedNumbers = indexList.map { numbers[it] }
    println(mixedNumbers)

    val zeroIndex = mixedNumbers.indexOf(0)
    val c1 = mixedNumbers[(zeroIndex + 1000).mod(numbers.size)]
    val c2 = mixedNumbers[(zeroIndex + 2000).mod(numbers.size)]
    val c3 = mixedNumbers[(zeroIndex + 3000).mod(numbers.size)]

    return c1 + c2 + c3
  }

  val inputDir = "src/aoc2022/day20/inputs"

  val testResult = runSolution("Part1 test") { part1(readInput("test", inputDir)) }
  Assertions.assertThat(testResult).isEqualTo(3)

  val input = readInput("input", inputDir)
  val part1Result = runSolution("Part1") { part1(input) }
  Assertions.assertThat(part1Result).isEqualTo(15297)

  val test2Result = runSolution("Part2 test") { part2(readInput("test", inputDir)) }
  Assertions.assertThat(test2Result).isEqualTo(1623178306)

  runSolution("Part2") { part2(input) }

}
