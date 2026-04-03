# Hikari

> Manga reader for Android, featuring offline downloads, security features, and a unified library.

<p align="left">
  <img src="https://skillicons.dev/icons?i=kotlin,android,gradle,sqlite" alt="Tech Stack" />
</p>
<p align="left">
  <img src="https://img.shields.io/badge/Language-Kotlin-blue?style=flat-square&logo=kotlin" alt="Language" />
  <img src="https://img.shields.io/badge/Platform-Android-green?style=flat-square&logo=android" alt="Platform" />
</p>

## Features

- Fetches and manages manga extensions from multiple repositories
- Reads and organizes manga into a customized library interface
- Syncs the Keiyoushi extension repository out-of-the-box upon first launch
- Downloads chapters for offline viewing
- Biometric lock and security modes for privacy
- Material You and modular UI based on Jetpack Compose

## Installation

### Prerequisites

- JDK 21
- Android SDK / Android Studio

### Build Setup

Use Gradle to fetch dependencies and compile the debug application.

```bash
# Clone the repository
git clone https://github.com/LeverTeam/hikari.git
cd hikari

# Build the debug APK (ensure gradlew is executable)
chmod +x gradlew
./gradlew assembleDebug
```

## Usage

1. Open the output directory after a successful build: `app/build/outputs/apk/debug/`.
2. Sideload the built `app-debug.apk` directly onto an Android device or emulator.
3. Upon launch, navigate to the **Browse** panel; the default extension repository will automatically initialize.
