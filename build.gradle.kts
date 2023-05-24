import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    application
    kotlin("jvm") version "1.8.0"
}

group = "com.wire"
version = "0.0.1"

val mClass = "com.wire.ApplicationKt"
application {
    mainClass.set(mClass)
}

repositories {
    mavenCentral()
}

dependencies {
    // explicitly defined reflect library
    implementation(kotlin("reflect"))

    // common goodies
    implementation("dev.forst:katlib:2.2.6")

    // Ktor server dependencies
    implementation("io.ktor:ktor-server-core:2.2.2")
    implementation("io.ktor:ktor-server-netty:2.2.2")

    implementation("io.ktor:ktor-server-content-negotiation:2.2.2")
    implementation("io.ktor:ktor-server-default-headers:2.2.2")
    implementation("io.ktor:ktor-server-forwarded-header:2.2.2")
    implementation("io.ktor:ktor-server-status-pages:2.2.2")

    implementation("io.ktor:ktor-server-auth:2.2.2")
    implementation("io.ktor:ktor-server-auth-jwt:2.2.2")
    implementation("io.ktor:ktor-server-sessions:2.2.2")

    implementation("io.ktor:ktor-server-call-id:2.2.2")

    implementation("io.ktor:ktor-serialization-jackson:2.2.2")

    // OpenAPI / Swagger
    implementation("dev.forst:ktor-openapi-generator:0.5.5")

    // Jackson JSON
    implementation("com.fasterxml.jackson.core:jackson-databind:2.14.1")
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin:2.14.1")
    implementation("com.fasterxml.jackson.datatype:jackson-datatype-jsr310:2.14.1")

    // configuration loader
    implementation("com.sksamuel.hoplite:hoplite-core:2.7.0")
    implementation("com.sksamuel.hoplite:hoplite-yaml:2.7.0")

    // passwords hashing
    implementation("com.lambdaworks:scrypt:1.4.0")

    // logging
    implementation("io.github.microutils:kotlin-logging-jvm:3.0.4")
    implementation("ch.qos.logback:logback-classic:1.4.5")
    // if-else in logback.xml
    implementation("org.codehaus.janino:janino:3.1.9")

    // database
    implementation("org.jetbrains.exposed:exposed-core:0.41.1")
    implementation("org.jetbrains.exposed:exposed-dao:0.41.1")
    implementation("org.jetbrains.exposed:exposed-jdbc:0.41.1")
    implementation("org.jetbrains.exposed:exposed-java-time:0.41.1")
    // PostgreSQL driver
    implementation("org.postgresql:postgresql:42.5.1")
    // hikari database pool for optimal database connections
    implementation("com.zaxxer:HikariCP:5.0.1")
    // di.di framework
    implementation("org.flywaydb:flyway-core:9.19.0")

    // DI
    implementation("org.kodein.di:kodein-di-jvm:7.18.0")
    implementation("org.kodein.di:kodein-di-framework-ktor-server-jvm:7.18.0")

    // tests
    testImplementation(kotlin("test"))

    // Ktor
    testImplementation("io.ktor:ktor-server-test-host:2.2.2")
    testImplementation("io.ktor:ktor-client-json:2.2.2")
    testImplementation("io.ktor:ktor-client-content-negotiation:2.2.2")
    testImplementation("io.ktor:ktor-client-jackson:2.2.2")
    testImplementation("io.ktor:ktor-client-okhttp:2.2.2")

    // mocking
    testImplementation("io.mockk:mockk:1.13.3")

    // JUnit
    testImplementation("org.junit.jupiter:junit-jupiter-api:5.9.2")
    testImplementation("org.junit.jupiter:junit-jupiter-params:5.9.2")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:5.9.2")
}

tasks {
    // setup unit test platform
    withType<Test> {
        useJUnitPlatform()
        // enable parallel execution https://docs.gradle.org/current/userguide/performance.html#parallel_test_execution
        // maxParallelForks = (Runtime.getRuntime().availableProcessors() / 2).takeIf { it > 0 } ?: 1
    }

    // enforce all compilation targets to be JVM 17
    val jvmTargetVersion = "17"
    withType<KotlinCompile> {
        kotlinOptions {
            jvmTarget = jvmTargetVersion
        }
    }
    compileJava {
        targetCompatibility = jvmTargetVersion
    }
    compileKotlin {
        kotlinOptions.jvmTarget = jvmTargetVersion
    }
    compileTestJava {
        targetCompatibility = jvmTargetVersion
    }
    compileTestKotlin {
        kotlinOptions.jvmTarget = jvmTargetVersion
    }

    // setup compiling names
    distTar {
        archiveFileName.set("app.tar")
    }

    register<Jar>("fatJar") {
        manifest {
            attributes["Main-Class"] = mClass
        }
        duplicatesStrategy = DuplicatesStrategy.EXCLUDE
        archiveFileName.set("app.jar")
        from(configurations.runtimeClasspath.get().map { if (it.isDirectory) it else zipTree(it) })
        from(sourceSets.main.get().output)
    }

    // resolves all dependencies
    register("resolveDependencies") {
        doLast {
            project.allprojects.forEach { subProject ->
                with(subProject) {
                    buildscript.configurations.forEach { if (it.isCanBeResolved) it.resolve() }
                    configurations.compileClasspath.get().resolve()
                    configurations.testCompileClasspath.get().resolve()
                }
            }
        }
    }
}
