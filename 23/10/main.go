package main

import (
	"fmt"
	"sander/aoc23/10/day10"
	"sander/aoc23/ds"
	"sander/aoc23/helpers"
)

func main() {
	p := helpers.AocProblem[int]{
		PackageName:   "10",
		Test1Expected: 4,
		Test2Expected: 8,
		P1Solver:      part1,
		P2Solver:      part2,
	}
	p.Solve()
}

func part1(filename string) int {
	pipeMap := parseInput(filename)
	startCoords := pipeMap.StartCoords()
	previous := []helpers.Coords{startCoords, startCoords}
	currents := pipeMap.ConnectingPipes(startCoords, helpers.Coords{X: -1, Y: -1})
	currentDistances := []int{1, 1}
	visited := make(ds.Set[helpers.Coords])
	visited.Add(startCoords, currents[0], currents[1])

	var next helpers.Coords
	stop := false
	for !stop {
		for i := 0; i < 2; i++ {
			next = pipeMap.ConnectingPipes(currents[i], previous[i])[0]
			if visited.Has(next) {
				stop = true
				break
			}
			visited.Add(next)
			previous[i] = currents[i]
			currents[i] = next
			currentDistances[i]++
		}
	}
	fmt.Println(currents)
	fmt.Println(currentDistances)

	return max(currentDistances[0], currentDistances[1])
}

func part2(filename string) int {
	pipeMap := parseInput(filename)
	startCoords := pipeMap.StartCoords()
	fmt.Println(startCoords)
	mainLine := pipeMap.MainLine()
	fmt.Println(mainLine)

	return 0
}

func parseInput(filename string) day10.PipeMap {
	lines := helpers.ReadInputFile(filename)
	return ds.Map(lines, func(line string) day10.PipeRow {
		return ds.MapChars(line, func(r uint8) day10.Pipe {
			return day10.Pipe(r)
		})
	})
}
