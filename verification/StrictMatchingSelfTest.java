package za.ac.richfield.smartpantry.verification;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import za.ac.richfield.smartpantry.model.PantryItem;
import za.ac.richfield.smartpantry.model.RecipeRequirement;
import za.ac.richfield.smartpantry.util.IngredientMatcher;

public class StrictMatchingSelfTest {
    private static int checksRun = 0;

    public static void main(String[] args) {
        requiresEveryIngredient();
        rejectsInsufficientQuantity();
        acceptsCommonPluralNames();
        convertsWeightUnits();
        convertsVolumeUnits();
        addsDuplicatePantryRows();
        rejectsIncompatibleUnits();

        System.out.println("PASS: " + checksRun + " strict-matching checks completed.");
    }

    private static void requiresEveryIngredient() {
        List<PantryItem> pantry = Arrays.asList(
                item("Eggs", 2, "item"),
                item("Butter", 20, "g"));
        List<RecipeRequirement> fullRecipe = Arrays.asList(
                requirement("Egg", 2, "item"),
                requirement("Butter", 10, "g"));
        List<RecipeRequirement> recipeWithMissingSalt = Arrays.asList(
                requirement("Egg", 2, "item"),
                requirement("Butter", 10, "g"),
                requirement("Salt", 0.25, "tsp"));

        check(IngredientMatcher.recipeMatches(pantry, fullRecipe),
                "A recipe should match when every ingredient is available.");
        check(!IngredientMatcher.recipeMatches(pantry, recipeWithMissingSalt),
                "A recipe must be rejected when one ingredient is missing.");
    }

    private static void rejectsInsufficientQuantity() {
        List<PantryItem> pantry = Arrays.asList(item("Tomato", 1, "item"));
        List<RecipeRequirement> recipe = Arrays.asList(requirement("Tomato", 2, "item"));
        check(!IngredientMatcher.recipeMatches(pantry, recipe),
                "A recipe must be rejected when quantity is too low.");
    }

    private static void acceptsCommonPluralNames() {
        List<PantryItem> pantry = Arrays.asList(item("Tomatoes", 3, "items"));
        List<RecipeRequirement> recipe = Arrays.asList(requirement("Tomato", 2, "item"));
        check(IngredientMatcher.recipeMatches(pantry, recipe),
                "Tomatoes should match the singular name tomato.");
    }

    private static void convertsWeightUnits() {
        List<PantryItem> pantry = Arrays.asList(item("Flour", 1, "kg"));
        List<RecipeRequirement> recipe = Arrays.asList(requirement("Flour", 750, "g"));
        check(IngredientMatcher.recipeMatches(pantry, recipe),
                "One kilogram should satisfy a 750 gram requirement.");
    }

    private static void convertsVolumeUnits() {
        List<PantryItem> pantry = Arrays.asList(item("Milk", 1, "l"));
        List<RecipeRequirement> recipe = Arrays.asList(requirement("Milk", 3, "cup"));
        check(IngredientMatcher.recipeMatches(pantry, recipe),
                "One litre should satisfy a three cup requirement.");
    }

    private static void addsDuplicatePantryRows() {
        List<PantryItem> pantry = Arrays.asList(
                item("Rice", 100, "g"),
                item("Rice", 150, "g"));
        List<RecipeRequirement> recipe = Arrays.asList(requirement("Rice", 200, "g"));
        check(IngredientMatcher.recipeMatches(pantry, recipe),
                "Separate rows for the same ingredient should be added together.");
    }

    private static void rejectsIncompatibleUnits() {
        List<PantryItem> pantry = Arrays.asList(item("Oil", 2, "item"));
        List<RecipeRequirement> recipe = Arrays.asList(requirement("Oil", 15, "ml"));
        check(!IngredientMatcher.recipeMatches(pantry, recipe),
                "Count units must not be compared with volume units.");
    }

    private static PantryItem item(String name, double quantity, String unit) {
        return new PantryItem(0, name, quantity, unit, null);
    }

    private static RecipeRequirement requirement(String name, double quantity, String unit) {
        return new RecipeRequirement(name, quantity, unit);
    }

    private static void check(boolean result, String message) {
        checksRun++;
        if (!result) {
            throw new AssertionError("FAIL: " + message);
        }
    }
}
