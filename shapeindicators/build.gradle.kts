plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.vanniktech.maven.publish)
    signing
}

android {
    namespace = "in.hridayan.shapeindicators"
    compileSdk = 36

    defaultConfig {
        minSdk = 24
        consumerProguardFiles("consumer-rules.pro")
    }

    buildFeatures { compose = true }
}

group = "io.github.DP-Hridayan"
version = "1.0.0"

mavenPublishing {
    publishToMavenCentral(com.vanniktech.maven.publish.SonatypeHost.DEFAULT)

    pom {
        name.set("ShapeIndicators")
        description.set("A Jetpack Compose library for pager indicators")
        url.set("https://github.com/DP-Hridayan/ShapeIndicators")
        licenses {
            license {
                name.set("MIT License")
                url.set("https://opensource.org/licenses/MIT")
            }
        }
        developers {
            developer {
                id.set("DP-Hridayan")
                name.set("Hridayan")
                email.set("hridayanofficial@gmail.com")
            }
        }
        scm {
            connection.set("scm:git:git://github.com/DP-Hridayan/ShapeIndicators.git")
            developerConnection.set("scm:git:ssh://github.com/DP-Hridayan/ShapeIndicators.git")
            url.set("https://github.com/DP-Hridayan/ShapeIndicators")
        }
    }

    signing {
        useGpgCmd()
    }
}