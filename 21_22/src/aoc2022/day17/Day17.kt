package aoc2022.day17

import Coords
import org.assertj.core.api.Assertions
import readInput
import runSolution

enum class ShapeType(val width: Int) {
  LINE(4),
  CROSS(3),
  L(3),
  POST(1),
  BOX(2),
}

enum class JetDirection {
  LEFT,
  RIGHT,
}

fun main() {
  val inputDir = "src/aoc2022/day17/inputs"

  fun emptyRow() = charArrayOf('.', '.', '.', '.', '.', '.', '.')

  data class Shape(
    val type: ShapeType,
    var pos: Coords = Coords(0, 0),
  ) {
    fun shapeCoords(): List<Coords> {
      return when (type) {
        ShapeType.LINE -> (0..3).map { Coords(pos.row, pos.col + it) }
        ShapeType.CROSS -> listOf(
          Coords(pos.row, pos.col + 1),
          Coords(pos.row + 1, pos.col),
          Coords(pos.row + 1, pos.col + 1),
          Coords(pos.row + 1, pos.col + 2),
          Coords(pos.row + 2, pos.col + 1),
        )
        ShapeType.L -> listOf(
          Coords(pos.row, pos.col),
          Coords(pos.row, pos.col + 1),
          Coords(pos.row, pos.col + 2),
          Coords(pos.row + 1, pos.col + 2),
          Coords(pos.row + 2, pos.col + 2),
        )
        ShapeType.POST -> (0..3).map { Coords(pos.row + it, pos.col) }
        ShapeType.BOX -> listOf(
          Coords(pos.row, pos.col),
          Coords(pos.row, pos.col + 1),
          Coords(pos.row + 1, pos.col),
          Coords(pos.row + 1, pos.col + 1),
        )
      }
    }

    fun push(direction: JetDirection): Shape {
      return when (direction) {
        JetDirection.RIGHT -> {
          if (type.width + pos.col < 7) {
            copy(pos = Coords(pos.row, pos.col + 1))
          } else {
            this
          }
        }
        JetDirection.LEFT -> {
          if (pos.col > 0) {
            copy(pos = Coords(pos.row, pos.col - 1))
          } else {
            this
          }
        }
      }
    }

    fun moveDown(): Shape {
      return copy(
        pos = Coords(pos.row - 1, pos.col)
      )
    }
  }

  data class State(
    var rows: MutableList<CharArray> = mutableListOf(),
    var highestPeak: Int = 0,
    var peakOffSet: Long = 0,
  ) {
    fun initShape(type: ShapeType): Shape {
      repeat(highestPeak - rows.size + 7) {
        rows.add(emptyRow())
      }
      return Shape(
        type = type,
        pos = Coords(highestPeak + 3, 2),
      )
    }

    fun addShape(shape: Shape) {
      shape.shapeCoords().forEach {
        rows[it.row][it.col] = '#'
      }

      (shape.pos.row+3 downTo shape.pos.row).firstOrNull { rowIndex ->
        if (rows[rowIndex].all { it == '#' }) {
          peakOffSet += rowIndex
          rows = rows.drop(rowIndex).toMutableList()
          true
        } else {
          false
        }
      }
      highestPeak = rows.indexOfLast { row -> row.any { it == '#' } } + 1
    }

    fun shapeCollides(shape: Shape): Boolean {
      return shape.shapeCoords().any {
        rows[it.row][it.col] == '#'
      }
    }

    fun pushShape(shape: Shape, direction: JetDirection): Shape {
      val pushedShape = shape.push(direction)
      if (shapeCollides(pushedShape)) {
        return shape
      }

      return pushedShape
    }

    fun fallShape(shape: Shape): Shape? {
      if (shape.pos.row == 0) {
        addShape(shape)
        return null
      }
      val movedDownShape = shape.moveDown()
      if (shapeCollides(movedDownShape)) {
        addShape(shape)
        return null
      }

      return movedDownShape
    }

    fun processNextTick(shape: Shape, direction: JetDirection): Shape? {
      return fallShape(pushShape(shape, direction))
    }

    fun printState() {
      rows.asReversed().forEach(::println)
      println("--------------------------")
    }
  }

  fun solve(jets: String, iterations: Long): Long {
    val state = State()

    var jetsIter = jets.chars().iterator()
    val jetsSequence = generateSequence {
      if (!jetsIter.hasNext()) {
        jetsIter = jets.chars().iterator()
      }

      if (jetsIter.next().toChar() == '<') {
        JetDirection.LEFT
      } else {
        JetDirection.RIGHT
      }
    }.iterator()

    var currentShapeType = ShapeType.BOX

    val shapeSequence = generateSequence {
      val next = when (currentShapeType) {
        ShapeType.LINE -> ShapeType.CROSS
        ShapeType.CROSS -> ShapeType.L
        ShapeType.L -> ShapeType.POST
        ShapeType.POST -> ShapeType.BOX
        ShapeType.BOX -> ShapeType.LINE
      }
      currentShapeType = next
      next
    }

    var i = 0
    var nextShapeType: ShapeType
    val shapesIter = shapeSequence.iterator()
    while (i < iterations) {
      if (i % 10_000_000 == 0) {
        println(i)
      }
      nextShapeType = shapesIter.next()
      var shape: Shape? = state.initShape(nextShapeType)
      while (shape != null) {
        shape = state.processNextTick(shape, jetsSequence.next())
      }
      i++
    }

    println(state.rows.any { it.all { c -> c == '#' } })

    return state.highestPeak + state.peakOffSet
  }

  val testResult = runSolution("Part1 test") { solve(readInput("test", inputDir)[0], 2022) }
  Assertions.assertThat(testResult).isEqualTo(3068)

  val input = readInput("input", inputDir)[0]
  runSolution("Part1") { solve(input, 2022) }

  val test2Result = runSolution("Part2 test") { solve(readInput("test", inputDir)[0], 1000000000000) }
  Assertions.assertThat(test2Result).isEqualTo(1514285714288)

//  runSolution("Part2") { part2(input) }
}
