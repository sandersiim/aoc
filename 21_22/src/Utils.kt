import java.io.File
import java.math.BigInteger
import java.security.MessageDigest
import kotlin.time.ExperimentalTime
import kotlin.time.measureTimedValue

/**
 * Reads lines from the given input txt file.
 */
fun readInput(name: String, dir: String = "src/inputs"): List<String> = File(dir, name).readLines()

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

fun IntRange.rangeIntersect(other: IntRange) = IntRange(
  kotlin.math.max(this.first, other.first),
  kotlin.math.min(this.last, other.last)
)

fun IntRange.size() = last - first + 1

enum class Direction {
  RIGHT,
  DOWN,
  LEFT,
  UP,;

  fun isSameRowDirection() = this == RIGHT || this == LEFT
  fun isSameColDirection() = this == UP || this == DOWN

  fun charString(): String {
    return when (this) {
      Direction.RIGHT -> ">"
      Direction.LEFT -> "<"
      Direction.UP -> "^"
      Direction.DOWN -> "v"
    }
  }
}

fun lcm(x: Int, y: Int): Int {
  if (x == 0) {
    return y
  } else if (y == 0) {
    return x
  }

  var a = x.coerceAtLeast(y)
  var b = x.coerceAtMost(y)
  while (b > 0) {
    val tmp = a % b
    a = b
    b = tmp
  }

  val gcd = a
  return x * y / gcd
}

class LazyMap<K, V>(
  private val compute: (K) -> V
) {
  private val map = mutableMapOf<K, V>()

  fun getValue(key: K): V = map.getOrPut(key) { compute(key) }
}
