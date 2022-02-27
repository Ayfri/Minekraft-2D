plugins {
    kotlin("js") version "1.6.10"
}

group = "fr.ayfri"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
    maven("https://maven.pkg.jetbrains.space/public/p/kotlinx-html/maven")
    maven {
        url = uri("https://s1.oss.sonatype.org/content/repositories/releases")
    }
}

dependencies {
    implementation("org.jetbrains.kotlinx:kotlinx-html:0.7.3")
    implementation("io.github.ayfri:PIXI-Kotlin:0.2.2")
}

kotlin {
    js(IR) {
        useCommonJs()
        browser {
            commonWebpackConfig {
                devServer?.open = false
                cssSupport.enabled = true
            }
        }
        binaries.executable()
    }
}
