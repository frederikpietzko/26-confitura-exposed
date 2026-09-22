import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    alias(libs.plugins.kotlin.jvm)
    alias(libs.plugins.kotlin.spring)
    alias(libs.plugins.kotlin.power.assert)
    alias(libs.plugins.kotlin.serialization)
    alias(libs.plugins.exposed.plugin)
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
    implementation("org.springframework.boot:spring-boot-starter-flyway")
    implementation("org.springframework.boot:spring-boot-starter-kotlinx-serialization-json")
    implementation("org.jetbrains.kotlin:kotlin-reflect")
    implementation(libs.exposed.spring.boot4.starter)
    implementation(libs.exposed.json)
    implementation(libs.exposed.money)

    runtimeOnly("org.postgresql:postgresql")
    runtimeOnly("org.flywaydb:flyway-database-postgresql")
    runtimeOnly(libs.moneta)

    testImplementation("org.springframework.boot:spring-boot-starter-test")
    testImplementation("org.springframework.boot:spring-boot-testcontainers")
    testImplementation("org.testcontainers:testcontainers-postgresql")
    testImplementation(kotlin("test"))
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")

    detektPlugins(libs.detekt.ktlint.wrapper)
}

exposed {
    migrations {
        tablesPackage.set("com.github.frederikpietzko.demo.taxi.tables")
        // the plugin spins up a throwaway Postgres, replays the committed migrations with Flyway
        // and diffs the table objects against the result
        testContainersImageName.set("postgres:18-alpine")
        fileDirectory.set(layout.projectDirectory.dir("src/main/resources/db/migration"))
    }
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
    config.setFrom(layout.projectDirectory.file("config/detekt/detekt.yml"))
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
