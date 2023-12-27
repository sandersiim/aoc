package helpers

import (
	"fmt"
	"log"
	"sander/aoc23/ds"
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

var AllDirections = []Direction{Up, Down, Left, Right}

func (c Coords) Next(direction Direction) Coords {
	return c.NextJump(direction, 1)
}

func (c Coords) NextJump(direction Direction, jumpAmount int) Coords {
	switch direction {
	case Up:
		return Coords{X: c.X, Y: c.Y - jumpAmount}
	case Down:
		return Coords{X: c.X, Y: c.Y + jumpAmount}
	case Left:
		return Coords{X: c.X - jumpAmount, Y: c.Y}
	case Right:
		return Coords{X: c.X + jumpAmount, Y: c.Y}
	}
	log.Fatalf("Unknown direction %v", uint8(direction))
	return Coords{}
}

func (c Coords) AdjacentNeighbours() []Coords {
	return []Coords{
		{X: c.X, Y: c.Y - 1},
		{X: c.X + 1, Y: c.Y},
		{X: c.X, Y: c.Y + 1},
		{X: c.X - 1, Y: c.Y},
	}
}

func (c Coords) AdjacentNeighboursWithBounds(boundsX int, boundsY int) []Coords {
	return ds.Filter(c.AdjacentNeighbours(), func(c Coords) bool {
		return !c.OutOfBounds(boundsX, boundsY)
	})
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

func (c Coords) OutOfBounds(boundsX int, boundsY int) bool {
	return c.X < 0 || c.Y < 0 || c.X >= boundsX || c.Y >= boundsY
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
