package main

import (
	"fmt"
	"log"
	"regexp"
	"sander/aoc23/ds"
	"sander/aoc23/helpers"
	"strings"
)

func main() {
	p := helpers.AocProblem[int]{
		PackageName:   "19",
		Test1Expected: 19114,
		Test2Expected: 167409079868000,
		P1Solver:      part1,
		P2Solver:      part2,
	}
	p.Solve()
}

type Item struct {
	x int
	m int
	a int
	s int
}

func (item Item) getValue(variable uint8) int {
	switch variable {
	case 'x':
		return item.x
	case 'm':
		return item.m
	case 'a':
		return item.a
	case 's':
		return item.s
	}
	log.Fatalln("Unknown variable", variable)
	return -1
}

func (item Item) sum() int {
	return item.a + item.s + item.m + item.x
}

type Rule interface {
	Apply(Item) string
	Output() string
	GetApplicableRange() XmasRange
}

type UnconditionalRule struct {
	output string
}

type Operation uint8

const (
	Lt Operation = '<'
	Gt Operation = '>'
)

type LtGtRule struct {
	variable   uint8
	limitValue int
	operation  Operation
	output     string
}

func (rule UnconditionalRule) Output() string {
	return rule.output
}

func (rule UnconditionalRule) Apply(_ Item) string {
	return rule.Output()
}

func (rule UnconditionalRule) GetApplicableRange() XmasRange {
	return FullXmasRange()
}

func (rule LtGtRule) Output() string {
	return rule.output
}

func (rule LtGtRule) Apply(item Item) string {
	if rule.operation == Lt {
		if item.getValue(rule.variable) < rule.limitValue {
			return rule.output
		}
	} else {
		if item.getValue(rule.variable) > rule.limitValue {
			return rule.output
		}
	}
	return "NEXT"
}

var fullIntRange = ds.NewIntRange(1, 4000)

func (rule LtGtRule) GetApplicableRange() XmasRange {
	switch rule.variable {
	case 'x':
		if rule.operation == Lt {
			return XmasRange{
				xRange: ds.NewIntRange(1, rule.limitValue-1),
				mRange: fullIntRange,
				aRange: fullIntRange,
				sRange: fullIntRange,
			}
		} else {
			return XmasRange{
				xRange: ds.NewIntRange(rule.limitValue+1, 4000),
				mRange: fullIntRange,
				aRange: fullIntRange,
				sRange: fullIntRange,
			}
		}
	case 'm':
		if rule.operation == Lt {
			return XmasRange{
				xRange: fullIntRange,
				mRange: ds.NewIntRange(1, rule.limitValue-1),
				aRange: fullIntRange,
				sRange: fullIntRange,
			}
		} else {
			return XmasRange{
				xRange: fullIntRange,
				mRange: ds.NewIntRange(rule.limitValue+1, 4000),
				aRange: fullIntRange,
				sRange: fullIntRange,
			}
		}
	case 'a':
		if rule.operation == Lt {
			return XmasRange{
				xRange: fullIntRange,
				mRange: fullIntRange,
				aRange: ds.NewIntRange(1, rule.limitValue-1),
				sRange: fullIntRange,
			}
		} else {
			return XmasRange{
				xRange: fullIntRange,
				mRange: fullIntRange,
				aRange: ds.NewIntRange(rule.limitValue+1, 4000),
				sRange: fullIntRange,
			}
		}
	case 's':
		if rule.operation == Lt {
			return XmasRange{
				xRange: fullIntRange,
				mRange: fullIntRange,
				aRange: fullIntRange,
				sRange: ds.NewIntRange(1, rule.limitValue-1),
			}
		} else {
			return XmasRange{
				xRange: fullIntRange,
				mRange: fullIntRange,
				aRange: fullIntRange,
				sRange: ds.NewIntRange(rule.limitValue+1, 4000),
			}
		}
	}
	log.Fatalln("Unknown variable", rule.variable)
	return XmasRange{}
}

type Workflow struct {
	name  string
	rules []Rule
}

func (w Workflow) Apply(item Item) string {
	for _, rule := range w.rules {
		result := rule.Apply(item)
		if result != "NEXT" {
			return result
		}
	}
	log.Fatalln("No rule applied")
	return ""
}

func part1(filename string) int {
	workflows, items := parseInputs(filename)

	sortItem := func(item Item) string {
		result := ""
		currentWorkflow := workflows["in"]
		var output string
		for result == "" {
			output = currentWorkflow.Apply(item)
			if output == "A" || output == "R" {
				result = output
			} else {
				currentWorkflow = workflows[output]
			}
		}
		return result
	}

	acceptedParts := make([]Item, 0, len(items))

	for _, item := range items {
		itemResult := sortItem(item)
		if itemResult == "A" {
			acceptedParts = append(acceptedParts, item)
		}
	}

	sum := 0
	for _, item := range acceptedParts {
		sum += item.sum()
	}

	return sum
}

type XmasRange struct {
	xRange ds.IntRange
	mRange ds.IntRange
	aRange ds.IntRange
	sRange ds.IntRange
}

type XmasRangeUnion []XmasRange

func (r XmasRange) Overlaps(r2 XmasRange) bool {
	return r.xRange.Overlaps(r2.xRange) && r.mRange.Overlaps(r2.mRange) && r.aRange.Overlaps(r2.aRange) && r.sRange.Overlaps(r2.sRange)
}

func (r XmasRange) IsEmpty() bool {
	return r.xRange.IsEmpty() || r.mRange.IsEmpty() || r.aRange.IsEmpty() || r.sRange.IsEmpty()
}

func (r XmasRange) SubTract(other XmasRange) XmasRangeUnion {
	if !r.Overlaps(other) {
		return XmasRangeUnion{r}
	}
	result := make(XmasRangeUnion, 0, 2)

	if r.xRange.Min < other.xRange.Min {
		result = append(result, XmasRange{ds.NewIntRange(r.xRange.Min, other.xRange.Min-1), r.mRange, r.aRange, r.sRange})
	}
	if other.xRange.Max < r.xRange.Max {
		result = append(result, XmasRange{ds.NewIntRange(other.xRange.Max+1, r.xRange.Max), r.mRange, r.aRange, r.sRange})
	}

	xIntersected := XmasRange{r.xRange.Intersection(other.xRange), r.mRange, r.aRange, r.sRange}

	if r.mRange.Min < other.mRange.Min {
		result = append(result, XmasRange{xIntersected.xRange, ds.NewIntRange(r.mRange.Min, other.mRange.Min-1), r.aRange, r.sRange})
	}
	if other.mRange.Max < r.mRange.Max {
		result = append(result, XmasRange{xIntersected.xRange, ds.NewIntRange(other.mRange.Max+1, r.mRange.Max), r.aRange, r.sRange})
	}

	xmIntersected := XmasRange{xIntersected.xRange, r.mRange.Intersection(other.mRange), r.aRange, r.sRange}

	if r.aRange.Min < other.aRange.Min {
		result = append(result, XmasRange{xmIntersected.xRange, xmIntersected.mRange, ds.NewIntRange(r.aRange.Min, other.aRange.Min-1), r.sRange})
	}
	if other.aRange.Max < r.aRange.Max {
		result = append(result, XmasRange{xmIntersected.xRange, xmIntersected.mRange, ds.NewIntRange(other.aRange.Max+1, r.aRange.Max), r.sRange})
	}

	xmaIntersected := XmasRange{xmIntersected.xRange, xmIntersected.mRange, r.aRange.Intersection(other.aRange), r.sRange}

	if r.sRange.Min < other.sRange.Min {
		result = append(result, XmasRange{xmaIntersected.xRange, xmaIntersected.mRange, xmaIntersected.aRange, ds.NewIntRange(r.sRange.Min, other.sRange.Min-1)})
	}
	if other.sRange.Max < r.sRange.Max {
		result = append(result, XmasRange{xmaIntersected.xRange, xmaIntersected.mRange, xmaIntersected.aRange, ds.NewIntRange(other.sRange.Max+1, r.sRange.Max)})
	}

	return result
}

func (r XmasRange) Intersection(other XmasRange) XmasRange {
	return XmasRange{
		xRange: r.xRange.Intersection(other.xRange),
		mRange: r.mRange.Intersection(other.mRange),
		aRange: r.aRange.Intersection(other.aRange),
		sRange: r.sRange.Intersection(other.sRange),
	}
}

func (u XmasRangeUnion) Intersection(other XmasRangeUnion) XmasRangeUnion {
	newUnion := make(XmasRangeUnion, 0, len(u))
	var intersection XmasRange
	for _, currentRange := range u {
		for _, otherRange := range other {
			intersection = currentRange.Intersection(otherRange)
			if !intersection.IsEmpty() {
				newUnion = append(newUnion, intersection)
			}
		}
	}

	return newUnion
}

func (u XmasRangeUnion) SubTract(r XmasRange) XmasRangeUnion {
	newUnion := make(XmasRangeUnion, 0, len(u))
	for _, currentRange := range u {
		newUnion = append(newUnion, currentRange.SubTract(r)...)
	}

	return newUnion
}

func (u XmasRangeUnion) AddRange(other XmasRange) XmasRangeUnion {
	newUnion := make(XmasRangeUnion, 0, len(u))
	newUnion = append(newUnion, u...)
	newAddedRanges := XmasRangeUnion{other}
	for _, currentRange := range u {
		newAddedRanges = newAddedRanges.SubTract(currentRange)
	}
	newUnion = append(newUnion, newAddedRanges...)

	return newUnion
}

func (u XmasRangeUnion) Add(other XmasRangeUnion) XmasRangeUnion {
	newUnion := make(XmasRangeUnion, 0, len(u))
	newUnion = append(newUnion, u...)
	for _, addedRange := range other {
		newUnion = newUnion.AddRange(addedRange)
	}

	return newUnion
}

func (w Workflow) findRangesWithOutput(allWorkflows map[string]*Workflow, targetOutput string) XmasRangeUnion {
	result := make(XmasRangeUnion, 0, len(w.rules))
	//fmt.Println("Searching for output", targetOutput, "in workflow", w.name)
	currentInputRange := XmasRangeUnion{FullXmasRange()}
	for _, rule := range w.rules {
		currentRuleInputRange := currentInputRange.Intersection(XmasRangeUnion{rule.GetApplicableRange()})
		if len(currentRuleInputRange) > 0 {
			if rule.Output() == targetOutput {
				result = result.Add(currentRuleInputRange)
			} else if rule.Output() != "A" && rule.Output() != "R" {
				nextFlow := allWorkflows[rule.Output()]
				// find rules from other workflows that have this rule as output
				otherRanges := nextFlow.findRangesWithOutput(allWorkflows, targetOutput)
				result = result.Add(currentRuleInputRange.Intersection(otherRanges))
			}
		} else {
			fmt.Println("No input range matches rule", rule, "skipping")
		}

		currentInputRange = currentInputRange.SubTract(rule.GetApplicableRange())
	}
	//fmt.Println("Found target in ranges", result)

	return result
}

func FullXmasRange() XmasRange {
	return XmasRange{
		xRange: fullIntRange,
		mRange: fullIntRange,
		aRange: fullIntRange,
		sRange: fullIntRange,
	}
}

func part2(filename string) int {
	workflows, _ := parseInputs(filename)
	acceptedRanges := workflows["in"].findRangesWithOutput(workflows, "A")

	sum := 0
	fmt.Println("result range", acceptedRanges)

	for _, r := range acceptedRanges {
		sum += (r.xRange.Max - r.xRange.Min + 1) * (r.mRange.Max - r.mRange.Min + 1) * (r.aRange.Max - r.aRange.Min + 1) * (r.sRange.Max - r.sRange.Min + 1)
	}

	return sum
}

func parseInputs(filename string) (map[string]*Workflow, []Item) {
	lines := helpers.ReadInputFile(filename)
	workflows := make(map[string]*Workflow)
	items := make([]Item, 0)
	lineIndex := 0
	for ; ; lineIndex++ {
		line := lines[lineIndex]
		if line != "" {
			newWorkflow := parseWorkflow(line)
			workflows[newWorkflow.name] = &newWorkflow
		} else {
			break
		}
	}
	lineIndex++
	for ; lineIndex < len(lines); lineIndex++ {
		items = append(items, parseItem(lines[lineIndex]))
	}

	return workflows, items
}

func parseWorkflow(line string) Workflow {
	splits := strings.Split(line, "{")
	name := splits[0]
	ruleStrings := strings.Split(strings.Replace(splits[1], "}", "", 1), ",")
	rules := ds.Map(ruleStrings, parseRule)
	return Workflow{
		name:  name,
		rules: rules,
	}
}

func parseRule(line string) Rule {
	if strings.Contains(line, ":") {
		splits := strings.Split(line, ":")

		var op Operation
		var ruleSplits []string
		if strings.Contains(splits[0], "<") {
			op = Lt
			ruleSplits = strings.Split(splits[0], "<")
		} else {
			op = Gt
			ruleSplits = strings.Split(splits[0], ">")
		}
		variable := ruleSplits[0][0]
		limitValue := helpers.ParseInt(ruleSplits[1])

		return LtGtRule{
			output:     splits[1],
			operation:  op,
			variable:   variable,
			limitValue: limitValue,
		}
	} else {
		return UnconditionalRule{line}
	}
}

func parseItem(line string) Item {
	re := regexp.MustCompile("x=(\\d+),m=(\\d+),a=(\\d+),s=(\\d+)")
	matches := re.FindStringSubmatch(line)
	return Item{
		x: helpers.ParseInt(matches[1]),
		m: helpers.ParseInt(matches[2]),
		a: helpers.ParseInt(matches[3]),
		s: helpers.ParseInt(matches[4]),
	}
}
