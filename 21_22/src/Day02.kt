import org.assertj.core.api.Assertions.assertThat

enum class MoveDirection {
  FORWARD,
  UP,
  DOWN
}

data class ParsedCommand(
  val direction: MoveDirection,
  val amount: Int
)

fun parseCommand(command: String): ParsedCommand {
  val parts = command.split(" ")

  return ParsedCommand(
    MoveDirection.valueOf(parts[0].uppercase()),
    parts[1].toInt()
  )
}

fun main() {
  fun part1(input: List<String>): Int {
    println("Input size: ${input.size}")
    val commands = input.map { parseCommand(it) }

    val hPosition = commands
      .filter { it.direction == MoveDirection.FORWARD }
      .sumOf { it.amount }

    val depth = commands
      .filter { it.direction != MoveDirection.FORWARD }
      .sumOf {
        when(it.direction) {
          MoveDirection.UP -> -it.amount
          MoveDirection.DOWN -> it.amount
          else -> 0
        }
      }

    println("Horizontal position: $hPosition, depth: $depth")

    return hPosition * depth
  }

  fun part2(input: List<String>): Int {
    val commands = input.map { parseCommand(it) }
    var hPosition = 0
    var depth = 0
    var aim = 0

    commands.forEach {
      when(it.direction) {
        MoveDirection.DOWN -> aim += it.amount
        MoveDirection.UP -> aim -= it.amount
        MoveDirection.FORWARD -> {
          hPosition += it.amount
          depth += aim * it.amount
        }
      }
    }

    println("Horizontal position: $hPosition, depth: $depth")

    return hPosition * depth
  }

  val testInput = readInput("day2_test")
  val testResult1 = runSolution("Part1 test") { part1(testInput) }
  assertThat(testResult1).isEqualTo(150)

  val testResult2 = runSolution("Part2 test") { part2(testInput) }
  assertThat(testResult2).isEqualTo(900)

  val input = readInput("day2_input")

  runSolution("Part1") { part1(input) }

  runSolution("Part2") { part2(input) }
}
