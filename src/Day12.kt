import org.assertj.core.api.Assertions.assertThat

fun main() {
  fun parseInput(input: List<String>): Graph<String> {
    val graph = Graph<String>()
    input.forEach { line ->
      val parts = line.split("-").map { it.trim() }
      graph.addEdge(parts[0], parts[1])
      graph.addEdge(parts[1], parts[0])
    }

    return graph
  }

  fun part1(input: List<String>): Long {
    println("Input size: ${input.size}")
    val graph = parseInput(input)
    val start = "start"
    val end = "end"

    println(graph.toString())
    val shouldSkipNextNode = { currentPath: List<String>, next: String ->
      next[0].isLowerCase() && next in currentPath
    }
    val paths = graph.allPaths(start, end, shouldSkipNextNode)

    return paths.size.toLong()
  }

  fun part2(input: List<String>): Long {
    val graph = parseInput(input)
    val start = "start"
    val end = "end"

    println(graph.toString())
    val shouldSkipNextNode = fun (currentPath: List<String>, next: String): Boolean {
      if (next[0].isLowerCase() && next in currentPath) {
        if (next == start) {
          return true
        }
        val smallCavesCounts = currentPath
          .filter { it[0].isLowerCase() }
          .groupingBy { it }
          .eachCount()

        if (smallCavesCounts.containsValue(2)) {
          return true
        }
      }

      return false
    }
    val paths = graph.allPaths(start, end, shouldSkipNextNode)
//    paths.forEach { println(it) }
    return paths.size.toLong()
  }

  val testInput1 = readInput("day12_test1")
  val testResult1 = runSolution("Part1 test1") { part1(testInput1) }
  assertThat(testResult1).isEqualTo(10)

  val testInput2 = readInput("day12_test2")
  val testResult2 = runSolution("Part1 test2") { part1(testInput2) }
  assertThat(testResult2).isEqualTo(19)
  val testInput3 = readInput("day12_test3")
  val testResult3 = runSolution("Part1 test3") { part1(testInput3) }
  assertThat(testResult3).isEqualTo(226)

  val input = readInput("day12_input")

  runSolution("Part1") { part1(input) }

  val testResult21 = runSolution("Part2 test1") { part2(testInput1) }
  assertThat(testResult21).isEqualTo(36)
  val testResult22 = runSolution("Part2 test2") { part2(testInput2) }
  assertThat(testResult22).isEqualTo(103)
  val testResult23 = runSolution("Part2 test3") { part2(testInput3) }
  assertThat(testResult23).isEqualTo(3509)

  runSolution("Part2") { part2(input) }
}
