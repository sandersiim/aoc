import org.assertj.core.api.Assertions.assertThat

fun main() {
  fun OctopusMap.get(coords: Coords) = this[coords.first][coords.second]
  fun OctopusMap.set(coords: Coords, value: Int) {
    this[coords.first][coords.second] = value
  }
  fun OctopusMap.print() {
    forEach { println(it.contentToString()) }
    println()
  }

  fun <T : Any>OctopusMap.mapCoordsNotNull(op: (Coords) -> T?): List<T> {
    return this.indices.flatMap { row ->
      this[0].indices.mapNotNull { col ->
        op(Coords(row, col))
      }
    }
  }
  fun OctopusMap.incrementAll() {
    this.indices.forEach { row ->
      this[0].indices.forEach { col ->
        this[row][col] += 1
      }
    }
  }

  fun OctopusMap.flashers(): Set<Coords> {
    return mapCoordsNotNull {
      if (get(it) > 9) {
        it
      } else {
        null
      }
    }.toSet()
  }

  fun OctopusMap.neighboursOf(coords: Coords): List<Coords> {
    val (row, col) = coords
    return (row-1 .. row+1).flatMap { nRow ->
      (col-1 .. col+1).map { nCol ->
        Coords(nRow, nCol)
      }
    }
      .filter { (row, col) ->
        (row != coords.first || col != coords.second) &&
          row >= 0 && col >= 0 &&
            row < this.size && col < this[0].size
      }
  }

  fun OctopusMap.incrementNeighbours(coords: Coords) {
    neighboursOf(coords).forEach { (row, col) -> this[row][col] += 1 }
  }

  fun OctopusMap.performStep(): Long {
    incrementAll()
    var previousAllFlashCoords = emptySet<Coords>()
    var allFlashCoords = flashers()

    while (true) {
      (allFlashCoords - previousAllFlashCoords).forEach { incrementNeighbours(it) }
      previousAllFlashCoords = allFlashCoords.toSet()
      allFlashCoords = flashers()

      if (allFlashCoords.size == previousAllFlashCoords.size) {
        break
      }
    }

    allFlashCoords.forEach { set(it, 0) }

    return allFlashCoords.size.toLong()
  }

  fun parseInput(input: List<String>): OctopusMap {
    return Array(input.size) { row ->
      input[row].split("").mapNotNull { it.toIntOrNull() }.toTypedArray()
    }
  }

  fun part1(input: List<String>, steps: Int): Long {
    println("Input size: ${input.size}")
    val octopusMap = parseInput(input)

    return (1..steps).sumOf { octopusMap.performStep() }
  }

  fun part2(input: List<String>): Long? {
    val octopusMap = parseInput(input)
    return generateSequence(Pair(0L, 0L)) {
      Pair(it.first + 1, octopusMap.performStep())
    }.find { it.second == 100L }?.first
  }

  val testInput = readInput("day11_test")
  val testResult1 = runSolution("Part1 test1") { part1(testInput, 10) }
  assertThat(testResult1).isEqualTo(204)
  val testResult2 = runSolution("Part1 test2") { part1(testInput, 100) }
  assertThat(testResult2).isEqualTo(1656)

  val input = readInput("day11_input")

  runSolution("Part1") { part1(input, 100) }

  val testResult3 = runSolution("Part2 test") { part2(testInput)!! }
  assertThat(testResult3).isEqualTo(195)

  runSolution("Part2") { part2(input)!! }
}

typealias OctopusMap = Array<Array<Int>>
