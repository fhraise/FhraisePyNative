plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.kotlinSerialization)
    alias(libs.plugins.ktor)
}

group = "xyz.xfqlittlefan.fhraise"
version = "0.1.0"

repositories {
    mavenCentral()
}

kotlin {
    linuxArm64 {
        binaries {
            sharedLib {
                baseName = "fhraise"
            }
        }
    }

    linuxX64 {
        binaries {
            sharedLib {
                baseName = "fhraise"
            }
        }
    }

    mingwX64 {
        binaries {
            sharedLib {
                baseName = "libfhraise"
            }
        }
    }

    applyDefaultHierarchyTemplate()

    sourceSets {
        val nativeMain by getting {
            dependencies {
                implementation(libs.kotlinx.coroutinesCore)
                implementation(libs.ktor.client.core)
                implementation(libs.ktor.client.resources)
                implementation(libs.ktor.client.contentNegotiation)
                implementation(libs.ktor.client.websockets)
                implementation(libs.ktor.serialization.kotlinx.cbor)
            }
        }

        val nonMingwMain by creating {
            dependsOn(nativeMain)
            dependencies {
                implementation(libs.ktor.client.cio)
            }
        }

        val mingwMain by getting {
            dependencies {
                implementation(libs.ktor.client.winhttp)
            }
        }

        val linuxMain by getting {
            dependsOn(nonMingwMain)
        }

        val nativeTest by getting
    }
}
