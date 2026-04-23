# Hikari

![Version](https://img.shields.io/badge/version-0.4.1-blue?style=flat-square)
![License](https://img.shields.io/badge/license-Apache--2.0-green?style=flat-square)
![Build](https://img.shields.io/github/actions/workflow/status/leverteam/hikari/build.yml?style=flat-square)

Manga reader for Android based on Mihon with NDK image decoding and sectional UI.

## Stack

![Android](https://img.shields.io/badge/Android-3DDC84?style=flat-square&logo=android&logoColor=white)
![Kotlin](https://img.shields.io/badge/Kotlin-7F52FF?style=flat-square&logo=kotlin&logoColor=white)
![Jetpack Compose](https://img.shields.io/badge/Compose-4285F4?style=flat-square&logo=jetpackcompose&logoColor=white)
![SQLite](https://img.shields.io/badge/SQLite-003B57?style=flat-square&logo=sqlite&logoColor=white)
![GitHub Actions](https://img.shields.io/badge/GitHub%20Actions-2088FF?style=flat-square&logo=githubactions&logoColor=white)

## Installation ![Platform](https://img.shields.io/badge/platform-android-lightgrey?style=flat-square)

Clone the repository and build the debug APK using Gradle:

```bash
git clone https://github.com/leverteam/hikari.git
cd hikari
./gradlew assembleDebug
```

Official APK releases are available on the [GitHub Releases](https://github.com/leverteam/hikari/releases) page.

## Usage

1. **Install**: Locate the built APK in `app/build/outputs/apk/debug/` and install it on an Android device.
2. **Extensions**: Navigate to **Browse > Extensions** to initialize and synchronize extension sources.
3. **Library**: Add manga to your library from extensions to enable tracking and notifications.
4. **Reader**: Configure the reader pipeline, including NDK decoding and upscaling options, in the sectional dashboard.
5. **Security**: Enable biometric locking in the settings dashboard for idle application security.
