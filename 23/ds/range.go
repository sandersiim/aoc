package ds

import "fmt"

type IntRange struct {
	Min int
	Max int
}

type IntRangeUnion []IntRange

func NewIntRange(min, max int) IntRange {
	if min > max {
		panic(fmt.Sprintf("Can't create a range with min=%v > max=%v", min, max))
	}
	return IntRange{min, max}
}

func (r IntRange) Contains(n int) bool {
	return n >= r.Min && n <= r.Max
}

func (r IntRange) IsEmpty() bool {
	return r.Min > r.Max
}

func (r IntRange) Overlaps(other IntRange) bool {
	if r.IsEmpty() || other.IsEmpty() {
		return false
	}
	return r.Contains(other.Min) || r.Contains(other.Max) || other.Contains(r.Min) || other.Contains(r.Max)
}

func (r IntRange) Intersection(other IntRange) IntRange {
	return IntRange{max(r.Min, other.Min), min(r.Max, other.Max)}
}

func (r IntRange) SubTract(other IntRange) IntRangeUnion {
	if !r.Overlaps(other) {
		return IntRangeUnion{r}
	}
	if r.Min < other.Min && r.Max > other.Max {
		return IntRangeUnion{NewIntRange(r.Min, other.Min-1), NewIntRange(other.Max+1, r.Max)}
	}
	if r.Min < other.Min {
		return IntRangeUnion{NewIntRange(r.Min, other.Min-1)}
	}
	if r.Max > other.Max {
		return IntRangeUnion{NewIntRange(other.Max+1, r.Max)}
	}
	return IntRangeUnion{}
}

func (rangeUnion IntRangeUnion) IsEmpty() bool {
	return len(rangeUnion) == 0
}

func (rangeUnion IntRangeUnion) AllValues() []int {
	if rangeUnion.IsEmpty() {
		return []int{}
	}
	result := make([]int, 0, len(rangeUnion)*(rangeUnion[0].Max-rangeUnion[0].Min+1))
	for _, r := range rangeUnion {
		for i := r.Min; i <= r.Max; i++ {
			result = append(result, i)
		}
	}
	return result
}
