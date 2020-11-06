package com.example.iotkitchen;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

//Custom adapter used to display the recipes
public class RecipeListAdapter extends RecyclerView.Adapter<RecipeListAdapter.MyViewHolder> {

    private ArrayList<RecipeModel> recipes;
    private RecipeSelect recipeSelect;

    // Provide a suitable constructor (depends on the kind of dataset)
    public RecipeListAdapter(ArrayList<RecipeModel> recipes, RecipeSelect recipeSelect) {
        this.recipes = recipes;
        this.recipeSelect = recipeSelect;
    }

    // Provide a reference to the views for each data item
    // Complex data items may need more than one view per item, and
    // you provide access to all the views for a data item in a view holder
    public static class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        // each data item is just a string in this case
        public TextView title;
        public TextView time;
        public RecipeSelect recipeSelect;

        public MyViewHolder(LinearLayout v, RecipeSelect recipeSelect) {
            super(v);
            title = v.findViewById(R.id.recipeTitle);
            time = v.findViewById(R.id.recipeTime);
            this.recipeSelect = recipeSelect;

            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            recipeSelect.onRecipeSelect(getAdapterPosition());
        }
    }

    // Create new views (invoked by the layout manager)
    @NonNull
    @Override
    public RecipeListAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent,
                                                             int viewType) {
        // create a new view
        LinearLayout v = (LinearLayout) LayoutInflater.from(parent.getContext())
                .inflate(R.layout.recipe_view, parent, false);
        MyViewHolder vh = new MyViewHolder(v,this.recipeSelect);
        return vh;
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        // - get element from your dataset at this position
        // - replace the contents of the view with that element
        holder.title.setText(recipes.get(position).getTitle());
        holder.time.setText(recipes.get(position).getTime());
    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        if (recipes != null) {
            return recipes.size();
        }
        else {
            return 0;
        }
    }

    public interface RecipeSelect {
        void onRecipeSelect(int position);
    }
}