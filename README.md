# RusDili Pro

RusDili Pro is an Android app for Azerbaijani speakers learning Russian. It combines a structured, Azerbaijani-language course with daily practice, a locally stored Russian–Azerbaijani dictionary, progress tracking, and a small news feed.

## Features

- **Home:** daily XP goal, streak and progress information, a continue-learning shortcut, a word of the day, and a news shortcut.
- **Book:** 15 Russian-learning chapters, from the alphabet and pronunciation through grammar, syntax, and spelling. Explanations are in Azerbaijani and examples are in Russian. Chapters unlock in order after the previous chapter reaches a score of at least 80%.
- **Practice:** a daily set of up to 10 questions drawn from passed chapters, plus vocabulary flashcards with spaced-review scheduling.
- **Dictionary:** search Russian or Azerbaijani words, browse favorites and recent searches, and view available noun case forms or verb forms. The seeded vocabulary is stored locally in Room and is available without a network connection.
- **News:** separate “Tramp” and “Dünya” filters. World headlines are fetched from GDELT and cached locally. Seeded sample content is labeled as unconfirmed where applicable.
- **Russian speech:** lesson sentences can be spoken using Android TextToSpeech with the `ru-RU` voice. Speech availability and voice quality depend on the device's installed TTS engine and language data.
- **Progress:** correct answers award 10 XP, the daily goal is 50 XP, and lesson completion contributes to the streak.

### News feed scope

The “Dünya” feed requests world-news articles from GDELT and stores returned items in Room. Their display title and summary are generic Azerbaijani descriptions; open the source link to read the original article and verify its context.

The “Tramp” tab displays locally stored items whose source is marked “Truth Social.” It does not connect to a live Truth Social feed. The seeded example is retained and marked unconfirmed; it should not be treated as independently verified reporting.

## Technology

- Kotlin, single-activity Android app
- Jetpack Compose and Material 3
- Navigation Compose
- Hilt dependency injection
- Room local database
- DataStore Preferences for user progress
- Retrofit, OkHttp, and Moshi for the GDELT API
- WorkManager for background world-news refresh
- Android TextToSpeech for Russian audio
- Minimum Android SDK: 24
- Compile and target Android SDK: 34

Dependency and plugin versions are managed in `gradle/libs.versions.toml`. The project uses Android Gradle Plugin 8.6.1, Kotlin 2.0.21, and Gradle 8.7.

## Project structure

| Module | Responsibility |
| --- | --- |
| `:app` | Application entry point, single activity, theme setup, and five-tab navigation |
| `:core:common` | Shared models, UI state, constants, and user-progress storage |
| `:core:ui` | Reusable Compose components |
| `:core:database` | Room entities, DAOs, database setup, and seeded course, vocabulary, and news data |
| `:core:network` | Retrofit service and GDELT response models |
| `:feature:home` | Home screen and progress/news summary |
| `:feature:book` | Course chapters, lesson flow, scoring, word lookup, and speech |
| `:feature:quiz` | Daily practice and vocabulary flashcards |
| `:feature:dictionary` | Dictionary search, favorites, history, and word details |
| `:feature:news` | News filters, feed refresh, and WorkManager worker |

Feature screens use ViewModels and observable UI state. Repository interfaces and implementations separate feature logic from the Room and network data sources. Most lesson content is defined in `feature/book/.../CourseContent.kt`; starter dictionary and news data are in `core/database/.../SeedData.kt`.

## Requirements

To build and run the Android app, install:

1. **JDK 17** (Android Gradle Plugin 8.6.1 requires JDK 17).
2. **Android Studio** with Android SDK Platform 34 and a compatible Android SDK Build-Tools installation.
3. An Android emulator or a physical Android device for installation and runtime checks.

The project manifest requests the `INTERNET` permission for fetching world news. Dictionary and course content are local. No login, Firebase, or advertising setup is required.

## Build and run on Windows

Open PowerShell in the project root and run:

```powershell
.\gradlew.bat :app:assembleDebug
```

The debug APK is written to:

```text
app\build\outputs\apk\debug\app-debug.apk
```

With an emulator running or an Android device connected with USB debugging enabled, install and launch the debug build with:

```powershell
.\gradlew.bat :app:installDebug
```

If Gradle cannot find Java, install JDK 17 and set `JAVA_HOME` to its installation directory, then open a new terminal. If the Android SDK is not found, install it with Android Studio and configure `ANDROID_HOME` or `ANDROID_SDK_ROOT`.

The repository includes the Windows Gradle wrapper (`gradlew.bat`). For macOS or Linux, open the project in Android Studio or use a local Gradle 8.7 installation.

## Development notes

- Run `.\gradlew.bat :app:assembleDebug` after code changes to check Android compilation.
- Run `.\gradlew.bat :app:installDebug` to deploy to a connected device or emulator.
- The app requests network access only for news retrieval; the dictionary and course seed data do not require a connection.
- GDELT and the Android TTS engine are external runtime dependencies. News refresh needs an internet connection, and speech requires an installed Russian voice.
- The “Tramp” tab currently relies on seeded/local Truth Social-labeled items; no live Truth Social integration is configured.

## Original specification

The initial product brief is kept in [`promt.txt`](./promt.txt).
