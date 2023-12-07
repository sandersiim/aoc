package aoc2022.day19

import org.assertj.core.api.Assertions
import readInput
import runSolution
import java.util.PriorityQueue
import java.util.Scanner

fun parseBluprint(data: String): BluePrint {
  val scanner = Scanner(data)

  scanner.next()
  val index = scanner.next().substringBefore(":").toInt()

  repeat(4) { scanner.next() }
  val oreRobotCost = scanner.nextInt()

  repeat(5) { scanner.next() }
  val clayRobotCost = scanner.nextInt()

  repeat(5) { scanner.next() }
  val obsidianRobotOreCost = scanner.nextInt()

  repeat(2) { scanner.next() }
  val obsidianRobotClayCost = scanner.nextInt()

  repeat(5) { scanner.next() }
  val geodeRobotOreCost = scanner.nextInt()

  repeat(2) { scanner.next() }
  val geodeRobotObsidianCost = scanner.nextInt()

  return BluePrint(
    index = index,
    oreRobotCost = oreRobotCost,
    clayRobotCost = clayRobotCost,
    obsidianRobotOreCost = obsidianRobotOreCost,
    obsidianRobotClayCost = obsidianRobotClayCost,
    geodeRobotOreCost = geodeRobotOreCost,
    geodeRobotObsidianCost = geodeRobotObsidianCost,
  )
}

data class BluePrint(
  val index: Int,
  val oreRobotCost: Int,
  val clayRobotCost: Int,
  val obsidianRobotOreCost: Int,
  val obsidianRobotClayCost: Int,
  val geodeRobotOreCost: Int,
  val geodeRobotObsidianCost: Int,
)

var maxTime = 24

data class State(
  val oreRobots: Int = 1,
  val clayRobots: Int = 0,
  val obsidianRobots: Int = 0,
  val geodeRobots: Int = 0,
  val ore: Int = 0,
  val clay: Int = 0,
  val obsidian: Int = 0,
  val geode: Int = 0,
  val currentTime: Int = 0,
) {
  val goodnessHeuristic by lazy {
    val timeLeft = maxTime - currentTime
    if (geode + geodeRobots * timeLeft >= 65536) {
      throw IllegalStateException("goodness problem: ${geode + geodeRobots * timeLeft}")
    }
    (geode + geodeRobots * timeLeft).toULong().shl(48) +
        (obsidian + obsidianRobots * timeLeft).toULong().shl(32) +
        (clay + clayRobots * timeLeft).toULong().shl(16) +
        (ore + oreRobots * timeLeft).toULong()
  }

  fun upperLimitGeodes(bluePrint: BluePrint): Int {
    var current = this
    while (current.currentTime < maxTime) {
      if (current.obsidian >= bluePrint.geodeRobotObsidianCost) {
        current = current.buildGeodeRobot(bluePrint)
      } else {
        current = if (current.clay >= bluePrint.obsidianRobotClayCost) {
          current.buildObsidianRobot(bluePrint)
        } else {
          current.progressTime()
        }
        current = current.copy(clayRobots = current.clayRobots + 1)
      }
    }
    return current.geode
  }

  fun canBuildOreRobot(bluePrint: BluePrint): Boolean {
    return ore >= bluePrint.oreRobotCost
  }

  fun canBuildClayRobot(bluePrint: BluePrint): Boolean {
    return ore >= bluePrint.clayRobotCost
  }

  fun canBuildObsidianRobot(bluePrint: BluePrint): Boolean {
    return ore >= bluePrint.obsidianRobotOreCost && clay >= bluePrint.obsidianRobotClayCost
  }

  fun canBuildGeodeRobot(bluePrint: BluePrint): Boolean {
    return ore >= bluePrint.geodeRobotOreCost && obsidian >= bluePrint.geodeRobotObsidianCost
  }

  fun buildOreRobot(bluePrint: BluePrint): State {
    return progressTime().let {
      it.copy(
        ore = it.ore - bluePrint.oreRobotCost,
        oreRobots = it.oreRobots + 1,
      )
    }
  }

  fun buildClayRobot(bluePrint: BluePrint): State {
    return progressTime().let {
      it.copy(
        ore = it.ore - bluePrint.clayRobotCost,
        clayRobots = it.clayRobots + 1,
      )
    }
  }

  fun buildObsidianRobot(bluePrint: BluePrint): State {
    return progressTime().let {
      it.copy(
        ore = it.ore - bluePrint.obsidianRobotOreCost,
        clay = it.clay - bluePrint.obsidianRobotClayCost,
        obsidianRobots = it.obsidianRobots + 1,
      )
    }
  }

  fun buildGeodeRobot(bluePrint: BluePrint): State {
    return progressTime().let {
      it.copy(
        ore = it.ore - bluePrint.geodeRobotOreCost,
        obsidian = it.obsidian - bluePrint.geodeRobotObsidianCost,
        geodeRobots = it.geodeRobots + 1,
      )
    }
  }

  fun nextStates(bluePrint: BluePrint): List<State> {
    val nextStates = mutableListOf(progressTime())
    if (canBuildGeodeRobot(bluePrint)) {
      nextStates.add(buildGeodeRobot(bluePrint))
    }

    if (canBuildObsidianRobot(bluePrint)) {
      nextStates.add(buildObsidianRobot(bluePrint))
    }

    if (canBuildClayRobot(bluePrint)) {
      nextStates.add(buildClayRobot(bluePrint))
    }

    if (canBuildOreRobot(bluePrint)) {
      nextStates.add(buildOreRobot(bluePrint))
    }

    return nextStates
  }

  private fun progressTime(): State {
    return copy(
      ore = ore + oreRobots,
      clay = clay + clayRobots,
      obsidian = obsidian + obsidianRobots,
      geode = geode + geodeRobots,
      currentTime = currentTime + 1,
    )
  }
}

fun main() {
  val inputDir = "src/aoc2022/day19/inputs"

  fun optimalBluePrintValue(bluePrint: BluePrint): Int {
    val stateQueue = PriorityQueue<State>(compareByDescending { it.goodnessHeuristic })
    val first = State()
    stateQueue.add(first)

    var bestPath = first

    var current: State
    var iters = 0
    while (stateQueue.size > 0) {
      if (iters % 50_000_000 == 0) {
        println(iters)
        println("states left - ${stateQueue.size}")
      }
      current = stateQueue.poll()
      val nextStates = current.nextStates(bluePrint)
      nextStates.forEach {
        if (it.currentTime == maxTime) {
          if (it.geode > bestPath.geode) {
            bestPath = it
            println("new best - ${bestPath.geode}")
          }
        } else if (it.upperLimitGeodes(bluePrint) > bestPath.geode) {
          stateQueue.add(it)
        }
      }
      iters++
    }

    return bestPath.geode
  }

  fun part1(input: List<String>): Int {
    val blueprints = input.map(::parseBluprint)

    maxTime = 24
    return blueprints.sumOf { blueprint ->
      optimalBluePrintValue(blueprint).also {
        println("Blueprint ${blueprint.index} : $it")
      } * blueprint.index
    }
  }

  fun part2(input: List<String>): Int {
    val blueprints = input.map(::parseBluprint)

    maxTime = 32

    return blueprints.take(3).map { blueprint ->
      optimalBluePrintValue(blueprint).let {
        println("Blueprint ${blueprint.index} : $it")
        it
      }
    }.fold(1) { acc, elem -> acc * elem }
  }

  val testResult = runSolution("Part1 test") { part1(readInput("test", inputDir)) }
  Assertions.assertThat(testResult).isEqualTo(33)

  val input = readInput("input", inputDir)
  runSolution("Part1") { part1(input) }

  val test2Result = runSolution("Part2 test") { part2(readInput("test", inputDir)) }
  Assertions.assertThat(test2Result).isEqualTo(3472)

  runSolution("Part2") { part2(input) }
}
