package com.example.iotkitchen;

import  androidx.appcompat.app.AppCompatActivity;


import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;


import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.ArrayList;

//Activity that is run when the user is cooking a recipe, displays the current instruction and walks the user through the recipe
public class Database extends AppCompatActivity {

    //UI parts
    Button next, previous;
    TextView a, b, c, d, e, w, in;

    //Current step number
    int i = 0;
    int size;

    //Current recipe being cooked, holds the name, steps, instructions, etc., holds general information; not persistent
    RecipeModel recipe;

    //List of the instructions for this recipe
    ArrayList<Step> instructions;

    //The current step
    Step activeStep;

    //The index of the current recipe
    int index;
    String tempText;

    //The current recipe being cooked, only pertinent information such as instructions and current step number; persistent
    private static CurrentRecipe currentRecipe;

    //When this page/activity is started, initialize the relevant UI elements
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        //data.put("exist", true);

        //mDatabase.collection("users").document(username)
                //.set(data, SetOptions.merge());

        //userData = mDatabase.collection("users").document(username);

        //Navigation
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_database);

        //
        a = (TextView) findViewById(R.id.Ingredient_view);
        b = (TextView) findViewById(R.id.procedure_view);
        c = (TextView) findViewById(R.id.unit_view);
        d = (TextView) findViewById(R.id.weight_view);
        e = (TextView) findViewById(R.id.step);
        w = findViewById(R.id.weight);
        in = findViewById(R.id.Ingredient);
        next = (Button) findViewById(R.id.next);
        previous = (Button) findViewById(R.id.previous);

        index = getIntent().getIntExtra("IndexClicked", -1);
        //Invalid situation, should not be here, leave, kicks user out
        if (index == -1 && currentRecipe == null) {
            finish();
        }
        //Returning to recipe, display the current step
        else if (currentRecipe != null && index == -1) {
            recipe = currentRecipe.getCurrentRecipe();
            i = currentRecipe.getStepNum();
            instructions = recipe.getInstructions();
            SetStep();
        }
        //New recipe being started, get and set relevant information
        else {
            recipe = DatabaseMaster.databaseMaster.GetPublicRecipes().get(index);
            if (currentRecipe == null) {
                currentRecipe = new CurrentRecipe(recipe);
                instructions = recipe.getInstructions();
                if (instructions != null) {
                    size = instructions.size();
                    activeStep = instructions.get(i);

                    SetStep();

                    //Start bluetooth
                    Intent serviceIntent = new Intent(this, BluetoothService.class);
                    startService(serviceIntent);
                }
            } else if (recipe.getTitle() != currentRecipe.getCurrentRecipe().getTitle()) {
                currentRecipe = new CurrentRecipe(DatabaseMaster.databaseMaster.GetPublicRecipes().get(index));
                instructions = recipe.getInstructions();
                if (instructions != null) {
                    size = instructions.size();
                    activeStep = instructions.get(i);

                    SetStep();
                }
            } else if (i != currentRecipe.getStepNum()) {
                i = currentRecipe.getStepNum();
                SetStep();
            }
        }

        //If next is clicked change UI to the next step or finish the recipe and save the data in the database
        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (i + 1 < size) {
                    i = i + 1;
                    SetStep();
                }
                else if (i == size-1) {
                    DatabaseMaster.databaseMaster.SaveData(currentRecipe.getRecipeData());
                    Intent intent = new Intent(Database.this, MainActivity.class);
                    intent.putExtra("Completed", "Congrats!\n\n  You just finished cooking the recipe:\n\n" + currentRecipe.getCurrentRecipe().getTitle() + "\n\n We hope you enjoy it!");
                    currentRecipe = null;
                    startActivity(intent);
                }
            }
        });
        //If previous is clicked, go to the previous step if not already on the first step
        previous.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (i >= 1) {
                    i = i - 1;
                    SetStep();
                }
            }
        });

        //Init and Assign Variables
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);

        //Set Home
        //BottomNavigationView.setSelectedItemId(R.id.Database)
        bottomNavigationView.setSelectedItemId(R.id.nav_Scale);

        //Perform
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                //BottomNavigationView.setOnNavigationItemSelectedListener();
                switch (menuItem.getItemId()) {

                    case R.id.nav_Scale: //scale

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
                        startActivity(new Intent(getApplicationContext()
                                , Recipe_select.class));
                        overridePendingTransition(0, 0);
                        return true;
                }
                return false;
            }
        });
    }

    //Set the UI text elements for the steps
    private void SetStep() {
        activeStep = instructions.get(i);
        if (activeStep.getIngredient() == null) {
            a.setText("");
            in.setText("");
        }
        else {
            in.setText("Ingredients");
            a.setText(activeStep.getIngredient());
            currentRecipe.setCurrentIngredient(activeStep.getIngredient());
        }
        b.setText(activeStep.getProcedure());
        if (activeStep.getWeight() == 0) {
            c.setText("");
            d.setText("");
            w.setText("");
        }
        else {
            w.setText("Weight");
            c.setText(activeStep.getUnit());
            d.setText(Integer.toString(activeStep.getWeight()));
        }
        tempText = "step" + " " + "" + Integer.toString(i+1) + "";
        next.setText("next");
        previous.setVisibility(View.VISIBLE);
        if (i == size-1) {
            tempText = "Final step";
            next.setText("Finish");
        }
        else if (i == 0) {
            previous.setVisibility(View.INVISIBLE);
        }
        e.setText(tempText);
    }

    @Override
    public void onResume () {
        super.onResume();
        if (instructions != null) {
            SetStep();
        }
    }

    @Override
    public void onPause () {
        super.onPause();
        if (currentRecipe != null) {
            currentRecipe.setStepNum(i);
        }
    }
}

