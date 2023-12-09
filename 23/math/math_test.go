package math

import (
	"sander/aoc23/helpers"
	"testing"
)

func TestGCD(t *testing.T) {
	type args struct {
		a int
		b int
	}
	tests := []helpers.TestCase[int, args]{
		{"1, 1", args{1, 1}, 1},
		{"2, 2", args{2, 2}, 2},
		{"7, 11", args{7, 11}, 1},
		{"15, 21", args{15, 21}, 3},
		{"15, 25", args{15, 25}, 5},
	}
	for _, tt := range tests {
		t.Run(tt.Name, func(t *testing.T) {
			if got := GCD(tt.Args.a, tt.Args.b); got != tt.Want {
				t.Errorf("GCD() = %v, want %v", got, tt.Want)
			}
		})
	}
}

func TestLCM(t *testing.T) {
	type args []int
	tests := []helpers.TestCase[int, []int]{
		{"1, 1", []int{1, 1}, 1},
		{"1, 2", []int{1, 2}, 2},
		{"1, 2, 3", []int{1, 2, 3}, 6},
	}
	for _, tt := range tests {
		t.Run(tt.Name, func(t *testing.T) {
			if got := LCM(tt.Args...); got != tt.Want {
				t.Errorf("LCM() = %v, want %v", got, tt.Want)
			}
		})
	}
}
