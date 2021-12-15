package day15

interface DijkstraNode<T> {
  var visited: Boolean
  val id: T
}

class DijkstraGraph<T> {
  private val nodes = mutableMapOf<T, DijkstraNode<T>>()
  private val adjacencyList = mutableMapOf<
    DijkstraNode<T>,
    MutableList<Pair<DijkstraNode<T>, Int>>
  >()

  fun addEdge(source: DijkstraNode<T>, target: DijkstraNode<T>, weight: Int) {
    nodes[source.id] = source
    nodes[target.id] = target
    if (adjacencyList.contains(source)) {
      adjacencyList[source]!!.add(Pair(target, weight))
    } else {
      adjacencyList[source] = mutableListOf(Pair(target, weight))
    }
  }

  fun getNode(id: T) = nodes[id] ?: throw Error("Unkown vertex $id")

  fun getNeighbors(id: T): List<Pair<DijkstraNode<T>, Int>> {
    if (!nodes.containsKey(id)) {
      throw Error("Unkown vertex $id")
    }
    return adjacencyList[nodes[id]!!] ?: emptyList()
  }

  override fun toString(): String {
    return adjacencyList.entries.joinToString("\n") {
      "${it.key.id} -> ${it.value.joinToString { pair -> "[${pair.first.id.toString()}, ${pair.second}]" }}"
    }
  }
}
