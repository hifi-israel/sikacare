import org.jetbrains.compose.desktop.application.dsl.TargetFormat
import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.androidApplication)
    alias(libs.plugins.composeMultiplatform)
    alias(libs.plugins.composeCompiler)
    alias(libs.plugins.composeHotReload)
    alias(libs.plugins.buildKonfig)
    alias(libs.plugins.kotlinSerialization)
}

kotlin {
    androidTarget {
        compilerOptions {
            jvmTarget.set(JvmTarget.JVM_11)
        }
    }

    listOf(
        iosArm64(),
        iosSimulatorArm64()
    ).forEach { iosTarget ->
        iosTarget.binaries.framework {
            baseName = "ComposeApp"
            isStatic = true
        }
    }

    jvm()

    sourceSets {
        commonMain.dependencies {
            implementation(compose.runtime)
            implementation(compose.foundation)
            implementation(compose.material3)
            implementation(compose.ui)
            implementation(compose.components.resources)
            implementation(compose.components.uiToolingPreview)
            implementation(libs.androidx.lifecycle.viewmodelCompose)
            implementation(libs.androidx.lifecycle.runtimeCompose)
            // Supabase (Auth) - dependencias directas para resolver problemas de classpath
            implementation("io.github.jan-tennert.supabase:auth-kt:3.2.4")
            implementation("io.github.jan-tennert.supabase:postgrest-kt:3.2.4")
            // Coroutines comunes
            implementation(libs.kotlinxCoroutinesCore)
            // HTTP para SendGrid
            implementation("io.ktor:ktor-client-core:2.3.12")
            implementation("io.ktor:ktor-client-json:2.3.12")
            implementation("io.ktor:ktor-client-serialization:2.3.12")
            implementation("io.ktor:ktor-client-logging:2.3.12")
        }
        commonTest.dependencies {
            implementation(libs.kotlin.test)
        }
        androidMain.dependencies {
            implementation(compose.preview)
            implementation(libs.androidx.activity.compose)
            implementation("androidx.core:core-splashscreen:1.0.1")
            // Ktor engine for Android/JVM
            implementation(libs.ktorClientOkhttp)
            // Google Sign-In for Android
            implementation("androidx.credentials:credentials:1.2.2")
            implementation("com.google.android.libraries.identity.googleid:googleid:1.1.0")
            implementation("androidx.credentials:credentials-play-services-auth:1.2.2")
        }
        iosMain.dependencies {
            // Ktor engine for iOS
            implementation(libs.ktorClientDarwin)
        }
        jvmMain.dependencies {
            implementation(compose.desktop.currentOs)
            implementation(libs.kotlinx.coroutinesSwing)
            // Ktor engine for Desktop JVM
            implementation(libs.ktorClientOkhttp)
            // Compose resources for JVM
            implementation(compose.components.resources)
        }
    }
}

android {
    namespace = "com.israeljuarez.sikacorekmp"
    compileSdk = libs.versions.android.compileSdk.get().toInt()

    defaultConfig {
        applicationId = "com.israeljuarez.sikacorekmp"
        minSdk = libs.versions.android.minSdk.get().toInt()
        targetSdk = libs.versions.android.targetSdk.get().toInt()
        versionCode = 1
        versionName = "1.0"
    }
    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
    buildTypes {
        getByName("release") {
            isMinifyEnabled = false
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
}

// BuildKonfig: expose config values to commonMain
buildkonfig {
    packageName = "com.israeljuarez.sikacorekmp.config"
    defaultConfigs {
        buildConfigField(
            com.codingfeline.buildkonfig.compiler.FieldSpec.Type.STRING,
            "SUPABASE_URL",
            providers.gradleProperty("SUPABASE_URL").orNull ?: ""
        )
        buildConfigField(
            com.codingfeline.buildkonfig.compiler.FieldSpec.Type.STRING,
            "SUPABASE_ANON_KEY",
            providers.gradleProperty("SUPABASE_ANON_KEY").orNull ?: ""
        )
        buildConfigField(
            com.codingfeline.buildkonfig.compiler.FieldSpec.Type.STRING,
            "WEB_GOOGLE_CLIENT_ID",
            providers.gradleProperty("WEB_GOOGLE_CLIENT_ID").orNull ?: ""
        )
        buildConfigField(
            com.codingfeline.buildkonfig.compiler.FieldSpec.Type.STRING,
            "ANDROID_GOOGLE_CLIENT_ID",
            providers.gradleProperty("ANDROID_GOOGLE_CLIENT_ID").orNull ?: ""
        )
    }
}

dependencies {
    debugImplementation(compose.uiTooling)
}

compose.desktop {
    application {
        mainClass = "com.israeljuarez.sikacorekmp.MainKt"

        nativeDistributions {
            targetFormats(TargetFormat.Dmg, TargetFormat.Msi, TargetFormat.Deb)
            packageName = "com.israeljuarez.sikacorekmp"
            packageVersion = "1.0.0"

            // Iconos de empaquetado (condicionales)
            val iconsDir = project.layout.projectDirectory.dir("composeApp/desktopIcons")
            val winIcon = iconsDir.file("logo.ico").asFile
            val macIcon = iconsDir.file("logo.icns").asFile
            val linuxPng = project.layout.projectDirectory.file("composeApp/src/commonMain/composeResources/drawable/logo.png").asFile

            windows {
                if (winIcon.exists()) {
                    iconFile.set(winIcon)
                }
            }
            macOS {
                if (macIcon.exists()) {
                    iconFile.set(macIcon)
                }
            }
            linux {
                if (linuxPng.exists()) {
                    iconFile.set(linuxPng)
                }
            }
        }
    }
}
