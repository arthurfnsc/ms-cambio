package br.com.fiap.mba.mscambio

import kotlin.system.exitProcess
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.core.env.SimpleCommandLinePropertySource
import springfox.documentation.swagger2.annotations.EnableSwagger2

@EnableSwagger2
@SpringBootApplication
open class MsCambioApplication

@Suppress("SpreadOperator")
fun main(args: Array<String>) {

    val source = SimpleCommandLinePropertySource(*args)

    require(
        source.containsProperty("spring.profiles.active") ||
            System.getenv().containsKey("SPRING_PROFILES_ACTIVE")
    ) {
        System.err.println("No Profile set, exiting")
        exitProcess(0)
    }

    runApplication<MsCambioApplication>(*args)
}
