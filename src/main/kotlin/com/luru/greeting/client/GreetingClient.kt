package com.luru.greeting.client

import com.proto.greet.*
import io.grpc.Deadline
import io.grpc.ManagedChannel
import io.grpc.ManagedChannelBuilder
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.flow
import java.util.concurrent.TimeUnit

class GreetingClient {

    suspend fun run(port: Int) {
        val channel: ManagedChannel = ManagedChannelBuilder
            .forAddress("localhost", port)
            .usePlaintext()
            .build()

        //doUnaryCall(channel)
        //doClientStreaming(channel)
        //doServerStreaming(channel)
        //doBidirectionalStreaming(channel)
        doWithDeadline(channel)
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
        val messages = client.longGreet(generateLongGreetingRequests())

        println(messages.result)
    }

    private fun generateLongGreetingRequests() = flow {
        val names = arrayListOf("Luis", "Natalia", "Paula", "Martina")
        names.forEach{ name ->
            val greeting = Greeting.newBuilder()
                .setFirstName(name)
                .build()
            val request = LongGreetRequest.newBuilder().setGreeting(greeting).build()
            emit(request)
        }
    }

    private suspend fun doServerStreaming(channel: ManagedChannel) {
        println("============= SERVER STREAMING CALL =============")

        val client = GreetServiceGrpcKt.GreetServiceCoroutineStub(channel)
        val greeting = Greeting.newBuilder()
            .setFirstName("Luis")
            .setLastName("Ruiz")
            .build()
        val request = GreetManyTimesRequest.newBuilder().setGreeting(greeting).build()
        client.greetManyTimes(request)
            .collect { message -> println(message) }
    }
    
    private suspend fun doBidirectionalStreaming(channel: ManagedChannel) {
        println("============= BI-DIRECTIONAL STREAMING CALL =============")
        
        val client = GreetServiceGrpcKt.GreetServiceCoroutineStub(channel)
        client.greetEveryone(generateGreetEveryoneGreetingRequests())
            .collect {
                    message -> println(message)
            }
    }

    private fun generateGreetEveryoneGreetingRequests() = flow {
        val names = arrayListOf("Luis", "Natalia", "Paula", "Martina")
        names.forEach{ name ->
            val greeting = Greeting.newBuilder()
                .setFirstName(name)
                .build()
            val request = GreetEveryoneRequest.newBuilder()
                .apply { this.greeting = greeting }
                .build()
            emit(request)
            println("Greeting $name")
            delay(500)
        }
    }

    private suspend fun doWithDeadline(channel: ManagedChannel) {
        println("============= DEADLINE CALL =============")

        val client = GreetServiceGrpcKt.GreetServiceCoroutineStub(channel)
        val greeting = Greeting.newBuilder()
            .setFirstName("Luis")
            .build()
        val request = GreetWithDeadlineRequest.newBuilder()
            .setGreeting(greeting)
            .build()
        println("============= DEADLINE CALL 800ms =============")
        client
            .withDeadline(Deadline.after(800, TimeUnit.MILLISECONDS))
            .greetWithDeadline(request)
        println("============= DEADLINE CALL 100ms =============")
        client
            .withDeadline(Deadline.after(100, TimeUnit.MILLISECONDS))
            .greetWithDeadline(request)
    }
}

suspend fun main() {
    val port = System.getenv("PORT")?.toInt() ?: 50051
    val greetingClient = GreetingClient()
    greetingClient.run(port)
}