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

    implementation(libs.ksmt.core)
    implementation(libs.ksmt.z3)

    implementation(libs.kool.core)
    implementation(libs.kool.physics)
}

kotlin {
    jvmToolchain(21)
}

allOpen {
    annotation("org.openjdk.jmh.annotations.State")
}

benchmark {
    configurations {
        named("main") {
            warmups = 3
            iterations = 5
            iterationTime = 1000
            iterationTimeUnit = "millis"
            include("y2023.*Day25")
        }
    }
    targets {
        register("main") { }
    }
}