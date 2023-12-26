package helpers

import "fmt"

type AocProblem[T comparable] struct {
	PackageName   string
	Test1Expected T
	Test2Expected T
	P1Solver      func(string) T
	P2Solver      func(string) T
	Part2Input    string
	SkipMain      bool
	SkipTest2     bool
}

func (p AocProblem[T]) Solve() {
	if p.P1Solver != nil {
		part1TestRes := p.P1Solver(p.test1File())
		if part1TestRes != p.Test1Expected {
			fmt.Printf("Error: Part1 Test Result: %v != %v\n", part1TestRes, p.Test1Expected)
		} else {
			fmt.Println("Part1 Test Result:", part1TestRes)
		}
		if !p.SkipMain {
			fmt.Println("Part1 Result:", p.P1Solver(p.inputFile()))
		}
	}
	fmt.Println()
	if p.P2Solver != nil {
		if !p.SkipTest2 {
			part2TestRes := p.P2Solver(p.test2File())
			if part2TestRes != p.Test2Expected {
				fmt.Printf("Error: Part2 Test Result: %v != %v\n", part2TestRes, p.Test2Expected)
			} else {
				fmt.Println("Part2 Test Result", part2TestRes)
			}
		}
		inputFile := p.inputFile()
		if p.Part2Input != "" {
			inputFile = p.Part2Input
		}
		if !p.SkipMain {
			fmt.Println("Part2 Result:", p.P2Solver(inputFile))
		}
	}
}

func (p AocProblem[T]) test1File() string {
	return fmt.Sprintf("%s/test1.txt", p.PackageName)
}

func (p AocProblem[T]) test2File() string {
	return fmt.Sprintf("%s/test2.txt", p.PackageName)
}

func (p AocProblem[T]) inputFile() string {
	return fmt.Sprintf("%s/input.txt", p.PackageName)
}
