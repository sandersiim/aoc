package aoc2022.day16

import org.assertj.core.api.Assertions
import readInput
import runSolution
import java.lang.IllegalStateException
import java.util.PriorityQueue

fun main() {
  fun parseRoomData(data: String): RoomData {
    val roomLabel = data.substring(6, 8)
    var rest = data.replace(Regex("""Valve \w\w has flow rate="""), "")
    val flowRate = rest.substringBefore(';').toInt()
    rest = rest.replace(Regex(""".* to valve(s?) """), "")
    val nextRooms = rest.split(", ").toSet()

    return RoomData(
      roomLabel = roomLabel,
      flowRate = flowRate,
      nextRooms = nextRooms,
    )
  }

  fun part1(input: List<String>): Int {
    val rooms = input.map(::parseRoomData).associateBy { it.roomLabel }
    rooms.forEach(::println)
    val valvesWorthOpening = rooms.values.filter { it.flowRate > 0 }.map { it.roomLabel }.toSet()
    println(valvesWorthOpening)

    data class ExplorationState(
      val currentRoom: RoomData = rooms.getValue("AA"),
      val currentPathPressure: Int = 0,
      val currentOpenValves: Set<String> = emptySet(),
      val currentTime: Int = 0,
      val currentPath: List<Pair<String, Int>> = listOf(),
      var maxPressure: Int = Int.MAX_VALUE,
    ) {
      init {
        updateMaxPressure()
      }

      fun updateMaxPressure() {
        val timeLeft = 30 - currentTime - 2
        val maxMoreToBeGained = valvesWorthOpening.minus(currentOpenValves)
          .map { rooms.getValue(it).flowRate }
          .sortedDescending()
          .foldIndexed(0) { index, acc, elem ->
            acc + elem * (timeLeft - index * 2).coerceAtLeast(0)
          }
        maxPressure = currentPathPressure + maxMoreToBeGained
      }

      fun moveToRoom(label: String): ExplorationState {
        if (label !in currentRoom.nextRooms) {
          throw IllegalStateException("$label not in current room next rooms ${currentRoom.roomLabel}")
        }
        return this.copy(
          currentPath = currentPath + listOf(Pair(label, 1)),
          currentRoom = rooms.getValue(label),
          currentTime = currentTime + 1,
        ).also { updateMaxPressure() }
      }

      fun moveAndOpenValve(label: String): ExplorationState {
        val newRoom = rooms.getValue(label)
        val newTime = currentTime + 2
        val addedPressure = (30 - newTime) * newRoom.flowRate

        return this.copy(
          currentRoom = newRoom,
          currentOpenValves = currentOpenValves + listOf(label),
          currentPath = currentPath + listOf(Pair(label, 2)),
          currentTime = newTime,
          currentPathPressure = currentPathPressure + addedPressure,
        ).also { updateMaxPressure() }
      }

      fun nextSteps(label: String): List<ExplorationState> {
        val nextRoom = rooms.getValue(label)
        val nexSteps = mutableListOf(moveToRoom(nextRoom.roomLabel))

        if (
          nextRoom.roomLabel !in currentOpenValves &&
          nextRoom.flowRate > 0 &&
          currentTime < 29
        ) {
          nexSteps.add(moveAndOpenValve(nextRoom.roomLabel))
        }
        return nexSteps
      }

      fun pointlessRoomsToGoToNext(): Set<String> {
        val lastValveOpeningIndex = currentPath.indexOfLast { it.second > 1 }
        if (lastValveOpeningIndex == -1) {
          return emptySet()
        }

        return currentPath.drop(lastValveOpeningIndex).map { it.first }.toSet()
      }

      fun nextStates(): List<ExplorationState> {
        val pointlessRooms = pointlessRoomsToGoToNext()
        return currentRoom.nextRooms.minus(pointlessRooms).flatMap { nextSteps(it) }
      }

      fun finished(): Boolean {
        return currentTime >= 30 || valvesWorthOpening == currentOpenValves
      }
    }

    val stateQueue = PriorityQueue<ExplorationState>(compareBy { -it.maxPressure })
    val first = ExplorationState()
    stateQueue.add(first)

    var bestPath = first

    var current: ExplorationState
    while (stateQueue.size > 0) {
      current = stateQueue.poll()
      if (current.finished()) {
        if (current.currentPathPressure > bestPath.currentPathPressure) {
          bestPath = current
        }
      } else if (current.maxPressure > bestPath.currentPathPressure) {
        stateQueue.addAll(current.nextStates())
      }
    }

    println(bestPath)
    bestPath.currentPath.forEach(::println)

    return bestPath.currentPathPressure
  }

  fun part2(input: List<String>): Int {
    val rooms = input.map(::parseRoomData).associateBy { it.roomLabel }
    rooms.forEach(::println)
    val valvesWorthOpening = rooms.values.filter { it.flowRate > 0 }.map { it.roomLabel }.toSet()
    println(valvesWorthOpening)

    data class PlayerState(
      val currentRoom: RoomData = rooms.getValue("AA"),
      val currentPath: List<Pair<String, Int>> = listOf(),
      val currentTime: Int = 0
    ) {
      fun nextRoomsToGoTo(): Set<String> {
        val lastValveOpeningIndex = currentPath.indexOfLast { it.second > 1 }
        if (lastValveOpeningIndex == -1) {
          return currentRoom.nextRooms
        }

        return currentRoom.nextRooms.minus(currentPath.drop(lastValveOpeningIndex).map { it.first }.toSet())
      }

      fun roomHasOpenableValve(label: String, currentOpenValves: Set<String>): Boolean {
        val room = rooms.getValue(label)
        return room.roomLabel !in currentOpenValves &&
          room.flowRate > 0 &&
          currentTime < 25
      }

      fun finished(currentOpenValves: Set<String>): Boolean {
        return currentTime >= 26 || valvesWorthOpening == currentOpenValves
      }
    }

    data class State(
      val player1: PlayerState = PlayerState(),
      val player2: PlayerState = PlayerState(),
      val currentPathPressure: Int = 0,
      val currentOpenValves: Set<String> = emptySet(),
      var maxPressure: Int = Int.MAX_VALUE,
    ) {
      init {
        updateMaxPressure()
      }

      fun updateMaxPressure() {
        var timeLeft1 = 26 - player1.currentTime - 2
        var timeLeft2 = 26 - player2.currentTime - 2
        val maxMoreToBeGained = valvesWorthOpening.minus(currentOpenValves)
          .map { rooms.getValue(it).flowRate }
          .sortedDescending()
          .foldIndexed(0) { _, acc, elem ->
            val newSum: Int
            if (timeLeft1 > timeLeft2) {
              newSum = acc + elem * timeLeft1
              timeLeft1 = (timeLeft1 - 2).coerceAtLeast(0)
            } else {
              newSum = acc + elem * timeLeft2
              timeLeft2 = (timeLeft2 - 2).coerceAtLeast(0)
            }
            newSum
          }
        maxPressure = currentPathPressure + maxMoreToBeGained
      }

      fun movePlayer1ToRoom(label: String): State {
        return this.copy(
          player1 = player1.copy(
            currentRoom = rooms.getValue(label),
            currentPath = player1.currentPath + listOf(Pair(label, 1)),
            currentTime = player1.currentTime + 1,
          ),
        )
      }

      fun movePlayer2ToRoom(label: String): State {
        return this.copy(
          player2 = player2.copy(
            currentRoom = rooms.getValue(label),
            currentPath = player2.currentPath + listOf(Pair(label, 1)),
            currentTime = player2.currentTime + 1,
          ),
        )
      }

      fun movePlayer1ToRoomAndOpenValve(label: String): State {
        val newRoom = rooms.getValue(label)
        val newTime = player1.currentTime + 2
        val addedPressure = (26 - newTime) * newRoom.flowRate

        return this.copy(
          player1 = player1.copy(
            currentRoom = newRoom,
            currentPath = player1.currentPath + listOf(Pair(label, 2)),
            currentTime = newTime,
          ),
          currentOpenValves = currentOpenValves + listOf(label),
          currentPathPressure = currentPathPressure + addedPressure,
        )
      }

      fun movePlayer2ToRoomAndOpenValve(label: String): State {
        val newRoom = rooms.getValue(label)
        val newTime = player2.currentTime + 2
        val addedPressure = (26 - newTime) * newRoom.flowRate

        return this.copy(
          player2 = player2.copy(
            currentRoom = newRoom,
            currentPath = player2.currentPath + listOf(Pair(label, 2)),
            currentTime = newTime,
          ),
          currentOpenValves = currentOpenValves + listOf(label),
          currentPathPressure = currentPathPressure + addedPressure,
        )
      }

      fun nextStates(): List<State> {
        val player1NextRooms = player1.nextRoomsToGoTo()
        val player2NextRooms = player2.nextRoomsToGoTo()

        return player1NextRooms.flatMap { player1NextRoom ->
          player2NextRooms.flatMap { player2NextRoom ->
            val nextSteps = mutableListOf<State>()
            nextSteps.add(
              movePlayer1ToRoom(player1NextRoom)
                .movePlayer2ToRoom(player2NextRoom)
                .also { updateMaxPressure() }
            )
            val player1ValveOpenable = player1.roomHasOpenableValve(player1NextRoom, currentOpenValves)
            val player2ValveOpenable = player2.roomHasOpenableValve(player2NextRoom, currentOpenValves)
            if (player1ValveOpenable) {
              nextSteps.add(
                movePlayer1ToRoomAndOpenValve(player1NextRoom)
                  .movePlayer2ToRoom(player2NextRoom)
                  .also { updateMaxPressure() }
              )
            }
            if (player2ValveOpenable) {
              nextSteps.add(
                movePlayer1ToRoom(player1NextRoom)
                  .movePlayer2ToRoomAndOpenValve(player2NextRoom)
                  .also { updateMaxPressure() }
              )
            }
            if (player1ValveOpenable && player2ValveOpenable && player1NextRoom != player2NextRoom) {
              nextSteps.add(
                movePlayer1ToRoomAndOpenValve(player1NextRoom)
                  .movePlayer2ToRoomAndOpenValve(player2NextRoom)
                  .also { updateMaxPressure() }
              )
            }

            nextSteps
          }
        }
      }

      fun finished(): Boolean {
        return player1.finished(currentOpenValves) && player2.finished(currentOpenValves)
      }
    }

    val stateQueue = PriorityQueue<State>(compareBy { -it.currentPathPressure })
    val first = State()
    stateQueue.add(first)

    var bestPath = first

    var current: State
    while (stateQueue.size > 0) {
      current = stateQueue.poll()
      if (current.finished()) {
        if (current.currentPathPressure > bestPath.currentPathPressure) {
          bestPath = current
          println(bestPath.currentPathPressure)
          println("states left - ${stateQueue.size}")
        }
      } else if (current.maxPressure > bestPath.currentPathPressure) {
        stateQueue.addAll(current.nextStates())
      }
    }
    // 2772

    println(bestPath)
    bestPath.player1.currentPath.forEach(::println)
    bestPath.player2.currentPath.forEach(::println)

    return bestPath.currentPathPressure
  }

  val inputDir = "src/aoc2022/day16/inputs"

  val testResult = runSolution("Part1 test") { part1(readInput("test", inputDir)) }
  Assertions.assertThat(testResult).isEqualTo(1651)

  val input = readInput("input", inputDir)
  runSolution("Part1") { part1(input) }

  val test2Result = runSolution("Part2 test") { part2(readInput("test", inputDir)) }
  Assertions.assertThat(test2Result).isEqualTo(1707)

  runSolution("Part2") { part2(input) }

}

data class RoomData(
  val roomLabel: String,
  val flowRate: Int,
  val nextRooms: Set<String>,
)
