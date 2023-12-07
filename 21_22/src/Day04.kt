import org.assertj.core.api.Assertions.assertThat

class BingoBoard(
  val rows: Array<Array<Int>>,
  val cols: Array<Array<Int>>
) {
  companion object {
    fun empty(): BingoBoard {
      return BingoBoard(
        Array(5) { Array(5) { 0 } },
        Array(5) { Array(5) { 0 } }
      )
    }
  }

  fun addRow(row: Array<Int>, rowIndex: Int) {
    rows[rowIndex] = row
    row.forEachIndexed { colIndex, number ->
      cols[colIndex][rowIndex] = number
    }
  }

  fun hasWon(drawnNumbers: List<Int>): Boolean {
    rows.forEach { row ->
      if (row.all { it in drawnNumbers }) {
        return true
      }
    }
    cols.forEach { row ->
      if (row.all { it in drawnNumbers }) {
        return true
      }
    }

    return false
  }

  fun unmarkedSum(drawnNumbers: List<Int>): Int {
    var result = 0
    rows.forEach { row ->
      result += row.filter { it !in drawnNumbers }.sum()
    }

    return result
  }
}

fun main() {
  data class ParsedInput(
    val drawnNumbers: List<Int>,
    val boards: List<BingoBoard>
  )

  fun parseInput(input: List<String>): ParsedInput {
    val numbers = input[0].split(",").mapNotNull { it.toIntOrNull() }
    val boards = mutableListOf<BingoBoard>()

    var currentBoard = BingoBoard.empty()
    var currentRowIndex = 0
    input.drop(2).forEach { inputLine ->
      if (inputLine.length > 0) {
        currentBoard.addRow(
          inputLine
            .split("\\s+".toRegex())
            .mapNotNull { it.toIntOrNull() }
            .toTypedArray(),
          currentRowIndex
        )
        currentRowIndex += 1
      } else {
        boards.add(currentBoard)
        currentBoard = BingoBoard.empty()
        currentRowIndex = 0
      }
    }
    boards.add(currentBoard)

    return ParsedInput(
      numbers,
      boards
    )
  }

  fun part1(input: List<String>): Int {
    println("Input size: ${input.size}")
    val parsedInput = parseInput(input)
    println("Draws: ${parsedInput.drawnNumbers}")
    println("Number of boards: ${parsedInput.boards.size}")

    (1 until parsedInput.drawnNumbers.size).forEach { numberOfDraws ->
      val currentNumbers = parsedInput.drawnNumbers.take(numberOfDraws)
      val winBoards = parsedInput.boards.filter { it.hasWon(currentNumbers) }
      if (winBoards.isNotEmpty()) {
        println("Winning numbers: $currentNumbers")
        val lastNumber = currentNumbers.last()
        val unmarkedSum = winBoards[0].unmarkedSum(currentNumbers)

        return lastNumber * unmarkedSum
      }
    }

    return 0
  }

  fun part2(input: List<String>): Int {
    val parsedInput = parseInput(input)
    println("Draws: ${parsedInput.drawnNumbers}")
    println("Number of boards: ${parsedInput.boards.size}")

    var lastWinningBoardIndex = -1
    (1 until parsedInput.drawnNumbers.size).forEach { numberOfDraws ->
      val currentNumbers = parsedInput.drawnNumbers.take(numberOfDraws)
      val notWonBoards = parsedInput.boards.filter { !it.hasWon(currentNumbers) }
      if (notWonBoards.size == 1) {
        lastWinningBoardIndex = parsedInput.boards.indexOfFirst { !it.hasWon(currentNumbers) }
      } else if (notWonBoards.isEmpty()) {
        println("Last winning board index: $lastWinningBoardIndex")
        println("Winning numbers: $currentNumbers")

        val lastNumber = currentNumbers.last()
        val lastWinningBoard = parsedInput.boards[lastWinningBoardIndex]
        val unmarkedSum = lastWinningBoard.unmarkedSum(currentNumbers)

        return lastNumber * unmarkedSum
      }
    }

    return 0
  }

  val testInput = readInput("day4_test")
  val testResult1 = runSolution("Part1 test") { part1(testInput) }
  assertThat(testResult1).isEqualTo(4512)

  val input = readInput("day4_input")

  runSolution("Part1") { part1(input) }

  val testResult2 = runSolution("Part2 test") { part2(testInput) }
  assertThat(testResult2).isEqualTo(1924)

  runSolution("Part2") { part2(input) }
}
