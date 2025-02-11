import com.android.build.gradle.BaseExtension
import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import org.jetbrains.kotlin.gradle.dsl.KotlinVersion
import org.jetbrains.kotlin.gradle.dsl.jvm.JvmTargetValidationMode
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile
// require Gradle 8.2+
import com.github.spotbugs.snom.Confidence
import com.github.spotbugs.snom.Effort

plugins {
  alias(libs.plugins.android.application) apply false
  alias(libs.plugins.android.library) apply false
  alias(libs.plugins.kotlin) apply false
  alias(libs.plugins.kotlin.compose) apply false
  alias(libs.plugins.kotlin.serialization) apply false
  
}

buildscript {
  dependencies { classpath(libs.androidx.navigation.safe.args.gradle.plugin) }
}

spotbugs {
    ignoreFailures = false
    showStackTraces = true
    showProgress = true
    effort = Effort.DEFAULT
    reportLevel = Confidence.DEFAULT
    visitors = listOf("FindSqlInjection", "SwitchFallthrough")
    omitVisitors = listOf("FindNonShortCircuit")
    reportsDir = file("$buildDir/spotbugs")
    includeFilter = file("include.xml")
    excludeFilter = file("exclude.xml")
    baselineFile = file("baseline.xml")
    //onlyAnalyze = listOf("com.foobar.MyClass", "com.foobar.mypkg.*")
    maxHeapSize = "1g"
    extraArgs = listOf("-nested:false")
    jvmArgs = listOf("-Duser.language=ja")
}


fun Project.configureBaseExtension() {
  extensions.findByType(BaseExtension::class)?.run {
    compileSdkVersion(34)
    buildToolsVersion = "34.0.0"

    defaultConfig {
      minSdk = 26
      targetSdk = 28
      versionCode = 200
      versionName = "2.0.0"
    }

    compileOptions {
      sourceCompatibility = JavaVersion.VERSION_17
      targetCompatibility = JavaVersion.VERSION_17
    }
  }
}

subprojects {
  plugins.withId("com.android.application") { configureBaseExtension() }
  plugins.withId("com.android.library") { configureBaseExtension() }

  tasks.withType<KotlinCompile>().configureEach {
    compilerOptions {
      apiVersion = KotlinVersion.KOTLIN_2_0
      languageVersion = KotlinVersion.KOTLIN_2_0
      jvmTarget = JvmTarget.JVM_17
      jvmTargetValidationMode = JvmTargetValidationMode.WARNING
      freeCompilerArgs.add("-Xjvm-default=all")
    }
  }
}

tasks.register<Delete>("clean") { delete(rootProject.layout.buildDirectory) }

// require Gradle 8.2+
tasks.spotbugsMain {
    reports.create("html") {
        required = true
        outputLocation = file("$buildDir/reports/spotbugs.html")
        setStylesheet("fancy-hist.xsl")
    }
}
