package com.example.iotkitchen;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.ArrayList;

//
//

//Activity/page where the user can select from all available public recipes in order to start cooking
public class Recipe_select extends AppCompatActivity implements RecipeListAdapter.RecipeSelect {

    //UI elements
    private RecyclerView recyclerView;
    private RecyclerView.LayoutManager layoutManager;
    RecipeListAdapter adapter;

    //Initialize UI elements
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recipe_select);

        //Init and Assign Variables
        BottomNavigationView bottomNavigationView =  findViewById(R.id.bottom_navigation);
        //
        //setContentView(R.layout.activity_recipe_select);
        bottomNavigationView.setSelectedItemId(R.id.nav_Recipe);

        //Perform
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                //BottomNavigationView.setOnNavigationItemSelectedListener();
                switch (menuItem.getItemId()) {

                    case R.id.nav_Scale: //scale
                        startActivity(new Intent(getApplicationContext()
                                , Database.class));
                        overridePendingTransition(0, 0);
                        return true;

                    case R.id.nav_home:
                        startActivity(new Intent(getApplicationContext()
                                , MainActivity.class));
                        overridePendingTransition(0, 0);
                        return true;

                    case R.id.nav_out:
                        startActivity(new Intent(getApplicationContext()
                                , SignOut.class));
                        overridePendingTransition(0, 0);
                        return true;

                    case R.id.nav_Recipe: //recipe
                        return true;
                }
                return false;
            }
        });

        recyclerView=findViewById(R.id.database);
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

        //Get the list of public recipes
        ArrayList<RecipeModel> test = DatabaseMaster.databaseMaster.GetPublicRecipes();

        //Create identical UI elements for every recipe and display in a list
        adapter=new RecipeListAdapter(test, this);
        recyclerView.setAdapter(adapter);
    }


    //When a recipe is selected, bring the user to ingredient page/activity to display all of the ingredients for the selected recipe
    @Override
    public void onRecipeSelect(int position) {
        Intent intent = new Intent(Recipe_select.this, ingredient.class);
        intent.putExtra("IndexClicked",position);
        startActivity(intent);
    }
}
