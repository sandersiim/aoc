package day20

import org.assertj.core.api.Assertions.assertThat
import readInput
import runSolution

enum class Pixel(val char: Char) {
  DARK('.'),
  LIGHT('#');

  companion object {
    fun fromChar(c: Char): Pixel {
      return when (c) {
        DARK.char -> DARK
        LIGHT.char -> LIGHT
        else -> throw Error("Cannot create pixel from $c")
      }
    }
  }
}

fun Pixel.repeat(n: Int) = generateSequence { char }.take(n).joinToString("")
fun Pixel.applyAlgorithm(algorithm: String) = when (this) {
  Pixel.DARK -> Pixel.fromChar(algorithm.first())
  Pixel.LIGHT -> Pixel.fromChar(algorithm.last())
}

fun List<String>.expandImage(expandBy: Int, p: Pixel): List<String> {
  val currentSize = this.size
  return List(expandBy) { p.repeat(currentSize + 2 * expandBy) } +
      this.map { p.repeat(expandBy) + it + p.repeat(expandBy) } +
      List(expandBy) { p.repeat(currentSize + 2 * expandBy) }
}

fun List<String>.applyAlgorithm(algorithm: String): List<String> {
  val currentOutsidePixel = Pixel.fromChar(this[0][0])
  val newOutsidePixel = currentOutsidePixel.applyAlgorithm(algorithm)

  fun getBinaryNumFromPosition(row: Int, col: Int): Int {
    val colRange = (col-1 .. col+1)
    val pixelString = this[row-1].substring(colRange) +
        this[row].substring(colRange) +
        this[row+1].substring(colRange)
    val result = pixelString.map { if (it == Pixel.DARK.char) '0' else '1' }.joinToString("")
    return result.toInt(2)
  }

  val middleRows = (1..(size - 2)).map { row ->
    var s = newOutsidePixel.char.toString()
    (1..(size - 2)).forEach { col ->
      val newChar = algorithm[getBinaryNumFromPosition(row, col)]
      s = "$s$newChar"
    }
    s + newOutsidePixel.char.toString()
  }
  return listOf(newOutsidePixel.repeat(size)) +
      middleRows +
      listOf(newOutsidePixel.repeat(size))
}

fun main() {
  fun part1(input: List<String>): Int {
    val imageAlgorithm = input[0]

    var image = input.drop(2)
    var currentOutsidePixel = Pixel.DARK
    repeat(2) {
      image.forEach(::println)
      println()
      image = image.expandImage(2, currentOutsidePixel)

      image.forEach(::println)
      println()

      image = image.applyAlgorithm(imageAlgorithm)
      currentOutsidePixel = currentOutsidePixel.applyAlgorithm(imageAlgorithm)
    }

    image.forEach(::println)
    println(currentOutsidePixel)

    return image.sumOf { it.count { c -> c == Pixel.LIGHT.char } }
  }

  fun part2(input: List<String>): Int {
    val imageAlgorithm = input[0]

    var image = input.drop(2)
    var currentOutsidePixel = Pixel.DARK
    repeat(50) {
      image = image.expandImage(2, currentOutsidePixel)
      image = image.applyAlgorithm(imageAlgorithm)
      currentOutsidePixel = currentOutsidePixel.applyAlgorithm(imageAlgorithm)
    }

    return image.sumOf { it.count { c -> c == Pixel.LIGHT.char } }
  }

  val inputDir = "src/day20/inputs"

  val testResult = runSolution("Part1 test") { part1(readInput("test", inputDir)) }
  assertThat(testResult).isEqualTo(35)

  val input = readInput("input", inputDir)
  runSolution("Part1") { part1(input) }

  val part2TestResult = runSolution("Part2 test") { part2(readInput("test", inputDir)) }
  assertThat(part2TestResult).isEqualTo(3351)

  runSolution("Part2") { part2(input) }
}
