package za.ac.richfield.smartpantry;

import android.app.Activity;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.Locale;

import za.ac.richfield.smartpantry.data.DatabaseHelper;
import za.ac.richfield.smartpantry.model.PantryItem;

public class AddEditIngredientActivity extends Activity {
    public static final String EXTRA_ITEM_ID = "item_id";
    private static final long NEW_ITEM_ID = -1;
    private static final String[] UNITS = {
            "item", "g", "kg", "ml", "l", "tsp", "tbsp", "cup"
    };

    private DatabaseHelper databaseHelper;
    private EditText nameInput;
    private EditText quantityInput;
    private EditText expiryInput;
    private Spinner unitSpinner;
    private long itemId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_edit_ingredient);

        databaseHelper = new DatabaseHelper(this);
        nameInput = findViewById(R.id.nameInput);
        quantityInput = findViewById(R.id.quantityInput);
        expiryInput = findViewById(R.id.expiryInput);
        unitSpinner = findViewById(R.id.unitSpinner);

        ArrayAdapter<String> unitAdapter = new ArrayAdapter<>(
                this, android.R.layout.simple_spinner_item, UNITS);
        unitAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        unitSpinner.setAdapter(unitAdapter);

        itemId = getIntent().getLongExtra(EXTRA_ITEM_ID, NEW_ITEM_ID);
        if (itemId != NEW_ITEM_ID) {
            loadItem();
        } else {
            int defaultUnit = getSharedPreferences("smart_pantry_settings", MODE_PRIVATE)
                    .getInt("default_unit_index", 0);
            unitSpinner.setSelection(Math.max(0, Math.min(defaultUnit, UNITS.length - 1)));
        }

        findViewById(R.id.backButton).setOnClickListener(view -> finish());
        findViewById(R.id.saveButton).setOnClickListener(view -> saveItem());
    }

    private void loadItem() {
        PantryItem item = databaseHelper.getPantryItem(itemId);
        if (item == null) {
            Toast.makeText(this, "Ingredient could not be found.", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        ((TextView) findViewById(R.id.formTitle)).setText(R.string.edit_ingredient);
        nameInput.setText(item.getName());
        quantityInput.setText(formatQuantity(item.getQuantity()));
        expiryInput.setText(item.getExpiryDate() == null ? "" : item.getExpiryDate());

        for (int index = 0; index < UNITS.length; index++) {
            if (UNITS[index].equals(item.getUnit())) {
                unitSpinner.setSelection(index);
                break;
            }
        }
    }

    private void saveItem() {
        String name = nameInput.getText().toString().trim();
        String quantityText = quantityInput.getText().toString().trim();
        String expiryDate = expiryInput.getText().toString().trim();

        if (name.isEmpty()) {
            nameInput.setError("Enter an ingredient name.");
            nameInput.requestFocus();
            return;
        }

        if (!name.matches("[A-Za-z0-9][A-Za-z0-9 '\\-]{0,59}")) {
            nameInput.setError("Use letters, numbers, spaces, apostrophes or hyphens.");
            nameInput.requestFocus();
            return;
        }

        double quantity;
        try {
            quantity = Double.parseDouble(quantityText);
        } catch (NumberFormatException exception) {
            quantityInput.setError("Enter a valid number.");
            quantityInput.requestFocus();
            return;
        }

        if (Double.isNaN(quantity) || Double.isInfinite(quantity)
                || quantity <= 0 || quantity > 100000) {
            quantityInput.setError("Quantity must be greater than 0 and at most 100000.");
            quantityInput.requestFocus();
            return;
        }

        if (!expiryDate.isEmpty() && !isValidDate(expiryDate)) {
            expiryInput.setError("Use a real date in YYYY-MM-DD format.");
            expiryInput.requestFocus();
            return;
        }

        String unit = unitSpinner.getSelectedItem().toString();
        if (itemId == NEW_ITEM_ID) {
            databaseHelper.addPantryItem(name, quantity, unit, expiryDate);
            Toast.makeText(this, "Ingredient added.", Toast.LENGTH_SHORT).show();
        } else {
            databaseHelper.updatePantryItem(itemId, name, quantity, unit, expiryDate);
            Toast.makeText(this, "Ingredient updated.", Toast.LENGTH_SHORT).show();
        }
        finish();
    }

    private boolean isValidDate(String value) {
        if (!value.matches("\\d{4}-\\d{2}-\\d{2}")) {
            return false;
        }
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd", Locale.ROOT);
        format.setLenient(false);
        ParsePosition position = new ParsePosition(0);
        return format.parse(value, position) != null && position.getIndex() == value.length();
    }

    private String formatQuantity(double quantity) {
        if (quantity == Math.rint(quantity)) {
            return String.valueOf((long) quantity);
        }
        return String.valueOf(quantity);
    }

    @Override
    protected void onDestroy() {
        databaseHelper.close();
        super.onDestroy();
    }
}
