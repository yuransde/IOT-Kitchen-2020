package com.droidman.mvvm_mealdb;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.MenuItem;

import com.droidman.mvvm_mealdb.adapters.SearchAdapter;
import com.droidman.mvvm_mealdb.models.Recipe;
import com.droidman.mvvm_mealdb.viewmodels.SearchRecipeViewModel;

import java.util.List;

public class SearchRecipeActivity extends AppCompatActivity {
    private static final String TAG = "SearchRecipeActivity";
    private SearchRecipeViewModel mRecipeListViewModel;
    private String mCategory;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_recipe);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mCategory = getIntent().getStringExtra("CATEGORY");
        setTitle(mCategory);

        mRecipeListViewModel = ViewModelProviders.of(this).get(SearchRecipeViewModel.class);
        subscribeObservers();
        searchRecipe(mCategory);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void subscribeObservers() {
        mRecipeListViewModel.getmRecipes().observe(this, new Observer<List<Recipe>>() {
            @Override
            public void onChanged(@Nullable List<Recipe> recipes) {
                initSearchView(recipes);
                for (Recipe recipe : recipes) {
                    Log.d(TAG, "onChanged: " + recipe.getStrMeal());
                }
            }
        });

    }

    private void searchRecipe(String query) {
        mRecipeListViewModel.searchRecipe(query);
    }

    private void initSearchView(List<Recipe> recipes) {
        LinearLayoutManager SearchLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        RecyclerView SearchRecyclerView = findViewById(R.id.recycler_search);
        SearchRecyclerView.setLayoutManager(SearchLayoutManager);
        SearchAdapter searchAdapter = new SearchAdapter(this, recipes);
        SearchRecyclerView.setAdapter(searchAdapter);

    }
}
