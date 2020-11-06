package com.example.iotkitchen;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

//Holds relevant user data in a static model class
public class UserData {
    public String id = null;
    public String userName = null;
    public FirebaseUser user = null;

    public static UserData userData = new UserData();

    public void SetData() {
        user = FirebaseAuth.getInstance().getCurrentUser();
        id = user.getUid();
        userName = user.getDisplayName();

        DatabaseMaster.databaseMaster.SetUp();
    }

    public static void LoggedOut() {
        userData  = new UserData();
    }
}
