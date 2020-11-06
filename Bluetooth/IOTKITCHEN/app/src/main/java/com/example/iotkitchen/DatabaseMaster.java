package com.example.iotkitchen;

import android.util.Log;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.SetOptions;


import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Objects;;

//All database calls
public class DatabaseMaster {
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    //Firebase reference to the users data, such as recipes and recipe data
    private DocumentReference userRef = db.collection("users").document(UserData.userData.id);

    //List of all the public recipes
    private ArrayList<RecipeModel> publicRecipes;

    //Persistant instance of this class
    public static DatabaseMaster databaseMaster = new DatabaseMaster();

    //Initialize the database and get users data
    public void SetUp() {
        userRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        Log.d("hyh", "Document exists");
                    } else {
                        //If new user, add relevant files in the database
                        HashMap empty = new HashMap();
                        userRef.collection("PersonalRecipes").document("tester")
                                .set(empty, SetOptions.merge());
                        userRef.collection("CookingSessions").document("tester")
                                .set(empty, SetOptions.merge());
                    }
                } else {
                    Log.d("huh", "get failed with ", task.getException());
                }
            }
        });
        //Get all the public recipes and set them in publicRecipes variable
        db.collection("recipes").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    publicRecipes = new ArrayList<>();
                    for (QueryDocumentSnapshot document : Objects.requireNonNull(task.getResult())) {
                        try {
                            RecipeModel temp;
                            temp = document.toObject(RecipeModel.class);
                            temp.setReference(document.getReference());
                            publicRecipes.add(temp);
                            running = false;
                        } catch (Exception e) {
                            Exception temp = e;
                        }
                    }
                } else {
                    Log.d("TAG", "Error getting documents: ", task.getException());
                }
                running = false;
            }
        });
    }

    public ArrayList<RecipeModel> GetPublicRecipes() {
        return publicRecipes;
    }

    //Clears data when user logs out
    public static void LoggedOut() {
        databaseMaster  = new DatabaseMaster();
    }

    //When the recipe is finished, saves the cooking session data in the database
    public void SaveData(RecipeData data) {
        //ArrayList<Integer> temp = data.getTemp();
        String name = Calendar.getInstance().getTime().toString();
        userRef.collection("CookingSessions").document(name)
                .set(data, SetOptions.merge());
    }
}
