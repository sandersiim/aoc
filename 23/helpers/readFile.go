package helpers

import (
	"bufio"
	"log"
	"os"
)

func ReadInputFile(filename string) (lines []string) {
	file, err := os.Open(filename)
	if err != nil {
		log.Fatal(err)
	}
	defer func(file *os.File) {
		err := file.Close()
		if err != nil {
			log.Fatal(err)
		}
	}(file)

	scanner := bufio.NewScanner(file)
	lines = make([]string, 0, 100)
	// optionally, resize scanner's capacity for lines over 64K
	for scanner.Scan() {
		lines = append(lines, scanner.Text())
	}

	if err := scanner.Err(); err != nil {
		log.Fatal(err)
	}

	return
}
