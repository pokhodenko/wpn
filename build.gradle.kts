plugins {
    id("java")
    id("org.jetbrains.kotlin.jvm") version "2.1.20"
    id("org.jetbrains.intellij.platform") version "2.10.2"
}

group = "com.wpn"
version = "1.1.1"

repositories {
    mavenCentral()
    intellijPlatform {
        defaultRepositories()
    }
}

// Read more: https://plugins.jetbrains.com/docs/intellij/tools-intellij-platform-gradle-plugin.html
dependencies {
    intellijPlatform {
        phpstorm("2025.2.4")
        testFramework(org.jetbrains.intellij.platform.gradle.TestFrameworkType.Platform)


        // Add plugin dependencies for compilation here, example:
        // bundledPlugin("com.intellij.java")
    }
}

intellijPlatform {
    pluginConfiguration {
        ideaVersion {
            sinceBuild = "252.25557"
        }

        changeNotes = """
            <ul>
                <li><b>Settings panel</b> — added <i>Settings → Tools → Workspace Package Navigator</i> with an option to restrict symlink remapping to library roots only (<i>Ignore files outside library root</i>)</li>
                <li><b>First-run notification</b> — on initial install the plugin now prompts to invalidate caches and restart the IDE, ensuring the VFS index reflects symlink targets correctly</li>
                <li><b>Performance: VFS-based symlink resolution</b> — symlink targets are now resolved using IntelliJ's VFS cache (<code>canonicalFile</code>) instead of blocking filesystem I/O (<code>Path.toRealPath()</code>).</li>
            </ul>
        """.trimIndent()
    }
}

tasks {
    // Set the JVM compatibility versions
    withType<JavaCompile> {
        sourceCompatibility = "21"
        targetCompatibility = "21"
    }
}

kotlin {
    compilerOptions {
        jvmTarget.set(org.jetbrains.kotlin.gradle.dsl.JvmTarget.JVM_21)
    }
}
