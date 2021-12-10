import org.assertj.core.api.Assertions.assertThat

fun main() {
  val openings = listOf('(', '[', '<', '{')
  val endings = listOf(')', ']', '>', '}')
  val openToEnd = mapOf(
    openings[0] to endings[0],
    openings[1] to endings[1],
    openings[2] to endings[2],
    openings[3] to endings[3],
  )

  fun part1(input: List<String>): Int {
    println("Input size: ${input.size}")
    val illegalChars = input.mapNotNull { line ->
      val stack = ArrayDeque<Char>()
      line.toCharArray().forEach {
        when {
          it in openings -> stack.addLast(it)
          it != openToEnd[stack.last()] -> return@mapNotNull it
          else -> stack.removeLast()
        }
      }
      null
    }

    println(illegalChars)

    return illegalChars.sumOf {
        val points = when (it) {
          ')' -> 3
          ']' -> 57
          '}' -> 1197
          '>' -> 25137
          else -> throw Error("Unknown char $it")
        }
        points
      }
  }

  fun part2(input: List<String>): Long {
    val endingSequences = input.mapNotNull { line ->
      val stack = ArrayDeque<Char>()
      line.toCharArray().forEach {
        when {
          it in openings -> stack.addLast(it)
          it != openToEnd[stack.last()] -> return@mapNotNull null
          else -> stack.removeLast()
        }
      }
      if (stack.isEmpty()) {
        null
      } else {
        stack.reversed().map { openToEnd[it] ?: throw Error("Unknown char $it") }
      }
    }

    println(endingSequences)

    val sortedPoints = endingSequences
      .map {
        val points = it.fold(0L) { acc, c ->
          when (c) {
            ')' -> acc * 5 + 1
            ']' -> acc * 5 + 2
            '}' -> acc * 5 + 3
            '>' -> acc * 5 + 4
            else -> throw Error("Unknown char $it")
          }
        }
        points
      }
      .sorted()

    println(sortedPoints)
    println(sortedPoints[sortedPoints.size / 2 - 1])
    println(sortedPoints[sortedPoints.size / 2])
    println(sortedPoints[sortedPoints.size / 2 + 2])
    return sortedPoints[sortedPoints.size / 2]
  }

  val testInput = readInput("day10_test")
  val testResult1 = runSolution("Part1 test") { part1(testInput) }
  assertThat(testResult1).isEqualTo(26397)

  val input = readInput("day10_input")

  runSolution("Part1") { part1(input) }

  val testResult2 = runSolution("Part2 test") { part2(testInput) }
  assertThat(testResult2).isEqualTo(288957)

  runSolution("Part2") { part2(input) }
}
