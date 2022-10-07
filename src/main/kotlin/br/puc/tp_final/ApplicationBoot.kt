package br.puc.tp_final

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class ApplicationBoot {}

fun main(args: Array<String>) {
    runApplication<ApplicationBoot>(*args)
}