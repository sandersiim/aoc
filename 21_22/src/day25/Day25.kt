package day25

import org.assertj.core.api.Assertions.assertThat
import readInput
import runSolution

enum class Tile(val c: Char) {
  DOWN('v'),
  RIGHT('>'),
  EMPTY('.')
}

data class CucumberMap(
  val rows: MutableList<MutableList<Tile>>
) {
  private val rowSize = rows[0].size
  private val colSize = rows.size

  companion object {
    fun parse(input: List<String>) = CucumberMap(
      input.map { line ->
        line.toCharArray().map {
          when (it) {
            Tile.DOWN.c -> Tile.DOWN
            Tile.RIGHT.c -> Tile.RIGHT
            Tile.EMPTY.c -> Tile.EMPTY
            else -> throw Error()
          }
        }.toMutableList()
      }.toMutableList()
    )
  }

  private fun forEachTile(action: (map: CucumberMap, rowIdx: Int, colIdx: Int) -> Unit) {
    rows.indices.forEach { rowIdx ->
      rows[0].indices.forEach { colIdx ->
        action(this, rowIdx, colIdx)
      }
    }
  }

  fun step(): CucumberMap {
    val result = this.clone()
    this.forEachTile { map, rowIdx, colIdx ->
      val nextColIdx = if (colIdx == map.rowSize - 1) 0 else colIdx + 1
      if (map.rows[rowIdx][colIdx] == Tile.RIGHT && map.rows[rowIdx][nextColIdx] == Tile.EMPTY) {
        result.rows[rowIdx][colIdx] = Tile.EMPTY
        result.rows[rowIdx][nextColIdx] = Tile.RIGHT
      }
    }
    val tmpResult = result.clone()
    tmpResult.forEachTile { map, rowIdx, colIdx ->
      val nextRowIdx = if (rowIdx == map.colSize - 1) 0 else rowIdx + 1
      if (map.rows[rowIdx][colIdx] == Tile.DOWN && map.rows[nextRowIdx][colIdx] == Tile.EMPTY) {
        result.rows[rowIdx][colIdx] = Tile.EMPTY
        result.rows[nextRowIdx][colIdx] = Tile.DOWN
      }
    }

    return result
  }

  private fun clone() = CucumberMap(rows.map { it.toMutableList() }.toMutableList())
}

fun main() {

  fun part1(input: List<String>): Int {
    var currentMap = CucumberMap.parse(input)
    generateSequence(1) { it + 1 }.forEach { iteration ->
      val nextMap = currentMap.step()
      if (nextMap == currentMap) {
        return iteration
      }
      currentMap = nextMap
    }

    return -1
  }

  fun part2(input: List<String>): Long {
    return 0
  }

  val inputDir = "src/day25/inputs"

  val testResult = runSolution("Part1 test") { part1(readInput("testInput", inputDir)) }
  assertThat(testResult).isEqualTo(58)

  runSolution("Part1") { part1(readInput("input", inputDir)) }
//  runSolution("Part2") { part2(readInput("input", inputDir)) }
}
