package helpers

import "fmt"

type AocProblem[T comparable] struct {
	PackageName   string
	Test1Expected T
	Test2Expected T
	Test1Solver   func(string) T
	Test2Solver   func(string) T
	P1Solver      func(string) T
	P2Solver      func(string) T
	Part2Input    string
	SkipMain      bool
	SkipTest2     bool
}

func (p AocProblem[T]) Solve() {
	test1Solver := p.Test1Solver
	if test1Solver == nil {
		test1Solver = p.P1Solver
	}
	if test1Solver != nil {
		part1TestRes := test1Solver(p.test1File())
		if part1TestRes != p.Test1Expected {
			fmt.Printf("Error: Part1 Test Result: %v != %v\n", part1TestRes, p.Test1Expected)
		} else {
			fmt.Println("Part1 Test Result:", part1TestRes)
		}
	}
	if !p.SkipMain && p.P1Solver != nil {
		fmt.Println("Part1 Result:", p.P1Solver(p.inputFile()))
	}
	fmt.Println()

	test2Solver := p.Test2Solver
	if test2Solver == nil {
		test2Solver = p.P2Solver
	}
	if !p.SkipTest2 && test2Solver != nil {
		part2TestRes := test2Solver(p.test2File())
		if part2TestRes != p.Test2Expected {
			fmt.Printf("Error: Part2 Test Result: %v != %v\n", part2TestRes, p.Test2Expected)
		} else {
			fmt.Println("Part2 Test Result", part2TestRes)
		}
	}

	if p.P2Solver != nil && !p.SkipMain {
		inputFile := p.inputFile()
		if p.Part2Input != "" {
			inputFile = p.Part2Input
		}
		fmt.Println("Part2 Result:", p.P2Solver(inputFile))
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
