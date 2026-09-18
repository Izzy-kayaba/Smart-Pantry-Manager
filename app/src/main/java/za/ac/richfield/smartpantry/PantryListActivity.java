package za.ac.richfield.smartpantry;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

import za.ac.richfield.smartpantry.adapter.PantryAdapter;
import za.ac.richfield.smartpantry.data.DatabaseHelper;
import za.ac.richfield.smartpantry.model.PantryItem;
import za.ac.richfield.smartpantry.util.NavigationHelper;

public class PantryListActivity extends Activity implements PantryAdapter.PantryItemActions {
    private DatabaseHelper databaseHelper;
    private ListView pantryList;
    private TextView pantryCount;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pantry_list);

        databaseHelper = new DatabaseHelper(this);
        pantryList = findViewById(R.id.pantryList);
        pantryCount = findViewById(R.id.pantryCount);
        View emptyPantry = findViewById(R.id.emptyPantry);
        pantryList.setEmptyView(emptyPantry);

        findViewById(R.id.addIngredientButton).setOnClickListener(view -> {
            Intent intent = new Intent(this, AddEditIngredientActivity.class);
            startActivity(intent);
        });

        NavigationHelper.setup(this, NavigationHelper.PANTRY);
    }

    @Override
    protected void onResume() {
        super.onResume();
        showPantryItems();
    }

    private void showPantryItems() {
        List<PantryItem> items = databaseHelper.getAllPantryItems();
        int itemCount = items.size();
        pantryCount.setText(getResources().getQuantityString(
                R.plurals.pantry_item_count, itemCount, itemCount));
        boolean alertsEnabled = getSharedPreferences("smart_pantry_settings", MODE_PRIVATE)
                .getBoolean("expiry_alerts", true);
        pantryList.setAdapter(new PantryAdapter(this, items, this, alertsEnabled));
    }

    @Override
    public void onEdit(PantryItem item) {
        Intent intent = new Intent(this, AddEditIngredientActivity.class);
        intent.putExtra(AddEditIngredientActivity.EXTRA_ITEM_ID, item.getId());
        startActivity(intent);
    }

    @Override
    public void onDelete(PantryItem item) {
        new AlertDialog.Builder(this)
                .setTitle("Delete ingredient?")
                .setMessage("Remove " + item.getName() + " from your pantry?")
                .setNegativeButton(R.string.cancel, null)
                .setPositiveButton(R.string.delete, (dialog, which) -> {
                    databaseHelper.deletePantryItem(item.getId());
                    showPantryItems();
                    Toast.makeText(
                            this,
                            getString(R.string.ingredient_deleted, item.getName()),
                            Toast.LENGTH_SHORT).show();
                })
                .show();
    }

    @Override
    protected void onDestroy() {
        databaseHelper.close();
        super.onDestroy();
    }
}
