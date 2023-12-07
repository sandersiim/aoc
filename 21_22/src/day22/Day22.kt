package day22

import Cuboid
import CuboidUnion
import org.assertj.core.api.Assertions.assertThat
import readInput
import runSolution

enum class CommandType {
  ON,
  OFF
}
data class RebootCommand(
  val type: CommandType,
  val cuboid: Cuboid
) {
  companion object {
    fun parse(input: String): RebootCommand {
      val parts = input.split(" ")
      val commandType = CommandType.valueOf(parts[0].uppercase())

      val ranges = parts[1].split(",").map {
        val nums = it.drop(2).split("..").map(String::toInt)
        IntRange(nums[0], nums[1])
      }

      return RebootCommand(
        commandType,
        Cuboid(
          ranges[0],
          ranges[1],
          ranges[2]
        )
      )
    }
  }
}

fun parseInput(input:List<String>) = input.map(RebootCommand.Companion::parse)

fun main() {

  fun part1(input: List<String>): Long {
    val commands = parseInput(input)
    val result = CuboidUnion()
    commands.take(20).forEach {
      when (it.type) {
        CommandType.ON -> result.add(it.cuboid)
        CommandType.OFF -> result.remove(it.cuboid)
      }
    }

    return result.size()
  }

  fun part2(input: List<String>): Long {
    val commands = parseInput(input)
    val result = CuboidUnion()
    commands.forEach {
      when (it.type) {
        CommandType.ON -> result.add(it.cuboid)
        CommandType.OFF -> result.remove(it.cuboid)
      }
    }

    return result.size()
  }

  val inputDir = "src/day22/inputs"

  val testResult = runSolution("Part1 test") { part1(readInput("testInput", inputDir)) }
  assertThat(testResult).isEqualTo(590784)

  runSolution("Part1") { part1(readInput("input", inputDir)) }

  val testResult2 = runSolution("Part2 test") { part2(readInput("testInput2", inputDir)) }
  assertThat(testResult2).isEqualTo(2758514936282235)

  runSolution("Part2") { part2(readInput("input", inputDir)) }
}
