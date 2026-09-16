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

1. Open this project folder in Android Studio.
2. Allow the Gradle sync to finish.
3. Confirm Android SDK 35 is installed in **Tools > SDK Manager**.
4. Select an emulator or connected Android device with Android 6.0 or newer.
5. Click **Run app**.

No API keys, accounts, server, or environment variables are needed.

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
