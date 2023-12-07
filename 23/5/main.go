package main

import (
	"fmt"
	"runtime"
	"sander/aoc23/ds"
	"sander/aoc23/helpers"
	"slices"
	"sync"
)

func main() {
	fmt.Println("Version", runtime.Version())
	fmt.Println("NumCPU", runtime.NumCPU())
	fmt.Println("GOMAXPROCS", runtime.GOMAXPROCS(0))
	p := helpers.AocProblem[uint64]{
		PackageName:   "5",
		Test1Expected: 35,
		Test2Expected: 46,
		P1Solver:      part1,
		P2Solver:      part2,
	}
	p.Solve()
}

type rangeMap struct {
	destinationStart uint64
	sourceStart      uint64
	r                uint64
}
type rangeMapping []rangeMap
type rangeMappings []rangeMapping

type seedRange struct {
	start uint64
	r     uint64
}

func (rMap rangeMap) contains(value uint64) bool {
	return value >= rMap.sourceStart && value < rMap.sourceStart+rMap.r
}

func (mapping rangeMapping) mapValue(value uint64) uint64 {
	for _, rMap := range mapping {
		if rMap.contains(value) {
			r := rMap.destinationStart + (value - rMap.sourceStart)
			return r
		}
	}
	return value
}

func (mappings rangeMappings) mapValue(value uint64) uint64 {
	currentVal := value
	for i := 0; i < len(mappings); i++ {
		currentVal = mappings[i].mapValue(currentVal)
	}
	return currentVal
}

func part1(filename string) uint64 {
	seeds, mappings := parseInput1(filename)
	fmt.Println(seeds)
	var minSeedValue uint64 = 0
	ds.ForEach(seeds, func(seed uint64) {
		mapped := mappings.mapValue(seed)
		if minSeedValue == 0 || mapped < minSeedValue {
			minSeedValue = mapped
		}
	})

	return minSeedValue
}

var wg sync.WaitGroup

func part2(filename string) uint64 {
	seedRanges, mappings := parseInput2(filename)
	fmt.Println(seedRanges)
	ch := make(chan uint64)
	ds.ForEachGo(seedRanges, func(sr seedRange) {
		var minSeed, minSeedValue uint64 = 0, 0
		var seed, mapped uint64
		for seed = sr.start; seed < sr.start+sr.r; seed++ {
			mapped = mappings.mapValue(seed)
			if minSeedValue == 0 || mapped < minSeedValue {
				minSeed = seed
				minSeedValue = mapped
			}
		}
		fmt.Println("result for seed range", sr.start, sr.r)
		fmt.Println(minSeed, minSeedValue)
		ch <- minSeedValue
	})
	minValues := make([]uint64, len(seedRanges))
	for i := range seedRanges {
		minValues[i] = <-ch
	}

	return slices.Min(minValues)
}

func parseInput1(filename string) ([]uint64, rangeMappings) {
	lines := helpers.ReadInputFile(filename)
	seeds := helpers.ParseUints(lines[0])
	lineIdx := 3
	mappings := make([]rangeMapping, 0, 7)
	currentMapping := make(rangeMapping, 0, len(lines)/7)
	for lineIdx < len(lines) {
		if lines[lineIdx] == "" {
			mappings = append(mappings, currentMapping)
			currentMapping = make(rangeMapping, 0, len(lines)/7)
			lineIdx += 2
		} else {
			nums := helpers.ParseUints(lines[lineIdx])
			currentMapping = append(currentMapping, rangeMap{
				r:                nums[2],
				sourceStart:      nums[1],
				destinationStart: nums[0],
			})
			lineIdx++
		}
	}
	mappings = append(mappings, currentMapping)

	return seeds, mappings
}

func parseInput2(filename string) ([]seedRange, rangeMappings) {
	lines := helpers.ReadInputFile(filename)
	seedNums := helpers.ParseUints(lines[0])
	seedRanges := make([]seedRange, 0, len(seedNums)/2)
	for i := 0; i < len(seedNums); i += 2 {
		seedRanges = append(seedRanges, seedRange{
			start: seedNums[i],
			r:     seedNums[i+1],
		})
	}
	lineIdx := 3
	mappings := make([]rangeMapping, 0, 7)
	currentMapping := make(rangeMapping, 0, len(lines)/7)
	for lineIdx < len(lines) {
		if lines[lineIdx] == "" {
			mappings = append(mappings, currentMapping)
			currentMapping = make(rangeMapping, 0, len(lines)/7)
			lineIdx += 2
		} else {
			nums := helpers.ParseUints(lines[lineIdx])
			currentMapping = append(currentMapping, rangeMap{
				r:                nums[2],
				sourceStart:      nums[1],
				destinationStart: nums[0],
			})
			lineIdx++
		}
	}
	mappings = append(mappings, currentMapping)

	return seedRanges, mappings
}
