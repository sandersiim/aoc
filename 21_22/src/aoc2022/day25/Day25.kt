package aoc2022.day25

import LazyMap
import org.assertj.core.api.Assertions
import readInput
import runSolution

fun main() {
  fun snafuToInt(snafu: String): Long {
    var result = 0L
    var factor = 1L
    snafu.reversed().forEach { digit ->
      result += when (digit) {
        '2' -> 2 * factor
        '1' -> factor
        '0' -> 0
        '-' -> factor.unaryMinus()
        '=' -> -2 * factor
        else -> throw IllegalArgumentException(digit.toString())
      }
      factor *= 5
    }

    return result
  }
  fun decimalDigitToSnafuDigit(digit: Int): String {
    return when (digit) {
      2 -> "2"
      1 -> "1"
      0 -> "0"
      -1 -> "-"
      -2 -> "="
      else -> throw IllegalArgumentException("$digit")
    }
  }

  fun intToSnafu(num: Long): String {
    fun largestNumWithFactor(factor: Long): Long {
      if (factor < 0L) {
        throw IllegalArgumentException(factor.toString())
      }
      if (factor == 0L) {
        return 0
      }
      if (factor == 1L) {
        return 2
      }

      return 2 * factor + largestNumWithFactor(factor/5)
    }
    val largestNumMap = LazyMap(::largestNumWithFactor)

    var factor = 1L
    while (num > largestNumMap.getValue(factor)) {
      factor *= 5
    }
    println("First factor: $factor")
    println(largestNumMap.getValue(factor/5))
    println((num + largestNumMap.getValue(factor/5)))

    val firstDiv = (num + largestNumMap.getValue(factor/5)) / factor
    println(firstDiv)
    var result = "$firstDiv"
    var remainder = num - (firstDiv*factor)
    factor /= 5
    println("Current: $result")
    println("Remainder: $remainder")

    while (factor > 0L) {
      println("Factor: $factor")
      val x = ((remainder + largestNumMap.getValue(factor)) / factor).toInt() - 2
      println(x)
      remainder -= x*factor
      result += decimalDigitToSnafuDigit(x)
      factor /= 5
      println("Current: $result")
      println("Remainder: $remainder")
    }
    println(result)

    return result
  }

  fun part1(input: List<String>): String {
    val result = input.sumOf { snafuToInt(it) }

    println("Decimal result: $result")

    return intToSnafu(result)
  }

  fun part2(input: List<String>): String {
    return ""
  }

  val inputDir = "src/aoc2022/day25/inputs"

  val testResult = runSolution("Part1 test") { part1(readInput("test", inputDir)) }
  Assertions.assertThat(testResult).isEqualTo("2=-1=0")

  val input = readInput("input", inputDir)
  runSolution("Part1") { part1(input) }

//  val test2Result = runSolution("Part2 test") { part2(readInput("test", inputDir)) }
//  Assertions.assertThat(test2Result).isEqualTo(4)
//
//  runSolution("Part2") { part2(input) }

}
