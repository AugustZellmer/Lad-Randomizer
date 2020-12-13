import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

val defaultProfile by extra("local")

plugins {
	id("org.springframework.boot") version "2.3.4.RELEASE"
	id("io.spring.dependency-management") version "1.0.10.RELEASE"
	id("com.google.cloud.tools.appengine") version "2.3.0"
	kotlin("jvm") version "1.4.10"
	kotlin("plugin.spring") version "1.4.10"
}

group = "me.augustzellmer.ladRandomizer"
version = "0.0.1-SNAPSHOT"
java.sourceCompatibility = JavaVersion.VERSION_11

repositories {
	mavenCentral()
}

dependencies {
	implementation("org.springframework.boot:spring-boot-starter-web")
	implementation("com.fasterxml.jackson.module:jackson-module-kotlin")
	implementation("org.jetbrains.kotlin:kotlin-reflect")
	implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8")
	implementation("org.slf4j:slf4j-api")
	implementation("org.slf4j:slf4j-jdk14")
	implementation("org.apache.commons:commons-lang3")
	implementation("org.springframework.boot:spring-boot-starter-data-jpa")
	implementation("org.springframework.cloud:spring-cloud-gcp-starter:1.2.5.RELEASE")
	implementation("org.springframework.cloud:spring-cloud-gcp-starter-sql-mysql:1.2.5.RELEASE")
	runtimeOnly("mysql:mysql-connector-java")
	testImplementation("org.springframework.boot:spring-boot-starter-test") {
		exclude(group = "org.junit.vintage", module = "junit-vintage-engine")
	}
}

configurations.all {
	exclude(group = "ch.qos.logback", module = "logback-classic")
}

tasks.withType<Test> {
	useJUnitPlatform()
}

tasks.withType<KotlinCompile> {
	kotlinOptions {
		freeCompilerArgs = listOf("-Xjsr305=strict")
		jvmTarget = "11"
	}
}

tasks.withType<Test>{
	doFirst {
		val props = systemProperties
		props.putIfAbsent("spring.profiles.active", defaultProfile)
	}
	useJUnitPlatform()
}

tasks.withType<org.springframework.boot.gradle.tasks.run.BootRun>{
	dependsOn("test")
	doFirst {
		val props = systemProperties
		props.putIfAbsent("spring.profiles.active", defaultProfile)
	}
}

tasks.withType<com.google.cloud.tools.gradle.appengine.core.DeployAllTask>{
	doFirst {
		appengine {
			stage{
				setAppEngineDirectory("src/main/appengine/prod")
			}
			deploy {
				projectId = "TODO"
				setAppEngineDirectory("src/main/appengine/prod")
			}
		}
	}
	dependsOn("test")
}
