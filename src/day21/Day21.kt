package day21

import org.assertj.core.api.Assertions.assertThat
import readInput
import runSolution
import kotlin.math.max
import kotlin.math.min

interface Dice {
  fun roll(): Int
}

fun main() {
  fun movePlayer(currentPos: Int, diceRoll: Int) = 1 + (currentPos + diceRoll - 1).mod(10)

  fun part1(input: List<String>): Long {
    class DeterministicDice(private val n: Int): Dice {
      private var current = 1

      override fun roll(): Int {
        val result = current
        current = 1 + current.mod(n)

        return result
      }
    }

    fun playDirac(player1StartPos: Int, player2StartPos: Int, dice: Dice, goal: Int): Triple<Long, Long, Long> {
      var diceRolls = 0L
      var player1Points = 0L
      var player2Points = 0L
      var player1Pos = player1StartPos
      var player2Pos = player2StartPos

      while (player1Points < goal && player2Points < goal) {
        diceRolls += 3
        player1Pos = movePlayer(player1Pos, (1..3).sumOf { dice.roll() })
        player1Points += player1Pos
        if (player1Points >= goal) {
          break
        }

        diceRolls += 3
        player2Pos = movePlayer(player2Pos, (1..3).sumOf { dice.roll() })
        player2Points += player2Pos
      }

      return Triple(diceRolls, player1Points, player2Points)
    }

    val p1Start = input[0].last().digitToInt()
    val p2Start = input[1].last().digitToInt()
    val (diceRolls, player1Points, player2Points) = playDirac(
      p1Start,
      p2Start,
      DeterministicDice(100),
      1000
    )

    return diceRolls * min(player1Points, player2Points)
  }

  fun part2(input: List<String>): Long {
    val p1StartPos = input[0].last().digitToInt()
    val p2StartPos = input[1].last().digitToInt()
    val diceRange = (1..3)
    val positions = (1..10)
    val outcomes = diceRange.flatMap { first ->
      diceRange.flatMap { second ->
        diceRange.map { third ->
          first + second + third
        }
      }
    }

    data class Player(
      val pos: Int,
      val points: Int
    )

    data class GameState(
      val p1: Player,
      val p2: Player,
      val p1Move: Boolean
    )

    val dpResults = mutableMapOf<GameState, Pair<Long, Long>>()

    fun solve(state: GameState): Pair<Long, Long> {
      if (dpResults.containsKey(state)) {
        return dpResults[state]!!
      }
      val currentPlayer = if (state.p1Move) state.p1 else state.p2
      var p1Wins = 0L
      var p2Wins = 0L
      outcomes.forEach {
        val newPos = movePlayer(currentPlayer.pos, it)
        val newPoints = currentPlayer.points + newPos
        if (newPoints >= 21) {
          if (state.p1Move) {
            p1Wins += 1
          } else {
            p2Wins += 1
          }
        } else {
          val newState = if (state.p1Move) {
            state.copy(p1 = Player(newPos, newPoints), p1Move = false)
          } else {
            state.copy(p2 = Player(newPos, newPoints), p1Move = true)
          }
          val allSubResults = solve(newState)
          p1Wins += allSubResults.first
          p2Wins += allSubResults.second
        }
      }

      val allResults = Pair(p1Wins, p2Wins)
      dpResults[state] = allResults

      return allResults
    }

    (20 downTo 0).forEach { p1Points ->
      (20 downTo 0).forEach { p2Points ->
        positions.forEach { p1Pos ->
          positions.forEach { p2Pos ->
            solve(GameState(Player(p1Pos, p1Points), Player(p2Pos, p2Points), true))
            solve(GameState(Player(p1Pos, p1Points), Player(p2Pos, p2Points), false))
          }
        }
      }
    }

    val allGameResults = dpResults[GameState(Player(p1StartPos, 0), Player(p2StartPos, 0), true)]!!
    println(allGameResults)
    return max(allGameResults.first, allGameResults.second)
  }

  val inputDir = "src/day21/inputs"

  val testResult = runSolution("Part1 test") { part1(readInput("testInput", inputDir)) }
  assertThat(testResult).isEqualTo(739785)

  runSolution("Part1") { part1(readInput("input", inputDir)) }

  val testResult2 = runSolution("Part2 test") { part2(readInput("testInput", inputDir)) }
  assertThat(testResult2).isEqualTo(444356092776315)

  runSolution("Part2") { part2(readInput("input", inputDir)) }
}
