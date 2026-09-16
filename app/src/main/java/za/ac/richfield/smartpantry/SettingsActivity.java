package za.ac.richfield.smartpantry;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.Switch;

import za.ac.richfield.smartpantry.util.NavigationHelper;
import za.ac.richfield.smartpantry.util.SimpleItemSelectedListener;

public class SettingsActivity extends Activity {
    private static final String PREFERENCES = "smart_pantry_settings";
    private static final String[] DEFAULT_UNIT_OPTIONS = {
            "item", "g", "kg", "ml", "l", "tsp", "tbsp", "cup"
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        SharedPreferences preferences = getSharedPreferences(PREFERENCES, MODE_PRIVATE);
        Switch expiryAlertSwitch = findViewById(R.id.expiryAlertSwitch);
        Spinner defaultUnitSpinner = findViewById(R.id.defaultUnitSpinner);

        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                this, android.R.layout.simple_spinner_item, DEFAULT_UNIT_OPTIONS);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        defaultUnitSpinner.setAdapter(adapter);

        expiryAlertSwitch.setChecked(preferences.getBoolean("expiry_alerts", true));
        int savedUnit = preferences.getInt("default_unit_index", 0);
        defaultUnitSpinner.setSelection(
                Math.max(0, Math.min(savedUnit, DEFAULT_UNIT_OPTIONS.length - 1)));

        expiryAlertSwitch.setOnCheckedChangeListener((button, isChecked) ->
                preferences.edit().putBoolean("expiry_alerts", isChecked).apply());

        defaultUnitSpinner.setOnItemSelectedListener(new SimpleItemSelectedListener(position ->
                preferences.edit().putInt("default_unit_index", position).apply()));

        NavigationHelper.setup(this, NavigationHelper.SETTINGS);
    }
}
