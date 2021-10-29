import com.google.protobuf.gradle.generateProtoTasks
import com.google.protobuf.gradle.id
import com.google.protobuf.gradle.ofSourceSet
import com.google.protobuf.gradle.plugins
import com.google.protobuf.gradle.protobuf
import com.google.protobuf.gradle.protoc
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

val grpcVersion = "1.34.0"
val grpcKotlinVersion = "1.0.0"
val protobufVersion = "3.14.0"
val coroutinesVersion = "1.4.2"
val pgvVersion = "0.4.1"

plugins {
    application
    idea
    kotlin("jvm") version "1.4.31"
    id("com.google.protobuf") version "0.8.14"
    id("org.jlleitschuh.gradle.ktlint") version "10.0.0"
}

group = "org.example"
version = "1.0-SNAPSHOT"

repositories {
    mavenLocal()
    google()
    mavenCentral()
    maven {
        url = uri("https://s3-us-west-2.amazonaws.com/dynamodb-local/release")
    }
}

dependencies {
    implementation(kotlin("stdlib-jdk8"))
    runtimeOnly("io.grpc:grpc-netty-shaded:$grpcVersion")
    implementation("javax.annotation:javax.annotation-api:1.3.2")
    implementation("com.google.protobuf:protobuf-java:3.11.4")
    implementation("com.google.protobuf:protobuf-java-util:3.11.4")
    implementation("io.grpc:grpc-kotlin-stub:$grpcKotlinVersion")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:$coroutinesVersion")
    implementation("org.jetbrains.kotlin:kotlin-reflect")
    implementation("org.jetbrains.kotlin:kotlin-stdlib")
    testImplementation("io.grpc:grpc-testing:$grpcKotlinVersion")
    implementation("io.envoyproxy.protoc-gen-validate:pgv-java-grpc:$pgvVersion")
}

idea {
    module {
        generatedSourceDirs.add(file("build/generated/source/proto/main/grpc"))
        generatedSourceDirs.add(file("build/generated/source/proto/main/grpckt"))
        generatedSourceDirs.add(file("build/generated/source/proto/main/java"))
    }
}

protobuf {
    protoc {
        artifact = "com.google.protobuf:protoc:$protobufVersion"
    }
    plugins {
        id("grpc") {
            artifact = "io.grpc:protoc-gen-grpc-java:$grpcVersion"
        }
        id("grpckt") {
            artifact = "io.grpc:protoc-gen-grpc-kotlin:$grpcKotlinVersion:jdk7@jar"
        }
        id("javapgv") {
            artifact = "io.envoyproxy.protoc-gen-validate:protoc-gen-validate:$pgvVersion"
        }
    }
    generateProtoTasks {
        ofSourceSet("main").forEach {
            it.plugins {
                id("grpc")
                id("grpckt")
                id("javapgv") {
                    option("lang=java")
                }
            }
        }
    }
}

java {
    sourceCompatibility = JavaVersion.VERSION_11
}

tasks.withType<KotlinCompile> {
    kotlinOptions.jvmTarget = "11"
}
