package com.droidman.mvvm_mealdb.repositories;

import android.arch.lifecycle.LiveData;

import com.droidman.mvvm_mealdb.models.Recipe;
import com.droidman.mvvm_mealdb.network.RecipeApiClient;

import java.util.List;

public class RecipeRepository {
    private static final String TAG = "RecipeRepository";

    private static RecipeRepository instance;
    private RecipeApiClient mRecipeApiClient;

    public static RecipeRepository getInstance() {
        if (instance == null) {
            instance = new RecipeRepository();
        }
        return instance;
    }

    public RecipeRepository() {
        mRecipeApiClient = RecipeApiClient.getInstance();
    }

    public LiveData<List<Recipe>> getmRecipes() {
        return mRecipeApiClient.getmRecipes();
    }

    public LiveData<List<Recipe>> getmRecipe() {
        return mRecipeApiClient.getmRecipe();
    }

    public void searchRecipe(String query){
        mRecipeApiClient.searchRecipe(query);
    }

    public void recipeDetails(int recipeId){
        mRecipeApiClient.recipeDetails(recipeId);
    }

}
