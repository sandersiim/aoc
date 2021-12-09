import org.assertj.core.api.Assertions.assertThat

fun main() {
  fun parseInput(input: List<String>): HeightMap {
    return input.map { it.toCharArray().map { c -> c.digitToInt() } }
  }

  fun <T : Any>HeightMap.mapCoordsNotNull(op: (Pair<Int, Int>) -> T?): List<T> {
    return this.indices.flatMap { row ->
      this[0].indices.mapNotNull { col ->
        op(Pair(row, col))
      }
    }
  }

  fun HeightMap.get(coords: Pair<Int, Int>) = this[coords.first][coords.second]

  fun HeightMap.neighboursOf(coords: Pair<Int, Int>): List<Pair<Int, Int>> {
    return listOf(
      Pair(coords.first - 1, coords.second),
      Pair(coords.first, coords.second - 1),
      Pair(coords.first + 1, coords.second),
      Pair(coords.first, coords.second + 1),
    )
      .filter { (row, col) -> row >= 0 && col >= 0 && row < this.size && col < this[0].size }
  }

  fun HeightMap.isLowPoint(coords: Pair<Int, Int>): Boolean {
    val valueAtCoords = get(coords)
    return neighboursOf(coords).map { get(it) }.all { it > valueAtCoords }
  }

  fun HeightMap.getBasin(coords: Pair<Int, Int>): List<Pair<Int, Int>> {
    fun getBasinInner(nextCoords: Pair<Int, Int>): List<Pair<Int, Int>> {
      val basinValuesRange = (get(nextCoords) + 1)..8
      return listOf(nextCoords) + neighboursOf(nextCoords)
        .filter { get(it) in basinValuesRange }
        .flatMap { getBasin(it) }
    }

    return (listOf(coords) + getBasinInner(coords)).distinct()
  }

  fun part1(input: List<String>): Int {
    println("Input size: ${input.size}")
    val heightMap = parseInput(input)

    return heightMap.mapCoordsNotNull { coords ->
      if (heightMap.isLowPoint(coords)) {
        heightMap.get(coords) + 1
      } else {
        0
      }
    }.sum()
  }

  fun part2(input: List<String>): Int {
    val heightMap = parseInput(input)
    val basins = heightMap.mapCoordsNotNull { coords ->
      if (heightMap.isLowPoint(coords)) {
        heightMap.getBasin(coords)
      } else {
        null
      }
    }

    return basins.map { it.size }.sorted().takeLast(3).reduce(Int::times)
  }

  val testInput = readInput("day9_test")
  val testResult1 = runSolution("Part1 test") { part1(testInput) }
  assertThat(testResult1).isEqualTo(15)

  val input = readInput("day9_input")

  runSolution("Part1") { part1(input) }

  val testResult2 = runSolution("Part2 test") { part2(testInput) }
  assertThat(testResult2).isEqualTo(1134)

  runSolution("Part2") { part2(input) }
}

typealias HeightMap = List<List<Int>>
