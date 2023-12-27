package main

import (
	"sander/aoc23/ds"
	"sander/aoc23/helpers"
	"testing"
)

func TestSubTract(t *testing.T) {
	type args struct {
		union      XmasRangeUnion
		subtracted XmasRange
	}
	tests := []helpers.TestCase[XmasRangeUnion, args]{
		{
			"case",
			args{
				XmasRangeUnion{
					{
						xRange: ds.NewIntRange(1, 4000),
						mRange: ds.NewIntRange(1, 2233),
						aRange: ds.NewIntRange(3128, 3442),
						sRange: ds.NewIntRange(2673, 3029),
					},
				},
				XmasRange{
					xRange: ds.NewIntRange(1, 873),
					mRange: ds.NewIntRange(2150, 2598),
					aRange: ds.NewIntRange(3128, 3442),
					sRange: ds.NewIntRange(1, 4000),
				},
			},
			XmasRangeUnion{
				{
					xRange: ds.NewIntRange(874, 4000),
					mRange: ds.NewIntRange(1, 2233),
					aRange: ds.NewIntRange(3128, 3442),
					sRange: ds.NewIntRange(2673, 3029),
				},
				{
					xRange: ds.NewIntRange(1, 873),
					mRange: ds.NewIntRange(1, 2149),
					aRange: ds.NewIntRange(3128, 3442),
					sRange: ds.NewIntRange(2673, 3029),
				},
			},
		},
	}
	for _, tt := range tests {
		t.Run(tt.Name, func(t *testing.T) {
			if got := tt.Args.union.SubTract(tt.Args.subtracted); !testEq(got, tt.Want) {
				t.Errorf("SubTract() = %v, want %v", got, tt.Want)
			}
		})
	}
}

func testEq(a, b XmasRangeUnion) bool {
	if len(a) != len(b) {
		return false
	}
	for i := range a {
		if a[i] != b[i] {
			return false
		}
	}
	return true
}
