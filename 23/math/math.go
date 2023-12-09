package math

func GCD(a, b int) int {
	var t int
	for b != 0 {
		t = b
		b = a % b
		a = t
	}
	return a
}

func LCM(integers ...int) int {
	if len(integers) < 2 {
		panic("LCM needs at least 2 integers")
	}
	result := integers[0] * integers[1] / GCD(integers[0], integers[1])

	for i := 2; i < len(integers); i++ {
		result = LCM(result, integers[i])
	}

	return result
}
