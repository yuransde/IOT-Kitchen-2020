package com.example.iotkitchen;

import com.google.firebase.firestore.DocumentReference;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

//Holds the current recipes data, such as the temperature and a reference to the original recipe in the database.
//To-Do: add weight and other information such as time and date
public class RecipeData {
    private DocumentReference masterRecipe;
    private ArrayList<Integer> temp;
    private Map<String, Integer> weight;

    public RecipeData() {};

    public RecipeData(RecipeModel recipeModel) {
        this.masterRecipe = recipeModel.getReference();
        temp = new ArrayList<Integer>();
        weight = new HashMap<String, Integer>();
    }

    //Required by firebase
    public DocumentReference getMasterRecipe() {
        return masterRecipe;
    }

    public ArrayList<Integer> getTemp() {
        return temp;
    }

    public void setTemp(ArrayList<Integer> temp) {
        this.temp = temp;
    }

    public Map<String, Integer> getWeight() {
        return weight;
    }

    public void setWeight(ArrayList<Integer> temp) {
        this.weight = weight;
    }

    public void addTemp(int temp) {
        this.temp.add(temp);
    }

    public void addWeight(int weight,String ing) {
        this.weight.put(ing,weight);
    }
}
