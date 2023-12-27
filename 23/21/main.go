package main

import (
	"fmt"
	"math"
	"sander/aoc23/ds"
	"sander/aoc23/helpers"
)

func main() {
	p := helpers.AocProblem[int]{
		PackageName:   "21",
		Test1Expected: 16,
		Test2Expected: 167004,
		Test1Solver:   test1,
		P1Solver:      part1,
		P2Solver:      part2,
		SkipTest2:     true,
	}
	p.Solve()
}

type Tile uint8

const (
	Empty Tile = '.'
	Start Tile = 'S'
	Wall  Tile = '#'
)

type TileWithCoords struct {
	Tile   Tile
	Coords helpers.Coords
}

func isEmptyTile(t TileWithCoords) bool {
	return t.Tile == Empty || t.Tile == Start
}

type TilesMap [][]TileWithCoords

func (t TilesMap) EmptyAdjacents(coords helpers.Coords) ds.Set[helpers.Coords] {
	adjacentCoords := coords.AdjacentNeighboursWithBounds(len(t[0]), len(t))
	result := make(ds.Set[helpers.Coords], 4)
	for _, c := range adjacentCoords {
		if isEmptyTile(t[c.Y][c.X]) {
			result.Add(c)
		}
	}
	return result
}

func (t TilesMap) AllEmptyCoords() []helpers.Coords {
	emptyCoords := make([]helpers.Coords, len(t)*len(t[0])/2)
	var tile TileWithCoords
	for x := range t {
		for y := range t[x] {
			tile = t[x][y]
			if isEmptyTile(tile) {
				emptyCoords = append(emptyCoords, tile.Coords)
			}
		}
	}
	return emptyCoords
}

func test1(filename string) int {
	tileMap := parseInputs(filename)
	return solve(tileMap, 6)
}

func part1(filename string) int {
	tileMap := parseInputs(filename)
	return solve(tileMap, 64)
}

func part2(filename string) int {
	tileMap := parseInputs(filename)
	return solve2(tileMap)
}

func solve(tileMap TilesMap, steps int) int {
	distancesMap := findDistances(tileMap)
	targetDistanceParity := steps % 2

	result := 0
	for y, row := range tileMap {
		rowStr := ""
		for x, tile := range row {
			coords := helpers.Coords{X: x, Y: y}
			d, ok := distancesMap[coords]
			if ok && d <= steps && d%2 == targetDistanceParity {
				result++
				rowStr += "O"
			} else {
				rowStr += string(tile.Tile)
			}
		}
		fmt.Println(rowStr)
	}
	return result
}

// This only works when the "diamond of reachable step tiles" reaches exactly to edge of an "odd block" (starting block or 2 blocks away from starting, etc),
// which is the case for the part2 input
func solve2(tileMap TilesMap) int {
	distancesMap := findDistances(tileMap)
	evenDistanceTilesInOneBlock := ds.CountValues(distancesMap, func(d int) bool {
		return d%2 == 0
	})
	oddDistanceTilesInOneBlock := ds.CountValues(distancesMap, func(d int) bool {
		return d%2 == 1
	})
	totalBlocksRadius := (26501365 - 65) / 131
	oddBlocks := (totalBlocksRadius + 1) * (totalBlocksRadius + 1)
	evenBlocks := totalBlocksRadius * totalBlocksRadius

	fullBlockTiles := evenDistanceTilesInOneBlock*evenBlocks + oddDistanceTilesInOneBlock*oddBlocks

	oddBlocksOfCornersToRemove := totalBlocksRadius + 1
	oddDistanceTilesInBlockCorners := ds.CountValues(distancesMap, func(d int) bool {
		return d%2 == 1 && d > 65
	})
	evenBlocksOfCornersToAdd := totalBlocksRadius
	evenDistanceTilesInBlockCorners := ds.CountValues(distancesMap, func(d int) bool {
		return d%2 == 0 && d > 65
	})

	return fullBlockTiles - oddBlocksOfCornersToRemove*oddDistanceTilesInBlockCorners + evenBlocksOfCornersToAdd*evenDistanceTilesInBlockCorners
}

func findDistances(tileMap TilesMap) map[helpers.Coords]int {
	distancesMap := make(map[helpers.Coords]int)
	startCoords := findStart(tileMap)
	distancesMap[startCoords] = 0
	coordsQueue := ds.NewPriorityQueue[helpers.Coords]()
	coordsQueue.Push(startCoords, 0)
	ds.ForEach(tileMap.AllEmptyCoords(), func(emptyCoords helpers.Coords) {
		if emptyCoords != startCoords {
			coordsQueue.Push(emptyCoords, math.MaxInt-1)
		}
	})
	for coordsQueue.Len() > 0 {
		next, e := coordsQueue.Pop()
		helpers.Fatality(e)
		d, ok := distancesMap[next]
		if !ok {
			d = math.MaxInt - 1
		}
		newDist := d + 1
		emptyAdjacents := tileMap.EmptyAdjacents(next)
		for emptyAdjacent := range emptyAdjacents {
			currentDist, ok2 := distancesMap[emptyAdjacent]
			if !ok2 {
				currentDist = math.MaxInt - 1
			}
			if newDist < currentDist {
				distancesMap[emptyAdjacent] = newDist
				coordsQueue.UpdatePriority(emptyAdjacent, float64(newDist))
			}
		}
	}

	return distancesMap
}

func findStart(tileMap [][]TileWithCoords) helpers.Coords {
	for _, row := range tileMap {
		for _, tile := range row {
			if tile.Tile == Start {
				return tile.Coords
			}
		}
	}
	panic("No start tile found")
}

func parseInputs(filename string) [][]TileWithCoords {
	lines := helpers.ReadInputFile(filename)
	result := make([][]TileWithCoords, len(lines))
	for y, row := range lines {
		rowTiles := make([]TileWithCoords, len(row))
		for x, tile := range row {
			rowTiles[x] = TileWithCoords{Tile: Tile(tile), Coords: helpers.Coords{X: x, Y: y}}
		}
		result[y] = rowTiles
	}

	return result
}
