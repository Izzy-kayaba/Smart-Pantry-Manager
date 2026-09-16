package za.ac.richfield.smartpantry.model;

public class RecipeRequirement {
    private final String ingredientName;
    private final double quantity;
    private final String unit;

    public RecipeRequirement(String ingredientName, double quantity, String unit) {
        this.ingredientName = ingredientName;
        this.quantity = quantity;
        this.unit = unit;
    }

    public String getIngredientName() {
        return ingredientName;
    }

    public double getQuantity() {
        return quantity;
    }

    public String getUnit() {
        return unit;
    }
}
