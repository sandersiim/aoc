package helpers

import (
	"fmt"
	"log"
)

type Coords struct {
	X int
	Y int
}

type Direction uint8

const (
	Up    Direction = '^'
	Down  Direction = 'v'
	Left  Direction = '<'
	Right Direction = '>'
)

func (c Coords) Next(direction Direction) Coords {
	switch direction {
	case Up:
		return Coords{X: c.X, Y: c.Y - 1}
	case Down:
		return Coords{X: c.X, Y: c.Y + 1}
	case Left:
		return Coords{X: c.X - 1, Y: c.Y}
	case Right:
		return Coords{X: c.X + 1, Y: c.Y}
	}
	log.Fatalf("Unknown direction %v", uint8(direction))
	return Coords{}
}

func (c Coords) AdjacentNeighbours() []Coords {
	return []Coords{
		{X: c.X, Y: c.Y - 1},
		{X: c.X + 1, Y: c.Y},
		{X: c.X, Y: c.Y - 1},
		{X: c.X - 1, Y: c.Y},
	}
}

func (c Coords) AllNeighbours() []Coords {
	return []Coords{
		{X: c.X, Y: c.Y - 1},
		{X: c.X + 1, Y: c.Y - 1},
		{X: c.X + 1, Y: c.Y},
		{X: c.X + 1, Y: c.Y + 1},
		{X: c.X, Y: c.Y + 1},
		{X: c.X - 1, Y: c.Y + 1},
		{X: c.X - 1, Y: c.Y},
		{X: c.X - 1, Y: c.Y - 1},
	}
}

func (c Coords) String() string {
	return fmt.Sprintf("(y:%v, x:%v)", c.Y, c.X)
}

func (d Direction) String() string {
	switch d {
	case Right:
		return ">"
	case Left:
		return "<"
	case Up:
		return "^"
	case Down:
		return "v"
	default:
		panic(fmt.Sprintf("unknown direction: %v", uint8(d)))
	}
}
