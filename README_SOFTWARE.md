# Software Report

## Android Mobile Application
#### Repository Overview
All code for the software components can be found in the Bluetooth folder of the repository. The IOTKITCHEN folder will include Navigation Bar, Bluetooth Connectivity, User Authentication as well as Front End Developemnt. The MealDB folder is not used for the final version of the project. 
Listed below is the complete function of what each module does on the IOTKITCHEN folder:

 * BluetoothService: Multithread Bluetooth socket that connects to our sensors and parses the incoming data to differentiate between temperature and scale data.  When data is received, it sends it to the callback located in the CurrentRecipe class.  It is started by the database class when a recipe is begun and ended once the recipe is finished.
    
 * CurrentRecipe: Current_Recipe class that has getter setter functions and declared variables and contains the callback used when the bluetooth service receives data.  The callback saves the data in a local variable that will be saved into the database upon completion of the recipe.
    
  * Database: An ill titled class that is in charge of setting the UI elements that walk the user through a recipe.  It also begins the BluetoothService and initiates the saving of recipe data by the DatabaseMaster at the completion of the recipe.
    
 * DatabaseMaster: Class that is in charge of all firebase calls.  Is a global static class.  Holds a referance to the database and receives all relevant information from the database upon a user loggin in.  It also saves relevant data in the database once a recipe is completed.

 * ingredient: A class that sets the UI to display all of the needed ingredients for the currently selected recipe and has a button for the user to start cooking the recipe.
    
 * LoginActivity: Login the user with google firebase authentication.  Upon successful login, send the user to the MainActivity page to be welcomed.
    
 * Main_activity: Welcome page shows upon successful login and the page that congratulates the user after completing a recipe.
    
 * MyApplication: Master application class that has a basic callback function that is set by the CurrentRecipe class and is accessed by the BluetoothService.
 
 * Recipe_select: Activity that uses our custom adapter, RecipeListAdapter, to display all of the available recipes in the UI.  The user can then select from one of the recipes to be brough to the ingredient Activity to view all of the ingredients for the selected recipe.

 * RecipeData: A model class of recipe data with getter and setter functions made to work so that all of the data contained within can be saved in the database with one call.
    
 * RecipeListAdapatar: Custom adapter used to display the recipes.

 * RecipeModel: Holds the recipes information similar to as in firebase; holds the name, duration, ingredients, and instructions.  It is formatted in a way so that a single call to the firebase database can populate the class.
    
 *  SignOut: Signout the user and return to the login page.
   
 *  Steps: Class model to hold the current step/instruction information with getter and setter functions that work with firebase.
    
 *  UserData: Holds pertinent user data in a static model so it can be accessed globally.


#### Dependencies
The following are dependecies for Android Studio build.gradle

    implementation fileTree(dir: 'libs', include: ['*.jar'])
    implementation 'androidx.appcompat:appcompat:1.1.0'
    implementation 'com.google.android.material:material:1.1.0'


    implementation 'androidx.constraintlayout:constraintlayout:1.1.3'
    implementation 'com.google.firebase:firebase-database:19.2.0'
    implementation 'com.google.firebase:firebase-storage:16.0.4'
    implementation 'com.android.support:appcompat-v7:29.+'
    implementation 'com.android.support.constraint:constraint-layout:1.1.3'
    implementation 'com.android.support:support-v4:29.+'
    implementation 'androidx.legacy:legacy-support-v4:1.0.0'
    implementation 'androidx.lifecycle:lifecycle-extensions:2.0.0'
    testImplementation 'junit:junit:4.12'
    androidTestImplementation 'androidx.test.ext:junit:1.1.1'
    androidTestImplementation 'androidx.test.espresso:espresso-core:3.2.0'

    implementation 'com.google.firebase:firebase-auth:19.1.0'
    implementation 'com.google.firebase:firebase-analytics:17.2.0'
    implementation 'com.google.android.gms:play-services-auth:17.0.0'
    implementation 'com.google.firebase:firebase-firestore:21.4.0'

#### Development Tools Information
[Android Studio 3.5.2](https://developer.android.com/studio)
    
    compileSdkVersion 29
    buildToolsVersion "29.0.2"

[Sketch Version 60](https://www.sketch.com/get/)

[Adobe XD 24.3.22.2](https://www.adobe.com/products/xd.html)

#### Installation Guide
With Android Studio downloaded you can add an existing project. This can be done by downloading the IOTKithcen folder as a zip and then opening it with Android Studio. Below is how you can navigate to the application. 

    20-21-IotKitchen/Bluetooth/IOTKITCHEN/app/src/main/

An error may occur if you initially try and build/run the app.  To fix this error click Gradle on the far upper left of Android Studio and go to

    IOTKITCHEN -> Tasks -> android

And then double click signingReport.  At this point some stuff should run in the run tab, accessed by clicking the Run button at the bottom left of Android Studio.  At this point copy the string of HEX to the right of SHA1: and go to your firebase project.  Once into your firebase project, navigate to Project Settings.  Finally, scroll down to SHA certificate fingerprints and add the copied string to this list.  Now the app should be ready to run.

Then connect your android device or create a virtual device and build and run the application using the green arrow near the top middle of Android Studio, amking sure your desired device is selected in the box to the left of the arrow.

## Natural Language Understanding
#### Repository Overview
All code involved in developing the Natural Language Understanding (NLU) module can be found in the Voice folder of the repository. The folder DFdemo/DBupload contains the code to initialize and run the code that connects the Dialogflow API to the Google Firestore Cloudstore database. Upload.js provides the fulfillment functions for each individual intent. This is the only code in the DFdemo/DBupload folder that was used for the final version of the project. The other files in this folder are for testing and initializing a basic javascript project or alternative ways to initialize the database. In the Voice folder, all other files and folders are for a proof of concept for an Android app that performs text-to-speech. This code was not used in the final project but could be useful for the continuation of the project. For the purpose of this report, I am only going to focus on the code in the DFdemo/DBupload folder, because this is the code that is currently used in the project. A discussion of how the rest of the Voice code could be used is in the Engineering Addendum. 

#### Dependencies
The dependencies for upload.js are as follows:
```const functions = require('firebase-functions');
const admin = require('firebase-admin');
const {WebhookClient} = require('dialogflow-fulfillment');
```
These dependenices are automatically mapped when using the Dialogflow inline editor and do not require any additional software downloads.

#### Development Tools Information
NodeJS v12.10.0

Google Dialogflow V2

Google Firebase Cloud Firestore admin 8.7.0

#### Installation and Development Guide
Development of the NLU module does not require any installation, as all the Google tools can be found on online platforms. Development requires the following tools:

[Dialogflow](https://dialogflow.cloud.google.com/)

[Google Firebase Cloud Firestore](https://console.firebase.google.com/)

[Actions on Google](https://console.actions.google.com/)

These tools require a developer to have a Google account and email. An account can be created by going to google.com and clicking the "Sign In" button in the upper right corner. This account must be signed in to to use any of the three development tools. Once a developer has an account, you need to initialize a Google project by selecting "Add project" on any of the development tools. Once created, this project must be selected from the drop down menu of projects in the other development tools to link the tools together. If all three tools are linked to the same project, data can be shared between them. The README.md file contains more information on how these tools are used together to build an NLU module. 
