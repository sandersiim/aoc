import org.assertj.core.api.Assertions.assertThat
import kotlin.math.abs

fun main() {
  data class Stats(
    val min: Int,
    val max: Int,
    val median: Double,
    val avg: Double
  )

  fun calcStats(positions: List<Int>): Stats {
    val sorted = positions.sorted()
    val l = sorted.size
    val min = sorted[0]
    val max = sorted[sorted.size - 1]

    val median = if (l % 2 == 0) {
      (sorted[l / 2] + sorted[l / 2 - 1]) / 2.0
    } else {
      sorted[l / 2].toDouble()
    }

    return Stats(min, max, median, sorted.average())
  }

  fun part1(input: List<String>): Int {
    val positions = input[0].split(",").mapNotNull { it.toIntOrNull() }
    println("Input size: ${positions.size}")

    val stats = calcStats(positions)
    println("Stats: $stats")

    return positions.sumOf { abs(it - stats.median.toInt()) }
  }

  fun part2(input: List<String>): Int {
    val positions = input[0].split(",").mapNotNull { it.toIntOrNull() }

    val stats = calcStats(positions)
    println("Stats: $stats")

    fun cost(positions: List<Int>, pos: Int): Int {
      return positions.sumOf {
        val diff = abs(it - pos)
        diff * (1 + diff) / 2
      }
    }

    val result = (1 until (positions.size - 1)).minOf {
      cost(positions, it)
    }

    return result
  }

  val testInput = readInput("day7_test")
  val testResult1 = runSolution("Part1 test") { part1(testInput) }
  assertThat(testResult1).isEqualTo(37)

  val input = readInput("day7_input")

  runSolution("Part1") { part1(input) }

  val testResult2 = runSolution("Part2 test") { part2(testInput) }
  assertThat(testResult2).isEqualTo(168)

  runSolution("Part2") { part2(input) }
}
