package aoc2022.day23

import Coords
import Direction
import org.assertj.core.api.Assertions.assertThat
import readInput
import runSolution

fun main() {
  fun parseInput(input: List<String>): List<Coords> {
    return input.indices.flatMap { row ->
      input[row].indices.mapNotNull { col ->
        if (input[row][col] == '#') {
          Coords(row, col)
        } else {
          null
        }
      }
    }
  }

  fun printElves(elves: List<Coords>) {
    val elvesByRow = elves.groupBy { it.row }.mapValues { it.value.map { c -> c.col }.toSet() }
    val rows = elves.map { it.row }
    val minRow = rows.min()
    val maxRow = rows.max()

    val cols = elves.map { it.col }
    val minCol = cols.min()
    val maxCol = cols.max()

    (minRow .. maxRow).forEach {
      var rowString = ""
      val rowElves = elvesByRow[it] ?: emptySet()
      (minCol .. maxCol).forEach { col ->
        rowString += if (col in rowElves) {
          '#'
        } else {
          '.'
        }
      }
      println(rowString)
    }
    println("---------------------------------")
  }

  fun solve(input: List<String>, part1: Boolean): Int {
    val directions = ArrayDeque<Direction>()
    directions.addLast(Direction.UP)
    directions.addLast(Direction.DOWN)
    directions.addLast(Direction.LEFT)
    directions.addLast(Direction.RIGHT)
    var elves = parseInput(input)
    var elvesByRow = elves.groupBy { it.row }.mapValues { it.value.map { c -> c.col }.toSet() }
    var elvesByCol = elves.groupBy { it.col }.mapValues { it.value.map { c -> c.row }.toSet() }
    printElves(elves)

    fun elfProposal(elf: Coords): Coords? {
      val possibleCols = (elf.col - 1 .. elf.col + 1)
      if (
        possibleCols.all { it !in (elvesByRow[elf.row - 1] ?: emptySet()) } &&
        elf.col - 1 !in (elvesByRow[elf.row] ?: emptySet()) &&
        elf.col + 1 !in (elvesByRow[elf.row] ?: emptySet()) &&
        possibleCols.all { it !in (elvesByRow[elf.row + 1] ?: emptySet()) }
      ) {
        return null
      }
      directions.forEach {
        if (it.isSameRowDirection()) {
          val newCol = if (it == Direction.LEFT) elf.col - 1 else elf.col + 1
          val colElves = elvesByCol[newCol] ?: emptySet()
          if (
            colElves.isEmpty() || (
              (elf.row - 1) !in colElves &&
                elf.row !in colElves &&
                (elf.row + 1) !in colElves
            )
          ) {
            return Coords(elf.row, newCol)
          }
        } else {
          val newRow = if (it == Direction.UP) elf.row - 1 else elf.row + 1
          val rowElves = elvesByRow[newRow] ?: emptySet()
          if (
            rowElves.isEmpty() || (
              (elf.col - 1) !in rowElves &&
                elf.col !in rowElves &&
                (elf.col + 1) !in rowElves
              )
          ) {
            return Coords(newRow, elf.col)
          }
        }
      }

      return null
    }
    var round = 1

    while(true) {
      if (part1 && round == 11) {
        break
      }
      val proposals = elves.associateWith { elfProposal(it) }
      val proposalsCounts = proposals.values.groupingBy { it }.eachCount()
      if (proposalsCounts.size == 1) {
        break
      }
      elves = proposals.map { (currentElf, proposal) ->
        if (proposal == null || proposalsCounts.getValue(proposal) > 1) {
          currentElf
        } else {
          proposal
        }
      }
      elvesByRow = elves.groupBy { it.row }.mapValues { it.value.map { c -> c.col }.toSet() }
      elvesByCol = elves.groupBy { it.col }.mapValues { it.value.map { c -> c.row }.toSet() }
      // printElves(elves)
      val firstDirection = directions.removeFirst()
      directions.addLast(firstDirection)
      // println(directions)
      round++
    }
    if (part1) {
      val rows = elves.map { it.row }
      val minRow = rows.min()
      val maxRow = rows.max()

      val cols = elves.map { it.col }
      val minCol = cols.min()
      val maxCol = cols.max()
      val rowLength = maxCol - minCol + 1

      var emptyCount = 0
      (minRow .. maxRow).forEach { row ->
        val rowElves = elvesByRow[row] ?: emptySet()
        emptyCount += (rowLength - rowElves.size)
      }

      return emptyCount
    } else {
      return round
    }
  }

  val inputDir = "src/aoc2022/day23/inputs"

  val testResult = runSolution("Part1 test") { solve(readInput("test", inputDir), true) }
  assertThat(testResult).isEqualTo(110)

  val input = readInput("input", inputDir)
  runSolution("Part1") { solve(input, true) }

  val test2Result = runSolution("Part2 test") { solve(readInput("test", inputDir), false) }
  assertThat(test2Result).isEqualTo(20)

  runSolution("Part2") { solve(input, false) }

}
