import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

group = "com.xunfos.tour"
version = "0.0.1"

plugins {
    kotlin("plugin.spring") version "1.4.31" apply false
    kotlin("jvm") version "1.4.31"
    id("org.springframework.boot") version "2.4.3" apply false
    id("io.spring.dependency-management") version "1.0.11.RELEASE" apply false
}

buildscript {
    repositories {
        mavenCentral()
    }
}
allprojects {
    repositories {
        mavenCentral()
        jcenter()
        maven { url = uri("https://maven.pkg.jetbrains.space/public/p/compose/dev") }
    }
}
subprojects {
    tasks.withType<KotlinCompile> {
        kotlinOptions.jvmTarget = "11"
    }
}
