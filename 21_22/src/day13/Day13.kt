package day13

import Coords
import org.assertj.core.api.Assertions.assertThat
import readInput
import runSolution

typealias ParsedInput = Pair<List<Coords>, List<Fold>>

enum class FoldDirection(val input: String) {
  COLUMN("x"),
  ROW("y");

  companion object {
    private val VALUES get() = values()
    fun getByInput(input: String) = VALUES.first { it.input == input }
  }
}

data class Fold(val dir: FoldDirection, val coordinate: Int) {
  companion object {
    fun fromInput(line: String) = line.removePrefix("fold along ")
      .split("=")
      .let { Fold(FoldDirection.getByInput(it[0]), it[1].toInt()) }
  }
}

fun main() {

  fun parseInput(input: List<String>): ParsedInput {
    val coordsList = mutableListOf<Coords>()
    val folds = mutableListOf<Fold>()
    input.forEach { line ->
      when {
        line.isEmpty() -> Unit
        line[0].isDigit() -> coordsList.add(Coords.fromInput(line))
        line[0] == 'f' -> folds.add(Fold.fromInput(line))
      }
    }

    return ParsedInput(coordsList, folds)
  }

  fun List<Coords>.foldBy(fold: Fold): List<Coords> {
    return this.map {
      when {
        fold.dir == FoldDirection.ROW && it.row > fold.coordinate -> {
          it.copy(row = 2 * fold.coordinate - it.row)
        }
        fold.dir == FoldDirection.COLUMN && it.col > fold.coordinate -> {
          it.copy(col = 2 * fold.coordinate - it.col)
        }
        else -> it
      }
    }.distinct()
  }

  fun List<Coords>.printCode() {
    val coordsSequence = this.asSequence()
    val maxRow = coordsSequence.maxOf { it.row }
    val maxCol = coordsSequence.maxOf { it.col }
    (0..maxRow).forEach { row ->
      println((0..maxCol).joinToString("") { if (Coords(row, it) in this) "#" else "." })
    }
    println("END")
  }

  fun solve(parsedInput: ParsedInput, numberOfFolds: Int = 1): Long {
    val (coordsList, folds) = parsedInput
    println(folds)

    val resultCoords = (0 until numberOfFolds).fold(coordsList) { acc, foldIndex ->
      acc.foldBy(folds[foldIndex])
    }

    resultCoords.printCode()

    return resultCoords.size.toLong()
  }

  fun part1(input: List<String>): Long {
    return solve(parseInput(input))
  }

  fun part2(input: List<String>): Long {
    val parsedInput = parseInput(input)

    return solve(parsedInput, parsedInput.second.size)
  }

  val testInput = readInput("day13_test")
  val testResult = runSolution("Part1 test") { part1(testInput) }
  assertThat(testResult).isEqualTo(17)

  val input = readInput("day13_input")

  runSolution("Part1") { part1(input) }

  runSolution("Part2 test") { part2(testInput) }
  runSolution("Part2") { part2(input) }
}
