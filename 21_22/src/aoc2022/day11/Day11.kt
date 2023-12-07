package aoc2022.day11

import org.assertj.core.api.Assertions
import readInput
import runSolution

// map of mod to current remainder
typealias Item = Map<Int, Int>

fun Item.add(x: Int): Item {
  return mapValues { (mod, current) -> (current + x).mod(mod) }
}

fun Item.mul(x: Int): Item {
  return mapValues { (mod, current) -> (current * x).mod(mod) }
}

fun Item.square(): Item {
  return mapValues { (mod, current) -> (current * current).mod(mod) }
}

fun Item.divisibleBy(x: Int): Boolean {
  return this.getValue(x) == 0
}

fun createItem(x: Int, divisors: List<Int>): Item {
  return divisors.associateWith { x.mod(it) }
}

data class Monkey1(
  val items: ArrayDeque<Int>,
  val op: (worryLevel: Int) -> Int,
  val testDivisor: Int,
  val ifTrueNextMonkey: Int,
  val ifFalseNextMonkey: Int,
  var inspections: Long,
) {
  companion object {
    fun parse(input: List<String>): Monkey1 {
      val items = input[1].removePrefix("  Starting items: ").split(", ").map {it.toInt() }
      val opString = input[2].removePrefix("  Operation: new = old ")
      val operation = opString[0]
      val operand = opString.substring(2)
      val op: (worryLevel: Int) -> Int = when (operation) {
        '+' -> { x -> x + operand.toInt() }
        '*' -> if (operand == "old") {
          { x -> x * x }
        } else {
          { x -> x * operand.toInt() }
        }

        else -> throw IllegalArgumentException("$operation")
      }
      val testDivisor = input[3].removePrefix("  Test: divisible by ").toInt()
      val ifTrueNextMonkey = input[4].removePrefix("    If true: throw to monkey ").toInt()
      val ifFalseNextMonkey = input[5].removePrefix("    If false: throw to monkey ").toInt()

      return Monkey1(
        items = ArrayDeque(items),
        op = op,
        testDivisor = testDivisor,
        ifTrueNextMonkey = ifTrueNextMonkey,
        ifFalseNextMonkey = ifFalseNextMonkey,
        inspections = 0,
      )
    }
  }
}

data class Monkey2(
  val items: ArrayDeque<Item>,
  val op: (worryLevel: Item) -> Item,
  val testDivisor: Int,
  val ifTrueNextMonkey: Int,
  val ifFalseNextMonkey: Int,
  var inspections: Long,
) {
  companion object {
    fun parse(input: List<String>, divisors: List<Int>): Monkey2 {
      val items = input[1].removePrefix("  Starting items: ").split(", ").map { createItem(it.toInt(), divisors) }
      val opString = input[2].removePrefix("  Operation: new = old ")
      val operation = opString[0]
      val operand = opString.substring(2)
      val op: (worryLevel: Item) -> Item = when (operation) {
        '+' -> { x -> x.add(operand.toInt()) }
        '*' -> if (operand == "old") {
          { x -> x.square() }
        } else {
          { x -> x.mul(operand.toInt()) }
        }

        else -> throw IllegalArgumentException("$operation")
      }
      val testDivisor = input[3].removePrefix("  Test: divisible by ").toInt()
      val ifTrueNextMonkey = input[4].removePrefix("    If true: throw to monkey ").toInt()
      val ifFalseNextMonkey = input[5].removePrefix("    If false: throw to monkey ").toInt()

      return Monkey2(
        items = ArrayDeque(items),
        op = op,
        testDivisor = testDivisor,
        ifTrueNextMonkey = ifTrueNextMonkey,
        ifFalseNextMonkey = ifFalseNextMonkey,
        inspections = 0,
      )
    }
  }
}

fun main() {
  fun part1(input: List<String>, rounds: Int): Long {
    val monkeys = mutableListOf<Monkey1>()
    var inputIdx = 0
    while (inputIdx*7 < input.size) {
      monkeys.add(Monkey1.parse(input.subList(inputIdx*7, inputIdx*7 + 6)))
      inputIdx++
    }
    println(monkeys)

    repeat(rounds) {
      monkeys.forEach { monkey ->
        while (monkey.items.isNotEmpty()) {
          var item = monkey.items.removeFirst()
          monkey.inspections++
          item = monkey.op(item) / 3
          val targetMonkey = if (item.rem(monkey.testDivisor) == 0) {
            monkey.ifTrueNextMonkey
          } else {
            monkey.ifFalseNextMonkey
          }
          monkeys[targetMonkey].items.addLast(item)
        }
      }
    }

    monkeys.sortByDescending { it.inspections }

    return monkeys[0].inspections * monkeys[1].inspections
  }

  fun part2(input: List<String>, rounds: Int): Long {
    val divisors = listOf(2, 3, 5, 7, 11, 13, 17, 19, 23)
    val monkeys = mutableListOf<Monkey2>()
    var inputIdx = 0
    while (inputIdx*7 < input.size) {
      monkeys.add(Monkey2.parse(input.subList(inputIdx*7, inputIdx*7 + 6), divisors))
      inputIdx++
    }
    repeat(rounds) {
      monkeys.forEach { monkey ->
        while (monkey.items.isNotEmpty()) {
          var item = monkey.items.removeFirst()
          monkey.inspections++
          item = monkey.op(item)
          val targetMonkey = if (item.divisibleBy(monkey.testDivisor)) {
            monkey.ifTrueNextMonkey
          } else {
            monkey.ifFalseNextMonkey
          }
          monkeys[targetMonkey].items.addLast(item)
        }
      }
    }

    monkeys.sortByDescending { it.inspections }

    return monkeys[0].inspections * monkeys[1].inspections
  }

  val inputDir = "src/aoc2022/day11/inputs"

  val testResult = runSolution("Part1 test") { part1(readInput("test", inputDir), 20) }
  Assertions.assertThat(testResult).isEqualTo(10605)

  val input = readInput("input", inputDir)
  runSolution("Part1") { part1(input, 20) }

  val test2Result = runSolution("Part2 test") { part2(readInput("test", inputDir), 10000) }
  Assertions.assertThat(test2Result).isEqualTo(2713310158)

  runSolution("Part2") { part2(input, 10000) }

}
