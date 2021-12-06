import org.assertj.core.api.Assertions.assertThat
import java.util.Collections

fun main() {
  fun solve(input: List<String>, days: Int): Long {
    println("Input size: ${input.size}")
    val fish = input[0].split(",").mapNotNull { it.toLongOrNull() }
    val numbersOfFishes = List(9) { 0L }.toMutableList()
    fish.forEach { numbersOfFishes[it.toInt()] += 1L }
    println("Initial population: $numbersOfFishes")
    println("Initial population size: ${numbersOfFishes.sum()}")

    repeat(days) {
      val numNewFish = numbersOfFishes[0]
      Collections.rotate(numbersOfFishes, -1)
      numbersOfFishes[8] = numNewFish
      numbersOfFishes[6] += numNewFish
    }
    println("End population: $numbersOfFishes")

    return numbersOfFishes.sum()
  }

  val testInput = readInput("day6_test")
  val testResult1 = runSolution("Part1 test") { solve(testInput, 18) }
  assertThat(testResult1).isEqualTo(26)
  val testResult12 = runSolution("Part1 test") { solve(testInput, 80) }
  assertThat(testResult12).isEqualTo(5934)

  val input = readInput("day6_input")

  runSolution("Part1") { solve(input, 80) }

  val testResult2 = runSolution("Part2 test") { solve(testInput, 256) }
  assertThat(testResult2).isEqualTo(26984457539)

  runSolution("Part2") { solve(input, 256) }
}
