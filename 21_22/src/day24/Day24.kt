package day24

import readInput
import runSolution

fun main() {
  fun parseInput(input: List<String>): List<Command> {
    fun variableOrInt(str: String): Any {
      return if (str in Variable.values().map { it.name }) {
        Variable.valueOf(str)
      } else {
        str.toInt()
      }
    }

    return input.map { line ->
      val tokens = line.split(" ")
      when (tokens[0]) {
        InpCommand.name -> InpCommand(Variable.valueOf(tokens[1]))
        AddCommand.name -> AddCommand(Variable.valueOf(tokens[1]), variableOrInt(tokens[2]))
        MulCommand.name -> MulCommand(Variable.valueOf(tokens[1]), variableOrInt(tokens[2]))
        DivCommand.name -> DivCommand(Variable.valueOf(tokens[1]), variableOrInt(tokens[2]))
        ModCommand.name -> ModCommand(Variable.valueOf(tokens[1]), variableOrInt(tokens[2]))
        EqlCommand.name -> EqlCommand(Variable.valueOf(tokens[1]), variableOrInt(tokens[2]))
        else -> throw Error()
      }
    }
  }

  val alu = ALU()

  fun runModelValidator(code: List<Command>, input: Iterator<Long>): Long {
    alu.runCode(code, input)
    return alu.getVar(Variable.z)
  }

  fun runModelValidatorWithPrints(code: List<Command>, testNumber: List<Long>): Long {
    val r = runModelValidator(
      code,
      sequence {
        testNumber.forEach {
          alu.printMemory()
          yield(it)
        }
      }.iterator()
    )
    alu.printMemory()

    return r
  }

  fun analyticalFormula(modelNumber: List<Long>): Long {
    var result = 0L
    result += modelNumber[0] + 12
    result *= 26
    result += modelNumber[1] + 7
    result *= 26
    result += modelNumber[2] + 1
    result *= 26
    result += modelNumber[3] + 2

    if (result.mod(26L) - 5 == modelNumber[4]) {
      result /= 26
    } else {
      result /= 26
      result *= 26
      result += modelNumber[4] + 4
    }

    result *= 26
    result += modelNumber[5] + 15
    result *= 26
    result += modelNumber[6] + 11

    if (result.mod(26L) - 13 == modelNumber[7]) {
      result /= 26
    } else {
      result /= 26
      result *= 26
      result += modelNumber[7] + 5
    }

    if (result.mod(26L) - 16 == modelNumber[8]) {
      result /= 26
    } else {
      result /= 26
      result *= 26
      result += modelNumber[8] + 3
    }

    if (result.mod(26L) - 8 == modelNumber[9]) {
      result /= 26
    } else {
      result /= 26
      result *= 26
      result += modelNumber[9] + 9
    }

    result *= 26
    result += modelNumber[10] + 2

    if (result.mod(26L) - 8 == modelNumber[11]) {
      result /= 26
    } else {
      result /= 26
      result *= 26
      result += modelNumber[11] + 3
    }

    if (result.mod(26L) == modelNumber[12]) {
      result /= 26
    } else {
      result /= 26
      result *= 26
      result += modelNumber[12] + 3
    }

    if (result.mod(26L) - 4 == modelNumber[13]) {
      result /= 26
    } else {
      result /= 26
      result *= 26
      result += modelNumber[13] + 11
    }

    return result
  }

  // constraints based on analytical formula
  // x4 = x3 - 3
  // x7 = x6 - 2
  // x8 = x5 - 1
  // x9 = x2 - 7
  // x11 = x10 - 6
  // x12 = x1 + 7
  // x13 = x0 + 8

  fun part1(input: List<String>): Long {
    val code = parseInput(input)

    val maxModelNumber = listOf(1, 2, 9, 9, 6, 9, 9, 7, 8, 2, 9, 3, 9, 9).map(Int::toLong)
    runModelValidatorWithPrints(code, maxModelNumber)

    return maxModelNumber.joinToString("").toLong()
  }

  fun part2(input: List<String>): Long {
    val code = parseInput(input)

    val maxModelNumber = listOf(1, 1, 8, 4, 1, 2, 3, 1, 1, 1, 7, 1, 8, 9).map(Int::toLong)
    runModelValidatorWithPrints(code, maxModelNumber)

    return maxModelNumber.joinToString("").toLong()
  }

  val inputDir = "src/day24/inputs"
  val testCode1 = parseInput(listOf("inp x", "mul x -1"))
  alu.runCode(testCode1, listOf(-23L).iterator())
  println(alu.getVar(Variable.x))
  alu.runCode(testCode1, listOf(1L).iterator())
  println(alu.getVar(Variable.x))

  val testCode2 = parseInput(listOf(
    "inp z",
    "inp x",
    "mul z 3",
    "eql z x"
  ))

  alu.runCode(testCode2, listOf(2L, 6L).iterator())
  println(alu.getVar(Variable.z))
  alu.runCode(testCode2, listOf(1L, 2L).iterator())
  println(alu.getVar(Variable.z))

  val testCode3 = parseInput(
    """
      inp w
      add z w
      mod z 2
      div w 2
      add y w
      mod y 2
      div w 2
      add x w
      mod x 2
      div w 2
      mod w 2
    """.trimIndent().split("\n")
  )

  alu.runCode(testCode3, listOf(9L).iterator())
  print(alu.getVar(Variable.z))
  print(alu.getVar(Variable.y))
  print(alu.getVar(Variable.x))
  println(alu.getVar(Variable.w))

  alu.runCode(testCode3, listOf(15L).iterator())
  print(alu.getVar(Variable.z))
  print(alu.getVar(Variable.y))
  print(alu.getVar(Variable.x))
  println(alu.getVar(Variable.w))

  runSolution("Part1") { part1(readInput("input", inputDir)) }
  runSolution("Part2") { part2(readInput("input", inputDir)) }
}
