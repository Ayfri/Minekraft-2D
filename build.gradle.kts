
import groovy.json.JsonSlurper
import org.jetbrains.kotlin.utils.addToStdlib.cast

plugins {
    kotlin("js") version "1.6.10"
}

group = "fr.ayfri"

val json = rootProject.file("src/main/resources/properties.json")
version = JsonSlurper().parse(json).cast<Map<String, Any>>()["version"] as String

repositories {
    mavenCentral()
    maven("https://maven.pkg.jetbrains.space/public/p/kotlinx-html/maven")
    maven {
        url = uri("https://s1.oss.sonatype.org/content/repositories/releases")
    }
}

dependencies {
    implementation("org.jetbrains.kotlinx:kotlinx-html:0.7.3")
    implementation("io.github.ayfri:PIXI-Kotlin:0.4.0")
    implementation(npm("@pixi/filter-outline", "4.1.5"))
    implementation(npm("@pixi/tilemap", "3.2.1"))
    implementation(npm("pixi-viewport", "4.34.4"))
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
