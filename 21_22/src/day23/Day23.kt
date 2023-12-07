package day23

import day23.part1.Amphipod
import day23.part1.State
import day23.part1.Room
import day23.part2.Room2
import day23.part2.State2
import org.assertj.core.api.Assertions.assertThat
import runSolution
import java.util.PriorityQueue

fun main() {
  fun part1(input: State): Long {
    val gScore = mutableMapOf(input to 0L)
    val fScore = mutableMapOf(input to input.heuristic())
    val cameFrom = mutableMapOf<State, Pair<State, Long>>()

    val compareByFscore: Comparator<State> = compareBy { fScore.getOrDefault(it, Long.MAX_VALUE) }
    val openSetQueue = PriorityQueue(compareByFscore)
    val openSetSet = mutableSetOf<State>()
    openSetQueue.add(input)
    openSetSet.add(input)

    println(input.toString())
    println("Input heuristic: ${input.heuristic()}")
    var current: State
    while (!openSetQueue.isEmpty()) {
      current = openSetQueue.poll()
      openSetSet.remove(current)

      if (current.isGoal()) {
        var result = 0L
        while (current in cameFrom) {
          println(current)
          val next = cameFrom[current]!!
          result += next.second
          println(next.second)
          current = next.first
        }
        return result
      }

      current.generateNextSteps().forEach {
        val (nextState, cost) = it
        val newGScore = gScore[current]!! + cost
        if (newGScore < gScore.getOrDefault(nextState, Long.MAX_VALUE)) {
          cameFrom[nextState] = Pair(current, cost)
          gScore[nextState] = newGScore
          fScore[nextState] = newGScore + nextState.heuristic()
          if (!openSetSet.contains(nextState)) {
            openSetQueue.add(nextState)
            openSetSet.add(nextState)
          }
        }
      }
    }

    return 0
  }

  fun part2(input: State2): Long {
    val gScore = mutableMapOf(input to 0L)
    val fScore = mutableMapOf(input to input.heuristic())
    val cameFrom = mutableMapOf<State2, Pair<State2, Long>>()

    val compareByFscore: Comparator<State2> = compareBy { fScore.getOrDefault(it, Long.MAX_VALUE) }
    val openSetQueue = PriorityQueue(compareByFscore)
    val openSetSet = mutableSetOf<State2>()
    openSetQueue.add(input)
    openSetSet.add(input)

    println(input.toString())
    var current: State2
    while (!openSetQueue.isEmpty()) {
      current = openSetQueue.poll()
      openSetSet.remove(current)

      if (current.isGoal()) {
        var result = 0L
        while (current in cameFrom) {
          println(current)
          val next = cameFrom[current]!!
          result += next.second
          println(next.second)
          current = next.first
        }
        return result
      }

      current.generateNextSteps().forEach {
        val (nextState, cost) = it
        val newGScore = gScore[current]!! + cost
        if (newGScore < gScore.getOrDefault(nextState, Long.MAX_VALUE)) {
          cameFrom[nextState] = Pair(current, cost)
          gScore[nextState] = newGScore
          fScore[nextState] = newGScore + nextState.heuristic()
          if (!openSetSet.contains(nextState)) {
            openSetSet.add(nextState)
            openSetQueue.add(nextState)
          }
        }
      }
    }

    return 0
  }

  val testInput = State(
    MutableList(11) { null },
    mutableListOf(
      Room(Amphipod.B, Amphipod.A, 0),
      Room(Amphipod.C, Amphipod.D, 1),
      Room(Amphipod.B, Amphipod.C, 2),
      Room(Amphipod.D, Amphipod.A, 3)
    )
  )
  val input = State(
    MutableList(11) { null },
    mutableListOf(
      Room(Amphipod.B, Amphipod.C, 0),
      Room(Amphipod.A, Amphipod.D, 1),
      Room(Amphipod.B, Amphipod.D, 2),
      Room(Amphipod.C, Amphipod.A, 3)
    )
  )

  val testResult = runSolution("Part1 test") { part1(testInput) }
  assertThat(testResult).isEqualTo(12521)

  runSolution("Part1") { part1(input) }

  val debug1 = State2(
    MutableList(11) { null },
    mutableListOf(
      Room2(listOf(Amphipod.B, Amphipod.A, Amphipod.A, Amphipod.A), 0),
      Room2(listOf(Amphipod.A, Amphipod.B, Amphipod.B, Amphipod.B), 1),
      Room2(listOf(Amphipod.D, Amphipod.C, Amphipod.C, Amphipod.C), 2),
      Room2(listOf(Amphipod.C, Amphipod.D, Amphipod.D, Amphipod.D), 3)
    )
  )
  println("Heuristic: ${debug1.heuristic()}")
  assertThat(debug1.heuristic()).isLessThanOrEqualTo(4646)

  val part2DebugResult1 = runSolution("Part2 debug1") { part2(debug1) }
  assertThat(part2DebugResult1).isEqualTo(4646)

  val debug2 = State2(
    MutableList(11) { null },
    mutableListOf(
      Room2(listOf(Amphipod.A, Amphipod.A, Amphipod.A, Amphipod.A), 0),
      Room2(listOf(Amphipod.B, Amphipod.B, Amphipod.B, Amphipod.B), 1),
      Room2(listOf(Amphipod.C, Amphipod.C, Amphipod.D, Amphipod.C), 2),
      Room2(listOf(Amphipod.D, Amphipod.D, Amphipod.C, Amphipod.D), 3)
    )
  )
  println("Heuristic: ${debug2.heuristic()}")
  assertThat(debug2.heuristic()).isLessThanOrEqualTo(22400)
  val part2DebugResult2 = runSolution("Part2 debug2") { part2(debug2) }
  assertThat(part2DebugResult2).isEqualTo(22400)

  val debug3 = State2(
    MutableList(11) { null },
    mutableListOf(
      Room2(listOf(Amphipod.A, Amphipod.A, Amphipod.A, Amphipod.A), 0),
      Room2(listOf(Amphipod.B, Amphipod.B, Amphipod.B, Amphipod.B), 1),
      Room2(listOf(Amphipod.C, Amphipod.C, Amphipod.C, Amphipod.D), 2),
      Room2(listOf(Amphipod.D, Amphipod.D, Amphipod.D, Amphipod.C), 3)
    )
  )
  println("Heuristic: ${debug3.heuristic()}")
  assertThat(debug3.heuristic()).isLessThanOrEqualTo(38200)
  val part2DebugResult3 = runSolution("Part2 debug3") { part2(debug3) }
  assertThat(part2DebugResult3).isEqualTo(38200)

  val part2Test = State2(
    MutableList(11) { null },
    mutableListOf(
      Room2(listOf(Amphipod.B, Amphipod.D, Amphipod.D, Amphipod.A), 0),
      Room2(listOf(Amphipod.C, Amphipod.C, Amphipod.B, Amphipod.D), 1),
      Room2(listOf(Amphipod.B, Amphipod.B, Amphipod.A, Amphipod.C), 2),
      Room2(listOf(Amphipod.D, Amphipod.A, Amphipod.C, Amphipod.A), 3)
    )
  )
  println("Heuristic: ${part2Test.heuristic()}")
  assertThat(part2Test.heuristic()).isLessThanOrEqualTo(44169)

  val part2TestResult = runSolution("Part2 test") {
    part2(part2Test)
  }
  assertThat(part2TestResult).isEqualTo(44169)

  val part2Input = State2(
    MutableList(11) { null },
    mutableListOf(
      Room2(listOf(Amphipod.B, Amphipod.D, Amphipod.D, Amphipod.C), 0),
      Room2(listOf(Amphipod.A, Amphipod.C, Amphipod.B, Amphipod.D), 1),
      Room2(listOf(Amphipod.B, Amphipod.B, Amphipod.A, Amphipod.D), 2),
      Room2(listOf(Amphipod.C, Amphipod.A, Amphipod.C, Amphipod.A), 3)
    )
  )
  println("Heuristic: ${part2Input.heuristic()}")

  runSolution("Part2") { part2(part2Input) }
}
