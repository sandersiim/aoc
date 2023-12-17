package day10

import (
	"sander/aoc23/ds"
	"sander/aoc23/helpers"
	"strings"
)

type Pipe uint8
type PipeRow []Pipe
type PipeMap []PipeRow

const (
	Empty Pipe = '.'
	Start Pipe = 'S'
	WE    Pipe = '-'
	NS    Pipe = '|'
	NE    Pipe = 'L'
	NW    Pipe = 'J'
	SW    Pipe = '7'
	SE    Pipe = 'F'
)

func (pipeMap PipeMap) StartCoords() helpers.Coords {
	for rowIndex, row := range pipeMap {
		for colIndex, pipe := range row {
			if pipe == Start {
				return helpers.Coords{X: colIndex, Y: rowIndex}
			}
		}
	}
	panic("Did not find S")
}

func (pipeMap PipeMap) ConnectingPipes(coords helpers.Coords, excluded helpers.Coords) []helpers.Coords {
	pipe := pipeMap[coords.Y][coords.X]
	connectingCoords := make([]helpers.Coords, 0, 2)
	var neighbour helpers.Coords
	var neighbourPipe Pipe
	// top neighbour
	if coords.Y > 0 && (pipe == NS || pipe == Start || pipe == NW || pipe == NE) {
		neighbour = helpers.Coords{X: coords.X, Y: coords.Y - 1}
		neighbourPipe = pipeMap[neighbour.Y][neighbour.X]
		if neighbour != excluded && (neighbourPipe == NS || neighbourPipe == Start || neighbourPipe == SW || neighbourPipe == SE) {
			connectingCoords = append(connectingCoords, neighbour)
		}
	}
	// bottom neighbour
	if coords.Y < len(pipeMap)-1 && (pipe == NS || pipe == Start || pipe == SW || pipe == SE) {
		neighbour = helpers.Coords{X: coords.X, Y: coords.Y + 1}
		neighbourPipe = pipeMap[neighbour.Y][neighbour.X]
		if neighbour != excluded && (neighbourPipe == NS || neighbourPipe == Start || neighbourPipe == NW || neighbourPipe == NE) {
			connectingCoords = append(connectingCoords, neighbour)
		}
	}
	// left neighbour
	if coords.X > 0 && (pipe == WE || pipe == Start || pipe == NW || pipe == SW) {
		neighbour = helpers.Coords{X: coords.X - 1, Y: coords.Y}
		neighbourPipe = pipeMap[neighbour.Y][neighbour.X]
		if neighbour != excluded && (neighbourPipe == WE || neighbourPipe == Start || neighbourPipe == NE || neighbourPipe == SE) {
			connectingCoords = append(connectingCoords, neighbour)
		}
	}
	// right neighbour
	if coords.X < len(pipeMap[0])-1 && (pipe == WE || pipe == Start || pipe == NE || pipe == SE) {
		neighbour = helpers.Coords{X: coords.X + 1, Y: coords.Y}
		neighbourPipe = pipeMap[neighbour.Y][neighbour.X]
		if neighbour != excluded && (neighbourPipe == WE || neighbourPipe == Start || neighbourPipe == NW || neighbourPipe == SW) {
			connectingCoords = append(connectingCoords, neighbour)
		}
	}
	return connectingCoords
}

func (pipeMap PipeMap) MainLine() ds.Set[helpers.Coords] {
	startCoords := pipeMap.StartCoords()
	previous := []helpers.Coords{startCoords, startCoords}
	currents := pipeMap.ConnectingPipes(startCoords, helpers.Coords{X: -1, Y: -1})
	result := make(ds.Set[helpers.Coords])
	result.Add(startCoords, currents[0], currents[1])

	var next helpers.Coords
	stop := false
	for !stop {
		for i := 0; i < 2; i++ {
			next = pipeMap.ConnectingPipes(currents[i], previous[i])[0]
			if result.Has(next) {
				stop = true
				break
			}
			result.Add(next)
			previous[i] = currents[i]
			currents[i] = next
		}
	}
	return result
}

func (pipes PipeRow) String() string {
	// concatenate all pipes into a string
	p := make([]byte, len(pipes))
	for i, pipe := range pipes {
		p[i] = byte(pipe)
	}
	return string(p)
}

func (pipeMap PipeMap) String() string {
	return strings.Join(
		ds.Map(pipeMap, func(row PipeRow) string {
			return row.String()
		}),
		"\n",
	)
}
