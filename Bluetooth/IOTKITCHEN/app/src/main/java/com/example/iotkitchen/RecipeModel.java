package com.example.iotkitchen;

import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;

//Holds the recipes information similar to as in firebase; holds the name, duration, ingredients, and instructions
public class RecipeModel {
    private String title;
    private String time;
    private DocumentReference reference;
    private ArrayList<String> ingredientList;
    private ArrayList<Step> instructions;

    public RecipeModel() {}  // Needed for Firebase

    public RecipeModel(String title, String time, DocumentReference reference, ArrayList<String> ingredients, ArrayList<Step> instructions) {
        this.title = title;
        this.time = time;
        this.reference = reference;
        this.ingredientList = ingredients;
        this.instructions = instructions;
    }

    public String getTitle() { return title; }

    public void setTitle(String title) { this.title = title; }

    public ArrayList<String> getIngredient() { return ingredientList; }

    public void setIngredient(ArrayList<String> ingredients) { this.ingredientList = ingredients; }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public ArrayList<Step> getInstructions() {
        return instructions;
    }

    public void setInstructions(ArrayList<Step> instructions) {
        this.instructions = instructions;
    }

    public DocumentReference getReference() {
        return reference;
    }

    public void setReference(DocumentReference reference) {
        this.reference = reference;
    }

    public ArrayList<String> getIngredientList() {
        return ingredientList;
    }

    public void setIngredientList(ArrayList<String> ingredientList) {
        this.ingredientList = ingredientList;
    }
}
