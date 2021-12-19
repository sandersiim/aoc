import java.io.File
import java.math.BigInteger
import java.security.MessageDigest
import kotlin.time.ExperimentalTime
import kotlin.time.measureTimedValue

/**
 * Reads lines from the given input txt file.
 */
fun readInput(name: String, dir: String = "src/inputs") = File(dir, name).readLines()

/**
 * Converts string to md5 hash.
 */
fun String.md5(): String = BigInteger(1, MessageDigest.getInstance("MD5").digest(toByteArray())).toString(16)

@OptIn(ExperimentalTime::class)
fun <T : Any>runSolution(name: String, runner: () -> T): T {
  println("Running $name\n")
  val timedResult = measureTimedValue {
    runner()
  }

  println("Execution time: ${timedResult.duration}")
  println("Result: ${timedResult.value}")
  println("--------------------\n")

  return timedResult.value
}
