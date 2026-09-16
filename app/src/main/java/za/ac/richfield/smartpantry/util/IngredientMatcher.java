package za.ac.richfield.smartpantry.util;

import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import za.ac.richfield.smartpantry.model.PantryItem;
import za.ac.richfield.smartpantry.model.RecipeRequirement;

public final class IngredientMatcher {
    private static final double QUANTITY_TOLERANCE = 0.0001;
    private static final Map<String, String> NAME_ALIASES = new HashMap<>();

    static {
        NAME_ALIASES.put("tomatoes", "tomato");
        NAME_ALIASES.put("potatoes", "potato");
        NAME_ALIASES.put("loaves", "bread");
        NAME_ALIASES.put("bread slice", "bread");
        NAME_ALIASES.put("bread slices", "bread");
        NAME_ALIASES.put("clove garlic", "garlic");
        NAME_ALIASES.put("cloves garlic", "garlic");
        NAME_ALIASES.put("garlic clove", "garlic");
        NAME_ALIASES.put("garlic cloves", "garlic");
    }

    private IngredientMatcher() {
    }

    public static boolean recipeMatches(
            List<PantryItem> pantryItems,
            List<RecipeRequirement> requirements) {
        for (RecipeRequirement requirement : requirements) {
            double availableQuantity = 0;

            for (PantryItem pantryItem : pantryItems) {
                boolean sameIngredient = normalizeName(pantryItem.getName())
                        .equals(normalizeName(requirement.getIngredientName()));
                boolean compatibleUnits = unitGroup(pantryItem.getUnit())
                        .equals(unitGroup(requirement.getUnit()));

                if (sameIngredient && compatibleUnits) {
                    availableQuantity += toBaseUnit(
                            pantryItem.getQuantity(), pantryItem.getUnit());
                }
            }

            double requiredQuantity = toBaseUnit(
                    requirement.getQuantity(), requirement.getUnit());

            if (availableQuantity + QUANTITY_TOLERANCE < requiredQuantity) {
                return false;
            }
        }

        return true;
    }

    public static String normalizeName(String value) {
        String cleaned = value == null ? "" : value
                .toLowerCase(Locale.ROOT)
                .replaceAll("[^a-z0-9 ]", " ")
                .replaceAll("\\s+", " ")
                .trim();

        if (NAME_ALIASES.containsKey(cleaned)) {
            return NAME_ALIASES.get(cleaned);
        }

        // This small plural rule covers common pantry words such as eggs and onions.
        if (cleaned.length() > 3 && cleaned.endsWith("s")
                && !cleaned.endsWith("ss")) {
            cleaned = cleaned.substring(0, cleaned.length() - 1);
        }

        if (NAME_ALIASES.containsKey(cleaned)) {
            return NAME_ALIASES.get(cleaned);
        }
        return cleaned;
    }

    private static String unitGroup(String unit) {
        String value = normalizeUnit(unit);
        if (value.equals("g") || value.equals("kg")) {
            return "weight";
        }
        if (value.equals("ml") || value.equals("l")
                || value.equals("tsp") || value.equals("tbsp") || value.equals("cup")) {
            return "volume";
        }
        return "count";
    }

    private static double toBaseUnit(double quantity, String unit) {
        switch (normalizeUnit(unit)) {
            case "kg":
                return quantity * 1000;
            case "l":
                return quantity * 1000;
            case "tsp":
                return quantity * 5;
            case "tbsp":
                return quantity * 15;
            case "cup":
                return quantity * 250;
            default:
                return quantity;
        }
    }

    private static String normalizeUnit(String unit) {
        String value = unit == null ? "" : unit.toLowerCase(Locale.ROOT).trim();
        switch (value) {
            case "grams":
            case "gram":
                return "g";
            case "kilograms":
            case "kilogram":
                return "kg";
            case "millilitres":
            case "milliliters":
            case "millilitre":
            case "milliliter":
                return "ml";
            case "litres":
            case "liters":
            case "litre":
            case "liter":
                return "l";
            case "teaspoon":
            case "teaspoons":
                return "tsp";
            case "tablespoon":
            case "tablespoons":
                return "tbsp";
            case "cups":
                return "cup";
            case "items":
            case "pieces":
            case "piece":
            case "pcs":
                return "item";
            default:
                return value;
        }
    }
}
