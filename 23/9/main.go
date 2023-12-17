package main

import (
	"fmt"
	"sander/aoc23/ds"
	"sander/aoc23/helpers"
)

func main() {
	p := helpers.AocProblem[int]{
		PackageName:   "9",
		Test1Expected: 114,
		Test2Expected: 2,
		P1Solver:      part1,
		P2Solver:      part2,
	}
	p.Solve()
}

type sequence []int

func (seq sequence) diff() sequence {
	diffs := make([]int, len(seq)-1)
	for i := 0; i < len(seq)-1; i++ {
		diffs[i] = seq[i+1] - seq[i]
	}
	return diffs
}

func (seq sequence) nextPrediction() int {
	// fmt.Println(seq)
	seqLen := len(seq)
	if seqLen < 1 {
		panic("empty sequence")
	}

	if ds.AllEqual(seq) {
		return seq[0]
	}
	return seq[seqLen-1] + seq.diff().nextPrediction()
}

func (seq sequence) prevPrediction() int {
	// fmt.Println(seq)
	seqLen := len(seq)
	if seqLen < 1 {
		panic("empty sequence")
	}

	if ds.AllEqual(seq) {
		return seq[0]
	}
	return seq[0] - seq.diff().prevPrediction()
}

func part1(filename string) int {
	seqs := parseInput(filename)
	sum := 0
	var prediction int
	ds.ForEach(seqs, func(seq sequence) {
		// fmt.Println("Calculating prediction")
		prediction = seq.nextPrediction()
		sum += prediction
		fmt.Println(prediction, seq)
	})

	return sum
}

func part2(filename string) int {
	seqs := parseInput(filename)
	sum := 0
	var prediction int
	ds.ForEach(seqs, func(seq sequence) {
		// fmt.Println("Calculating prediction")
		prediction = seq.prevPrediction()
		sum += prediction
		fmt.Println(prediction, seq)
	})

	return sum
}

func parseInput(filename string) []sequence {
	lines := helpers.ReadInputFile(filename)
	return ds.Map(lines, func(line string) sequence {
		return helpers.ParseInts(line)
	})
}
