package aoc2022.day7

import org.assertj.core.api.Assertions
import readInput
import runSolution

sealed interface File {
  val name: String
  val parent: Directory?
  val size: Long
}

data class Directory(
  override val name: String,
  override val parent: Directory?,
  val files: MutableList<File> = mutableListOf(),
): File {
  override val size: Long by lazy { files.sumOf { it.size } }

  fun foldersOf(predicate: (f: Directory) -> Boolean): List<Directory> {
    return files.flatMap {
      when (it) {
        is SimpleFile -> emptyList()
        is Directory -> {
          if (predicate(it)) {
            listOf(it) + it.foldersOf(predicate)
          } else {
            it.foldersOf(predicate)
          }
        }
      }
    }
  }

  override fun toString(): String {
    fun rec(next: File, depth: Int): String {
      val prefix = "  ".repeat(depth) + "-"
      return when (next) {
        is SimpleFile -> "$prefix ${next.name} (file, size=${next.size})"
        is Directory -> "$prefix ${next.name} (dir, size=${next.size})\n" + next.files.joinToString("\n") { rec(it, depth + 1) }
      }
    }

    return rec(this, 0)
  }
}

data class SimpleFile(
  override val name: String,
  override val parent: Directory?,
  override val size: Long,
): File

fun main() {
  val cdRegex = Regex("""\$ cd (.+)""")
  val dirRegex = Regex("""dir (.+)""")
  val fileRegex = Regex("""(\d+) (.+)""")

  fun parseRoot(inputs: List<String>): Directory {
    val root = Directory("/", null)
    var currentDir = root

    inputs.drop(1).forEach {
      if (it.matches(cdRegex)) {
        val newFolder = cdRegex.matchEntire(it)!!.groupValues[1]
        currentDir = when (newFolder) {
          ".." -> currentDir.parent!!
          "/" -> root
          else -> currentDir.files.find { file -> file is Directory && file.name == newFolder } as Directory
        }
      } else if (it.startsWith("dir")) {
        val newFolder = dirRegex.matchEntire(it)!!.groupValues[1]
        currentDir.files.add(Directory(newFolder, currentDir))
      } else if (it.first().isDigit()) {
        val regexMatch = fileRegex.matchEntire(it)!!.groupValues
        currentDir.files.add(SimpleFile(regexMatch[2], currentDir, regexMatch[1].toLong()))
      }
    }

    return root
  }

  fun part1(inputs: List<String>, targetSize: Long): Long {
    val root = parseRoot(inputs)

    return root.foldersOf { it.size < targetSize }.sumOf { it.size }
  }

  fun part2(inputs: List<String>): Long {
    val root = parseRoot(inputs)
    val targetSize = 30000000 - (70000000 - root.size)
    println(root.size)
    println(targetSize)
    val foldersToDelete = root.foldersOf { it.size >= targetSize }
    foldersToDelete.forEach { println(it.name) }

    return foldersToDelete.minBy { it.size }.size
  }

  val inputDir = "src/aoc2022/day7/inputs"

  val testResult = runSolution("Part1 test") { part1(readInput("test", inputDir), 100000) }
  Assertions.assertThat(testResult).isEqualTo(95437)

  val input = readInput("input", inputDir)
  runSolution("Part1") { part1(input, 100000) }

  val test2Result = runSolution("Part2 test") { part2(readInput("test", inputDir)) }
  Assertions.assertThat(test2Result).isEqualTo(24933642)

  runSolution("Part2") { part2(input) }

}
