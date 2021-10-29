package com.luru.greeting.server

import com.proto.greet.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collect

class GreetingService : GreetServiceGrpcKt.GreetServiceCoroutineImplBase() {

    override suspend fun greet(request: GreetingRequest): GreetingResponse {
        var message = "Hello ${request.greeting.firstName} ${request.greeting.lastName} from gRPC"

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
}