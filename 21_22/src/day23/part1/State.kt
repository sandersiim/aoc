package day23.part1

import kotlin.math.abs
import kotlin.math.max
import kotlin.math.min

val roomPositions = mapOf(
  Amphipod.A to 2,
  Amphipod.B to 4,
  Amphipod.C to 6,
  Amphipod.D to 8,
)

val roomIndexes = mapOf(
  Amphipod.A to 0,
  Amphipod.B to 1,
  Amphipod.C to 2,
  Amphipod.D to 3,
)

enum class Amphipod(val stepCost: Int) {
  A(1),
  B(10),
  C(100),
  D(1000);

  companion object {
    fun forRoomIndex(roomIndex: Int): Amphipod {
      return when (roomIndex) {
        0 -> A
        1 -> B
        2 -> C
        3 -> D
        else -> throw Error()
      }
    }
  }

  fun targetRoomPosition(): Int {
    return roomPositions[this]!!
  }

  fun targetRoomIndex(): Int {
    return roomIndexes[this]!!
  }
}

fun MutableList<Amphipod?>.hallwayString(): String {
  return this.joinToString("") {
    it?.name ?: "."
  }
}

data class Room(val outer: Amphipod?, val inner: Amphipod?, val index: Int) {
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

  fun isCorrect(): Boolean {
    return inner == correctAmphipod && outer == correctAmphipod
  }
}

data class State(
  val hallway: MutableList<Amphipod?>,
  val rooms: MutableList<Room>
) {
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
      if (room.outer != null) {
        if (room.outer != correctAmphipod) {
          val horizontalMoves = abs(room.position() - room.outer.targetRoomPosition())
          result += (horizontalMoves + 2) * room.outer.stepCost
          if (room.inner != null && room.inner != correctAmphipod) {
            val otherHorizontalMoves = abs(room.position() - room.inner.targetRoomPosition())
            result += (otherHorizontalMoves + 3) * room.inner.stepCost
          }
        } else {
          if (room.inner == null) {
            result += correctAmphipod.stepCost
          }
          if (room.inner != null && room.inner != correctAmphipod) {
            val otherHorizontalMoves = abs(room.position() - room.inner.targetRoomPosition())
            result += 5 * correctAmphipod.stepCost + (otherHorizontalMoves + 3) * room.inner.stepCost
          }
        }
      } else {
        if (room.inner != null && room.inner != correctAmphipod) {
          val otherHorizontalMoves = abs(room.position() - room.inner.targetRoomPosition())
          result += (otherHorizontalMoves + 3) * room.inner.stepCost
        }
      }
    }

    return result
  }

  fun generateNextSteps(): List<Pair<State, Long>> {
    return hallway.flatMapIndexed { hallwayIndex, amphipod ->
      if (amphipod == null) {
        emptyList()
      } else {
        val steps = mutableListOf<Pair<State, Long>>()
        val correctRoom = rooms[amphipod.targetRoomIndex()]
        val hallwayPathRange = if (correctRoom.position() < hallwayIndex) {
          correctRoom.position() until hallwayIndex
        } else {
          (hallwayIndex + 1).rangeTo(correctRoom.position())
        }

        if (hallwayPathRange.all { hallway[it] == null }) {
          val horizontalSteps = abs(correctRoom.position() - hallwayIndex)
          if (correctRoom.outer == null && correctRoom.inner == null) {
            val cost = (horizontalSteps + 2) * amphipod.stepCost
            val newState = this.clone()
            newState.hallway[hallwayIndex] = null
            newState.rooms[amphipod.targetRoomIndex()] = correctRoom.copy(inner = amphipod)
            steps.add(Pair(newState, cost.toLong()))
          } else if (correctRoom.outer == null) {
            val cost = (horizontalSteps + 1) * amphipod.stepCost
            val newState = this.clone()
            newState.hallway[hallwayIndex] = null
            newState.rooms[amphipod.targetRoomIndex()] = correctRoom.copy(outer = amphipod)
            steps.add(Pair(newState, cost.toLong()))
          }
        }

        steps
      }
    } + rooms.flatMap { room ->
      if (room.outer == null && room.inner == null) {
        emptyList()
      } else if (room.outer != null) {
        if (room.isCorrect()) {
          emptyList()
        } else {
          listOf(0, 1, 3, 5, 7, 9, 10).mapNotNull { hallwayIndex ->
            val hallwayPathRange = min(hallwayIndex, room.position()).rangeTo(max(hallwayIndex, room.position()))
            if (hallwayPathRange.all { hallway[it] == null }) {
              val newState = this.clone()
              newState.hallway[hallwayIndex] = room.outer
              newState.rooms[room.index] = room.copy(outer = null)
              val cost = (abs(hallwayIndex - room.position()) + 1) * room.outer.stepCost
              Pair(newState, cost.toLong())
            } else {
              null
            }
          }
        }
      } else if (room.inner != null) {
        if (room.inner == room.correctAmphipod) {
          emptyList()
        } else {
          listOf(0, 1, 3, 5, 7, 9, 10).mapNotNull { hallwayIndex ->
            val hallwayPathRange = min(hallwayIndex, room.position()).rangeTo(max(hallwayIndex, room.position()))
            if (hallwayPathRange.all { hallway[it] == null }) {
              val newState = this.clone()
              newState.hallway[hallwayIndex] = room.inner
              newState.rooms[room.index] = room.copy(inner = null)
              val cost = (abs(hallwayIndex - room.position()) + 2) * room.inner.stepCost
              Pair(newState, cost.toLong())
            } else {
              null
            }
          }
        }
      } else {
        emptyList()
      }
    }
  }

  fun isGoal() = rooms.all { it.isCorrect() }

  fun clone(): State {
    return State(
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
      sb.append(it.outer?.name ?: ".")
      sb.append("#")
    }
    sb.append("##\n")
    sb.append("  #")
    rooms.forEach {
      sb.append(it.inner?.name ?: ".")
      sb.append("#")
    }
    sb.append("  \n")

    sb.append("  #########  \n")

    return sb.toString()
  }
}
