package helpers

import (
	"regexp"
	"sander/aoc23/ds"
	"strconv"
)

var SingleDigitRegexp = regexp.MustCompile("[0-9]")
var DigitsRegexp = regexp.MustCompile("[0-9]+")

func ParseInt(s string) int {
	i, err := strconv.Atoi(s)
	Fatality(err)
	return i
}

func ParseInts(s string) []int {
	return ds.Map(DigitsRegexp.FindAllString(s, -1), func(s string) int {
		i, err := strconv.Atoi(s)
		Fatality(err)
		return i
	})
}

func ParseUints(s string) []uint64 {
	return ds.Map(DigitsRegexp.FindAllString(s, -1), func(s string) uint64 {
		i, err := strconv.ParseUint(s, 10, 64)
		Fatality(err)
		return i
	})
}
