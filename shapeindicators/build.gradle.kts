plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.vanniktech.maven.publish)
}

android {
    namespace = "in.hridayan.shapeindicators"
    compileSdk = 36

    defaultConfig {
        minSdk = 24
        consumerProguardFiles("consumer-rules.pro")
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_21
        targetCompatibility = JavaVersion.VERSION_21
    }

    kotlin {
        jvmToolchain(21)
    }

    buildFeatures { compose = true }
}

dependencies {
    implementation(platform(libs.compose.bom))
    implementation(libs.activity.compose)
    implementation(libs.compose.ui)
    implementation(libs.compose.ui.graphics)
    implementation(libs.compose.ui.tooling.preview)
    implementation(libs.compose.ui.tooling)
    implementation(libs.material)
    implementation(libs.material3)
    implementation(libs.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
}

mavenPublishing {
    coordinates(
        groupId = "io.github.DP-Hridayan",
        artifactId = "shapeindicators",
        version = "1.0.0"
    )

    pom {
        name.set("ShapeIndicators")
        description.set("A Jetpack Compose library for pager indicators")
        inceptionYear.set("2025")
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
            url.set("https://github.com/DP-Hridayan/ShapeIndicators")
            connection.set("scm:git:git://github.com/DP-Hridayan/ShapeIndicators.git")
            developerConnection.set("scm:git:ssh://git@github.com/DP-Hridayan/ShapeIndicators.git")
        }
    }

    publishToMavenCentral()
    signAllPublications()
}