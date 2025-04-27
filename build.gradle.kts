import com.android.build.gradle.BaseExtension
import io.gitlab.arturbosch.detekt.Detekt
import io.gitlab.arturbosch.detekt.report.ReportMergeTask

// Top-level build file where you can add configuration options common to all sub-projects/modules.
plugins {
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.jetbrains.kotlin.android) apply false
    alias(libs.plugins.android.library) apply false
    alias(libs.plugins.compose.compiler) apply false
    alias(libs.plugins.ksp) apply false
    alias(libs.plugins.roborazzi.plugin) apply false
    alias(libs.plugins.detekt)
    alias(libs.plugins.google.services) apply false
    alias(libs.plugins.firebase.clashlytics) apply false
}

buildscript {
    repositories {
        mavenCentral()
    }
    dependencies {
        classpath(libs.android.gradle)
        classpath(libs.android.application.plugin)
        classpath(libs.android.library.plugin)
        classpath(libs.roborazzi.gradle.plugin)
        classpath(libs.deploygate.plugin)
    }
}

val reportMerge = tasks.register<ReportMergeTask>("reportMerge") {
    output = rootProject.file("./reports/detekt.xml")
}

subprojects {
    apply(plugin = "io.gitlab.arturbosch.detekt")

    plugins.withId("com.android.library") {
        extensions.configure<BaseExtension> {
            lintOptions {
                textReport = true
                textOutput("stdout")
                isAbortOnError = true
                isCheckDependencies = true
            }
        }
    }

    detekt {
        autoCorrect = true
        parallel = true
        config.setFrom("$rootDir/config/detekt/detekt.yml")
        buildUponDefaultConfig = true
        ignoreFailures = true
        basePath = file("$rootDir/../").absolutePath
    }

    tasks.withType<Detekt> {
        finalizedBy(reportMerge)
    }

    reportMerge.configure {
        input.from(tasks.withType(Detekt::class).map { it.xmlReportFile })
    }

    dependencies {
        detektPlugins(rootProject.libs.detekt.formatting)
    }

}

task("clean", Delete::class) {
    delete(rootProject.layout.buildDirectory)
}
