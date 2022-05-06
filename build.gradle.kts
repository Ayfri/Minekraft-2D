import groovy.json.JsonSlurper
import org.jetbrains.kotlin.gradle.targets.js.webpack.KotlinWebpackConfig.Mode
import org.jetbrains.kotlin.utils.addToStdlib.cast

object Project {
    const val pixiVersion = "0.5.0"
}

plugins {
    kotlin("js") version "1.6.21"
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
    implementation("io.github.ayfri:PIXI-Kotlin-pixi:${Project.pixiVersion}")
    implementation("io.github.ayfri:PIXI-Kotlin-events:${Project.pixiVersion}")
    implementation("io.github.ayfri:PIXI-Kotlin-math-extras:${Project.pixiVersion}")
    implementation("org.jetbrains.kotlin-wrappers:kotlin-extensions:1.0.1-pre.333")
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
                mode = Mode.DEVELOPMENT
                sourceMaps = true
                showProgress = true
            }
        }
        binaries.executable()
    }
}
