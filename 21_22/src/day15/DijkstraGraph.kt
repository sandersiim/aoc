package day15

import java.util.PriorityQueue

class DijkstraGraph<T> {
  private val adjacencyList = mutableMapOf<
    T,
    MutableList<Pair<T, Int>>
  >()

  fun addEdge(source: T, target: T, weight: Int) {
    if (adjacencyList.contains(source)) {
      adjacencyList[source]!!.add(Pair(target, weight))
    } else {
      adjacencyList[source] = mutableListOf(Pair(target, weight))
    }
  }

  private fun getNeighbors(id: T): List<Pair<T, Int>> {
    return adjacencyList[id] ?: emptyList()
  }

  fun shortestPath(from: T, to: T): Int {
    val distances = mutableMapOf(from to 0)
    val compareByDistance: Comparator<T> = compareBy { distances.getOrDefault(it, Int.MAX_VALUE) }
    val visitedNodes = mutableSetOf<T>()
    val unvisitedNodes = PriorityQueue(compareByDistance)
    adjacencyList.keys.forEach { unvisitedNodes.add(it) }

    var currentNode = unvisitedNodes.poll()

    while (currentNode != null) {
      if (currentNode == to) {
        break
      }
      getNeighbors(currentNode).forEach { (neighbor, weight) ->
        if (neighbor !in visitedNodes) {
          val newDist = distances[currentNode]!! + weight
          if (newDist < distances.getOrDefault(neighbor, Int.MAX_VALUE)) {
            distances[neighbor] = newDist
            unvisitedNodes.remove(neighbor)
            unvisitedNodes.add(neighbor)
          }
        }
      }
      visitedNodes.add(currentNode)
      currentNode = unvisitedNodes.poll()
    }

    return distances[to]!!
  }

  override fun toString(): String {
    return adjacencyList.entries.joinToString("\n") {
      "${it.key} -> ${it.value.joinToString { pair -> "[${pair.first.toString()}, ${pair.second}]" }}"
    }
  }
}
