package day22

import org.assertj.core.api.Assertions.assertThat
import readInput
import runSolution
import kotlin.math.max
import kotlin.math.min

enum class CommandType {
  ON,
  OFF
}
data class RebootCommand(
  val type: CommandType,
  val cuboid: Cuboid
) {
  companion object {
    fun parse(input: String): RebootCommand {
      val parts = input.split(" ")
      val commandType = CommandType.valueOf(parts[0].uppercase())

      val ranges = parts[1].split(",").map {
        val nums = it.drop(2).split("..").map(String::toInt)
        IntRange(nums[0], nums[1])
      }

      return RebootCommand(
        commandType,
        Cuboid(
          ranges[0],
          ranges[1],
          ranges[2]
        )
      )
    }
  }
}

fun parseInput(input:List<String>) = input.map(RebootCommand.Companion::parse)
fun IntRange.rangeIntersect(other: IntRange) = IntRange(
  max(this.first, other.first),
  min(this.last, other.last)
)
fun IntRange.size() = last - first + 1

data class Cuboid(
  val xRange: IntRange,
  val yRange: IntRange,
  val zRange: IntRange
) {
  val size = xRange.size().toLong() * yRange.size().toLong() * zRange.size().toLong()

  fun intersectionWith(other: Cuboid): Cuboid? {
    val xIntersection = xRange.rangeIntersect(other.xRange)
    if (xIntersection.isEmpty()) {
      return null
    }
    val yIntersection = yRange.rangeIntersect(other.yRange)
    if (yIntersection.isEmpty()) {
      return null
    }
    val zIntersection = zRange.rangeIntersect(other.zRange)
    if (zIntersection.isEmpty()) {
      return null
    }

    return Cuboid(xIntersection, yIntersection, zIntersection)
  }

  fun subtract(other: Cuboid): List<Cuboid> {
    val intersection = intersectionWith(other)
    return if (intersection == null) {
      listOf(this)
    } else {
      val result = mutableListOf<Cuboid>()
      val xIntersection = xRange.rangeIntersect(other.xRange)
      if (xRange.first < other.xRange.first) {
        result.add(Cuboid((xRange.first until other.xRange.first), yRange, zRange))
      }
      if (xRange.last > other.xRange.last) {
        result.add(Cuboid((other.xRange.last + 1..xRange.last), yRange, zRange))
      }
      val xIntersectedCuboid = Cuboid(xIntersection, yRange, zRange)

      val yIntersection = yRange.rangeIntersect(other.yRange)
      if (yRange.first < other.yRange.first) {
        result.add(Cuboid(xIntersectedCuboid.xRange, (yRange.first until other.yRange.first), zRange))
      }
      if (yRange.last > other.yRange.last) {
        result.add(Cuboid(xIntersectedCuboid.xRange, (other.yRange.last+1..yRange.last), zRange))
      }
      val xyIntersectedCuboid = Cuboid(xIntersectedCuboid.xRange, yIntersection, zRange)

      if (zRange.first < other.zRange.first) {
        result.add(Cuboid(xyIntersectedCuboid.xRange, xyIntersectedCuboid.yRange, (zRange.first until other.zRange.first)))
      }
      if (zRange.last > other.zRange.last) {
        result.add(Cuboid(xyIntersectedCuboid.xRange, xyIntersectedCuboid.yRange, (other.zRange.last+1..zRange.last)))
      }

      result
    }
  }
}
data class CuboidUnion(var cuboids: MutableList<Cuboid> = mutableListOf()) {
  fun size() = cuboids.sumOf { it.size }

  fun add(cuboid: Cuboid) {
    var newCuboids = listOf(cuboid)
    cuboids.forEach { existingCuboid ->
      newCuboids = newCuboids.flatMap { it.subtract(existingCuboid) }
    }
    cuboids.addAll(newCuboids)
  }

  fun remove(cuboid: Cuboid) {
    cuboids = cuboids.flatMap { it.subtract(cuboid) }.toMutableList()
  }
}

fun main() {

  fun part1(input: List<String>): Long {
    val commands = parseInput(input)
    val result = CuboidUnion()
    commands.take(20).forEach {
      when (it.type) {
        CommandType.ON -> result.add(it.cuboid)
        CommandType.OFF -> result.remove(it.cuboid)
      }
    }

    return result.size()
  }

  fun part2(input: List<String>): Long {
    val commands = parseInput(input)
    val result = CuboidUnion()
    commands.forEach {
      when (it.type) {
        CommandType.ON -> result.add(it.cuboid)
        CommandType.OFF -> result.remove(it.cuboid)
      }
    }

    return result.size()
  }

  val inputDir = "src/day22/inputs"

  val testResult = runSolution("Part1 test") { part1(readInput("testInput", inputDir)) }
  assertThat(testResult).isEqualTo(590784)

  runSolution("Part1") { part1(readInput("input", inputDir)) }

  val testResult2 = runSolution("Part2 test") { part2(readInput("testInput2", inputDir)) }
  assertThat(testResult2).isEqualTo(2758514936282235)

  runSolution("Part2") { part2(readInput("input", inputDir)) }
}
