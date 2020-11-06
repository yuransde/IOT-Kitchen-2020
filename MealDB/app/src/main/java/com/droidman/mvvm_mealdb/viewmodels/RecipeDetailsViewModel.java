package com.droidman.mvvm_mealdb.viewmodels;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.ViewModel;

import com.droidman.mvvm_mealdb.models.Recipe;
import com.droidman.mvvm_mealdb.repositories.RecipeRepository;

import java.util.List;

public class RecipeDetailsViewModel extends ViewModel {
    private static final String TAG = "RecipeDetailsViewModel";

    private RecipeRepository mRecipeRepository;

    public RecipeDetailsViewModel() {
        mRecipeRepository = RecipeRepository.getInstance();
    }

    public LiveData<List<Recipe>> getmRecipe() {
        return mRecipeRepository.getmRecipe();
    }

    public void recipeDetails(int recipeId) {
        mRecipeRepository.recipeDetails(recipeId);
    }
}
