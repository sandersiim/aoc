package main

import (
	"fmt"
	"github.com/dominikbraun/graph"
	"log"
	"math"
	"sander/aoc23/helpers"
	"slices"
)

func main() {
	p := helpers.AocProblem[int]{
		PackageName:   "17",
		Test1Expected: 102,
		Test2Expected: 94,
		P1Solver:      part1,
		P2Solver:      part2,
		SkipMain:      false,
	}
	p.Solve()
}

type LavaPathGraph graph.Graph[string, LavaPathVertex]
type LavaPathVertex struct {
	coords    helpers.Coords
	direction helpers.Direction
}

func (v LavaPathVertex) String() string {
	return fmt.Sprintf("%v,%v", v.coords, v.direction)
}

func lavaPathVertexHash(v LavaPathVertex) string {
	return v.coords.String() + fmt.Sprintf(",%v", v.direction)
}

func pathWeight(g LavaPathGraph, path []string) int {
	result := 0
	for i := range path {
		if i == len(path)-1 {
			break
		}
		edge, err := g.Edge(path[i], path[i+1])
		helpers.Fatality(err)
		result += edge.Properties.Weight
	}
	return result
}

func part1(filename string) int {
	return solve(filename, 1, 3)
}

func part2(filename string) int {
	return solve(filename, 4, 10)
}

func solve(filename string, minMovement int, maxMovement int) int {
	lavaGraph, boundsX, boundsY := buildGraph(filename, minMovement, maxMovement)
	source := LavaPathVertex{coords: helpers.Coords{X: 0, Y: 0}, direction: helpers.Right}
	targetCoords := helpers.Coords{X: boundsX - 1, Y: boundsY - 1}
	targets := []LavaPathVertex{
		{coords: targetCoords, direction: helpers.Up},
		{coords: targetCoords, direction: helpers.Down},
		{coords: targetCoords, direction: helpers.Left},
		{coords: targetCoords, direction: helpers.Right},
	}
	targetShortestPathLens := make([]int, 4)
	for i, target := range targets {
		fmt.Println(source, target)
		shortestPath, err := graph.ShortestPath[string, LavaPathVertex](lavaGraph, lavaPathVertexHash(source), lavaPathVertexHash(target))
		if err != nil {
			log.Print(err)
			targetShortestPathLens[i] = math.MaxInt
		} else {
			//fmt.Println(shortestPath)
			targetShortestPathLens[i] = pathWeight(lavaGraph, shortestPath)
			fmt.Println(targetShortestPathLens[i])
		}
	}

	return slices.Min(targetShortestPathLens)
}

func buildGraph(filename string, minMovement int, maxMovement int) (LavaPathGraph, int, int) {
	lines := helpers.ReadInputFile(filename)
	boundsX := len(lines[0])
	boundsY := len(lines)
	g := graph.New(lavaPathVertexHash, graph.Directed(), graph.Weighted())
	for _, direction := range helpers.AllDirections {
		err := g.AddVertex(LavaPathVertex{coords: helpers.Coords{}, direction: direction}, graph.VertexWeight(0))
		helpers.Fatality(err)
	}

	for y, line := range lines {
		for x, char := range line {
			if x == 0 && y == 0 {
				continue
			}
			weight := helpers.ParseInt(string(char))
			coords := helpers.Coords{X: x, Y: y}
			for _, direction := range helpers.AllDirections {
				newVertex := LavaPathVertex{coords: coords, direction: direction}
				//fmt.Println("Vertex: ", newVertex)
				err := g.AddVertex(newVertex, graph.VertexWeight(weight))
				helpers.Fatality(err)
			}
		}
	}

	adjacencyMap, err1 := g.AdjacencyMap()
	helpers.Fatality(err1)

	addEdge := func(source string, newVertex LavaPathVertex, weight int) {
		newVertexHash := lavaPathVertexHash(newVertex)
		//fmt.Println("Edge: ", source, newVertexHash, weight)
		err := g.AddEdge(source, newVertexHash, graph.EdgeWeight(weight))
		helpers.Fatality(err)
	}

	var currentCoords helpers.Coords
	var weight int
	moveOneStep := func(direction helpers.Direction) {
		currentCoords = currentCoords.Next(direction)
		_, props, err := g.VertexWithProperties(lavaPathVertexHash(LavaPathVertex{coords: currentCoords, direction: helpers.Left}))
		helpers.Fatality(err)
		weight += props.Weight
	}

	for vertexHash := range adjacencyMap {
		vertex, err2 := g.Vertex(vertexHash)
		helpers.Fatality(err2)

		weight = 0
		currentCoords = vertex.coords
		switch vertex.direction {
		case helpers.Up:
			{
				spaceToMove := min(maxMovement, vertex.coords.Y)
				for i := 1; i <= spaceToMove; i++ {
					moveOneStep(vertex.direction)
					if currentCoords.X == 0 && currentCoords.Y == 0 {
						break
					}
					if i >= minMovement {
						addEdge(vertexHash, LavaPathVertex{coords: currentCoords, direction: helpers.Left}, weight)
						addEdge(vertexHash, LavaPathVertex{coords: currentCoords, direction: helpers.Right}, weight)
					}
				}
			}
		case helpers.Down:
			{
				spaceToMove := min(maxMovement, boundsY-1-vertex.coords.Y)
				for i := 1; i <= spaceToMove; i++ {
					moveOneStep(vertex.direction)
					if i >= minMovement {
						addEdge(vertexHash, LavaPathVertex{coords: currentCoords, direction: helpers.Left}, weight)
						addEdge(vertexHash, LavaPathVertex{coords: currentCoords, direction: helpers.Right}, weight)
					}
				}
			}
		case helpers.Left:
			{
				spaceToMove := min(maxMovement, vertex.coords.X)
				for i := 1; i <= spaceToMove; i++ {
					moveOneStep(vertex.direction)
					if currentCoords.X == 0 && currentCoords.Y == 0 {
						break
					}
					if i >= minMovement {
						addEdge(vertexHash, LavaPathVertex{coords: currentCoords, direction: helpers.Up}, weight)
						addEdge(vertexHash, LavaPathVertex{coords: currentCoords, direction: helpers.Down}, weight)
					}
				}
			}
		case helpers.Right:
			{
				spaceToMove := min(maxMovement, boundsX-1-vertex.coords.X)
				for i := 1; i <= spaceToMove; i++ {
					moveOneStep(vertex.direction)
					if i >= minMovement {
						addEdge(vertexHash, LavaPathVertex{coords: currentCoords, direction: helpers.Up}, weight)
						addEdge(vertexHash, LavaPathVertex{coords: currentCoords, direction: helpers.Down}, weight)
					}
				}
			}
		}
	}

	return g, boundsX, boundsY
}
