package main

import (
	"fmt"
	"regexp"
	"sander/aoc23/ds"
	"sander/aoc23/helpers"
	"strconv"
)

func main() {
	p := helpers.AocProblem[int]{
		PackageName:   "3",
		Test1Expected: 4361,
		Test2Expected: 467835,
		P1Solver:      part1,
		P2Solver:      part2,
	}
	p.Solve()
}

type NumberCoords struct {
	start helpers.Coords
	end   helpers.Coords
}

func (n NumberCoords) isPartNumber(
	symbols ds.Set[helpers.Coords],
	rowLength int,
) bool {
	return n.adjacentCoords(rowLength).Any(
		func(c helpers.Coords) bool {
			return symbols.Has(c)
		},
	)
}

func (n NumberCoords) adjacentCoords(rowLength int) ds.Set[helpers.Coords] {
	adjacent := make(ds.Set[helpers.Coords], n.numLength()*2+6)
	for i := n.start.X - 1; i <= n.end.X+1; i++ {
		if i > 0 || i < rowLength {
			if n.start.Y >= 1 {
				adjacent.Add(helpers.Coords{X: i, Y: n.start.Y - 1})
			}
			if n.start.Y < rowLength-1 {
				adjacent.Add(helpers.Coords{X: i, Y: n.end.Y + 1})
			}
		}
	}
	if n.start.X > 0 {
		adjacent.Add(helpers.Coords{X: n.start.X - 1, Y: n.start.Y})
	}
	if n.start.X < rowLength-1 {
		adjacent.Add(helpers.Coords{X: n.end.X + 1, Y: n.start.Y})
	}
	return adjacent
}

func (n NumberCoords) numLength() int {
	return n.end.X - n.start.X + 1
}

func (n NumberCoords) value(inputLines []string) int {
	line := inputLines[n.start.Y]
	v, err := strconv.Atoi(line[n.start.X : n.end.X+1])
	helpers.Fatality(err)

	return v
}

func (n NumberCoords) isAdjacent(
	coords helpers.Coords,
	rowLength int,
) bool {
	return n.adjacentCoords(rowLength).Any(
		func(c helpers.Coords) bool {
			return c == coords
		},
	)
}

var symbolRegex = regexp.MustCompile(`[^0-9.]`)

func part1(filename string) int {
	lines := helpers.ReadInputFile(filename)
	rowLength := len(lines[0])
	numbers, symbols := parseInput(lines)

	partNumbers := ds.Filter(numbers, func(n NumberCoords) bool {
		return n.isPartNumber(symbols, rowLength)
	})

	result := ds.SumIntBy(
		partNumbers,
		func(n NumberCoords) int {
			return n.value(lines)
		},
	)

	return result
}

func part2(filename string) int {
	lines := helpers.ReadInputFile(filename)
	rowLength := len(lines[0])
	numbers, symbols := parseInput(lines)
	partNumbers := ds.Filter(numbers, func(n NumberCoords) bool {
		return n.isPartNumber(symbols, rowLength)
	})

	sum := 0
	symbols.ForEach(func(gear helpers.Coords) {
		if lines[gear.Y][gear.X] != '*' {
			return
		}
		fmt.Println("gear:", gear)
		adjacentNums := ds.Filter(partNumbers, func(n NumberCoords) bool {
			return n.isAdjacent(gear, rowLength)
		})
		fmt.Println(adjacentNums)
		if len(adjacentNums) == 2 {
			sum += adjacentNums[0].value(lines) * adjacentNums[1].value(lines)
		}
	})

	return sum
}

func parseInput(lines []string) ([]NumberCoords, ds.Set[helpers.Coords]) {
	numbers := make([]NumberCoords, 0, len(lines))
	symbols := make(ds.Set[helpers.Coords], len(lines))
	ds.ForEachIndex(lines, func(line string, i int) {
		numberMatches := helpers.DigitsRegexp.FindAllStringIndex(line, -1)
		ds.ForEach(numberMatches, func(match []int) {
			numbers = append(numbers, NumberCoords{
				start: helpers.Coords{X: match[0], Y: i},
				end:   helpers.Coords{X: match[1] - 1, Y: i},
			})
		})
		symbolMatches := symbolRegex.FindAllStringIndex(line, -1)
		ds.ForEach(symbolMatches, func(match []int) {
			symbols.Add(helpers.Coords{X: match[0], Y: i})
		})
	})
	return numbers, symbols
}
