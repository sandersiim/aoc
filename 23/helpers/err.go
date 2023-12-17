package helpers

import "log"

func Fatality(err error) {
	if err != nil {
		log.Panic(err)
	}
}
