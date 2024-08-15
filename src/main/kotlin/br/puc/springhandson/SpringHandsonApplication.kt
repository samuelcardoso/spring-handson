package br.puc.springhandson

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class SpringHandsonApplication {}

fun main(args: Array<String>) {
	runApplication<SpringHandsonApplication>(*args)
}
