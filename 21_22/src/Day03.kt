import org.assertj.core.api.Assertions.assertThat

fun main() {
  fun getMostCommonBits(input: List<CharArray>): List<Char> {
    val bitLength = input.first().size

    val positionalSums = Array(bitLength) { 0 }
    input.forEach { bitString ->
      bitString
        .forEachIndexed { index, bit ->
          positionalSums[index] += (bit - '0')
        }
    }

    return positionalSums.map {
      when(it >= input.size / 2.0) {
        true -> '1'
        false -> '0'
      }
    }
  }

  fun part1(input: List<String>): UInt {
    println("Input size: ${input.size}")
    val bitLength = input.first().length
    println("Bit length: $bitLength")

    val gammaBitString = getMostCommonBits(input.map { it.toCharArray() }).joinToString("")
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
    val bitLength = input.first().length
    println("Bit length: $bitLength")

    var oxygenRatingCandidates = input.map { it.toCharArray() }
    var co2RatingCandidates = input.map { it.toCharArray() }
    (0 until bitLength).forEach { bitIndex ->
      if (oxygenRatingCandidates.size > 1) {
        oxygenRatingCandidates = oxygenRatingCandidates.filter {
          it[bitIndex] == getMostCommonBits(oxygenRatingCandidates)[bitIndex]
        }
      }

      if (co2RatingCandidates.size > 1) {
        co2RatingCandidates = co2RatingCandidates.filter {
          it[bitIndex] != getMostCommonBits(co2RatingCandidates)[bitIndex]
        }
      }
    }

    val oxygenRating = oxygenRatingCandidates.first().joinToString("").toUInt(2)
    val co2Rating = co2RatingCandidates.first().joinToString("").toUInt(2)
    println("Oxygen rating: $oxygenRating, co2 rating: $co2Rating")

    return oxygenRating * co2Rating
  }

  val testInput = readInput("day3_test")
  val testResult1 = runSolution("Part1 test") { part1(testInput) }
  assertThat(testResult1).isEqualTo(198u)

  val input = readInput("day3_input")

  runSolution("Part1") { part1(input) }

  val testResult2 = runSolution("Part2 test") { part2(testInput) }
  assertThat(testResult2).isEqualTo(230u)

  runSolution("Part2") { part2(input) }
}
