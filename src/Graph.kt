class Graph<T> {
  private val edges = mutableMapOf<T, MutableList<T>>()

  fun addEdge(source: T, target: T) {
    if (edges.containsKey(source)) {
      edges[source]!!.add(target)
    } else {
      edges[source] = mutableListOf(target)
    }
  }

  private fun getNeighbors(source: T): List<T> {
    if (!edges.containsKey(source)) {
      throw Error("Unkown vertex $source")
    }
    return edges[source]!!
  }

  fun allPaths(
    source: T,
    target: T,
    shouldSkip: (currentPath: List<T>, next: T) -> Boolean = { currentPath, next -> next in currentPath }
  ): List<List<T>> {
    fun innerRec(next: T, currentPath: List<T>): List<List<T>> {
      val newPath = currentPath + listOf(next)
      when {
        next == target -> return listOf(newPath)
        shouldSkip(currentPath, next) -> return emptyList()
      }

      return getNeighbors(next).flatMap { innerRec(it, newPath) }
    }

    return innerRec(source, emptyList())
  }

  override fun toString(): String {
    return edges.entries.joinToString("\n") {
      "${it.key} -> ${it.value.joinToString()}"
    }
  }
}
