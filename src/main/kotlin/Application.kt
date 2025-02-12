package stapi

import io.ktor.server.application.*

fun main(args: Array<String>) {

    io.ktor.server.netty.EngineMain.main(args)

}

@Suppress("unused")
fun Application.module() {
    configureSerialization()
    configureStatusPages()
    configureSecurity()
    configureValidation()
    configureDatabases()
    configureHTTP()
    configureMonitoring()
    // configureSockets()
    //configureMigrations()
}
