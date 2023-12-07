package aoc2022.day15

import Coords
import rangeIntersect
import org.assertj.core.api.Assertions
import readInput
import runSolution
import java.lang.Integer.min
import java.util.LinkedList
import kotlin.math.abs
import kotlin.math.max

data class AreaRange(
  val p1: Coords,
  val p2: Coords,
)

data class SensorData(
  val pos: Coords,
  val closestBeacon: Coords,
) {
  val sensorRange: Int by lazy { pos.manhattanDistFrom(closestBeacon) }

  companion object {
    fun parseFromInput(input: String): SensorData {
      val posStrings = Regex("""Sensor at x=(-?\d+), y=(-?\d+)""").matchAt(input, 0)!!
        .groupValues.drop(1)
      val beaconStrings = Regex(""".*closest beacon is at x=(-?\d+), y=(-?\d+)""").matchAt(input, 0)!!
        .groupValues.drop(1)

      return SensorData(
        pos = Coords(col = posStrings[0].toInt(), row = posStrings[1].toInt()),
        closestBeacon = Coords(col = beaconStrings[0].toInt(), row = beaconStrings[1].toInt()),
      )
    }
  }
}



fun main() {
  fun IntRange.unionWith(other: IntRange): IntRange {
    return IntRange(min(first, other.first), max(last, other.last))
  }

  fun IntRange.exclude(other: IntRange): List<IntRange> {
    val result = mutableListOf<IntRange>()
    if (this.first < other.first) {
      result.add(IntRange(this.first, other.first - 1))
    }
    if (this.last > other.last) {
      result.add(IntRange(other.last + 1, this.last))
    }

    return result
  }

  fun LinkedList<IntRange>.addNonIntersecting(newRange: IntRange) {
    this.add(newRange)
    this.sortBy { it.first }

    var i = 0
    while (i < this.size - 1) {
      if (this[i].rangeIntersect(this[i + 1]).isEmpty()) {
        i++
      } else {
        this[i] = this[i].unionWith(this[i + 1])
        this.removeAt(i + 1)
      }
    }
  }

  fun LinkedList<IntRange>.subtract(newRange: IntRange) {
    var i = 0
    while (i < this.size) {
      if (this[i].rangeIntersect(newRange).isEmpty()) {
        i++
      } else {
        val current = this.removeAt(i)
        val newRanges = current.exclude(newRange)
        this.addAll(i, newRanges)
        i += newRanges.size
      }
    }

  }

  fun part1(inputs: List<String>, targetRow: Int): Int {
    val exclusionRanges = LinkedList<IntRange>()
    val beaconsAtRow = mutableSetOf<Int>()
    inputs.forEach { input ->
      val sensorData = SensorData.parseFromInput(input)
      if (sensorData.closestBeacon.row == targetRow) {
        beaconsAtRow.add(sensorData.closestBeacon.col)
      }
      val distToRow = abs(sensorData.pos.row - targetRow)

      val horizontalRange = sensorData.sensorRange - distToRow
      if (horizontalRange > 0) {
        val targetRowExclusionRange = IntRange(
          sensorData.pos.col - horizontalRange,
          sensorData.pos.col + horizontalRange,
        )

        exclusionRanges.addNonIntersecting(targetRowExclusionRange)
        println(exclusionRanges)
      }
    }

    println(beaconsAtRow)

    return exclusionRanges.sumOf { it.last - it.first + 1 } -
        beaconsAtRow.count { exclusionRanges.any { range -> range.contains(it) } }
  }

  fun part2(inputs: List<String>, targetPosition: Int): Long {
    val targetBeaconRowRanges = mutableMapOf<Int, LinkedList<IntRange>>()
    (0 .. targetPosition).forEach {
      val list = LinkedList<IntRange>()
      list.add(IntRange(0, targetPosition))
      targetBeaconRowRanges[it] = list
    }

    inputs.forEach { input ->
      val sensorData = SensorData.parseFromInput(input)

      val rowsToCheck = IntRange(
        (sensorData.pos.row - sensorData.sensorRange).coerceAtLeast(0).coerceAtMost(targetPosition),
        (sensorData.pos.row + sensorData.sensorRange).coerceAtLeast(0).coerceAtMost(targetPosition),
      )

      rowsToCheck.forEach { rowToCheck ->
        val distToRow = abs(sensorData.pos.row - rowToCheck)

        val horizontalRange = sensorData.sensorRange - distToRow
        val targetRowExclusionRange = IntRange(
          sensorData.pos.col - horizontalRange,
          sensorData.pos.col + horizontalRange,
        )
        targetBeaconRowRanges[rowToCheck]!!.subtract(targetRowExclusionRange)
      }
    }

    val possibleRows = targetBeaconRowRanges.filterValues { !it.isEmpty() }
    println(possibleRows)

    val res = possibleRows.entries.first()
    println(res)
    val resultRow = res.key
    val resultCol = res.value.first.first
    val resultCoords = Coords(row = resultRow, col = resultCol)

    return resultCoords.row.toLong() + resultCoords.col.toLong() * 4000000
  }

  val inputDir = "src/aoc2022/day15/inputs"

  val testResult = runSolution("Part1 test") { part1(readInput("test", inputDir), 10) }
  Assertions.assertThat(testResult).isEqualTo(26)

  val input = readInput("input", inputDir)
  runSolution("Part1") { part1(input, 2_000_000) }

  val test2Result = runSolution("Part2 test") { part2(readInput("test", inputDir), 20) }
  Assertions.assertThat(test2Result).isEqualTo(56000011)

  runSolution("Part2") { part2(input, 4000000) }
}
