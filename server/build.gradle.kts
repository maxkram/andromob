plugins {
    kotlin("jvm") version "2.0.0"
    kotlin("plugin.serialization") version "2.0.0"
    application
}

group = "ru.school21"
version = "1.0.0"

repositories {
    mavenCentral()
}

sourceSets {
    main {
        kotlin.srcDir("main/kotlin")
        resources.srcDir("main/resources")
    }
}

dependencies {
    implementation(platform("io.ktor:ktor-bom:2.3.12"))
    implementation("io.ktor:ktor-server-core")
    implementation("io.ktor:ktor-server-netty")
    implementation("io.ktor:ktor-server-content-negotiation")
    implementation("io.ktor:ktor-serialization-kotlinx-json")
    implementation("io.ktor:ktor-serialization-gson")
    implementation("io.ktor:ktor-server-auth")

    implementation("org.jetbrains.exposed:exposed-core:0.50.1")
    implementation("org.jetbrains.exposed:exposed-jdbc:0.50.1")
    implementation("org.postgresql:postgresql:42.7.13")

    implementation("io.insert-koin:koin-ktor:3.5.6")
    implementation("io.insert-koin:koin-logger-slf4j:3.5.6")

    implementation("org.slf4j:slf4j-simple:2.0.13")
}

application {
    mainClass.set("io.ktor.server.netty.EngineMain")
}