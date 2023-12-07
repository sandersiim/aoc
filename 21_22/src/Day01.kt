import org.assertj.core.api.Assertions.assertThat

fun main() {
  fun part1(input: List<String>): Int {
    println("Input size: ${input.size}")
    val pairs = input.map { it.toInt() }.zipWithNext()
    println("Pairs size: ${pairs.size}")

    return pairs.count { it.second > it.first }
  }

  fun part2(input: List<String>): Int {
    val sumsOfTriples = input
      .map { it.toInt() }
      .windowed(3)
      .map { it.sum() }
    val pairsOfSums = sumsOfTriples.zipWithNext()

    println("Pairs of triple sums size: ${pairsOfSums.size}")

    return pairsOfSums.count { it.second > it.first }
  }

  val testInput = readInput("day1_test")
  val testResult = runSolution("Part1 test") { part1(testInput) }

  assertThat(testResult).isEqualTo(7)

  val input = readInput("day1_input")

  runSolution("Part1") { part1(input) }

  runSolution("Part2") { part2(input) }
}
