package aoc2022.day13

import org.assertj.core.api.Assertions
import readInput
import runSolution

fun compareLists(first: List<*>, second: List<*>): Int {
  first.indices.forEach {
    if (it >= second.size) {
      return 1
    }
    val comparison = compareItems(first[it]!!, second[it]!!)
    if (comparison != 0) {
      return comparison
    }
  }
  if (first.size == second.size) {
    return 0
  }
  return -1
}

fun compareItems(first: Any, second: Any): Int {
  return if (first is Int && second is Int) {
    first.compareTo(second)
  } else if (first is List<*> && second is List<*>) {
    compareLists(first, second)
  } else if (first is List<*>) {
    compareLists(first, listOf(second))
  } else if (second is List<*>) {
    compareLists(listOf(first), second)
  } else {
    throw IllegalStateException("problems")
  }
}

fun main() {
  fun parseArr(arrStr: String): Pair<List<Any>, Int> {
    val items = mutableListOf<Any>()
    var i = 1
    while (i < arrStr.length) {
      if (arrStr[i] == '[') {
        val (addedItem, lastIndex) = parseArr(arrStr.substring(i))
        items.add(addedItem)
        i += lastIndex
      } else if (arrStr[i].isDigit()) {
        if (arrStr[i+1].isDigit()) {
          items.add(arrStr.substring(i, i+2).toInt())
          i += 2
        } else {
          items.add(arrStr[i].digitToInt())
          i++
        }
      } else if (arrStr[i] == ',') {
        i++
      } else {
        return Pair(items, i + 1)
      }
    }

    return Pair(items, i)
  }

  fun parsePairs(input: List<String>): List<Pair<List<Any>, List<Any>>> {
    val pairs = mutableListOf<Pair<List<Any>, List<Any>>>()
    var i = 0
    while (i < input.size) {
      val first = parseArr(input[i]).first
      val second = parseArr(input[i+1]).first
      pairs.add(Pair(first, second))
      i += 3
    }

    return pairs
  }

  fun parseAllPackets(input: List<String>): List<List<Any>> {
    val packets = mutableListOf<List<Any>>()
    input.forEach {
      if (it.isNotEmpty()) {
        packets.add(parseArr(it).first)
      }
    }
    return packets
  }

  fun part1(input: List<String>): Int {
    val pairs = parsePairs(input)
    println(pairs)

    return pairs.indices.sumOf {
      val pair = pairs[it]
      val result = compareLists(pair.first, pair.second)

      if (result < 0) {
        it + 1
      } else {
        0
      }
    }
  }

  fun part2(input: List<String>): Int {
    val packets = parseAllPackets(input) + listOf(listOf(listOf(2)), listOf(listOf(6)))
    val sortedPackets = packets.sortedWith { o1, o2 -> compareLists(o1, o2) }
    val firstIndex = sortedPackets.indexOfFirst {
      it.size == 1 && it[0] is List<*> && (it[0] as List<*>).size == 1 && (it[0] as List<*>)[0] == 2
    }
    val secondIndex = sortedPackets.indexOfFirst {
      it.size == 1 && it[0] is List<*> && (it[0] as List<*>).size == 1 && (it[0] as List<*>)[0] == 6
    }
    return (firstIndex + 1) * (secondIndex + 1)
  }

  val inputDir = "src/aoc2022/day13/inputs"

  val testResult = runSolution("Part1 test") { part1(readInput("test", inputDir)) }
  Assertions.assertThat(testResult).isEqualTo(13)

  val input = readInput("input", inputDir)
  runSolution("Part1") { part1(input) }

  val test2Result = runSolution("Part2 test") { part2(readInput("test", inputDir)) }
  Assertions.assertThat(test2Result).isEqualTo(140)

  runSolution("Part2") { part2(input) }
}
