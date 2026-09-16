# Assignment Requirement Checklist

This checklist connects each app requirement to its implementation and repeatable evidence.

## Functional requirements

- [x] Add pantry item - `AddEditIngredientActivity.saveItem()` calls `DatabaseHelper.addPantryItem()`.
- [x] Read pantry items - `PantryListActivity.showPantryItems()` loads SQLite rows into `PantryAdapter`.
- [x] Edit pantry item - the row Edit button passes `item_id` through an intent and updates that database row.
- [x] Delete pantry item - the row Delete button shows confirmation and calls `deletePantryItem()`.
- [x] Persistent data - `DatabaseHelper` uses the on-device `smart_pantry.db` SQLite file.
- [x] Dynamic list with custom adapter - `PantryAdapter` and `RecipeAdapter` extend `BaseAdapter` and populate `ListView` rows.
- [x] At least 15-20 recipes - `seedRecipes()` inserts exactly 20 recipes on first database creation.
- [x] Suggested Recipes screen - loads only the result of `getSuggestedRecipes()`.
- [x] Strict match - `IngredientMatcher.recipeMatches()` returns true only after every requirement has enough compatible pantry quantity.
- [x] Robust simple matching - common plurals are normalised and compatible weight/volume units are converted.
- [x] Recipe detail - an intent passes `recipe_id`, then the detail screen shows every ingredient and all steps.
- [x] Settings - expiry alerts and the default unit for new items persist with `SharedPreferences`.
- [x] Zero-match feedback - the suggestions `ListView` has a clear empty view.
- [x] Form validation - name, positive quantity, upper quantity limit, and real `YYYY-MM-DD` date checks.
- [x] Navigation - Pantry, Recipes, and Settings buttons are present on all three main sections.
- [x] Java only - all app source files use Java; no Kotlin source is present.
- [x] Out-of-scope restrictions - the manifest requests no location permission and the project has no maps or payment dependency.

## Verification evidence

- [x] Debug app build: `assembleDebug` completed successfully.
- [x] Core-rule checks: `verify-matching.ps1` reports `PASS: 8 strict-matching checks completed.`
- [ ] Manual device/emulator walkthrough: install the APK and follow the demonstration in `README.md`.

The final manual walkthrough remains a separate check because a successful build and Java logic checks do not prove touch behaviour or screen appearance on a real Android device.
