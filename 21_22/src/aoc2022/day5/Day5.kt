package aoc2022.day5

import org.assertj.core.api.Assertions
import readInput
import runSolution

fun main() {

  fun part1(input: List<String>, numOfStacks: Int): String {
    val stacks: MutableList<ArrayDeque<Char>> = mutableListOf()
    repeat(numOfStacks) { stacks.add(ArrayDeque()) }
    var inputIdx = 0
    var current = input[inputIdx]
    while (!current.startsWith(" 1 ")) {
      var stackIdx = 0
      while (stackIdx*4 < current.length) {
        val crate = current[stackIdx*4 + 1]
        if (crate != ' ') {
          stacks[stackIdx].addLast(crate)
        }
        stackIdx++
      }
      inputIdx++
      current = input[inputIdx]
    }
    inputIdx += 2
    println(stacks)

    val moveRegex = Regex("""move (\d+) from (\d+) to (\d+)""")
    (inputIdx until input.size).forEach {
      val move = moveRegex.matchEntire(input[it])!!.groupValues.drop(1)
      val moveNum = move[0].toInt()
      val from = move[1].toInt() - 1
      val to = move[2].toInt() - 1

      repeat(moveNum) {
        stacks[to].addFirst(stacks[from].removeFirst())
      }
      println(stacks)
    }

    return stacks.map { it.first() }.joinToString("")
  }

  fun part2(input: List<String>, numOfStacks: Int): String {
    val stacks: MutableList<ArrayDeque<Char>> = mutableListOf()
    repeat(numOfStacks) { stacks.add(ArrayDeque()) }
    var inputIdx = 0
    var current = input[inputIdx]
    while (!current.startsWith(" 1 ")) {
      var stackIdx = 0
      while (stackIdx*4 < current.length) {
        val crate = current[stackIdx*4 + 1]
        if (crate != ' ') {
          stacks[stackIdx].addLast(crate)
        }
        stackIdx++
      }
      inputIdx++
      current = input[inputIdx]
    }
    inputIdx += 2
    println(stacks)

    val moveRegex = Regex("""move (\d+) from (\d+) to (\d+)""")
    (inputIdx until input.size).forEach {
      val move = moveRegex.matchEntire(input[it])!!.groupValues.drop(1)
      val moveNum = move[0].toInt()
      val from = move[1].toInt() - 1
      val to = move[2].toInt() - 1

      val tmpStack = ArrayDeque<Char>()
      repeat(moveNum) { tmpStack.addFirst(stacks[from].removeFirst()) }
      repeat(moveNum) {
        stacks[to].addFirst(tmpStack.removeFirst())
      }
      println(stacks)
    }

    return stacks.map { it.first() }.joinToString("")
  }

  val inputDir = "src/aoc2022/day5/inputs"

  val testResult = runSolution("Part1 test") { part1(readInput("test", inputDir), 3) }
  Assertions.assertThat(testResult).isEqualTo("CMZ")

  val input = readInput("input", inputDir)
  runSolution("Part1") { part1(input, 9) }

  val test2Result = runSolution("Part2 test") { part2(readInput("test", inputDir), 3) }
  Assertions.assertThat(test2Result).isEqualTo("MCD")

  runSolution("Part2") { part2(input, 9) }

}
