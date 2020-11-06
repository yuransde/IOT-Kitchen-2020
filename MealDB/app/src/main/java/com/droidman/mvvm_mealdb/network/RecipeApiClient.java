package com.droidman.mvvm_mealdb.network;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.util.Log;

import com.droidman.mvvm_mealdb.models.Recipe;
import com.droidman.mvvm_mealdb.models.responses.RecipeResponse;
import com.droidman.mvvm_mealdb.utils.Helper;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RecipeApiClient {
    private static final String TAG = "RecipeApiClient";

    private static RecipeApiClient instance;
    private MutableLiveData<List<Recipe>> mRecipes;
    private MutableLiveData<List<Recipe>> mRecipe;


    public static RecipeApiClient getInstance() {
        if (instance == null) {
            instance = new RecipeApiClient();
        }
        return instance;
    }

    public RecipeApiClient() {
        mRecipes = new MutableLiveData<>();
        mRecipe = new MutableLiveData<>();
    }

    public LiveData<List<Recipe>> getmRecipes() {
        return mRecipes;
    }

    public LiveData<List<Recipe>> getmRecipe() {
        return mRecipe;
    }

    public void searchRecipe(String query) {
        Call<RecipeResponse> searchCall = Helper.getRestApi().searchRecipe(query);
        searchCall.enqueue(new Callback<RecipeResponse>() {
            @Override
            public void onResponse(Call<RecipeResponse> call, Response<RecipeResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<Recipe> recipeList = new ArrayList<>(response.body().getRecipeList());
                    mRecipes.postValue(recipeList);
                } else {
                    String error = String.valueOf(response.errorBody());
                    Log.e(TAG, "searchRecipe: " + error);
                }
            }

            @Override
            public void onFailure(Call<RecipeResponse> call, Throwable t) {
                Log.d(TAG, "onFailure: " + t.toString());
            }
        });
    }


    public void recipeDetails(int recipeId) {
        Call<RecipeResponse> detailCall = Helper.getRestApi().getRecipe(recipeId);
        detailCall.enqueue(new Callback<RecipeResponse>() {
            @Override
            public void onResponse(Call<RecipeResponse> call, Response<RecipeResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<Recipe> recipeList = response.body().getRecipeList();
                    mRecipe.postValue(recipeList);
                }
            }

            @Override
            public void onFailure(Call<RecipeResponse> call, Throwable t) {
                Log.d(TAG, "onFailure: " + t.toString());
            }
        });
    }
}
