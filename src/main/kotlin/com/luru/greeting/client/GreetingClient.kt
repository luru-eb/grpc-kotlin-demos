package com.luru.greeting.client

import com.proto.greet.*
import io.grpc.ManagedChannel
import io.grpc.ManagedChannelBuilder
import kotlinx.coroutines.flow.flow
import java.util.concurrent.CountDownLatch

class GreetingClient {

    suspend fun run(port: Int) {
        val channel: ManagedChannel = ManagedChannelBuilder
            .forAddress("localhost", port)
            .usePlaintext()
            .build()

        //doUnaryCall(channel)
        doClientStreaming(channel)
    }

    private suspend fun doUnaryCall(channel: ManagedChannel) {
        println("============= UNARY CALL =============")

        val client = GreetServiceGrpcKt.GreetServiceCoroutineStub(channel)

        val greeting = Greeting.newBuilder()
            .setFirstName("Luis")
            .setLastName("Ruiz")
            .build()

        val request = GreetingRequest.newBuilder()
            .setGreeting(greeting)
            .build()

        val response = client.greet(request)

        println(response.result)
    }

    private suspend fun doClientStreaming(channel: ManagedChannel) {
        println("============= CLIENT STREAMING CALL =============")

        val client = GreetServiceGrpcKt.GreetServiceCoroutineStub(channel)
        val messages = client.longGreet(generateGreetingRequests())

        println(messages.result)
    }

    private fun generateGreetingRequests() = flow {
        val names = arrayListOf("Luis", "Natalia", "Paula", "Martina")
        names.forEach{ name ->
            val greeting = Greeting.newBuilder()
                .setFirstName(name)
                .build()
            val request = LongGreetRequest.newBuilder().setGreeting(greeting).build()
            emit(request)
        }
    }
}

suspend fun main() {
    val port = System.getenv("PORT")?.toInt() ?: 50051
    val greetingClient = GreetingClient()
    greetingClient.run(port)
}