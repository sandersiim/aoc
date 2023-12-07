package day23.part2

import day23.part1.Amphipod
import kotlin.math.abs
import kotlin.math.max
import kotlin.math.min

fun MutableList<Amphipod?>.hallwayString(): String {
  return this.joinToString("") {
    it?.name ?: "."
  }
}

data class Room2(val pods: List<Amphipod?>, val index: Int) {
  fun position(): Int {
    return when (index) {
      0 -> 2
      1 -> 4
      2 -> 6
      3 -> 8
      else -> throw Error()
    }
  }

  val correctAmphipod get() = Amphipod.forRoomIndex(index)
  fun outermostPodIndex() = pods.indexOfFirst { it != null }
  fun firstFreePodIndex(): Int {
    return when (val outermostPod = outermostPodIndex()) {
      -1 -> 3
      0 -> -1
      else -> outermostPod - 1
    }
  }

  fun hasOnlyCorrectPods() = pods.all { it == null || it == correctAmphipod }
  fun isCorrect(): Boolean {
    return pods.all { it == correctAmphipod }
  }
}

data class State2(
  val hallway: MutableList<Amphipod?>,
  val rooms: MutableList<Room2>
) {
  companion object {
    val consecutivePodsInWayCosts = listOf(0, 1, 1)
  }

  fun heuristic(): Long {
    var result = 0L
    hallway.forEachIndexed { index, amphipod ->
      if (amphipod != null) {
        val horizontalMoves = abs(index - amphipod.targetRoomPosition())
        result += (horizontalMoves + 1) * amphipod.stepCost
      }
    }
    rooms.forEach { room ->
      val correctAmphipod = room.correctAmphipod
      val firstWrongPodIndex = (3 downTo 0).find { podIndex ->
        val pod = room.pods[podIndex]
        pod != null && pod != correctAmphipod
      }
      if (firstWrongPodIndex != null) {
        var correctPodsInTheWayCounter = 0
        (firstWrongPodIndex downTo 0).forEach {
          val pod = room.pods[it]
          if (pod != null) {
            if (pod != correctAmphipod) {
              result += (it + 2 + abs(pod.targetRoomPosition() - room.position())) * pod.stepCost
            } else {
              result += (it + 2 + consecutivePodsInWayCosts[correctPodsInTheWayCounter]) * 2 * pod.stepCost
              correctPodsInTheWayCounter += 1
            }
          }
        }
      }
      (3 downTo 0).forEach { podIndex ->
        if (room.pods[podIndex] != correctAmphipod) {
          result += podIndex * correctAmphipod.stepCost
        }
      }
    }

    return result
  }

  fun generateNextSteps(): List<Pair<State2, Long>> {
    return hallway.mapIndexedNotNull { hallwayIndex, amphipod ->
      if (amphipod == null) {
        null
      } else {
        val correctRoom = rooms[amphipod.targetRoomIndex()]
        val hallwayPathRange = if (correctRoom.position() < hallwayIndex) {
          correctRoom.position() until hallwayIndex
        } else {
          (hallwayIndex + 1).rangeTo(correctRoom.position())
        }

        if (hallwayPathRange.all { hallway[it] == null } && correctRoom.hasOnlyCorrectPods()) {
          val firstFreeIndex = correctRoom.firstFreePodIndex()
          if (firstFreeIndex >= 0) {
            val horizontalSteps = abs(correctRoom.position() - hallwayIndex)
            val cost = (horizontalSteps + firstFreeIndex + 1) * amphipod.stepCost
            val newState = this.clone()
            newState.hallway[hallwayIndex] = null
            val newPods = correctRoom.pods.toMutableList()
            newPods[firstFreeIndex] = amphipod
            newState.rooms[amphipod.targetRoomIndex()] = correctRoom.copy(pods = newPods)
            return@mapIndexedNotNull Pair(newState, cost.toLong())
          }
        }

        null
      }
    } + rooms.flatMap { room ->
      if (room.pods.all { it == null } || room.isCorrect()) {
        emptyList()
      } else {
        val podIndex = room.outermostPodIndex()
        val pod = room.pods[podIndex]!!
        val correctRoom = rooms[pod.targetRoomIndex()]
        val pathToTargetRoomRange = min(correctRoom.position(), room.position()).rangeTo(max(correctRoom.position(), room.position()))
        if (pod.targetRoomIndex() != room.index && correctRoom.hasOnlyCorrectPods() && pathToTargetRoomRange.all { hallway[it] == null }) {
          val newState = this.clone()
          val newPods1 = room.pods.toMutableList()
          newPods1[podIndex] = null
          newState.rooms[room.index] = room.copy(pods = newPods1)
          val newPods2 = correctRoom.pods.toMutableList()
          val freePlaceIndex = correctRoom.firstFreePodIndex()
          newPods2[freePlaceIndex] = pod
          newState.rooms[correctRoom.index] = correctRoom.copy(pods = newPods2)
          val cost = (abs(correctRoom.position() - room.position()) + podIndex + freePlaceIndex + 2) * pod.stepCost
          listOf(Pair(newState, cost.toLong()))
        } else {
          listOf(0, 1, 3, 5, 7, 9, 10).mapNotNull { hallwayIndex ->
            val hallwayPathRange = min(hallwayIndex, room.position()).rangeTo(max(hallwayIndex, room.position()))
            if (hallwayIndex != room.position() && hallwayPathRange.all { hallway[it] == null }) {
              val newState = this.clone()
              newState.hallway[hallwayIndex] = pod
              val newPods = room.pods.toMutableList()
              newPods[podIndex] = null
              newState.rooms[room.index] = room.copy(pods = newPods)
              val cost = (abs(hallwayIndex - room.position()) + podIndex + 1) * pod.stepCost
              Pair(newState, cost.toLong())
            } else {
              null
            }
          }
        }
      }
    }
  }

  fun isGoal() = rooms.all { it.isCorrect() }

  fun clone(): State2 {
    return State2(
      hallway.toMutableList(),
      rooms.map { it.copy() }.toMutableList()
    )
  }

  override fun toString(): String {
    val sb = StringBuilder("#############\n")
    sb.append("#")
    sb.append(hallway.hallwayString())
    sb.append("#\n")

    sb.append("###")
    rooms.forEach {
      sb.append(it.pods[0]?.name ?: ".")
      sb.append("#")
    }
    sb.append("##\n")

    rooms[0].pods.indices.drop(1).forEach { index ->
      sb.append("  #")
      rooms.forEach { room ->
        sb.append(room.pods[index]?.name ?: ".")
        sb.append("#")
      }
      sb.append("  \n")
    }

    sb.append("  #########  \n")

    return sb.toString()
  }
}
