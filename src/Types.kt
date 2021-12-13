data class Coords(val row: Int, val col: Int) {
  companion object {
    fun fromInput(line: String) = line.split(",").let {
      Coords(it[1].toInt(), it[0].toInt())
    }
  }

  val y get() = row
  val x get() = col
}
