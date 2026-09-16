package za.ac.richfield.smartpantry.adapter;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.TextView;

import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import za.ac.richfield.smartpantry.R;
import za.ac.richfield.smartpantry.model.PantryItem;

public class PantryAdapter extends BaseAdapter {
    public interface PantryItemActions {
        void onEdit(PantryItem item);

        void onDelete(PantryItem item);
    }

    private final LayoutInflater inflater;
    private final List<PantryItem> items;
    private final PantryItemActions actions;
    private final boolean showExpiryAlerts;
    private final DecimalFormat quantityFormat = new DecimalFormat("0.##");

    public PantryAdapter(
            Context context,
            List<PantryItem> items,
            PantryItemActions actions,
            boolean showExpiryAlerts) {
        this.inflater = LayoutInflater.from(context);
        this.items = items;
        this.actions = actions;
        this.showExpiryAlerts = showExpiryAlerts;
    }

    @Override
    public int getCount() {
        return items.size();
    }

    @Override
    public PantryItem getItem(int position) {
        return items.get(position);
    }

    @Override
    public long getItemId(int position) {
        return getItem(position).getId();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.row_pantry_item, parent, false);
            holder = new ViewHolder();
            holder.name = convertView.findViewById(R.id.itemName);
            holder.quantity = convertView.findViewById(R.id.itemQuantity);
            holder.expiry = convertView.findViewById(R.id.itemExpiry);
            holder.edit = convertView.findViewById(R.id.editButton);
            holder.delete = convertView.findViewById(R.id.deleteButton);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        PantryItem item = getItem(position);
        holder.name.setText(item.getName());
        holder.quantity.setText(holder.quantity.getContext().getString(
                R.string.quantity_and_unit,
                quantityFormat.format(item.getQuantity()),
                item.getUnit()));

        if (item.getExpiryDate() == null || item.getExpiryDate().isEmpty()) {
            holder.expiry.setText(R.string.no_expiry_date);
            holder.expiry.setTextColor(Color.parseColor("#5D6A63"));
        } else {
            boolean expiringSoon = showExpiryAlerts && isWithinThreeDays(item.getExpiryDate());
            holder.expiry.setText(holder.expiry.getContext().getString(
                    expiringSoon ? R.string.expires_soon : R.string.expires_on,
                    item.getExpiryDate()));
            holder.expiry.setTextColor(Color.parseColor(expiringSoon ? "#A33A32" : "#5D6A63"));
        }

        holder.edit.setOnClickListener(view -> actions.onEdit(item));
        holder.delete.setOnClickListener(view -> actions.onDelete(item));
        return convertView;
    }

    private boolean isWithinThreeDays(String expiryDate) {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd", Locale.ROOT);
        format.setLenient(false);
        try {
            Date expiry = format.parse(expiryDate);
            Calendar end = Calendar.getInstance();
            end.add(Calendar.DAY_OF_YEAR, 3);
            Calendar start = Calendar.getInstance();
            start.add(Calendar.DAY_OF_YEAR, -1);
            return expiry != null && !expiry.after(end.getTime()) && expiry.after(start.getTime());
        } catch (ParseException exception) {
            return false;
        }
    }

    private static class ViewHolder {
        private TextView name;
        private TextView quantity;
        private TextView expiry;
        private Button edit;
        private Button delete;
    }
}
