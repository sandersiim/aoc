import org.assertj.core.api.Assertions.assertThat
import kotlin.math.abs
import kotlin.math.max
import kotlin.math.min

fun main() {
  data class Coordinates(
    val y: Int,
    val x: Int
  )

  data class Line(
    val p1: Coordinates,
    val p2: Coordinates
  )

  fun parseInput(input: List<String>): List<Line> {
    return input.map { line ->
      val lineParts = line.split(" -> ")
      val p1Parts = lineParts[0].split(",").mapNotNull { it.toIntOrNull() }
      val p2Parts = lineParts[1].split(",").mapNotNull { it.toIntOrNull() }

      Line(
        Coordinates(p1Parts[1], p1Parts[0]),
        Coordinates(p2Parts[1], p2Parts[0])
      )
    }
  }

  fun solve(
    input: List<String>,
    boardSize: Int,
    includeDiagonals: Boolean = false
  ): Pair<Array<Array<Int>>, Int> {
    val lines = parseInput(input)
    println("Lines first, last: ${lines.first()};${lines.last()}")

    var result = 0
    val board = Array(boardSize) { Array(boardSize) { 0 } }

    lines.forEach { line ->
      if (line.p1.x == line.p2.x) {
        val startY = min(line.p1.y, line.p2.y)
        val endY = max(line.p1.y, line.p2.y)
        (startY..endY).forEach { y ->
          board[y][line.p1.x] += 1
          if (board[y][line.p1.x] == 2) {
            result += 1
          }
        }
      } else if (line.p1.y == line.p2.y) {
        val startX = min(line.p1.x, line.p2.x)
        val endX = max(line.p1.x, line.p2.x)
        (startX..endX).forEach { x ->
          board[line.p1.y][x] += 1
          if (board[line.p1.y][x] == 2) {
            result += 1
          }
        }
      } else if (includeDiagonals && abs(line.p1.x - line.p2.x) == abs(line.p1.y - line.p2.y)) {
        println(line)
        val (startCoords, endCoords) = when (line.p1.x < line.p2.x) {
          true -> line
          false -> Line(line.p2, line.p1)
        }

        var y = startCoords.y
        val yIsIncreasing = startCoords.y < endCoords.y

        (startCoords.x..endCoords.x).forEach { x ->
          println("$y, $x")
          board[y][x] += 1
          if (board[y][x] == 2) {
            result += 1
          }

          if (yIsIncreasing) {
            y += 1
          } else {
            y -= 1
          }
        }
      }
    }

    return Pair(board, result)
  }

  fun part1(input: List<String>, boardSize: Int): Int {
    println("Input size: ${input.size}")
    return solve(input, boardSize, false).second
  }

  fun part2(input: List<String>, boardSize: Int): Int {
    val (_, result) = solve(input, boardSize, true)

    return result
  }

  val testInput = readInput("day5_test")
  val testResult1 = runSolution("Part1 test") { part1(testInput, 10) }
  assertThat(testResult1).isEqualTo(5)

  val input = readInput("day5_input")

  runSolution("Part1") { part1(input, 1000) }

  val testResult2 = runSolution("Part2 test") { part2(testInput, 10) }
  assertThat(testResult2).isEqualTo(12)

  runSolution("Part2") { part2(input, 1000) }
}
