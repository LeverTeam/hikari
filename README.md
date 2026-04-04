# Hikari

Manga reader for Android based on Mihon featuring offline downloads and security features.

![Tech Stack](https://skillicons.dev/icons?i=kotlin,android,compose,gradle,sqlite,firebase)

![Language](https://img.shields.io/badge/language-Kotlin-blue?style=flat-square)
![License](https://img.shields.io/github/license/LeverTeam/hikari?style=flat-square)
![Repo Size](https://img.shields.io/github/repo-size/LeverTeam/hikari?style=flat-square)

## Features

- Extension management from multiple repositories
- Library organization with customized interface
- Out-of-the-box Keiyoushi extension sync
- Chapter downloads for offline viewing
- Biometric lock and security modes
- Material You UI built with Jetpack Compose
- Automatic library updates and background backups

## Installation

### Prerequisites

- JDK 21
- Android SDK

### Build

```bash
git clone https://github.com/LeverTeam/hikari.git
cd hikari
chmod +x gradlew
./gradlew assembleDebug
```

## Usage

1. Locate the built APK in `app/build/outputs/apk/debug/`
2. Install the APK on an Android device
3. Navigate to the Browse panel to initialize the default extension repository
