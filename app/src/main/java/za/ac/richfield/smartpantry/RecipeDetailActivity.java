package za.ac.richfield.smartpantry;

import android.app.Activity;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import java.text.DecimalFormat;

import za.ac.richfield.smartpantry.data.DatabaseHelper;
import za.ac.richfield.smartpantry.model.Recipe;
import za.ac.richfield.smartpantry.model.RecipeRequirement;

public class RecipeDetailActivity extends Activity {
    public static final String EXTRA_RECIPE_ID = "recipe_id";
    private DatabaseHelper databaseHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recipe_detail);

        databaseHelper = new DatabaseHelper(this);
        findViewById(R.id.backButton).setOnClickListener(view -> finish());

        long recipeId = getIntent().getLongExtra(EXTRA_RECIPE_ID, -1);
        Recipe recipe = databaseHelper.getRecipe(recipeId);
        if (recipe == null) {
            Toast.makeText(this, "Recipe could not be found.", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        ((TextView) findViewById(R.id.recipeTitle)).setText(recipe.getName());
        ((TextView) findViewById(R.id.ingredientsText)).setText(formatIngredients(recipe));
        ((TextView) findViewById(R.id.methodText)).setText(recipe.getSteps());
    }

    private String formatIngredients(Recipe recipe) {
        DecimalFormat quantityFormat = new DecimalFormat("0.##");
        StringBuilder text = new StringBuilder();
        for (RecipeRequirement requirement : recipe.getRequirements()) {
            if (text.length() > 0) {
                text.append("\n");
            }
            text.append("- ")
                    .append(quantityFormat.format(requirement.getQuantity()))
                    .append(" ")
                    .append(requirement.getUnit())
                    .append(" ")
                    .append(requirement.getIngredientName());
        }
        return text.toString();
    }

    @Override
    protected void onDestroy() {
        databaseHelper.close();
        super.onDestroy();
    }
}
