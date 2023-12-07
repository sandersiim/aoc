package aoc2022.day21

import org.assertj.core.api.Assertions
import readInput
import runSolution

class MonkeyMath(
  val ops: MutableMap<String, Op>
) {
  sealed interface Op
  sealed interface MathOp : Op {
    val op1: String
    val op2: String
  }

  data class ValueOp(
    val value: Long,
  ): Op

  data class AddOp(
    override val op1: String,
    override val op2: String,
  ): MathOp

  data class SubOp(
    override val op1: String,
    override val op2: String,
  ): MathOp

  data class MulOp(
    override val op1: String,
    override val op2: String,
  ): MathOp

  data class DivOp(
    override val op1: String,
    override val op2: String,
  ): MathOp

  fun valueOf(opId: String): Long {
    return when (val op = ops.getValue(opId)) {
      is ValueOp -> op.value
      is AddOp -> valueOf(op.op1) + valueOf(op.op2)
      is SubOp -> valueOf(op.op1) - valueOf(op.op2)
      is MulOp -> valueOf(op.op1) * valueOf(op.op2)
      is DivOp -> valueOf(op.op1) / valueOf(op.op2)
    }
  }

  fun containsHumnOp(opId: String) = containsSubOp(opId, "humn")
  fun containsSubOp(opId: String, targetOpId: String): Boolean {
    if (opId == targetOpId) {
      return true
    }

    return when (val op = ops.getValue(opId)) {
      is ValueOp -> false
      is MathOp -> containsSubOp(op.op1, targetOpId) || containsSubOp(op.op2, targetOpId)
    }
  }

  fun correctHumnNumber(executedOpId: String, targetNumber: Long): Long {
    if (executedOpId == "humn") {
      return targetNumber
    }
    val executedOpp = ops[executedOpId]
    if (executedOpp is ValueOp) {
      throw IllegalStateException("got to non-humn valueOp")
    }
    val executedOp = executedOpp as MathOp

    return if (containsHumnOp(executedOp.op1)) {
      val newTargetValue = when (executedOp) {
        is AddOp -> targetNumber - valueOf(executedOp.op2)
        is SubOp -> targetNumber + valueOf(executedOp.op2)
        is MulOp -> targetNumber / valueOf(executedOp.op2)
        is DivOp -> targetNumber * valueOf(executedOp.op2)
      }
      correctHumnNumber(executedOp.op1, newTargetValue)
    } else {
      val newTargetValue = when (executedOp) {
        is AddOp -> targetNumber - valueOf(executedOp.op1)
        is SubOp -> valueOf(executedOp.op1) - targetNumber
        is MulOp -> targetNumber / valueOf(executedOp.op1)
        is DivOp -> valueOf(executedOp.op1) / targetNumber
      }
      correctHumnNumber(executedOp.op2, newTargetValue)
    }
  }
}

fun main() {
  fun parseInput(inputs: List<String>): MonkeyMath {
    val monkeyOps = mutableMapOf<String, MonkeyMath.Op>()
    inputs.forEach {
      val id = it.substring(0, 4)
      if (it[6].isDigit()) {
        monkeyOps[id] = MonkeyMath.ValueOp(it.substringAfter(": ").toLong())
      } else if (it[11] == '+') {
        monkeyOps[id] = MonkeyMath.AddOp(it.substring(6, 10), it.substring(13, 17))
      } else if (it[11] == '-') {
        monkeyOps[id] = MonkeyMath.SubOp(it.substring(6, 10), it.substring(13, 17))
      } else if (it[11] == '*') {
        monkeyOps[id] = MonkeyMath.MulOp(it.substring(6, 10), it.substring(13, 17))
      } else if (it[11] == '/') {
        monkeyOps[id] = MonkeyMath.DivOp(it.substring(6, 10), it.substring(13, 17))
      }
    }

    return MonkeyMath(monkeyOps)
  }

  fun part1(input: List<String>): Long {
    return parseInput(input).valueOf("root")
  }

  fun part2(input: List<String>): Long {
    val math = parseInput(input)
    val rootOp = math.ops.getValue("root") as MonkeyMath.MathOp
    println("rootOp: $rootOp")

    println(math.containsHumnOp(rootOp.op1))
    println(math.containsHumnOp(rootOp.op2))

    val targetNumber = math.valueOf(rootOp.op2)
    println("target num: $targetNumber")

    return math.correctHumnNumber(rootOp.op1, targetNumber)
  }

  val inputDir = "src/aoc2022/day21/inputs"

  val testResult = runSolution("Part1 test") { part1(readInput("test", inputDir)) }
  Assertions.assertThat(testResult).isEqualTo(152)

  val input = readInput("input", inputDir)
  runSolution("Part1") { part1(input) }

  val test2Result = runSolution("Part2 test") { part2(readInput("test", inputDir)) }
  Assertions.assertThat(test2Result).isEqualTo(301)

  runSolution("Part2") { part2(input) }

}
