package main

import (
	"container/list"
	"fmt"
	"sander/aoc23/ds"
	"sander/aoc23/helpers"
	"strconv"
	"strings"
)

func main() {
	p := helpers.AocProblem[int]{
		PackageName:   "4",
		Test1Expected: 13,
		Test2Expected: 30,
		P1Solver:      part1,
		P2Solver:      part2,
	}
	p.Solve()
}

type scratchCard struct {
	id          int
	myNums      ds.Set[int]
	winningNums ds.Set[int]
}

func (c scratchCard) String() string {
	return fmt.Sprintf("{id: %v myNums: %v winningNums: %v}", c.id, c.myNums, c.winningNums)
}

func (c scratchCard) value() int {
	v := 0
	c.myNums.ForEach(func(num int) {
		if c.winningNums.Has(num) {
			if v == 0 {
				v = 1
			} else {
				v <<= 1
			}
		}
	})
	return v
}

func (c scratchCard) numOfWinnings() int {
	v := 0
	c.myNums.ForEach(func(num int) {
		if c.winningNums.Has(num) {
			v += 1
		}
	})
	return v
}

func part1(filename string) int {
	cards := parseInput(filename)

	return ds.SumIntBy(cards, func(card scratchCard) int {
		return card.value()
	})
}

func part2(filename string) int {
	cards := parseInput(filename)
	cardQueue := list.New()
	ds.ForEach(cards, func(card scratchCard) {
		cardQueue.PushBack(card)
	})
	num := 0
	for cardQueue.Len() > 0 {
		card := cardQueue.Remove(cardQueue.Front()).(scratchCard)
		num += 1
		v := card.numOfWinnings()
		for i := card.id; i < card.id+v; i++ {
			cardQueue.PushBack(cards[i])
		}
	}
	return num
}

func parseInput(filename string) []scratchCard {
	lines := helpers.ReadInputFile(filename)
	return ds.Map(lines, func(line string) scratchCard {
		splitted := strings.Split(line, ": ")
		idString := helpers.DigitsRegexp.FindString(splitted[0])
		id, err := strconv.Atoi(idString)
		helpers.Fatality(err)

		splitNumsString := strings.Split(splitted[1], " | ")
		myNumsString, winningNumsString := splitNumsString[0], splitNumsString[1]
		card := scratchCard{
			id:          id,
			myNums:      make(ds.Set[int]),
			winningNums: make(ds.Set[int]),
		}
		ds.ForEach(
			helpers.DigitsRegexp.FindAllString(myNumsString, -1),
			func(numString string) {
				num, err := strconv.Atoi(numString)
				helpers.Fatality(err)
				card.myNums.Add(num)
			},
		)
		ds.ForEach(
			helpers.DigitsRegexp.FindAllString(winningNumsString, -1),
			func(numString string) {
				num, err := strconv.Atoi(numString)
				helpers.Fatality(err)
				card.winningNums.Add(num)
			},
		)

		return card
	})
}
