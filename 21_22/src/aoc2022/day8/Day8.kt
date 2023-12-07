package aoc2022.day8

import org.assertj.core.api.Assertions
import readInput
import runSolution

fun main() {
  fun part1(input: List<String>): Int {
    val size = input.first().length
    val rows = input.map {
      it.map { d -> d.digitToInt() }
    }
    val cols = (0 until size).map {i ->
      rows.map { it[i] }
    }
    var result = 0
    (0 until size).forEach {row ->
      (0 until size ).forEach { col ->
        if (row == 0 || row == size - 1 || col == 0 || col == size - 1) {
          result += 1
        } else {
          val tree = rows[row][col]
          if (
            rows[row].subList(0, col).all { it < tree } ||
            rows[row].subList(col + 1, size).all { it < tree } ||
            cols[col].subList(0, row).all { it < tree } ||
            cols[col].subList(row + 1, size).all { it < tree }
          ) {
            result += 1
          }
        }
      }
    }

    return result
  }

  fun part2(input: List<String>): Int {
    val size = input.first().length
    val rows = input.map {
      it.map { d -> d.digitToInt() }
    }
    val cols = (0 until size).map {i ->
      rows.map { it[i] }
    }
    var max = 0
    (0 until size).forEach {row ->
      (0 until size ).forEach { col ->
        val tree = rows[row][col]
        var treeScore = 1
        val index1 = rows[row].subList(0, col).indexOfLast { it >= tree }
        treeScore *= if (index1 != -1) {
          col - index1
        } else {
          col
        }
        val index2 = rows[row].subList(col + 1, size).indexOfFirst { it >= tree }
        treeScore *= if (index2 != -1) {
          index2 + 1
        } else {
          size - col - 1
        }
        val index3 = cols[col].subList(0, row).indexOfLast { it >= tree }
        treeScore *= if (index3 != -1) {
          row - index3
        } else {
          row
        }

        val index4 = cols[col].subList(row + 1, size).indexOfFirst { it >= tree }
        treeScore *= if (index4 != -1) {
          index4 + 1
        } else {
          size - row - 1
        }

        if (treeScore > max) {
          max = treeScore
        }
      }
    }

    return max
  }

  val inputDir = "src/aoc2022/day8/inputs"

  val testResult = runSolution("Part1 test") { part1(readInput("test", inputDir)) }
  Assertions.assertThat(testResult).isEqualTo(21)

  val input = readInput("input", inputDir)
  runSolution("Part1") { part1(input) }

  val test2Result = runSolution("Part2 test") { part2(readInput("test", inputDir)) }
  Assertions.assertThat(test2Result).isEqualTo(8)

  runSolution("Part2") { part2(input) }

}
