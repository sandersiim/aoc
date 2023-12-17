package main

import (
	"container/list"
	"fmt"
	"sander/aoc23/ds"
	"sander/aoc23/helpers"
)

func main() {
	p := helpers.AocProblem[int]{
		PackageName:   "16",
		Test1Expected: 46,
		Test2Expected: 51,
		P1Solver:      part1,
		P2Solver:      part2,
	}
	p.Solve()
}

type MirrorTile uint8
type MirrorRow []MirrorTile
type MirrorMap []MirrorRow

const (
	Empty           MirrorTile = '.'
	DownMirror      MirrorTile = '\\'
	UpMirror        MirrorTile = '/'
	HorizontalSplit MirrorTile = '-'
	VerticalSplit   MirrorTile = '|'
)

type MovementVector struct {
	direction helpers.Direction
	coords    helpers.Coords
}

func (v MovementVector) String() string {
	return fmt.Sprintf("MovementVector{direction: %v, coords: %v}", v.direction, v.coords)
}

func nextReflectionDirection(currentDirection helpers.Direction, mirror MirrorTile) helpers.Direction {
	switch mirror {
	case Empty:
		return currentDirection
	case DownMirror:
		switch currentDirection {
		case helpers.Up:
			return helpers.Left
		case helpers.Down:
			return helpers.Right
		case helpers.Left:
			return helpers.Up
		case helpers.Right:
			return helpers.Down
		}
	case UpMirror:
		switch currentDirection {
		case helpers.Up:
			return helpers.Right
		case helpers.Down:
			return helpers.Left
		case helpers.Left:
			return helpers.Down
		case helpers.Right:
			return helpers.Up
		}
	}
	return currentDirection
}

func (mMap MirrorMap) NextVectors(v MovementVector) []MovementVector {
	nextCoords := v.coords.Next(v.direction)
	if nextCoords.X < 0 || nextCoords.Y < 0 || nextCoords.X >= len(mMap[0]) || nextCoords.Y >= len(mMap) {
		return []MovementVector{}
	}

	nextTile := mMap[nextCoords.Y][nextCoords.X]
	if nextTile == HorizontalSplit {
		if v.direction == helpers.Up || v.direction == helpers.Down {
			return []MovementVector{
				{direction: helpers.Left, coords: nextCoords},
				{direction: helpers.Right, coords: nextCoords},
			}
		} else {
			return []MovementVector{{direction: v.direction, coords: nextCoords}}
		}
	} else if nextTile == VerticalSplit {
		if v.direction == helpers.Left || v.direction == helpers.Right {
			return []MovementVector{
				{direction: helpers.Up, coords: nextCoords},
				{direction: helpers.Down, coords: nextCoords},
			}
		} else {
			return []MovementVector{{direction: v.direction, coords: nextCoords}}
		}
	} else {
		return []MovementVector{{direction: nextReflectionDirection(v.direction, nextTile), coords: nextCoords}}
	}
}

func part1(filename string) int {
	mirrorMap := parseInput(filename)

	return solveForStartVector(mirrorMap, MovementVector{direction: helpers.Right, coords: helpers.Coords{X: -1, Y: 0}})
}

func part2(filename string) int {
	mirrorMap := parseInput(filename)
	rows := len(mirrorMap)
	cols := len(mirrorMap[0])
	maxVal := 0
	var energizedTiles int
	for y := 0; y < rows; y++ {
		energizedTiles = solveForStartVector(mirrorMap, MovementVector{direction: helpers.Right, coords: helpers.Coords{X: -1, Y: y}})
		if energizedTiles > maxVal {
			maxVal = energizedTiles
		}
		energizedTiles = solveForStartVector(mirrorMap, MovementVector{direction: helpers.Left, coords: helpers.Coords{X: cols, Y: y}})
		if energizedTiles > maxVal {
			maxVal = energizedTiles
		}
	}

	for x := 0; x < cols; x++ {
		energizedTiles = solveForStartVector(mirrorMap, MovementVector{direction: helpers.Down, coords: helpers.Coords{X: x, Y: -1}})
		if energizedTiles > maxVal {
			maxVal = energizedTiles
		}
		energizedTiles = solveForStartVector(mirrorMap, MovementVector{direction: helpers.Up, coords: helpers.Coords{X: x, Y: rows}})
		if energizedTiles > maxVal {
			maxVal = energizedTiles
		}
	}

	return maxVal
}

func solveForStartVector(mirrorMap MirrorMap, startVector MovementVector) int {
	seenVectors := ds.Set[MovementVector]{}
	energizedTiles := ds.Set[helpers.Coords]{}
	queue := list.New()
	for _, nextVector := range mirrorMap.NextVectors(startVector) {
		queue.PushBack(nextVector)
	}

	for queue.Len() > 0 {
		v := queue.Remove(queue.Front()).(MovementVector)
		seenVectors.Add(v)
		energizedTiles.Add(v.coords)
		//fmt.Println(v)
		for _, nextVector := range mirrorMap.NextVectors(v) {
			if !seenVectors.Has(nextVector) {
				queue.PushBack(nextVector)
			}
		}
	}

	return len(energizedTiles)
}

func parseInput(filename string) MirrorMap {
	lines := helpers.ReadInputFile(filename)
	return ds.Map(lines, func(line string) MirrorRow {
		return ds.MapChars(line, func(r uint8) MirrorTile {
			return MirrorTile(r)
		})
	})
}
