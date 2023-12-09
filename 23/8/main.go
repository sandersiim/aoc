package main

import (
	"fmt"
	"sander/aoc23/ds"
	"sander/aoc23/helpers"
	"sander/aoc23/math"
	"strings"
)

type RL int

const (
	Right RL = 1
	Left  RL = 0
)

func (m RL) String() string {
	switch m {
	case Right:
		return "R"
	case Left:
		return "L"
	default:
		panic(fmt.Sprintf("unknown move: %v", uint8(m)))
	}
}

type path struct {
	source string
	left   string
	right  string
}

func main() {
	p := helpers.AocProblem[int]{
		PackageName:   "8",
		Test1Expected: 2,
		Test2Expected: 6,
		P1Solver:      part1,
		P2Solver:      part2,
	}
	p.Solve()
}

func part1(filename string) int {
	moves, paths := parseInput(filename)

	current := "AAA"
	totalMoves := 0
	var move RL
	for current != "ZZZ" {
		move = moves[totalMoves%len(moves)]

		current = paths[current][move]
		totalMoves++
	}

	return totalMoves
}

type positionState struct {
	numOfMoves int
	node       string
}

type moveToZ struct {
	distance int
	endNode  string
}

func calcNewPos(pos positionState, move *moveToZ) positionState {
	return positionState{
		numOfMoves: pos.numOfMoves + move.distance,
		node:       move.endNode,
	}
}

func (pos *positionState) applyMove(move *moveToZ) {
	pos.numOfMoves += move.distance
	pos.node = move.endNode
}

func part2(filename string) int {
	moves, paths := parseInput(filename)
	movesArrLength := len(moves)
	fmt.Println("movesArrLength", movesArrLength)
	pathsToNextZ := make(map[positionState]moveToZ, len(paths))

	var getMoveToNextZ func(from positionState) *moveToZ
	var lrMove RL
	var nextNode string
	var move moveToZ
	var moveFromNext *moveToZ
	getMoveToNextZ = func(from positionState) *moveToZ {
		normalizedFrom := from
		if from.numOfMoves >= movesArrLength {
			normalizedFrom.numOfMoves %= movesArrLength
		}
		if v, ok := pathsToNextZ[normalizedFrom]; ok {
			return &v
		}

		lrMove = moves[normalizedFrom.numOfMoves]
		nextNode = paths[normalizedFrom.node][lrMove]
		if nextNode[2] == 'Z' {
			move = moveToZ{1, nextNode}
			pathsToNextZ[normalizedFrom] = move
			return &move
		} else {
			moveFromNext = getMoveToNextZ(calcNewPos(normalizedFrom, &moveToZ{1, nextNode}))
			move = moveToZ{moveFromNext.distance + 1, moveFromNext.endNode}
			pathsToNextZ[normalizedFrom] = move
			return &move
		}
	}

	currents := make([]*positionState, 0, 100)
	ds.ForEachKey(paths, func(k string) {
		if k[2] == 'A' {
			startPos := positionState{0, k}
			nextPos := calcNewPos(startPos, getMoveToNextZ(startPos))
			fmt.Println(startPos, nextPos)
			currents = append(currents, &nextPos)
		}
	})
	//ds.ForEachKey(paths, func(k string) {
	//	if k[2] == 'Z' {
	//		p := getMoveToNextZ(positionState{0, k})
	//		fmt.Println(k, p.endNode, p.distance)
	//	}
	//})

	return math.LCM(
		ds.Map(currents, func(pos *positionState) int { return pos.numOfMoves })...,
	)
}

func parseInput(filename string) ([]RL, map[string][2]string) {
	lines := helpers.ReadInputFile(filename)
	moves := make([]RL, len(lines[0]))
	for i, c := range lines[0] {
		switch c {
		case 'L':
			moves[i] = Left
		case 'R':
			moves[i] = Right
		}
	}

	paths := make(map[string][2]string, len(lines)-2)
	for i := 2; i < len(lines); i++ {
		split1 := strings.Split(lines[i], " = ")
		split2 := strings.Split(split1[1][1:len(split1[1])-1], ", ")
		paths[split1[0]] = [2]string{split2[0], split2[1]}
	}

	return moves, paths
}
