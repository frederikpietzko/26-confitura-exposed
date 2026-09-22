import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    alias(libs.plugins.kotlin.jvm)
    alias(libs.plugins.kotlin.spring)
    alias(libs.plugins.kotlin.power.assert)
    alias(libs.plugins.kotlin.serialization)
    alias(libs.plugins.spring.boot)
    alias(libs.plugins.spring.dependency.management)
    alias(libs.plugins.detekt)
}

group = "com.github.frederikpietzko"
version = "0.0.1-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("org.springframework.boot:spring-boot-starter-kotlinx-serialization-json")
    implementation("org.jetbrains.kotlin:kotlin-reflect")
    implementation(libs.exposed.spring.boot4.starter)
    implementation(libs.exposed.json)
    implementation(libs.exposed.money)

    runtimeOnly("com.h2database:h2")

    testImplementation("org.springframework.boot:spring-boot-starter-test")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")

    detektPlugins(libs.detekt.ktlint.wrapper)
}

kotlin {
    jvmToolchain(25)

    compilerOptions {
        jvmTarget = JvmTarget.JVM_25
        freeCompilerArgs.addAll(
            "-Xjsr305=strict",
            "-Wextra",
            "-Xcollection-literals",
            "-Xconsistent-data-class-copy-visibility",
            "-Xexplicit-context-arguments",
            "-Xname-based-destructuring=complete",
            "-Xreturn-value-checker=check",
            "-Xintrinsic-const-evaluation",
        )
    }
}

detekt {
    toolVersion = libs.versions.detekt.get()
    buildUponDefaultConfig = true
}

tasks.withType<dev.detekt.gradle.Detekt>().configureEach {
    reports {
        sarif.required.set(true)
        html.required.set(true)
    }
}

tasks.test {
    useJUnitPlatform()
}
