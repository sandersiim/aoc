package main

import (
	"fmt"
	"runtime"
	"sander/aoc23/ds"
	"sander/aoc23/helpers"
)

func main() {
	fmt.Println("Version", runtime.Version())
	fmt.Println("NumCPU", runtime.NumCPU())
	fmt.Println("GOMAXPROCS", runtime.GOMAXPROCS(0))
	p := helpers.AocProblem[int]{
		PackageName:   "6",
		Test1Expected: 288,
		Test2Expected: 71503,
		P1Solver:      part1,
		P2Solver:      part2,
		Part2Input:    "6/input2.txt",
	}
	p.Solve()
}

type race struct {
	time, distance int
}

func calcDistance(buttonTime int, moveTime int) int {
	return buttonTime * moveTime
}

func (r race) numOfWays() int {
	result := 0
	for i := 1; i < r.time; i++ {
		if r.distance < calcDistance(i, r.time-i) {
			result++
		}
	}
	return result
}

func part1(filename string) int {
	races := parseInput(filename)
	ways := ds.Map(races, func(x race) int {
		return x.numOfWays()
	})
	product := 1
	ds.ForEach(ways, func(x int) {
		product *= x
	})

	return product
}

func part2(filename string) int {
	race := parseInput2(filename)

	return race.numOfWays()
}

func parseInput(filename string) []race {
	lines := helpers.ReadInputFile(filename)
	times := helpers.ParseInts(lines[0])
	distances := helpers.ParseInts(lines[1])
	races := make([]race, len(times))
	for i, d := range distances {
		races[i] = race{time: times[i], distance: d}
	}

	return races
}

func parseInput2(filename string) race {
	lines := helpers.ReadInputFile(filename)
	time := helpers.ParseInts(lines[0])[0]
	distance := helpers.ParseInts(lines[1])[0]

	return race{time: time, distance: distance}
}
