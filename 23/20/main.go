package main

import (
	"fmt"
	"log"
	"sander/aoc23/ds"
	"sander/aoc23/helpers"
	"sander/aoc23/math"
	"slices"
	"strings"
)

func main() {
	p := helpers.AocProblem[int]{
		PackageName:   "20",
		Test1Expected: 32000000,
		Test2Expected: 94,
		P1Solver:      nil,
		P2Solver:      part2,
		SkipMain:      false,
		SkipTest2:     true,
	}
	p.Solve()
}

type MachineType uint8

const (
	Broadcast   MachineType = iota
	FlipFlop    MachineType = '%'
	Conjunction MachineType = '&'
)

type Machine struct {
	machineType   MachineType
	label         string
	outputs       []*Machine
	flipFlopState bool
	inputs        map[string]int
}

type Pulse struct {
	value       int
	source      string
	destination string
}

func part1(filename string) int {
	machines := parseInputs(filename)
	totalNumOfPulses := []int{0, 0}
	for i := 0; i < 1000; i++ {
		numOfPulses, _ := playPulses(machines, []string{})
		totalNumOfPulses[0] += numOfPulses[0]
		totalNumOfPulses[1] += numOfPulses[1]
	}

	fmt.Println(totalNumOfPulses)

	return totalNumOfPulses[0] * totalNumOfPulses[1]
}

func part2(filename string) int {
	machines := parseInputs(filename)
	lsMachine := machines["ls"]
	var lsInputLabels []string
	for label := range lsMachine.inputs {
		lsInputLabels = append(lsInputLabels, label)
	}
	lsInputConjunctionIters := make([]int, 0)
	for i := 0; ; i++ {
		_, conjunctionsWithHigh := playPulses(machines, lsInputLabels)

		if len(conjunctionsWithHigh) > 0 {
			ds.ForEach(conjunctionsWithHigh, func(label string) {
				fmt.Println(label, i+1)
				lsInputConjunctionIters = append(lsInputConjunctionIters, i+1)
			})
		}
		if len(lsInputConjunctionIters) >= len(lsInputLabels) {
			break
		}
	}
	return math.LCM(lsInputConjunctionIters...)
}

func playPulses(machines map[string]*Machine, interestingLabels []string) ([]int, []string) {
	numOfPulses := []int{0, 0}
	pulses := []Pulse{{0, "button", "broadcaster"}}
	var machine *Machine
	var newPulses []Pulse
	var outputPulse int
	conjunctionsWithHigh := make([]string, 0, len(interestingLabels))

	for len(pulses) > 0 {
		//fmt.Println(pulses)
		newPulses = make([]Pulse, 0, 10)
		for _, pulse := range pulses {
			machine = machines[pulse.destination]
			if machine.machineType == Broadcast {
				for _, output := range machine.outputs {
					newPulses = append(newPulses, Pulse{pulse.value, machine.label, output.label})
				}
				numOfPulses[0] += 1
				numOfPulses[pulse.value] += len(machine.outputs)
			} else if machine.machineType == FlipFlop {
				if pulse.value == 0 {
					machine.flipFlopState = !machine.flipFlopState
					outputPulse = 0
					if machine.flipFlopState {
						outputPulse = 1
					}
					for _, output := range machine.outputs {
						newPulses = append(newPulses, Pulse{outputPulse, machine.label, output.label})
					}
					numOfPulses[outputPulse] += len(machine.outputs)
				}
			} else if machine.machineType == Conjunction {
				machine.inputs[pulse.source] = pulse.value
				outputPulse = 0
				//fmt.Println("Conjunction", machine.inputs)
				for _, inputPulse := range machine.inputs {
					if inputPulse == 0 {
						outputPulse = 1
						break
					}
				}
				if outputPulse == 1 && slices.Contains(interestingLabels, machine.label) {
					conjunctionsWithHigh = append(conjunctionsWithHigh, machine.label)
				}

				for _, output := range machine.outputs {
					newPulses = append(newPulses, Pulse{outputPulse, machine.label, output.label})
				}

				numOfPulses[outputPulse] += len(machine.outputs)
			} else {
				log.Fatalln("Unknown machine type: ", machine.machineType)
			}
		}

		pulses = newPulses
	}

	return numOfPulses, conjunctionsWithHigh
}

func parseInputs(filename string) map[string]*Machine {
	lines := helpers.ReadInputFile(filename)
	var machine *Machine
	machines := make(map[string]*Machine)
	ds.ForEach(lines, func(line string) {
		splits := strings.Split(line, " -> ")
		var label string
		if splits[0] == "broadcaster" {
			label = splits[0]
			machine = &Machine{
				machineType: Broadcast,
				label:       label,
			}
			machines[label] = machine
		} else if splits[0][0] == '%' {
			label = splits[0][1:]
			machine = &Machine{
				machineType: FlipFlop,
				label:       label,
			}
			machines[label] = machine
		} else if splits[0][0] == '&' {
			label = splits[0][1:]
			machine = &Machine{
				machineType: Conjunction,
				label:       label,
				inputs:      make(map[string]int),
			}
			machines[label] = machine
		} else {
			fmt.Println(line)
			log.Fatalln("Unknown machine type: ", splits[0])
		}
	})

	ds.ForEach(lines, func(line string) {
		splits := strings.Split(line, " -> ")
		inputLabel := splits[0]
		if inputLabel != "broadcaster" {
			inputLabel = inputLabel[1:]
		}
		machine = machines[inputLabel]
		outputLabels := strings.Split(splits[1], ", ")
		ds.ForEach(outputLabels, func(outputLabel string) {
			outputMachine, ok := machines[outputLabel]
			if !ok {
				outputMachine = &Machine{
					label:       outputLabel,
					machineType: FlipFlop,
				}
				machines[outputLabel] = outputMachine
			}
			machine.outputs = append(machine.outputs, outputMachine)
			if outputMachine.machineType == Conjunction {
				outputMachine.inputs[machine.label] = 0
			}
		})
	})

	return machines
}
