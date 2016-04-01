package com.squadpay;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.firebase.client.Firebase;

// usually extends AppCompatActivity but I changed it to activity so that there would
// be no action bar. May be a better way to fix this
public class MainActivity extends AppCompatActivity{

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // This is a test to see if commit and push works for me (Max B.)
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Firebase.setAndroidContext(this);

        Firebase myFirebaseRef = new Firebase("https://squadpay-live.firebaseio.com/");
    }

    public void attemptLogin(View view){
        Intent intent = new Intent(this, SquadPayTabs.class);
        startActivity(intent);
    }
}

