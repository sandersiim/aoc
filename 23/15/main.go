package main

import (
	"container/list"
	"fmt"
	"sander/aoc23/ds"
	"sander/aoc23/helpers"
	"strings"
)

func main() {
	p := helpers.AocProblem[int]{
		PackageName:   "15",
		Test1Expected: 1320,
		Test2Expected: 145,
		P1Solver:      part1,
		P2Solver:      part2,
	}
	p.Solve()
}

func part1(filename string) int {
	instructions := parseInput(filename)

	return ds.SumIntBy(instructions, func(s string) int {
		return int(HASH(s))
	})
}

type Box struct {
	index  int
	lenses *list.List
}

type Lens struct {
	focalLength int
	label       string
}

func (b *Box) addLens(l Lens) {
	//fmt.Printf("Adding %v to box %v\n", l, b.index)
	var existingLens Lens
	for e := b.lenses.Front(); e != nil; e = e.Next() {
		existingLens = e.Value.(Lens)
		if l.label == existingLens.label {
			b.lenses.InsertAfter(l, e)
			b.lenses.Remove(e)
			return
		}
	}

	b.lenses.PushBack(l)
}
func (b *Box) removeLens(label string) {
	//fmt.Printf("Removing %v from box %v\n", label, b.index)
	var existingLens Lens
	for e := b.lenses.Front(); e != nil; e = e.Next() {
		existingLens = e.Value.(Lens)
		if label == existingLens.label {
			b.lenses.Remove(e)
			return
		}
	}
}
func (b *Box) sum() int {
	result := 0
	var l Lens
	i := 1
	for e := b.lenses.Front(); e != nil; e = e.Next() {
		l = e.Value.(Lens)
		result += (b.index + 1) * l.focalLength * i
		i++
	}
	return result
}

func (b *Box) String() string {
	var sb strings.Builder
	sb.WriteString(fmt.Sprintf("Box %v: ", b.index))
	for e := b.lenses.Front(); e != nil; e = e.Next() {
		sb.WriteString(fmt.Sprintf("%v, ", e.Value.(Lens)))
	}
	return sb.String()
}

func part2(filename string) int {
	instructions := parseInput(filename)
	boxes := make([]*Box, 256)
	for i := 0; i < 256; i++ {
		boxes[i] = &Box{
			index:  i,
			lenses: list.New(),
		}
	}
	var label string
	var boxIndex, focalLength int
	ds.ForEach(instructions, func(s string) {
		if strings.Contains(s, "-") {
			label = strings.Split(s, "-")[0]
			boxIndex = int(HASH(label))
			boxes[boxIndex].removeLens(label)
		} else {
			splits := strings.Split(s, "=")
			label = splits[0]
			boxIndex = int(HASH(label))
			focalLength = helpers.ParseInt(splits[1])
			boxes[boxIndex].addLens(Lens{label: label, focalLength: focalLength})
		}
	})

	return ds.SumIntBy(boxes, func(v *Box) int {
		return v.sum()
	})
}

func HASH(input string) uint8 {
	//Determine the ASCII code for the current character of the string.
	//	Increase the current value by the ASCII code you just determined.
	//	Set the current value to itself multiplied by 17.
	//Set the current value to the remainder of dividing itself by 256.
	var result int32 = 0
	for _, c := range input {
		result += c
		result *= 17
		result %= 256
	}
	return uint8(result)
}

func parseInput(filename string) []string {
	lines := helpers.ReadInputFile(filename)
	return strings.Split(lines[0], ",")
}
