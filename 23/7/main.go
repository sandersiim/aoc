package main

import (
	"log"
	"sander/aoc23/ds"
	"sander/aoc23/helpers"
	"slices"
	"strings"
)

func main() {
	p := helpers.AocProblem[int]{
		PackageName:   "7",
		Test1Expected: 6440,
		Test2Expected: 5905,
		P1Solver:      part1,
		P2Solver:      part2,
	}
	p.Solve()
}

type hand struct {
	cards  [5]int
	counts map[int]int
}

func newHand(cards [5]int) hand {
	counts := ds.Count(cards[:])

	return hand{cards: cards, counts: counts}
}

func (h hand) typeVal() int {
	maxCount := ds.MapMaxValue(h.counts)
	l := len(h.counts)
	switch l {
	case 5:
		return 0 // high card
	case 4:
		return 1 // pair
	case 3:
		if maxCount == 2 {
			return 2 // two pair
		} else {
			return 3 // three of a kind
		}
	case 2:
		if maxCount == 3 {
			return 4 // house
		} else {
			return 5 // four of a kind
		}
	case 1:
		return 6 // five of a kind
	default:
		log.Fatal("invalid hand: ", h)
		return -1
	}
}

func (h hand) typeValJoker() int {
	jokers := h.counts[1]
	if jokers == 0 {
		return h.typeVal()
	}
	maxCount := ds.MapMaxValue(h.counts)
	l := len(h.counts)
	switch l {
	case 5:
		return 1 // pair
	case 4:
		return 3 // tree of a kind
	case 3:
		if jokers == 1 {
			if maxCount == 3 {
				return 5 // four of a kind
			} else {
				return 4 // full house

			}
		} else {
			return 5 // four of a kind
		}
	case 2:
		return 6 // five of a kind
	case 1:
		return 6 // five of a kind
	default:
		log.Fatal("invalid hand: ", h)
		return -1
	}
}

type handAndBid struct {
	h   hand
	bid int
}

func part1(filename string) int {
	handsAndBids := parseInput(filename, parseHand1)
	slices.SortFunc(handsAndBids, func(a, b handAndBid) int {
		d := a.h.typeVal() - b.h.typeVal()
		if d != 0 {
			return d
		}
		for i := 0; i < 5; i++ {
			d = a.h.cards[i] - b.h.cards[i]
			if d != 0 {
				return d
			}
		}
		return 0
	})

	return ds.SumIntByIndex(handsAndBids, func(hb handAndBid, i int) int {
		return (i + 1) * hb.bid
	})
}

func part2(filename string) int {
	handsAndBids := parseInput(filename, parseHand2)
	slices.SortFunc(handsAndBids, func(a, b handAndBid) int {
		d := a.h.typeValJoker() - b.h.typeValJoker()
		if d != 0 {
			return d
		}
		for i := 0; i < 5; i++ {
			d = a.h.cards[i] - b.h.cards[i]
			if d != 0 {
				return d
			}
		}
		return 0
	})

	return ds.SumIntByIndex(handsAndBids, func(hb handAndBid, i int) int {
		return (i + 1) * hb.bid
	})
}

func parseInput(filename string, handParser func(s string) hand) []handAndBid {
	lines := helpers.ReadInputFile(filename)
	return ds.Map(lines, func(l string) handAndBid {
		splits := strings.Split(l, " ")
		h := handParser(splits[0])
		bid := helpers.ParseInt(splits[1])
		return handAndBid{h: h, bid: bid}
	})
}

var cardsMap = map[int32]int{
	'2': 2,
	'3': 3,
	'4': 4,
	'5': 5,
	'6': 6,
	'7': 7,
	'8': 8,
	'9': 9,
	'T': 10,
	'J': 11,
	'Q': 12,
	'K': 13,
	'A': 14,
}

func parseHand1(s string) hand {
	var cards [5]int
	for i, c := range s {
		cards[i] = cardsMap[c]
	}
	return newHand(cards)
}

var cardsMap2 = map[int32]int{
	'J': 1,
	'2': 2,
	'3': 3,
	'4': 4,
	'5': 5,
	'6': 6,
	'7': 7,
	'8': 8,
	'9': 9,
	'T': 10,
	'Q': 11,
	'K': 12,
	'A': 13,
}

func parseHand2(s string) hand {
	var cards [5]int
	for i, c := range s {
		cards[i] = cardsMap2[c]
	}
	return newHand(cards)
}
