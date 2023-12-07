plugins {
  kotlin("jvm") version "1.7.21"
  id("org.jlleitschuh.gradle.ktlint") version "10.2.0"
}

repositories {
  mavenCentral()
}

tasks {
  sourceSets {
    main {
      java.srcDirs("src")
    }
  }

//  withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile> {
//    kotlinOptions.freeCompilerArgs = listOf("-Xuse-k2")
//  }

  wrapper {
    gradleVersion = "7.3"
  }
}

dependencies {
  implementation("org.assertj:assertj-core:3.23.1")
}
