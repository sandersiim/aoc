import org.assertj.core.api.Assertions.assertThat

val numberOfSegments = mapOf(
  0 to 6,
  1 to 2,
  2 to 5,
  3 to 5,
  4 to 4,
  5 to 5,
  6 to 6,
  7 to 3,
  8 to 7,
  9 to 6
)

fun main() {
  data class ParsedInput(
    val allPatterns: List<CharArray>,
    val outputPatterns: List<CharArray>
  )

  fun CharArray.isEqualTo(other: CharArray): Boolean {
    if (this.size != other.size) {
      return false
    }
    this.forEachIndexed { idx, c ->
      if (other[idx] != c) {
        return false
      }
    }

    return true
  }

  fun CharArray.overlapSizeWith(other: CharArray): Int {
    return if (other.size > this.size) {
      this.count { it in other }
    } else {
      other.count { it in this }
    }
  }

  fun CharArray.includesPattern(other: CharArray): Boolean {
    return this.overlapSizeWith(other) == other.size
  }

  fun parseInput(input: List<String>): List<ParsedInput> {
    return input.map { inputLine ->
      val (allPatternsString, outputPatternsString) = inputLine.split(" | ")
      ParsedInput(
        allPatternsString.split(" ").map { it.toCharArray().sortedArray() },
        outputPatternsString.split(" ").map { it.toCharArray().sortedArray() }
      )
    }
  }

  fun part1(input: List<String>): Int {
    println("Input size: ${input.size}")
    val parsedInput = parseInput(input)
    val uniqueLengths = listOf(1, 4, 7, 8).map { numberOfSegments[it] }

    return parsedInput.sumOf { displayData ->
      displayData.outputPatterns.filter { it.size in uniqueLengths }.size
    }
  }

  fun part2(input: List<String>): Int {
    val parsedInput = parseInput(input)
    val nums = parsedInput.map { displayData ->
      val mapping = mutableMapOf<String, Int>()
      val one = displayData.allPatterns.find { it.size == numberOfSegments[1] }!!
      mapping[one.joinToString("")] = 1
      val four = displayData.allPatterns.find { it.size == numberOfSegments[4] }!!
      mapping[four.joinToString("")] = 4
      val seven = displayData.allPatterns.find { it.size == numberOfSegments[7] }!!
      mapping[seven.joinToString("")] = 7
      val eight = displayData.allPatterns.find { it.size == numberOfSegments[8] }!!
      mapping[eight.joinToString("")] = 8

      val three = displayData.allPatterns.find { pattern ->
        pattern.size == numberOfSegments[3] && pattern.includesPattern(one)
      }!!
      mapping[three.joinToString("")] = 3

      val five = displayData.allPatterns.find { pattern ->
        pattern.size == numberOfSegments[5] &&
            !pattern.isEqualTo(three) &&
            pattern.overlapSizeWith(four) == 3
      }!!
      mapping[five.joinToString("")] = 5

      val two = displayData.allPatterns.find { pattern ->
        pattern.size == numberOfSegments[2] &&
            !pattern.isEqualTo(three) &&
            !pattern.isEqualTo(five)
      }!!
      mapping[two.joinToString("")] = 2

      val six = displayData.allPatterns.find { pattern ->
        pattern.size == numberOfSegments[6] &&
            pattern.overlapSizeWith(one) == 1
      }!!
      mapping[six.joinToString("")] = 6

      val zero = displayData.allPatterns.find { pattern ->
        pattern.size == numberOfSegments[0] &&
            !pattern.isEqualTo(six) &&
            pattern.overlapSizeWith(four) == 3
      }!!
      mapping[zero.joinToString("")] = 0

      val nine = displayData.allPatterns.find { pattern ->
        pattern.size == numberOfSegments[9] &&
            pattern.includesPattern(four)
      }!!
      mapping[nine.joinToString("")] = 9
      println(mapping)

      mapping[displayData.outputPatterns[0].joinToString("")]!! * 1000 +
        mapping[displayData.outputPatterns[1].joinToString("")]!! * 100 +
        mapping[displayData.outputPatterns[2].joinToString("")]!! * 10 +
        mapping[displayData.outputPatterns[3].joinToString("")]!!
    }
    println(nums)

    return nums.sum()
  }

  val testInput = readInput("day8_test")
  val testResult1 = runSolution("Part1 test") { part1(testInput) }
  assertThat(testResult1).isEqualTo(26)

  val input = readInput("day8_input")

  runSolution("Part1") { part1(input) }

  val testResult2 = runSolution("Part2 test") { part2(testInput) }
  assertThat(testResult2).isEqualTo(61229)

  runSolution("Part2") { part2(input) }
}
