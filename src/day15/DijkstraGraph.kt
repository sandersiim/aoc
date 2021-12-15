package day15

import java.util.PriorityQueue

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

  fun shortestPath(from: T, to: T): Int {
    val distances = mutableMapOf(from to 0)
    val compareByDistance: Comparator<T> = compareBy { distances.getOrDefault(it, Int.MAX_VALUE) }
    val unvisitedNodes = PriorityQueue(compareByDistance)
    nodes.keys.forEach { unvisitedNodes.add(it) }

    var currentNodeId = unvisitedNodes.poll()

    while (currentNodeId != null) {
      if (currentNodeId == to) {
        break
      }
      getNeighbors(currentNodeId).forEach { (neighbor, weight) ->
        if (!neighbor.visited) {
          val newDist = distances[currentNodeId]!! + weight
          if (newDist < distances.getOrDefault(neighbor.id, Int.MAX_VALUE)) {
            distances[neighbor.id] = newDist
            unvisitedNodes.remove(neighbor.id)
            unvisitedNodes.add(neighbor.id)
          }
        }
      }
      getNode(currentNodeId).visited = true
      currentNodeId = unvisitedNodes.poll()
    }

    return distances[to]!!
  }

  override fun toString(): String {
    return adjacencyList.entries.joinToString("\n") {
      "${it.key.id} -> ${it.value.joinToString { pair -> "[${pair.first.id.toString()}, ${pair.second}]" }}"
    }
  }
}
