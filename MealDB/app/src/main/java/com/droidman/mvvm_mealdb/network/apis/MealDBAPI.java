package com.droidman.mvvm_mealdb.network.apis;

import com.droidman.mvvm_mealdb.models.responses.RecipeResponse;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface MealDBAPI {

    /*Search Recipes*/
    @GET("search.php")
    Call<RecipeResponse> searchRecipe(
            @Query("s") String s
    );

    /*Get Recipe*/
    @GET("lookup.php")
    Call<RecipeResponse> getRecipe(
            @Query("i") int recipeId
    );
}
