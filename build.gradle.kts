import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    id("org.springframework.boot") version "2.7.0"
    id("io.spring.dependency-management") version "1.0.11.RELEASE"
    id("org.sonarqube") version "3.0"
    id("jacoco")
    kotlin("jvm") version "1.6.21"
    kotlin("plugin.spring") version "1.6.21"
}

group = "com.trendyol"
version = "0.0.1-SNAPSHOT"
java.sourceCompatibility = JavaVersion.VERSION_17

repositories {
    mavenCentral()
    maven { url = uri("REPLACEME/packages/maven") }
}

dependencies {
    implementation("org.springframework.boot:spring-boot-starter-actuator")
    implementation("org.springframework.boot:spring-boot-starter-webflux")
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin")
    implementation("io.projectreactor.kotlin:reactor-kotlin-extensions")
    implementation("com.couchbase.client:kotlin-client:1.0.0")
    implementation("org.springframework.boot:spring-boot-starter-validation")
    implementation("org.hibernate.validator:hibernate-validator")
    implementation("javax.validation:validation-api:2.0.1.Final")
    implementation("org.jetbrains.kotlin:kotlin-reflect")
    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8")
    implementation("com.trendyol:dynamic-configuration-processor:1.1.5")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-reactor")
    implementation("com.newrelic.agent.java:newrelic-api:7.6.0")
    implementation("net.logstash.logback:logstash-logback-encoder:7.1.1")
    implementation("io.springfox:springfox-swagger-ui:3.0.0")
    implementation("io.springfox:springfox-boot-starter:3.0.0")
    implementation("com.trendyol:kediatr-spring-starter:1.0.18")
    testImplementation("org.springframework.boot:spring-boot-starter-test")
    testImplementation("io.projectreactor:reactor-test")
    testImplementation("com.appmattus.fixture:fixture:1.2.0")
    testImplementation("org.mockito.kotlin:mockito-kotlin:4.0.0")
    annotationProcessor("org.springframework.boot:spring-boot-configuration-processor")
}

tasks.withType<KotlinCompile> {
    kotlinOptions {
        freeCompilerArgs = listOf("-Xjsr305=strict")
        jvmTarget = "17"
    }
}

tasks.withType<Test> {
    useJUnitPlatform()
}
sonarqube {
    properties {
        property("sonar.sources", "src/main/")
        property("sonar.tests", "src/test/")
        property(
            "sonar.exclusions",
            "**/model/**, **/dto/**, **/config/**, **/exception/**, **/entity/**,**/request/**,**/response/**, **/domain/**"
        )
    }
}

tasks.jacocoTestReport {
    reports {
        xml.required.set(true)
        csv.required.set(true)
        html.required.set(true)
    }
}