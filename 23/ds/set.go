package ds

import (
	"fmt"
	"strings"
)

type Set[Key comparable] map[Key]struct{}

func (s Set[Key]) String() string {
	var sb strings.Builder
	sb.WriteString("Set(")
	for k := range s {
		sb.WriteString(fmt.Sprintf("%v ", k))
	}
	sb.WriteString(")")
	return sb.String()
}

func (s Set[Key]) Add(values ...Key) {
	for _, v := range values {
		s[v] = struct{}{}
	}
}

func (s Set[Key]) AddAll(sets ...Set[Key]) {
	for _, otherSet := range sets {
		for otherElem := range otherSet {
			s[otherElem] = struct{}{}
		}
	}
}

func (s Set[Key]) Has(value Key) bool {
	_, ok := s[value]
	return ok
}

func (s Set[Key]) Remove(value Key) bool {
	if s.Has(value) {
		delete(s, value)
		return true
	}
	return false
}

func (s Set[Key]) Any(f func(x Key) bool) bool {
	for k := range s {
		if f(k) {
			return true
		}
	}
	return false
}

func (s Set[Key]) ForEach(f func(x Key)) {
	for k := range s {
		f(k)
	}
}
