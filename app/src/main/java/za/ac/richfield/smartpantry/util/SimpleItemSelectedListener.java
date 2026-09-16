package za.ac.richfield.smartpantry.util;

import android.view.View;
import android.widget.AdapterView;

public class SimpleItemSelectedListener implements AdapterView.OnItemSelectedListener {
    public interface SelectionAction {
        void onSelected(int position);
    }

    private final SelectionAction action;

    public SimpleItemSelectedListener(SelectionAction action) {
        this.action = action;
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        action.onSelected(position);
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {
        // A Spinner always keeps one selected option in this screen.
    }
}
