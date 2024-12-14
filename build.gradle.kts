plugins {
    alias(libs.plugins.kotlin.jvm)
    alias(libs.plugins.kotlin.benchmark)
    alias(libs.plugins.kotlin.allopen)
}

repositories {
    maven { url = uri("https://oss.sonatype.org/content/repositories/snapshots") }
    mavenCentral()
}

dependencies {
    implementation(libs.kotlin.coroutines)
    implementation(libs.kotlin.benchmark)
    implementation(libs.kotlin.serialization.core)
    implementation(libs.kotlin.serialization.json)
    implementation(libs.commons.math    )

    implementation(libs.ksmt.core)
    implementation(libs.ksmt.z3)

    implementation(libs.kool.core)
    implementation(libs.kool.physics)
}

kotlin {
    jvmToolchain(21)
    compilerOptions {
        freeCompilerArgs.add("-Xwhen-guards")
    }
}

allOpen {
    annotation("org.openjdk.jmh.annotations.State")
}

benchmark {
    configurations {
        named("main") {
            warmups = 5
            iterations = 5
            iterationTime = 1000
            iterationTimeUnit = "millis"
            outputTimeUnit = "millis"
            //mode = "thrpt"
            include("y2024.*DefaultBenchmark")
        }
    }
    targets {
        register("main") { }
    }
}