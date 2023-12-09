package helpers

func DereferencePtrSlice[T any](a []*T) []T {
	result := make([]T, len(a))
	for i, v := range a {
		result[i] = *v
	}
	return result
}
