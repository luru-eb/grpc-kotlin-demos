package com.luru.greeting.server

import com.proto.greet.*
import io.grpc.Context
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.flow

class GreetingService : GreetServiceGrpcKt.GreetServiceCoroutineImplBase() {

    override suspend fun greet(request: GreetingRequest): GreetingResponse {
        val message = "Hello ${request.greeting.firstName} ${request.greeting.lastName} from gRPC"

        return GreetingResponse
            .newBuilder()
            .setResult(message)
            .build()
    }

    override suspend fun longGreet(requests: Flow<LongGreetRequest>): LongGreetResponse {
        var result = ""
        requests.collect { request ->
            result += "Hello ${request.greeting.firstName} ${request.greeting.lastName} ${System.lineSeparator()}"
        }

        return LongGreetResponse.newBuilder().apply {
            this.result = result
        }.build()
    }

    override fun greetManyTimes(request: GreetManyTimesRequest): Flow<GreetManyTimeResponse> {
        return flow {
            for (i in 1..10) {
                val message = "Hello ${request.greeting.firstName} ${request.greeting.lastName} from gRPC $i"
                val response = GreetManyTimeResponse.newBuilder()
                    .setResult(message)
                    .build()
                emit(response)
            }
        }
    }

    override fun greetEveryone(requests: Flow<GreetEveryoneRequest>): Flow<GreetEveryoneResponse> {
        return flow {
            requests.collect { request ->
                val message = "Hello ${request.greeting.firstName} from gRPC"
                val response = GreetEveryoneResponse.newBuilder()
                    .setResult(message)
                    .build()
                emit(response)
            }
        }
    }

    override suspend fun greetWithDeadline(request: GreetWithDeadlineRequest): GreetWithDeadlineResponse {
        val context = Context.current()

        if (context.isCancelled) {
            return GreetWithDeadlineResponse.newBuilder().build()
        }

        delay(100)

        return GreetWithDeadlineResponse.newBuilder()
            .apply { this.result = "Hello ${request.greeting.firstName} from gRPC"}
            .build()
    }
}