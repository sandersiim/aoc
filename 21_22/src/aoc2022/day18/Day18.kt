package aoc2022.day18

import org.assertj.core.api.Assertions
import readInput
import runSolution

data class Point3D(val x: Int, val y: Int, val z: Int) {
  fun adjacentPoints(): List<Point3D> {
    return listOf(
      Point3D(x - 1, y, z),
      Point3D(x + 1, y, z),
      Point3D(x, y - 1, z),
      Point3D(x, y + 1, z),
      Point3D(x, y, z + 1),
      Point3D(x, y, z - 1),
    )
  }
}

private fun Set<Point3D>.isInteriorSpace(
  point: Point3D,
  xRange: IntRange,
  yRange: IntRange,
  zRange: IntRange,
): Boolean {
  val processedPoints = mutableSetOf<Point3D>()
  val freePoints = mutableSetOf<Point3D>()
  freePoints.add(point)
  var current: Point3D

  while (!freePoints.isEmpty()) {
    current = freePoints.first()
    freePoints.remove(current)
    processedPoints.add(current)

    current.adjacentPoints().forEach {
      if (!xRange.contains(it.x) || !yRange.contains(it.y) || !zRange.contains(it.z)) {
        return false
      }
      if (it !in processedPoints && it !in this) {
        freePoints.add(it)
      }
    }
  }

  return true
}

fun main() {
  fun parseInput(input: List<String>): List<Point3D> {
    val r = Regex("""(\d+),(\d+),(\d+)""")
    return input.map { line ->
      val coords = r.matchEntire(line)!!.groupValues.drop(1).map { it.toInt() }
      Point3D(coords[0], coords[1], coords[2])
    }
  }

  fun part1(input: List<String>): Int {
    val points = parseInput(input).toSet()
    var surfaceArea = 0

    points.forEach { point ->
      surfaceArea += point.adjacentPoints().count {
        it !in points
      }
    }
    return surfaceArea
  }

  fun part2(input: List<String>): Int {
    val points = parseInput(input).toSet()
    var surfaceArea = 0
    val xRange = IntRange(points.minOf { it.x }, points.maxOf { it.x })
    val yRange = IntRange(points.minOf { it.y }, points.maxOf { it.y })
    val zRange = IntRange(points.minOf { it.z }, points.maxOf { it.z })

    points.forEach { point ->
      surfaceArea += point.adjacentPoints().count {
        it !in points && !points.isInteriorSpace(it, xRange, yRange, zRange)
      }
    }
    return surfaceArea
  }

  val inputDir = "src/aoc2022/day18/inputs"

  val testResult = runSolution("Part1 test") { part1(readInput("test", inputDir)) }
  Assertions.assertThat(testResult).isEqualTo(64)

  val input = readInput("input", inputDir)
  runSolution("Part1") { part1(input) }

//  val test2Result = runSolution("Part2 test") { part2(readInput("test", inputDir)) }
//  Assertions.assertThat(test2Result).isEqualTo(58)

  runSolution("Part2") { part2(input) }
}
