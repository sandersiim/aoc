package day18

import org.assertj.core.api.Assertions.assertThat
import readInput
import runSolution
import kotlin.math.ceil

data class Node(
  var left: Node?,
  var right: Node?,
  var value: Int? = null,
  var parent: Node? = null
) {

  enum class ReduceAction {
    EXPLODE,
    SPLIT,
    NONE
  }

  companion object {
    fun fromString(str: String): Node {
      val stack = ArrayDeque<Node>()
      str.toCharArray().forEach { c ->
        when {
          c.isDigit() -> stack.addLast(Node(null, null, c.digitToInt()))
          c == ']' -> {
            val right = stack.removeLast()
            val left = stack.removeLast()
            stack.addLast(Node(left, right))
          }
        }
      }

      val root = stack.removeLast()
      root.addParentReferences()
      return root
    }
  }

  fun addParentReferences(newParent: Node? = null) {
    this.parent = newParent
    left?.addParentReferences(this)
    right?.addParentReferences(this)
  }

  fun add(other: Node): Node {
    val newNode = Node(this.clone(), other.clone())
    newNode.addParentReferences()

    return newNode
  }

  private fun clone(): Node {
    return Node(left?.clone(), right?.clone(), value)
  }

  fun magnitude(): Long {
    if (value != null) {
      return value!!.toLong()
    }

    return 3 * left!!.magnitude() + 2 * right!!.magnitude()
  }

  private fun addToLeftMostChild(addValue: Int) {
    if (value != null) {
      value = value!! + addValue
    } else {
      left!!.addToLeftMostChild(addValue)
    }
  }

  private fun addToRightMostChild(addValue: Int) {
    if (value != null) {
      value = value!! + addValue
    } else {
      right!!.addToRightMostChild(addValue)
    }
  }

  private fun nextLeftSubTree(): Node? {
    return if (parent == null) {
      null
    } else if (parent!!.left === this) {
      parent!!.nextLeftSubTree()
    } else {
      parent!!.left
    }
  }

  private fun nextRightSubTree(): Node? {
    return if (parent == null) {
      null
    } else if (parent!!.right === this) {
      parent!!.nextRightSubTree()
    } else {
      parent!!.right
    }
  }

  private fun addToNextLeftNumber(addValue: Int) {
    nextLeftSubTree()?.addToRightMostChild(addValue)
  }

  private fun addToNextRightNumber(addValue: Int) {
    nextRightSubTree()?.addToLeftMostChild(addValue)
  }

  private fun explode() {
    addToNextLeftNumber(left!!.value!!)
    addToNextRightNumber(right!!.value!!)
    if (parent!!.left === this) {
      parent!!.left = Node(null, null, 0, parent)
    } else {
      parent!!.right = Node(null, null, 0, parent)
    }
  }

  private fun attemptExplode(depth: Int = 0): Boolean {
    if (depth == 4 && value == null) {
      this.explode()
      return true
    }

    if (left != null && right != null) {
      if (left!!.attemptExplode(depth + 1)) {
        return true
      }
      if (right!!.attemptExplode(depth + 1)) {
        return true
      }
    }

    return false
  }

  private fun split() {
    val newLeftValue = value!! / 2
    val newRightValue = ceil(value!! / 2.0).toInt()
    value = null
    left = Node(null, null, newLeftValue, this)
    right = Node(null, null, newRightValue, this)
  }

  private fun attemptSplit(): Boolean {
    if ((value ?: 0) >= 10) {
      this.split()
      return true
    }

    if (left != null && right != null) {
      if (left!!.attemptSplit()) {
        return true
      }
      if (right!!.attemptSplit()) {
        return true
      }
    }

    return false
  }

  fun reduceOnce(): ReduceAction {
    if (attemptExplode()) {
      return ReduceAction.EXPLODE
    }
    if (attemptSplit()) {
      return ReduceAction.SPLIT
    }

    return ReduceAction.NONE
  }

  fun reduce(): Node {
    do {
      val reduceAction = reduceOnce()
//      println(reduceAction)
//      println(addition)
    } while (reduceAction != ReduceAction.NONE)

    return this
  }

  override fun toString(): String {
    if (value != null) {
      return value.toString()
    }
    return "[$left,$right]"
  }
}

fun main() {
  fun parseInput(input: List<String>): List<Node> {
    return input.map(Node.Companion::fromString)
  }

  fun List<Node>.addAllAndReduce() = this.reduce { acc, next ->
    val addition = acc.add(next)
    println(addition)

    addition.reduce()
  }

  fun part1(input: List<String>): Pair<String, Long> {
    val numbers = parseInput(input)
    val endResult = numbers.addAllAndReduce()

    println(endResult)

    return Pair(endResult.toString(), endResult.magnitude())
  }

  fun part2(input: List<String>): Long {
    val numbers = parseInput(input)
    var max = 0L
    numbers.indices.forEach { i ->
      (i+1 until numbers.size).forEach { j ->
        val add1 = numbers[i].add(numbers[j]).reduce()
        val add1Magnitude = add1.magnitude()
        if (add1Magnitude > max) {
          max = add1Magnitude
        }

        val add2 = numbers[j].add(numbers[i]).reduce()
        val add2Magnitude = add2.magnitude()
        if (add2Magnitude > max) {
          max = add2Magnitude
        }
      }
    }

    return max
  }

  val inputDir = "src/day18/inputs"

  val test0Result = runSolution("Part1 test0") { part1(readInput("test0", inputDir)) }
  assertThat(test0Result.first).isEqualTo("[[[[0,7],4],[[7,8],[6,0]]],[8,1]]")

  val test1Result = runSolution("Part1 test1") { part1(readInput("test1", inputDir)) }
  assertThat(test1Result.first).isEqualTo("[[[[1,1],[2,2]],[3,3]],[4,4]]")

  val test2Result = runSolution("Part1 test2") { part1(readInput("test2", inputDir)) }
  assertThat(test2Result.first).isEqualTo("[[[[3,0],[5,3]],[4,4]],[5,5]]")

  val test3Result = runSolution("Part1 test3") { part1(readInput("test3", inputDir)) }
  assertThat(test3Result.first).isEqualTo("[[[[5,0],[7,4]],[5,5]],[6,6]]")

  val test4Input = readInput("test4", inputDir)
  val testResult4 = runSolution("Part4 test") { part1(test4Input) }
  assertThat(testResult4.second).isEqualTo(4140)

  val input = readInput("input", inputDir)
  runSolution("Part1") { part1(input) }

  val part2TestResult = runSolution("Part2 test") { part2(test4Input) }
  assertThat(part2TestResult).isEqualTo(3993)

  runSolution("Part2") { part2(input) }
}
