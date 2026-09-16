package za.ac.richfield.smartpantry;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ListView;

import java.util.List;

import za.ac.richfield.smartpantry.adapter.RecipeAdapter;
import za.ac.richfield.smartpantry.data.DatabaseHelper;
import za.ac.richfield.smartpantry.model.Recipe;
import za.ac.richfield.smartpantry.util.NavigationHelper;

public class SuggestedRecipesActivity extends Activity {
    private DatabaseHelper databaseHelper;
    private ListView recipeList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_suggested_recipes);

        databaseHelper = new DatabaseHelper(this);
        recipeList = findViewById(R.id.recipeList);
        View emptyRecipes = findViewById(R.id.emptyRecipes);
        recipeList.setEmptyView(emptyRecipes);
        NavigationHelper.setup(this, NavigationHelper.RECIPES);
    }

    @Override
    protected void onResume() {
        super.onResume();
        List<Recipe> recipes = databaseHelper.getSuggestedRecipes();
        RecipeAdapter adapter = new RecipeAdapter(this, recipes);
        recipeList.setAdapter(adapter);
        recipeList.setOnItemClickListener((parent, view, position, id) -> {
            Recipe recipe = adapter.getItem(position);
            Intent intent = new Intent(this, RecipeDetailActivity.class);
            intent.putExtra(RecipeDetailActivity.EXTRA_RECIPE_ID, recipe.getId());
            startActivity(intent);
        });
    }

    @Override
    protected void onDestroy() {
        databaseHelper.close();
        super.onDestroy();
    }
}
