package ds

func Any[T any](a []T, f func(x T) bool) bool {
	for _, v := range a {
		if f(v) {
			return true
		}
	}
	return false
}

func All[T any](a []T, f func(x T) bool) bool {
	for _, v := range a {
		if !f(v) {
			return false
		}
	}
	return true
}

func Map[T any, R any](a []T, mapper func(x T) R) []R {
	result := make([]R, len(a))
	for i, v := range a {
		result[i] = mapper(v)
	}
	return result
}

func Filter[T any](a []T, f func(x T) bool) []T {
	result := make([]T, 0, len(a))
	for _, v := range a {
		if f(v) {
			result = append(result, v)
		}
	}
	return result
}

func ForEach[T any](a []T, f func(x T)) {
	for _, v := range a {
		f(v)
	}
}

func ForEachGo[T any](a []T, f func(x T)) {
	for _, v := range a {
		go f(v)
	}
}

func ForEachIndex[T any](a []T, f func(x T, i int)) {
	for i, v := range a {
		f(v, i)
	}
}

func Count[T comparable](a []T) map[T]int {
	result := make(map[T]int, len(a)/2)
	for _, v := range a {
		_, ok := result[v]
		if !ok {
			result[v] = 1
		} else {
			result[v]++
		}
	}
	return result
}

func SumInt(a []int) int {
	result := 0
	for _, v := range a {
		result += v
	}
	return result
}

func SumIntBy[T any](a []T, f func(x T) int) int {
	result := 0
	for _, v := range a {
		result += f(v)
	}
	return result
}

func SumIntByIndex[T any](a []T, f func(x T, i int) int) int {
	result := 0
	for i, v := range a {
		result += f(v, i)
	}
	return result
}
