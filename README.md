# Smart Pantry Manager

Smart Pantry Manager is a Java Android application for reducing food waste. A user records leftover ingredients in a pantry, and the app suggests only recipes for which every required ingredient and quantity is already available.

## What the app demonstrates

- Five screens: Pantry List, Add/Edit Ingredient, Suggested Recipes, Recipe Detail, and Settings.
- Screen navigation with Android intents.
- A custom `BaseAdapter` and `ListView` for pantry items and recipes.
- Create, read, update, and delete operations for pantry items.
- On-device SQLite storage that remains after the app closes.
- Twenty recipes inserted into SQLite on first use.
- Strict recipe matching, including quantity checks, common singular/plural names, and simple unit conversion.
- Form validation and helpful empty-list messages.
- A settings screen with persistent expiry-alert and default-unit preferences.
- No maps, location services, payments, network services, or external database.

## Why SQLite was chosen

SQLite is built into Android and works without an internet connection or account. It makes the complete data path easy to explain: an activity calls `DatabaseHelper`, `DatabaseHelper` runs an SQL operation, and the `ListView` shows the returned records. This is the smallest option that demonstrates genuine persistent storage and full CRUD.

## Project structure

```text
app/src/main/
|-- AndroidManifest.xml
|-- java/za/ac/richfield/smartpantry/
|   |-- *Activity.java       screens and user actions
|   |-- adapter/             custom ListView adapters
|   |-- data/                SQLite database and seed data
|   |-- model/               simple data classes
|   `-- util/                matching and navigation helpers
`-- res/
    |-- layout/              screen and list-row layouts
    |-- drawable/            simple backgrounds and app icon
    `-- values/              colours, sizes, text, and styles
```

The main flow is deliberately shallow:

```text
Activity -> DatabaseHelper -> SQLite
    |
    `-> custom Adapter -> ListView
```

## Run in Android Studio

Follow these steps on Windows to import, configure, and run the app. Android Studio menu wording can differ slightly by version.

### 1. Install the project requirements

This project uses the Gradle wrapper included in the repository, Android Gradle Plugin 8.5.1, Gradle 8.7, Java 17, compile SDK 35 (Android 15), and Android SDK Build-Tools 36.0.0. The app supports Android 6.0 (API 23) and newer.

1. Install and start Android Studio. Complete its setup wizard and allow it to install the Android SDK.
2. Open **Tools > SDK Manager**.
3. On the **SDK Platforms** tab, select **Android 15 (API 35)** and click **Apply**.
4. On the **SDK Tools** tab, enable **Show Package Details** and select **Android SDK Build-Tools 36.0.0**. Also install **Android SDK Platform-Tools**. If you plan to use an emulator, install **Android Emulator**.
5. Apply the selections and accept the SDK licence prompts.

The SDK Manager is Android Studio's supported interface for installing SDK platforms and tools. See the official [SDK Manager instructions](https://developer.android.com/studio/intro/update#sdk-manager).

### 2. Open the correct project folder

1. From Android Studio's welcome screen, choose **Open**. If another project is open, use **File > Open**.
2. Select the **Smart-Pantry-Manager project root**: the folder containing settings.gradle, build.gradle, gradlew.bat, and the app folder. Do not open the app subfolder by itself.
3. If asked whether to trust the project, confirm that you want to open it.
4. Wait for Android Studio to finish importing and syncing the Gradle project. The first sync requires internet access so Gradle can download its wrapper and Android build dependencies. Accept any additional SDK licence prompts.

Android Studio creates a machine-specific local.properties file for the SDK location. It is excluded from Git, so do not copy another computer's SDK path into this project.

### 3. Set the Gradle Java version

The project is configured for Java 17. In Android Studio, open **File > Settings > Build, Execution, Deployment > Build Tools > Gradle** and set **Gradle JDK** to a Java 17 installation (Android Studio's bundled JDK is suitable when it is version 17). Apply the setting and sync again if you change it. On macOS, the equivalent settings are under **Android Studio > Settings**.

### 4. Create or select an Android device

To use an emulator:

1. Open **Tools > Device Manager** (in some Android Studio versions, use **View > Tool Windows > Device Manager**).
2. Choose **Create Device**, select a phone profile, and continue.
3. Download and select an Android 15 (API 35) system image, then finish creating the virtual device.
4. Start the virtual device from Device Manager.

An API 35 emulator is a convenient match for the project's compile SDK. A physical device running Android 6.0 or later can also run the app. To use one over USB, enable Developer options and USB debugging on the device, connect it, and approve the debugging prompt. On Windows, install the device manufacturer's USB driver if Android Studio does not detect the phone. See [Run apps on an emulator](https://developer.android.com/studio/run/emulator) or [Run apps on a hardware device](https://developer.android.com/studio/run/device).

### 5. Build and launch the app

1. In the top toolbar, select the **app** run configuration.
2. Select the running emulator or connected device in the target-device menu.
3. Click **Run** (the green triangle). Android Studio builds the debug version, installs it, and launches Smart Pantry Manager.
4. On the Pantry screen, add pantry items to try the list and CRUD actions. Open **Recipes** to view recipes that meet all ingredient and quantity requirements; **Settings** contains the expiry-alert and default-unit preferences.

You can also check compilation using **Build > Make Project**. To build from Android Studio's Terminal or Windows PowerShell, use the wrapper command below. The generated debug APK is at app/build/outputs/apk/debug/app-debug.apk.

### Troubleshooting

- **Gradle sync cannot download dependencies:** Check the internet connection and make sure Gradle is not in offline mode. Retry sync after the connection is restored.
- **A platform or Build-Tools package is missing:** Reopen SDK Manager and install Android 15 (API 35) and Android SDK Build-Tools 36.0.0 as listed above.
- **Gradle reports an incompatible Java version:** Set Gradle JDK to Java 17, then sync and build again.
- **The emulator or phone is missing from the target-device menu:** Start the emulator first. For a USB phone, unlock it, accept the USB debugging prompt, and check that Platform-Tools is installed. On Windows, a manufacturer USB driver may be required.
- **The app opens with an empty pantry:** This is the expected initial state. Add the example ingredients in the demonstration-data section below; the built-in recipes are seeded locally when the database is first created.

## Build from Windows PowerShell

```powershell
.\gradlew.bat assembleDebug
```

The APK is produced at `app/build/outputs/apk/debug/app-debug.apk`.

## Verify the strict-matching rule

The small verification program uses only Java and has no extra library dependency:

```powershell
.\verify-matching.bat
```

It checks complete matches, a missing ingredient, insufficient quantity, plural names, weight and volume conversions, duplicate pantry rows, and incompatible units.

## Simple demonstration data

To make **Tomato Omelette** appear, add these pantry items:

- Egg - 2 items
- Tomato or Tomatoes - 1 item
- Butter - 10 g
- Salt - 0.25 tsp

Delete Salt or reduce Eggs to 1, then reopen Suggested Recipes. Tomato Omelette must disappear because the app does not allow partial matches.

## Important source files

- `DatabaseHelper.java`: tables, pantry CRUD, twenty seeded recipes, and the database query flow.
- `IngredientMatcher.java`: the strict matching decision and unit conversions.
- `PantryAdapter.java`: binds pantry database records to custom list rows.
- `NavigationHelper.java`: opens the three main sections with intents.
- `AddEditIngredientActivity.java`: create/update form and input validation.

## Clean project note

Build output folders such as `build`, `app/build`, and `.gradle` are ignored. They can be deleted and recreated by Gradle. User pantry data is stored by Android on the emulator or device, not in the source folder.
