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

func ForEachKey[K comparable, E any](m map[K]E, f func(k K)) {
	for k := range m {
		f(k)
	}
}

func ForEachEntry[K comparable, E any](m map[K]E, f func(k K, v E)) {
	for k, v := range m {
		f(k, v)
	}
}
