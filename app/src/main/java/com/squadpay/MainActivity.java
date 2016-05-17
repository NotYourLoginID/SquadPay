package com.squadpay;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.firebase.client.AuthData;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;

// usually extends AppCompatActivity but I changed it to activity so that there would
// be no action bar. May be a better way to fix this
public class MainActivity extends AppCompatActivity{

    private static final String FIREBASE_URL = "https://squadpay-live.firebaseio.com";
    private Firebase firebaseRef;

    private EditText emailInput;
    private EditText passwordInput;
    private Button signInButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        firebaseRef = new Firebase(FIREBASE_URL);

        emailInput = (EditText) findViewById(R.id.loginEmail);
        passwordInput = (EditText) findViewById(R.id.loginPassword);

        // neat thing that allows you to hit enter instead of having to find button
        // probably should implement in other activity as well
        passwordInput.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int id, KeyEvent keyEvent) {
                if (id == R.id.login || id == EditorInfo.IME_NULL) {
                    attemptFirebaseLogin();
                }
                return false;
            }
        });

        signInButton = (Button) findViewById(R.id.loginButton);
        signInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //hide keyboard when successfully logged in
                InputMethodManager kbm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                kbm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);

                attemptFirebaseLogin();
            }
        });

    }

    private void attemptFirebaseLogin(){
        String email = this.emailInput.getText().toString();
        String password = this.passwordInput.getText().toString();

        firebaseRef.authWithPassword(email, password, new Firebase.AuthResultHandler() {

            @Override
            public void onAuthenticated(AuthData authData) {
                finish();
            }

            @Override
            public void onAuthenticationError(FirebaseError firebaseError) {
                // clear the text fields if wrong
                emailInput.setText("");
                passwordInput.setText("");
            }
        });
    }
}

