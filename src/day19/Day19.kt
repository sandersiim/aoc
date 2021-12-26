package day19

import org.assertj.core.api.Assertions.assertThat
import readInput
import runSolution
import kotlin.math.abs

data class Coordinates(
  val x: Int,
  val y: Int,
  val z: Int,
) {
  fun manhattanDist(other: Coordinates): Int {
    return abs(x - other.x) + abs(y - other.y) + abs(z - other.z)
  }

  fun add(translation: Coordinates) = Coordinates(x + translation.x, y + translation.y, z + translation.z)

  fun rotate(rotation: RotationMatrix): Coordinates {
    val newX = rotation.xVector[0]*x + rotation.xVector[1]*y + rotation.xVector[2]*z
    val newY = rotation.yVector[0]*x + rotation.yVector[1]*y + rotation.yVector[2]*z
    val newZ = rotation.zVector[0]*x + rotation.zVector[1]*y + rotation.zVector[2]*z
    return Coordinates(newX, newY, newZ)
  }
}

data class RotationMatrix(
  val xVector: List<Int>,
  val yVector: List<Int>,
  val zVector: List<Int>
) {
  companion object {
    val allRotations = listOf(
      RotationMatrix(listOf(1,0,0),listOf(0,1,0), listOf(0,0,1)),
      RotationMatrix(listOf(1,0,0),listOf(0,0,-1), listOf(0,1,0)),
      RotationMatrix(listOf(1,0,0),listOf(0,-1,0), listOf(0,0,-1)),
      RotationMatrix(listOf(1,0,0),listOf(0,0,1), listOf(0,-1,0)),
      RotationMatrix(listOf(0,-1,0),listOf(1,0,0), listOf(0,0,1)),
      RotationMatrix(listOf(0,0,1),listOf(1,0,0), listOf(0,1,0)),
      RotationMatrix(listOf(0,1,0),listOf(1,0,0), listOf(0,0,-1)),
      RotationMatrix(listOf(0,0,-1),listOf(1,0,0), listOf(0,-1,0)),
      RotationMatrix(listOf(-1,0,0),listOf(0,-1,0), listOf(0,0,1)),
      RotationMatrix(listOf(-1,0,0),listOf(0,0,-1), listOf(0,-1,0)),
      RotationMatrix(listOf(-1,0,0),listOf(0,1,0), listOf(0,0,-1)),
      RotationMatrix(listOf(-1,0,0),listOf(0,0,1), listOf(0,1,0)),
      RotationMatrix(listOf(0,1,0),listOf(-1,0,0), listOf(0,0,1)),
      RotationMatrix(listOf(0,0,1),listOf(-1,0,0), listOf(0,-1,0)),
      RotationMatrix(listOf(0,-1,0),listOf(-1,0,0), listOf(0,0,-1)),
      RotationMatrix(listOf(0,0,-1),listOf(-1,0,0), listOf(0,1,0)),
      RotationMatrix(listOf(0,0,-1),listOf(0,1,0), listOf(1,0,0)),
      RotationMatrix(listOf(0,1,0),listOf(0,0,1), listOf(1,0,0)),
      RotationMatrix(listOf(0,0,1),listOf(0,-1,0), listOf(1,0,0)),
      RotationMatrix(listOf(0,-1,0),listOf(0,0,-1), listOf(1,0,0)),
      RotationMatrix(listOf(0,0,-1),listOf(0,-1,0), listOf(-1,0,0)),
      RotationMatrix(listOf(0,-1,0),listOf(0,0,1), listOf(-1,0,0)),
      RotationMatrix(listOf(0,0,1),listOf(0,1,0), listOf(-1,0,0)),
      RotationMatrix(listOf(0,1,0),listOf(0,0,-1), listOf(-1,0,0))
    )
  }
}

data class Scanner(
  val id: Int,
  var probeCoordinates: List<Coordinates>
) {
  var scannerLocation: Coordinates? = null

  fun distancesFrom(probeIndex: Int): Map<Pair<Int, Int>, Int> {
    return ((0 until probeIndex) + (probeIndex+1 until probeCoordinates.size)).map {
      Pair(probeIndex, it) to probeCoordinates[probeIndex].manhattanDist(probeCoordinates[it])
    }.toMap()
  }

  fun applyRotation(rotation: RotationMatrix) {
    probeCoordinates = probeCoordinates.map { it.rotate(rotation) }
  }

  fun applyTranslation(translation: Coordinates) {
    this.scannerLocation = Coordinates(-translation.x, -translation.y, -translation.z)
    probeCoordinates = probeCoordinates.map { it.add(translation) }
  }
}

fun parseInput(input: List<String>): List<Scanner> {
  val iter = input.iterator()
  val scanners = mutableListOf<Scanner>()
  var probeCoordinates = mutableListOf<Coordinates>()
  var scannerId = 0
  while (iter.hasNext()) {
    val line = iter.next()
    if (line.isEmpty()) {
      scanners.add(Scanner(scannerId, probeCoordinates))
      probeCoordinates = mutableListOf()
    } else if (line.startsWith("---")) {
      scannerId = line.replace("--- scanner ", "").replace(" ---", "").toInt()
    } else {
      val parsedCoords = line.split(",").map(String::toInt)
      probeCoordinates.add(Coordinates(parsedCoords[0], parsedCoords[1], parsedCoords[2]))
    }
  }
  scanners.add(Scanner(scannerId, probeCoordinates))

  return scanners
}

fun Collection<Int>.intersectionSize(other: Collection<Int>): Int {
  val candidates = other.toMutableList()
  var intersectionSize = 0
  this.forEach {
    if (it in candidates) {
      intersectionSize++
      candidates.remove(it)
    }
  }
  return intersectionSize
}

fun main() {
  fun coordinateSystemsMatch(ref1: Coordinates, ref2: Coordinates, c1: Coordinates, c2: Coordinates) =
    ref1.x - ref2.x == c1.x - c2.x && ref1.y - ref2.y == c1.y - c2.y && ref1.z - ref2.z == c1.z - c2.z

  fun matchAndTransformScanners(scanners: List<Scanner>) {
    scanners.forEach { println("${it.id}: ${it.probeCoordinates.size}") }
    val correctlyTranslatedScanners = mutableSetOf(0)
    val scannersToMatch = scanners.indices.drop(1).toMutableSet()
    scanners[0].scannerLocation = Coordinates(0, 0, 0)

    while (scannersToMatch.isNotEmpty()) {
      println("correctlyTranslatedScanners: $correctlyTranslatedScanners")
      println("scannersToMatch: $scannersToMatch")

      val matchedScannerIdx = scannersToMatch.find { currentScannerIdx ->
        println("Trying to match scanner $currentScannerIdx")
        val currentScanner = scanners[currentScannerIdx]
        val matchedReferenceScanner = correctlyTranslatedScanners.find { referenceScannerIdx ->
          val referenceScanner = scanners[referenceScannerIdx]
          val matchingProbeIndexes = mutableListOf<Pair<Int, Int>>()

          referenceScanner.probeCoordinates.indices.forEach { probeIndex ->
            val distances = referenceScanner.distancesFrom(probeIndex)
            currentScanner.probeCoordinates.indices.forEach { probeIndex2 ->
              val distances2 = currentScanner.distancesFrom(probeIndex2)
              if (distances.values.intersectionSize(distances2.values) >= 11) {
                matchingProbeIndexes.add(Pair(probeIndex, probeIndex2))
              }
            }
          }

          if (matchingProbeIndexes.size >= 12) {
            println("Scanners $currentScannerIdx and $referenceScannerIdx match")
            val referenceProbe1 = referenceScanner.probeCoordinates[matchingProbeIndexes[0].first]
            val referenceProbe2 = referenceScanner.probeCoordinates[matchingProbeIndexes[1].first]
            val matchedProbe1 = currentScanner.probeCoordinates[matchingProbeIndexes[0].second]
            val matchedProbe2 = currentScanner.probeCoordinates[matchingProbeIndexes[1].second]

            val correctRotation = RotationMatrix.allRotations.find {
              coordinateSystemsMatch(referenceProbe1, referenceProbe2, matchedProbe1.rotate(it), matchedProbe2.rotate(it))
            } ?: throw Error("Could not find rotation to bring to same coordinate system")
            println("Applying rotation $correctRotation")
            currentScanner.applyRotation(correctRotation)
            val matchedProbe1Rotated = currentScanner.probeCoordinates[matchingProbeIndexes[0].second]
            val translation = Coordinates(
              referenceProbe1.x - matchedProbe1Rotated.x,
              referenceProbe1.y - matchedProbe1Rotated.y,
              referenceProbe1.z - matchedProbe1Rotated.z,
            )
            currentScanner.applyTranslation(translation)
            assertThat(matchingProbeIndexes.all {
              referenceScanner.probeCoordinates[it.first] == currentScanner.probeCoordinates[it.second]
            }).isTrue
            println("Scanner $currentScannerIdx location is ${currentScanner.scannerLocation}")

            true
          } else {
            false
          }
        }

        matchedReferenceScanner != null
      }

      if (matchedScannerIdx != null) {
        correctlyTranslatedScanners.add(matchedScannerIdx)
        scannersToMatch.remove(matchedScannerIdx)
      } else {
        throw Error("Could not match any scanners to known ones")
      }
    }
  }

  fun part1(input: List<String>): Int {
    val scanners = parseInput(input)
    matchAndTransformScanners(scanners)

    val uniqueProbes = scanners.flatMap(Scanner::probeCoordinates).distinct()

    return uniqueProbes.size
  }

  fun part2(input: List<String>): Int {
    val scanners = parseInput(input)
    matchAndTransformScanners(scanners)

    var maxManhattanDist = 0
    scanners.indices.forEach { i ->
      ((0 until i) + (i+1 until scanners.size)).forEach { j ->
        val dist = scanners[i].scannerLocation!!.manhattanDist(scanners[j].scannerLocation!!)
        if (dist > maxManhattanDist) {
          maxManhattanDist = dist
        }
      }
    }

    return maxManhattanDist
  }

  val inputDir = "src/day19/inputs"

  val testResult = runSolution("Part1 test") { part1(readInput("testInput", inputDir)) }
  assertThat(testResult).isEqualTo(79)

  runSolution("Part1") { part1(readInput("input", inputDir)) }

  val testResult2 = runSolution("Part2 test") { part2(readInput("testInput", inputDir)) }
  assertThat(testResult2).isEqualTo(3621)

  runSolution("Part2") { part2(readInput("input", inputDir)) }
}
