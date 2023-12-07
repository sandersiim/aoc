package ds

import "cmp"

func MapMaxValue[K comparable, E cmp.Ordered](m map[K]E) E {
	var mx E
	for _, v := range m {
		if v > mx {
			mx = v
		}
	}

	return mx
}
