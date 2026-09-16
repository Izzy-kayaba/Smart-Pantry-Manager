package za.ac.richfield.smartpantry.util;

import android.app.Activity;
import android.content.Intent;
import android.widget.Button;

import za.ac.richfield.smartpantry.PantryListActivity;
import za.ac.richfield.smartpantry.R;
import za.ac.richfield.smartpantry.SettingsActivity;
import za.ac.richfield.smartpantry.SuggestedRecipesActivity;

public final class NavigationHelper {
    public static final String PANTRY = "pantry";
    public static final String RECIPES = "recipes";
    public static final String SETTINGS = "settings";

    private NavigationHelper() {
    }

    public static void setup(Activity activity, String currentScreen) {
        Button pantryButton = activity.findViewById(R.id.navPantry);
        Button recipesButton = activity.findViewById(R.id.navRecipes);
        Button settingsButton = activity.findViewById(R.id.navSettings);

        setCurrentButton(currentScreen, pantryButton, recipesButton, settingsButton);

        pantryButton.setOnClickListener(view -> open(
                activity, currentScreen, PANTRY, PantryListActivity.class));
        recipesButton.setOnClickListener(view -> open(
                activity, currentScreen, RECIPES, SuggestedRecipesActivity.class));
        settingsButton.setOnClickListener(view -> open(
                activity, currentScreen, SETTINGS, SettingsActivity.class));
    }

    private static void setCurrentButton(
            String currentScreen, Button pantry, Button recipes, Button settings) {
        pantry.setEnabled(!PANTRY.equals(currentScreen));
        recipes.setEnabled(!RECIPES.equals(currentScreen));
        settings.setEnabled(!SETTINGS.equals(currentScreen));
    }

    private static void open(
            Activity activity,
            String currentScreen,
            String destination,
            Class<? extends Activity> destinationActivity) {
        if (currentScreen.equals(destination)) {
            return;
        }

        Intent intent = new Intent(activity, destinationActivity);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        activity.startActivity(intent);
        activity.finish();
    }
}
