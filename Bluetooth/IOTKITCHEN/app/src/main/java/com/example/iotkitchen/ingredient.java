package com.example.iotkitchen;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;

import java.util.ArrayList;

//Activity and page that lists all the ingredients of the selected recipe, before starting the recipes
public class ingredient extends AppCompatActivity {
    Button start;

    private ListView listView;
    ArrayAdapter<String> adapter;
    ArrayList<String> ingredients;

    int index;

    //Initialize all the UI elements
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ingredient);

        start=findViewById(R.id.start);
        index=getIntent().getIntExtra("IndexClicked",0);

        listView=findViewById(R.id.database);
        ingredients=DatabaseMaster.databaseMaster.GetPublicRecipes().get(index).getIngredient();

        //Set the adapter to display all of the ingredients in a list view
        if (ingredients != null) {
            adapter=new ArrayAdapter<>(ingredient.this, android.R.layout.simple_list_item_1, ingredients);
            listView.setAdapter(adapter);
        }


        //When the start button is clicked begin the recipe by starting the Database activity
        start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ingredient.this, Database.class);
                intent.putExtra("IndexClicked",index);
                startActivity(intent);
            }
        });

    }
}
