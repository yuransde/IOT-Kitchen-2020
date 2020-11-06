package com.droidman.mvvm_mealdb;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.droidman.mvvm_mealdb.adapters.CategoriesAdapter;

import java.util.ArrayList;

public class CategoriesActivity extends AppCompatActivity {

    private static final String TAG = "CategoriesActivity";
    private ArrayList<String> mImageUrls = new ArrayList<>();
    private ArrayList<String> mNames = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_categories);
        setTitle("Categories");

        mImageUrls.add("https://www.themealdb.com/images/category/beef.png");
        mNames.add("Beef");

        mImageUrls.add("https://www.themealdb.com/images/category/Chicken.png");
        mNames.add("Chicken");

        mImageUrls.add("https://www.themealdb.com/images/category/Vegan.png");
        mNames.add("Vegan");

        mImageUrls.add("https://www.themealdb.com/images/category/Lamb.png");
        mNames.add("Lamb");

        mImageUrls.add("https://www.themealdb.com/images/category/Pasta.png");
        mNames.add("Pasta");

        mImageUrls.add("https://www.themealdb.com/images/category/Seafood.png");
        mNames.add("Seafood");

        //NEW
/*
        mImageUrls.add("https://www.themealdb.com/images/category/Dessert.png");
        mNames.add("Dessert");
*/
        initCategoriesView();

    }

    private void initCategoriesView() {
        LinearLayoutManager SearchLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        RecyclerView CategoriesRecyclerView = findViewById(R.id.recylcer_categories);
        CategoriesRecyclerView.setLayoutManager(SearchLayoutManager);
        CategoriesAdapter categoriesAdapter = new CategoriesAdapter(this, mNames, mImageUrls);
        CategoriesRecyclerView.setAdapter(categoriesAdapter);

    }

}
