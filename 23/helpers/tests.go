package helpers

type TestCase[R any, Args any] struct {
	Name string
	Args Args
	Want R
}
