package za.ac.richfield.smartpantry.data;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.List;

import za.ac.richfield.smartpantry.model.PantryItem;
import za.ac.richfield.smartpantry.model.Recipe;
import za.ac.richfield.smartpantry.model.RecipeRequirement;
import za.ac.richfield.smartpantry.util.IngredientMatcher;

public class DatabaseHelper extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "smart_pantry.db";
    private static final int DATABASE_VERSION = 1;

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase database) {
        database.execSQL("CREATE TABLE pantry_items ("
                + "id INTEGER PRIMARY KEY AUTOINCREMENT, "
                + "name TEXT NOT NULL, "
                + "quantity REAL NOT NULL CHECK(quantity > 0), "
                + "unit TEXT NOT NULL, "
                + "expiry_date TEXT)");

        database.execSQL("CREATE TABLE recipes ("
                + "id INTEGER PRIMARY KEY AUTOINCREMENT, "
                + "name TEXT NOT NULL, "
                + "steps TEXT NOT NULL)");

        database.execSQL("CREATE TABLE recipe_ingredients ("
                + "id INTEGER PRIMARY KEY AUTOINCREMENT, "
                + "recipe_id INTEGER NOT NULL, "
                + "ingredient_name TEXT NOT NULL, "
                + "quantity REAL NOT NULL, "
                + "unit TEXT NOT NULL, "
                + "FOREIGN KEY(recipe_id) REFERENCES recipes(id) ON DELETE CASCADE)");

        seedRecipes(database);
    }

    @Override
    public void onUpgrade(SQLiteDatabase database, int oldVersion, int newVersion) {
        // Version 1 is the first database structure. Future upgrades belong here.
    }

    @Override
    public void onConfigure(SQLiteDatabase database) {
        super.onConfigure(database);
        database.setForeignKeyConstraintsEnabled(true);
    }

    public long addPantryItem(String name, double quantity, String unit, String expiryDate) {
        return getWritableDatabase().insertOrThrow(
                "pantry_items", null, pantryValues(name, quantity, unit, expiryDate));
    }

    public int updatePantryItem(
            long id, String name, double quantity, String unit, String expiryDate) {
        return getWritableDatabase().update(
                "pantry_items",
                pantryValues(name, quantity, unit, expiryDate),
                "id = ?",
                new String[]{String.valueOf(id)});
    }

    public int deletePantryItem(long id) {
        return getWritableDatabase().delete(
                "pantry_items", "id = ?", new String[]{String.valueOf(id)});
    }

    public PantryItem getPantryItem(long id) {
        try (Cursor cursor = getReadableDatabase().query(
                "pantry_items",
                null,
                "id = ?",
                new String[]{String.valueOf(id)},
                null,
                null,
                null)) {
            if (cursor.moveToFirst()) {
                return pantryItemFromCursor(cursor);
            }
        }
        return null;
    }

    public List<PantryItem> getAllPantryItems() {
        List<PantryItem> items = new ArrayList<>();
        try (Cursor cursor = getReadableDatabase().query(
                "pantry_items", null, null, null, null, null, "name COLLATE NOCASE")) {
            while (cursor.moveToNext()) {
                items.add(pantryItemFromCursor(cursor));
            }
        }
        return items;
    }

    public Recipe getRecipe(long id) {
        try (Cursor cursor = getReadableDatabase().query(
                "recipes", null, "id = ?", new String[]{String.valueOf(id)},
                null, null, null)) {
            if (cursor.moveToFirst()) {
                Recipe recipe = new Recipe(
                        cursor.getLong(cursor.getColumnIndexOrThrow("id")),
                        cursor.getString(cursor.getColumnIndexOrThrow("name")),
                        cursor.getString(cursor.getColumnIndexOrThrow("steps")));
                loadRequirements(recipe);
                return recipe;
            }
        }
        return null;
    }

    public List<Recipe> getSuggestedRecipes() {
        List<PantryItem> pantryItems = getAllPantryItems();
        List<Recipe> matches = new ArrayList<>();

        try (Cursor cursor = getReadableDatabase().query(
                "recipes", null, null, null, null, null, "name COLLATE NOCASE")) {
            while (cursor.moveToNext()) {
                Recipe recipe = new Recipe(
                        cursor.getLong(cursor.getColumnIndexOrThrow("id")),
                        cursor.getString(cursor.getColumnIndexOrThrow("name")),
                        cursor.getString(cursor.getColumnIndexOrThrow("steps")));
                loadRequirements(recipe);

                if (IngredientMatcher.recipeMatches(pantryItems, recipe.getRequirements())) {
                    matches.add(recipe);
                }
            }
        }
        return matches;
    }

    private void loadRequirements(Recipe recipe) {
        try (Cursor cursor = getReadableDatabase().query(
                "recipe_ingredients",
                null,
                "recipe_id = ?",
                new String[]{String.valueOf(recipe.getId())},
                null,
                null,
                "id")) {
            while (cursor.moveToNext()) {
                recipe.addRequirement(new RecipeRequirement(
                        cursor.getString(cursor.getColumnIndexOrThrow("ingredient_name")),
                        cursor.getDouble(cursor.getColumnIndexOrThrow("quantity")),
                        cursor.getString(cursor.getColumnIndexOrThrow("unit"))));
            }
        }
    }

    private ContentValues pantryValues(
            String name, double quantity, String unit, String expiryDate) {
        ContentValues values = new ContentValues();
        values.put("name", name.trim());
        values.put("quantity", quantity);
        values.put("unit", unit);
        if (expiryDate == null || expiryDate.trim().isEmpty()) {
            values.putNull("expiry_date");
        } else {
            values.put("expiry_date", expiryDate.trim());
        }
        return values;
    }

    private PantryItem pantryItemFromCursor(Cursor cursor) {
        return new PantryItem(
                cursor.getLong(cursor.getColumnIndexOrThrow("id")),
                cursor.getString(cursor.getColumnIndexOrThrow("name")),
                cursor.getDouble(cursor.getColumnIndexOrThrow("quantity")),
                cursor.getString(cursor.getColumnIndexOrThrow("unit")),
                cursor.getString(cursor.getColumnIndexOrThrow("expiry_date")));
    }

    private void seedRecipes(SQLiteDatabase database) {
        database.beginTransaction();
        try {
            addRecipe(database, "Scrambled Eggs",
                    "1. Beat the eggs.\n2. Melt the butter in a pan.\n3. Cook the eggs gently and season with salt.",
                    ingredient("Egg", 2, "item"), ingredient("Butter", 10, "g"), ingredient("Salt", 0.25, "tsp"));
            addRecipe(database, "Tomato Omelette",
                    "1. Chop the tomato.\n2. Beat the eggs.\n3. Melt the butter, add tomato, then cook the eggs until set.",
                    ingredient("Egg", 2, "item"), ingredient("Tomato", 1, "item"), ingredient("Butter", 10, "g"), ingredient("Salt", 0.25, "tsp"));
            addRecipe(database, "Grilled Cheese Sandwich",
                    "1. Butter the bread.\n2. Place cheese between the slices.\n3. Toast both sides in a pan until golden.",
                    ingredient("Bread", 2, "item"), ingredient("Cheese", 50, "g"), ingredient("Butter", 10, "g"));
            addRecipe(database, "Peanut Butter Banana Toast",
                    "1. Toast the bread.\n2. Spread with peanut butter.\n3. Slice the banana over the toast.",
                    ingredient("Bread", 2, "item"), ingredient("Peanut butter", 30, "g"), ingredient("Banana", 1, "item"));
            addRecipe(database, "Tomato Toast",
                    "1. Toast the bread.\n2. Slice the tomato.\n3. Top the toast with tomato and a little salt.",
                    ingredient("Bread", 2, "item"), ingredient("Tomato", 1, "item"), ingredient("Salt", 0.25, "tsp"));
            addRecipe(database, "Fruit Salad",
                    "1. Wash the fruit.\n2. Chop it into bite-sized pieces.\n3. Mix and serve.",
                    ingredient("Apple", 1, "item"), ingredient("Banana", 1, "item"), ingredient("Orange", 1, "item"));
            addRecipe(database, "Baked Potatoes",
                    "1. Heat the oven to 200 C.\n2. Pierce and bake the potatoes until soft.\n3. Split, add butter and season.",
                    ingredient("Potato", 2, "item"), ingredient("Butter", 20, "g"), ingredient("Salt", 0.5, "tsp"));
            addRecipe(database, "Mashed Potatoes",
                    "1. Peel and boil the potatoes.\n2. Drain them.\n3. Mash with milk, butter and salt.",
                    ingredient("Potato", 3, "item"), ingredient("Butter", 30, "g"), ingredient("Milk", 100, "ml"), ingredient("Salt", 0.5, "tsp"));
            addRecipe(database, "Simple Rice",
                    "1. Rinse the rice.\n2. Add rice, water and salt to a pot.\n3. Cover and simmer until the water is absorbed.",
                    ingredient("Rice", 200, "g"), ingredient("Water", 400, "ml"), ingredient("Salt", 0.5, "tsp"));
            addRecipe(database, "Garlic Rice",
                    "1. Chop and lightly fry the garlic.\n2. Add rice, water and salt.\n3. Cover and simmer until cooked.",
                    ingredient("Rice", 200, "g"), ingredient("Water", 400, "ml"), ingredient("Garlic", 2, "item"), ingredient("Salt", 0.5, "tsp"));
            addRecipe(database, "Tomato Rice",
                    "1. Chop the onion and tomatoes.\n2. Cook them until soft.\n3. Add rice, water and salt, then simmer until cooked.",
                    ingredient("Rice", 200, "g"), ingredient("Water", 400, "ml"), ingredient("Tomato", 2, "item"), ingredient("Onion", 1, "item"), ingredient("Salt", 0.5, "tsp"));
            addRecipe(database, "Lentil Soup",
                    "1. Chop the vegetables.\n2. Put every ingredient in a pot.\n3. Simmer until the lentils are soft.",
                    ingredient("Lentils", 200, "g"), ingredient("Water", 750, "ml"), ingredient("Onion", 1, "item"), ingredient("Tomato", 2, "item"), ingredient("Salt", 0.5, "tsp"));
            addRecipe(database, "Tuna Sandwich",
                    "1. Mix the tuna and mayonnaise.\n2. Spread the mixture over one bread slice.\n3. Close the sandwich and serve.",
                    ingredient("Tuna", 150, "g"), ingredient("Bread", 2, "item"), ingredient("Mayonnaise", 30, "g"));
            addRecipe(database, "Egg Fried Rice",
                    "1. Chop and fry the onion in oil.\n2. Scramble in the eggs.\n3. Add cooked rice and stir until hot.",
                    ingredient("Rice", 300, "g"), ingredient("Egg", 2, "item"), ingredient("Oil", 15, "ml"), ingredient("Onion", 1, "item"));
            addRecipe(database, "Banana Smoothie",
                    "1. Peel the bananas.\n2. Blend them with the milk until smooth.\n3. Serve immediately.",
                    ingredient("Banana", 2, "item"), ingredient("Milk", 300, "ml"));
            addRecipe(database, "Apple Oats",
                    "1. Chop the apple.\n2. Simmer oats and milk until thick.\n3. Stir in the apple and serve.",
                    ingredient("Oats", 80, "g"), ingredient("Milk", 250, "ml"), ingredient("Apple", 1, "item"));
            addRecipe(database, "Simple Pancakes",
                    "1. Mix flour, milk, egg and sugar into a batter.\n2. Heat oil in a pan.\n3. Cook spoonfuls until golden on both sides.",
                    ingredient("Flour", 200, "g"), ingredient("Milk", 300, "ml"), ingredient("Egg", 2, "item"), ingredient("Sugar", 30, "g"), ingredient("Oil", 15, "ml"));
            addRecipe(database, "Tomato Pasta",
                    "1. Boil the pasta.\n2. Fry chopped onion and tomato in oil.\n3. Add the drained pasta, season and mix.",
                    ingredient("Pasta", 200, "g"), ingredient("Tomato", 3, "item"), ingredient("Onion", 1, "item"), ingredient("Oil", 15, "ml"), ingredient("Salt", 0.5, "tsp"));
            addRecipe(database, "Cucumber Salad",
                    "1. Slice the vegetables.\n2. Add oil and salt.\n3. Toss gently and serve.",
                    ingredient("Cucumber", 1, "item"), ingredient("Tomato", 2, "item"), ingredient("Onion", 0.5, "item"), ingredient("Oil", 15, "ml"), ingredient("Salt", 0.25, "tsp"));
            addRecipe(database, "Garlic Pasta",
                    "1. Boil the pasta.\n2. Gently fry chopped garlic in oil.\n3. Add pasta and salt, then toss well.",
                    ingredient("Pasta", 200, "g"), ingredient("Garlic", 2, "item"), ingredient("Oil", 30, "ml"), ingredient("Salt", 0.5, "tsp"));
            database.setTransactionSuccessful();
        } finally {
            database.endTransaction();
        }
    }

    private void addRecipe(
            SQLiteDatabase database, String name, String steps, SeedIngredient... ingredients) {
        ContentValues recipeValues = new ContentValues();
        recipeValues.put("name", name);
        recipeValues.put("steps", steps);
        long recipeId = database.insertOrThrow("recipes", null, recipeValues);

        for (SeedIngredient ingredient : ingredients) {
            ContentValues ingredientValues = new ContentValues();
            ingredientValues.put("recipe_id", recipeId);
            ingredientValues.put("ingredient_name", ingredient.name);
            ingredientValues.put("quantity", ingredient.quantity);
            ingredientValues.put("unit", ingredient.unit);
            database.insertOrThrow("recipe_ingredients", null, ingredientValues);
        }
    }

    private SeedIngredient ingredient(String name, double quantity, String unit) {
        return new SeedIngredient(name, quantity, unit);
    }

    private static class SeedIngredient {
        private final String name;
        private final double quantity;
        private final String unit;

        private SeedIngredient(String name, double quantity, String unit) {
            this.name = name;
            this.quantity = quantity;
            this.unit = unit;
        }
    }
}
