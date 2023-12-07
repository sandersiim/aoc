data class Cuboid(
  val xRange: IntRange,
  val yRange: IntRange,
  val zRange: IntRange
) {
  val size = xRange.size().toLong() * yRange.size().toLong() * zRange.size().toLong()

  private fun intersectionWith(other: Cuboid): Cuboid? {
    val xIntersection = xRange.rangeIntersect(other.xRange)
    if (xIntersection.isEmpty()) {
      return null
    }
    val yIntersection = yRange.rangeIntersect(other.yRange)
    if (yIntersection.isEmpty()) {
      return null
    }
    val zIntersection = zRange.rangeIntersect(other.zRange)
    if (zIntersection.isEmpty()) {
      return null
    }

    return Cuboid(xIntersection, yIntersection, zIntersection)
  }

  fun subtract(other: Cuboid): List<Cuboid> {
    val intersection = intersectionWith(other)
    return if (intersection == null) {
      listOf(this)
    } else {
      val result = mutableListOf<Cuboid>()
      val xIntersection = xRange.rangeIntersect(other.xRange)
      if (xRange.first < other.xRange.first) {
        result.add(Cuboid((xRange.first until other.xRange.first), yRange, zRange))
      }
      if (xRange.last > other.xRange.last) {
        result.add(Cuboid((other.xRange.last + 1..xRange.last), yRange, zRange))
      }
      val xIntersectedCuboid = Cuboid(xIntersection, yRange, zRange)

      val yIntersection = yRange.rangeIntersect(other.yRange)
      if (yRange.first < other.yRange.first) {
        result.add(Cuboid(xIntersectedCuboid.xRange, (yRange.first until other.yRange.first), zRange))
      }
      if (yRange.last > other.yRange.last) {
        result.add(Cuboid(xIntersectedCuboid.xRange, (other.yRange.last + 1..yRange.last), zRange))
      }
      val xyIntersectedCuboid = Cuboid(xIntersectedCuboid.xRange, yIntersection, zRange)

      if (zRange.first < other.zRange.first) {
        result.add(Cuboid(xyIntersectedCuboid.xRange, xyIntersectedCuboid.yRange, (zRange.first until other.zRange.first)))
      }
      if (zRange.last > other.zRange.last) {
        result.add(Cuboid(xyIntersectedCuboid.xRange, xyIntersectedCuboid.yRange, (other.zRange.last + 1..zRange.last)))
      }

      result
    }
  }

//  fun unionWith(other: Cuboid): List<Cuboid> {
//    val intersection = intersectionWith(other)
//    if (intersection == null) {
//      return listOf(this, other)
//    }
//  }
//
//  fun touches(other: Cuboid): Boolean {
//    val intersection = intersectionWith(other)
//    if (intersection == null) {
//      return
//    } else {
//      return true
//    }
//  }
}

data class CuboidUnion(
  var cuboids: MutableList<Cuboid> = mutableListOf()
) {
  fun size() = cuboids.sumOf { it.size }

  fun add(cuboid: Cuboid) {
    var newCuboids = listOf(cuboid)
    cuboids.forEach { existingCuboid ->
      newCuboids = newCuboids.flatMap { it.subtract(existingCuboid) }
    }
    cuboids.addAll(newCuboids)
  }

  fun remove(cuboid: Cuboid) {
    cuboids = cuboids.flatMap { it.subtract(cuboid) }.toMutableList()
  }

  fun surfaceArea(): Int {
    TODO()
  }
}
