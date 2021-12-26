package day17

import org.assertj.core.api.Assertions.assertThat
import readInput
import runSolution

data class TargetArea(val xRange: LongRange, val yRange: LongRange) {
  companion object {
    fun parse(str: String): TargetArea {
      val rangeStrings = str.replace("target area: ", "").split(", ")
      val xNums = rangeStrings[0].drop(2).split("..").map(String::toLong)
      val yNums = rangeStrings[1].drop(2).split("..").map(String::toLong)
      return TargetArea(
        LongRange(xNums[0], xNums[1]),
        LongRange(yNums[0], yNums[1])
      )
    }
  }

  fun shoot(initialVx: Long, initialVy: Long): Long? {
    var xPos = 0L
    var yPos = 0L
    var vx = initialVx
    var vy = initialVy
    var maxYPos = 0L

    while (yPos > yRange.first) {
      xPos += vx
      yPos += vy
      if (vy > 0) { maxYPos = yPos }

      if (vx > 0) {
        vx -= 1
      } else if (vx < 0) {
        vx += 1
      }
      vy -= 1

      if (xPos in xRange && yPos in yRange) {
        return maxYPos
      }
    }
    return null
  }
}

fun main() {
  fun part1(input: List<String>): Long {
    val targetArea = TargetArea.parse(input[0])
    println(targetArea)

    val maxVelocity = (targetArea.xRange.last downTo 1).maxOf { vx ->
      val maxVy = 100L
      (maxVy downTo targetArea.yRange.first).maxOf { vy ->
        targetArea.shoot(vx, vy) ?: 0
      }
    }

    return maxVelocity
  }

  fun part2(input: List<String>): Long {
    val targetArea = TargetArea.parse(input[0])
    println(targetArea)

    val countOfVelocities = (targetArea.xRange.last downTo 1).sumOf { vx ->
      val maxVy = 100L
      (maxVy downTo targetArea.yRange.first).count { vy ->
        targetArea.shoot(vx, vy) != null
      }
    }

    return countOfVelocities.toLong()
  }

  val inputDir = "src/day17/inputs"

  val testResult = runSolution("Part1 test") { part1(readInput("testInput", inputDir)) }
  assertThat(testResult).isEqualTo(45)

  runSolution("Part1") { part1(readInput("input", inputDir)) }

  val testResult2 = runSolution("Part2 test") { part2(readInput("testInput", inputDir)) }
  assertThat(testResult2).isEqualTo(112)

  runSolution("Part2") { part2(readInput("input", inputDir)) }
}
