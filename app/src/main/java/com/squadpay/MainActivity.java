package com.squadpay;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;

import com.firebase.client.AuthData;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;

// usually extends AppCompatActivity but I changed it to activity so that there would
// be no action bar. May be a better way to fix this
public class MainActivity extends AppCompatActivity {

    private static final String FIREBASE_URL = "https://squadpay-live.firebaseio.com";
    private Firebase squadPayFBRef;

    private EditText loginEmailField;
    private EditText loginPasswordField;
    private CoordinatorLayout mainCoordinatorLayout;
    private Button loginButton;
    private Button signUpButton;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        squadPayFBRef = new Firebase(FIREBASE_URL);

        loginEmailField = (EditText) findViewById(R.id.loginEmail);
        loginPasswordField = (EditText) findViewById(R.id.loginPassword);
        mainCoordinatorLayout = (CoordinatorLayout) findViewById(R.id.mainCoordinatorLayout);
        loginButton = (Button) findViewById(R.id.loginButton);
        signUpButton = (Button) findViewById(R.id.signUpButton);


        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                InputMethodManager kbm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);

                // this line below apparently will throw exceptions, the comment tells android to shutup about it for now
                //noinspection ConstantConditions
                kbm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);

                squadPayFBRef.authWithPassword(loginEmailField.getText().toString(), loginPasswordField.getText().toString(), new Firebase.AuthResultHandler() {
                    Snackbar loginSnackbar = Snackbar.make(mainCoordinatorLayout, "Invalid Login", Snackbar.LENGTH_SHORT);

                    @Override
                    public void onAuthenticated(AuthData authData) {
                        loginSnackbar.setText("Login Successful");
                        loginSnackbar.setCallback(new Snackbar.Callback() {
                            @Override
                            public void onDismissed(Snackbar s, int event) {
                                startActivity(new Intent(MainActivity.this, SquadPayTabs.class));
                                MainActivity.this.finish();
                            }
                        });
                        loginSnackbar.show();
                    }

                    @Override
                    public void onAuthenticationError(FirebaseError firebaseError) {
                        loginSnackbar.setCallback(new Snackbar.Callback() {
                            @Override
                            public void onDismissed(Snackbar s, int event) {
                                loginEmailField.setText("");
                                loginPasswordField.setText("");
                            }
                        });
                        loginSnackbar.show();
                    }
                });
            }
        });
    }
}


