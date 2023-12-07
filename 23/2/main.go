package main

import (
	"regexp"
	"sander/aoc23/ds"
	"sander/aoc23/helpers"
	"strconv"
	"strings"
)

type game struct {
	id    int
	cubes []colorCubes
}

func (g game) isImpossible() bool {
	return ds.Any(g.cubes, func(c colorCubes) bool {
		return c.red > 12 || c.green > 13 || c.blue > 14
	})
}

func (g game) minimumSet() colorCubes {
	m := colorCubes{}
	ds.ForEach(g.cubes, func(c colorCubes) {
		if c.red > m.red {
			m.red = c.red
		}
		if c.green > m.green {
			m.green = c.green
		}
		if c.blue > m.blue {
			m.blue = c.blue
		}
	})
	return m
}

type colorCubes struct {
	red   int
	green int
	blue  int
}

func (c colorCubes) Power() int {
	return c.red * c.green * c.blue
}

func main() {
	p := helpers.AocProblem[int]{
		PackageName:   "2",
		Test1Expected: 8,
		Test2Expected: 2286,
		P1Solver:      part1,
		P2Solver:      part2,
	}
	p.Solve()
}

func part1(filename string) int {
	lines := helpers.ReadInputFile(filename)
	games := ds.Map(lines, parseGame)
	possibleGames := ds.Filter(games, func(g game) bool {
		return !g.isImpossible()
	})
	return ds.SumIntBy(possibleGames, func(g game) int {
		return g.id
	})
}

func part2(filename string) int {
	lines := helpers.ReadInputFile(filename)
	games := ds.Map(lines, parseGame)

	return ds.SumIntBy(games, func(g game) int {
		return g.minimumSet().Power()
	})
}

func parseGame(line string) game {
	gameIdIdx := helpers.DigitsRegexp.FindStringIndex(line)
	gameId, err := strconv.Atoi(line[gameIdIdx[0]:gameIdIdx[1]])
	helpers.Fatality(err)

	return game{
		id:    gameId,
		cubes: parseDraws(line[gameIdIdx[1]+2:]),
	}
}

func parseDraws(line string) []colorCubes {
	cubeDraws := strings.Split(line, "; ")

	return ds.Map(cubeDraws, parseDraw)
}

var redRe = regexp.MustCompile("([0-9]+) red")
var greenRe = regexp.MustCompile("([0-9]+) green")
var blueRe = regexp.MustCompile("([0-9]+) blue")

func parseDraw(line string) colorCubes {
	result := colorCubes{}
	redMatch := redRe.FindStringSubmatch(line)
	if redMatch != nil {
		redNum, err := strconv.Atoi(redMatch[1])
		helpers.Fatality(err)
		result.red = redNum
	}
	greenMatch := greenRe.FindStringSubmatch(line)
	if greenMatch != nil {
		greenNum, err := strconv.Atoi(greenMatch[1])
		helpers.Fatality(err)
		result.green = greenNum
	}
	blueMatch := blueRe.FindStringSubmatch(line)
	if blueMatch != nil {
		blueNum, err := strconv.Atoi(blueMatch[1])
		helpers.Fatality(err)
		result.blue = blueNum
	}

	return result
}
