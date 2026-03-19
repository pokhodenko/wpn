# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Project Overview

**GoToLiks** is an IntelliJ Platform plugin (Kotlin) that improves "Go to Declaration" navigation for JavaScript/TypeScript projects using npm workspaces with symlinked packages. It resolves symlinks in `node_modules` to point to real workspace package source files instead of symlink intermediaries.

- **Plugin ID:** `com.pokhodenko.wpn`
- **Target IDE:** PhpStorm (2025.2.4+), compatible with other JetBrains IDEs
- **Version:** 1.1

## Build & Run Commands

```bash
# Build
./gradlew build

# Run IDE sandbox with plugin loaded
./gradlew runIde

# Run tests
./gradlew test

# Verify plugin compatibility
./gradlew verifyPlugin

# Publish to JetBrains Marketplace (requires .env with PUBLISH_TOKEN)
./publish
```

## Architecture

The plugin consists of a single Kotlin class:

**`SymlinksGotoDeclarationHandler`** (`src/main/kotlin/com/pokhodenko/wpn/SymlinksGotoDeclarationHandler.kt`)
- Implements IntelliJ's `GotoDeclarationHandler` extension point
- Registered in `src/main/resources/META-INF/plugin.xml`

**Navigation flow:**
1. `getGotoDeclarationTargets()` — intercepts Go-to-Declaration, resolves references, remaps symlinked targets
2. `resolveTargets()` — handles both single and poly-variant PSI references
3. `remapToRealFile()` — resolves symlinks via `Path.toRealPath()`, finds the real `VirtualFile`, and maps the PSI element to it
4. `climbToReasonableTarget()` — climbs the PSI tree in the real file to find an element with a matching type and range

## Tech Stack

- Kotlin 2.1.20, JVM target Java 21
- IntelliJ Platform Gradle Plugin 2.10.2
- Gradle 9.0.0 (wrapper)
- IntelliJ PSI API and `GotoDeclarationHandler` extension point
