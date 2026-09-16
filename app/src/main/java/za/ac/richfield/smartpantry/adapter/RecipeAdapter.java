package za.ac.richfield.smartpantry.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.List;

import za.ac.richfield.smartpantry.R;
import za.ac.richfield.smartpantry.model.Recipe;

public class RecipeAdapter extends BaseAdapter {
    private final LayoutInflater inflater;
    private final List<Recipe> recipes;

    public RecipeAdapter(Context context, List<Recipe> recipes) {
        this.inflater = LayoutInflater.from(context);
        this.recipes = recipes;
    }

    @Override
    public int getCount() {
        return recipes.size();
    }

    @Override
    public Recipe getItem(int position) {
        return recipes.get(position);
    }

    @Override
    public long getItemId(int position) {
        return getItem(position).getId();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.row_recipe, parent, false);
            holder = new ViewHolder();
            holder.name = convertView.findViewById(R.id.recipeName);
            holder.ingredientCount = convertView.findViewById(R.id.ingredientCount);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        Recipe recipe = getItem(position);
        holder.name.setText(recipe.getName());
        int count = recipe.getRequirements().size();
        holder.ingredientCount.setText(holder.ingredientCount.getResources()
                .getQuantityString(R.plurals.ingredient_count, count, count));
        return convertView;
    }

    private static class ViewHolder {
        private TextView name;
        private TextView ingredientCount;
    }
}
