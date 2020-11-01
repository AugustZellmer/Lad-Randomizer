package me.augustzellmer.ladRandomizer.backend

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class LadRandomizerBackendApplication

fun main(args: Array<String>) {
	runApplication<LadRandomizerBackendApplication>(*args)
}
