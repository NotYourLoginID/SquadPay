package com.squadpay;

import android.app.Activity;
import android.os.Bundle;

import com.firebase.client.Firebase;

// usually extends AppCompatActivity but I changed it to activity so that there would
// be no action bar. May be a better way to fix this
public class MainActivity extends Activity{

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // This is a test to see if commit and push works for me (Max B.)
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Firebase.setAndroidContext(this);

        Firebase myFirebaseRef = new Firebase("https://squadpay-live.firebaseio.com/");
    }
}

