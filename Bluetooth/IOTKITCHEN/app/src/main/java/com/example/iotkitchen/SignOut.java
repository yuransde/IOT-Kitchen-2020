package com.example.iotkitchen;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.content.Intent;
import android.view.MenuItem;
import android.widget.Button;

import androidx.annotation.NonNull;

import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;

import android.view.View;

//Sign out the user and bring them to the login page
public class SignOut extends AppCompatActivity {


    Button button;
    FirebaseAuth.AuthStateListener mAuthListener;
    GoogleSignInClient mGoogleSignInClient;
    FirebaseAuth mAuth;

    @Override
    protected void onStart(){
        super.onStart();
        mAuth = FirebaseAuth.getInstance();
        mAuth.addAuthStateListener(mAuthListener);

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_out);

        button=(Button)findViewById(R.id.button);

        //Init and Assign Variables
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);

        //For the navigation ICON
        //navigation.setSelectedItemId(R.id.nav_home);
        bottomNavigationView.setSelectedItemId(R.id.nav_out);

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



        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mAuth.signOut();
                DatabaseMaster.databaseMaster.LoggedOut();
                UserData.userData.LoggedOut();
            }
        });




        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                if (firebaseAuth.getCurrentUser() == null) {
                    startActivity(new Intent(SignOut.this, MainActivity.class));
                    finish();
                }
            }
        };


    }
}
