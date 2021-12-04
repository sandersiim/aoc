import org.assertj.core.api.Assertions.assertThat

fun main() {
  fun part1(input: List<String>): UInt {
    println("Input size: ${input.size}")
    val bitLength = input.first().length

    println("Bit length: $bitLength")
    val positionalSums = Array(bitLength) { 0 }
    input.forEach { bitString ->
      bitString
        .toCharArray()
        .forEachIndexed { index, bit ->
          positionalSums[index] += (bit - '0')
        }
    }

    val gammaBitString = positionalSums.map {
      when(it > input.size / 2) {
        true -> '1'
        false -> '0'
      }
    }.joinToString("")
    println("Gamma rate bitstring: $gammaBitString")

    val gammaRate = gammaBitString.toUInt(2)
    val mask = (1u shl (bitLength)) - 1u
    println("bit mask: ${mask.toString(2)}")
    val epsilonRate = gammaRate xor mask
    println("Epsilon rate bitstring: ${epsilonRate.toString(2)}")

    println("Gamma rate: $gammaRate, epsilon rate: $epsilonRate")

    return gammaRate * epsilonRate
  }

  fun part2(input: List<String>): UInt {
    return 0u
  }

  val testInput = readInput("day3_test")
  val testResult1 = runSolution("Part1 test") { part1(testInput) }
  assertThat(testResult1).isEqualTo(198u)

  val testResult2 = runSolution("Part2 test") { part2(testInput) }
  assertThat(testResult2).isEqualTo(230u)

  val input = readInput("day3_input")

  runSolution("Part1") { part1(input) }

  runSolution("Part2") { part2(input) }
}
