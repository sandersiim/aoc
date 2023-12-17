package ds

func MapChars[R any](s string, mapper func(x uint8) R) []R {
	result := make([]R, len(s))
	for i, v := range s {
		result[i] = mapper(uint8(v))
	}
	return result
}

func ForEachCharIndex(s string, f func(i int, x uint8)) {
	for i, v := range s {
		f(i, uint8(v))
	}
}
