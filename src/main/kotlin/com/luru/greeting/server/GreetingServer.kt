package com.luru.greeting.server

import io.grpc.Server
import io.grpc.ServerBuilder

class GreetingServer(private val port: Int) {
    val server: Server = ServerBuilder
        .forPort(port)
        .addService(GreetingService())
        .build()

    fun start() {
        server.start()
        println("Server started at port $port")

        Runtime.getRuntime().addShutdownHook(
            Thread {
                println("*** shutting down gRPC server ***")
                server.shutdown()
            }
        )

        server.awaitTermination()
    }
}

fun main() {
    val port = System.getenv("PORT")?.toInt() ?: 50051
    var server = GreetingServer(port)
    server.start()
}