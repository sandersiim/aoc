package main

import (
	"fmt"
	"log"
	"regexp"
	"sander/aoc23/helpers"
	"strconv"
)

func main() {
	lines := helpers.ReadInputFile("1/input.txt")

	fmt.Println("Part1 Test Result:", part1(helpers.ReadInputFile("1/test1.txt")))
	fmt.Println("Part1 Result:", part1(lines))
	fmt.Println("Part2 Test Result:", part2(helpers.ReadInputFile("1/test2.txt")))
	fmt.Println("Part2 Result:", part2(lines))
}

func part1(lines []string) int {
	sum := 0
	for _, line := range lines {
		digitMatches := helpers.SingleDigitRegexp.FindAllString(line, -1)
		if len(digitMatches) == 0 {
			log.Fatal("Didn't find any digit matches in: ", line)
		}
		first, err1 := strconv.Atoi(digitMatches[0])
		last, err2 := strconv.Atoi(digitMatches[len(digitMatches)-1])
		helpers.Fatality(err1)
		helpers.Fatality(err2)
		sum += first*10 + last
	}
	return sum
}

var stringDigitRe = regexp.MustCompile("one|two|three|four|five|six|seven|eight|nine|[0-9]")

func part2(lines []string) int {
	conversionMap := map[string]int{
		"one":   1,
		"two":   2,
		"three": 3,
		"four":  4,
		"five":  5,
		"six":   6,
		"seven": 7,
		"eight": 8,
		"nine":  9,
	}
	for i := 1; i < 10; i++ {
		conversionMap[strconv.Itoa(i)] = i
	}

	sum := 0
	var num, first, last int
	var currentMatch []int
	var matchString, currentLine string
	for _, line := range lines {
		first, last = -1, -1
		currentLine = line
		currentMatch = stringDigitRe.FindStringIndex(currentLine)

		for len(currentLine) > 0 && currentMatch != nil {
			matchString = currentLine[currentMatch[0]:currentMatch[1]]
			num, ok := conversionMap[matchString]
			if !ok {
				log.Fatalf("Could not convert digit match '%v' to int in line '%v'", matchString, line)
			}
			if first == -1 {
				first = num
				last = num
			} else {
				last = num
			}
			currentLine = currentLine[currentMatch[0]+1:]
			currentMatch = stringDigitRe.FindStringIndex(currentLine)
		}
		num = first*10 + last
		// fmt.Println(line, num)
		sum += num
	}
	return sum
}
