package day16

import org.assertj.core.api.Assertions.assertThat
import readInput
import runSolution
import kotlin.experimental.and

interface Packet {
  val version: Byte
  val typeId: Byte

  fun sumOfVersions(): Long
  fun eval(): ULong
}

data class LiteralValuePacket(
  override val version: Byte,
  override val typeId: Byte,
  val value: ULong
) : Packet {
  override fun sumOfVersions() = version.toLong()
  override fun eval() = value
}

enum class LengthType(val id: Byte) {
  TOTAL_LENGTH(0),
  NUM_OF_SUBPACKETS(1)
}

const val SUM_PACKET_TYPE: Byte = 0
const val PRODUCT_PACKET_TYPE: Byte = 1
const val MIN_PACKET_TYPE: Byte = 2
const val MAX_PACKET_TYPE: Byte = 3
const val VALUE_PACKET_TYPE: Byte = 4
const val GT_PACKET_TYPE: Byte = 5
const val LT_PACKET_TYPE: Byte = 6
const val EQ_PACKET_TYPE: Byte = 7

data class OperatorPacket(
  override val version: Byte,
  override val typeId: Byte,
  val lengthType: LengthType,
  val subPackets: List<Packet>
) : Packet {
  override fun sumOfVersions() = version.toLong() + subPackets.sumOf { it.sumOfVersions() }
  override fun eval(): ULong {
    return when (typeId) {
      SUM_PACKET_TYPE -> subPackets.sumOf { it.eval() }
      PRODUCT_PACKET_TYPE -> subPackets.fold(1uL) { acc, p -> acc * p.eval() }
      MIN_PACKET_TYPE -> subPackets.minOf { it.eval() }
      MAX_PACKET_TYPE -> subPackets.maxOf { it.eval() }
      GT_PACKET_TYPE -> if (subPackets[0].eval() > subPackets[1].eval()) 1uL else 0uL
      LT_PACKET_TYPE -> if (subPackets[0].eval() < subPackets[1].eval()) 1uL else 0uL
      EQ_PACKET_TYPE -> if (subPackets[0].eval() == subPackets[1].eval()) 1uL else 0uL
      else -> throw Error("unexpected type $typeId")
    }
  }
}

fun ListIterator<Char>.readBitString(n: Int) = (0 until n).map { next() }.joinToString("")
fun ListIterator<Char>.readByte(n: Int) = readBitString(n).toByte(2)
fun ListIterator<Char>.readInt(n: Int) = readBitString(n).toInt(2)

fun ListIterator<Char>.readLiteralValue(): ULong {
  var value: ULong = 0u
  while (true) {
    value = value shl 4
    val nextGroup = readByte(5)
    val masked = nextGroup and 15
    value = value xor masked.toULong()
    if (masked == nextGroup) {
      break
    }
  }

  return value
}

fun ListIterator<Char>.readSubPackets(lengthType: LengthType): List<Packet> {
  val subPackets = mutableListOf<Packet>()

  if (lengthType == LengthType.TOTAL_LENGTH) {
    var remaining = readInt(15)
    var currentPos = nextIndex()
    do {
      subPackets.add(parsePacket(this))
      val newPos = nextIndex()
      remaining -= (newPos - currentPos)
      currentPos = newPos
    } while (remaining > 0)
  } else {
    val numOfSubpackets = readInt(11)
    (0 until numOfSubpackets).map { subPackets.add(parsePacket(this)) }
  }

  return subPackets
}

fun parsePacket(inputIter: ListIterator<Char>): Packet {
  val ver = inputIter.readByte(3)
  val type = inputIter.readByte(3)

  return if (type == VALUE_PACKET_TYPE) {
    LiteralValuePacket(ver, type, inputIter.readLiteralValue())
  } else {
    val lengthTypeId = inputIter.readByte(1)
    val lengthType = if (lengthTypeId == LengthType.TOTAL_LENGTH.id) LengthType.TOTAL_LENGTH else LengthType.NUM_OF_SUBPACKETS
    OperatorPacket(ver, type, lengthType, inputIter.readSubPackets(lengthType))
  }
}

fun main() {
  fun String.hexToBinary(): String {
    return this.map { c ->
      val binaryInt = c.digitToInt(16).toString(2)
      "0".repeat(4 - binaryInt.length) + binaryInt
    }.joinToString("")
  }

  fun part1(input: String): Long {
    println(input)
    val binary = input.hexToBinary()
    println(binary)

    val packet = parsePacket(binary.toCharArray().toList().listIterator())

    println(packet.toString())

    return packet.sumOfVersions()
  }

  fun part2(input: String): ULong {
    val binary = input.hexToBinary()
    val packet = parsePacket(binary.toCharArray().toList().listIterator())

    return packet.eval()
  }

  val inputDir = "src/day16/inputs"

  val test0Result = runSolution("Part1 test0") { part1("D2FE28") }
  assertThat(test0Result).isEqualTo(6)

  val test1Result = runSolution("Part1 test1") { part1("38006F45291200") }
  assertThat(test1Result).isEqualTo(9)

  val test2Result = runSolution("Part1 test2") { part1("EE00D40C823060") }
  assertThat(test2Result).isEqualTo(14)

  val test3Result = runSolution("Part1 test3") { part1("8A004A801A8002F478") }
  assertThat(test3Result).isEqualTo(16)

  val test4Result = runSolution("Part1 test4") { part1("620080001611562C8802118E34") }
  assertThat(test4Result).isEqualTo(12)

  val input = readInput("input", inputDir)[0]
  runSolution("Part1") { part1(input) }

  val part2TestResult0 = runSolution("Part2 test0") { part2("C200B40A82") }
  assertThat(part2TestResult0).isEqualTo(3uL)

  runSolution("Part2") { part2(input) }

}
