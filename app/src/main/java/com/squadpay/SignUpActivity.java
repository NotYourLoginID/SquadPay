package com.squadpay;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.firebase.client.AuthData;
import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;

import java.util.HashMap;
import java.util.Map;

public class SignUpActivity extends AppCompatActivity {
    private Firebase baseRef;

    private TextView mFirstName;
    private TextView mLastName;
    private TextView mUsername;
    private TextView mEmail;
    private TextView mPassword;
    private TextView mPasswordConf;
    private Button mSubmitButton;

    private HashMap<String, String> userDataHash;
    private Firebase usernamesRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        baseRef = new Firebase(getString(R.string.FIREBASE_URL));

        mFirstName = (EditText) findViewById(R.id.editTextFirstName);
        mLastName = (EditText) findViewById(R.id.editTextLastName);
        mUsername = (EditText) findViewById(R.id.editTextUsername);
        mEmail = (EditText) findViewById(R.id.editTextEmail);
        mPassword = (EditText) findViewById(R.id.editTextPassword);
        mPasswordConf = (EditText) findViewById(R.id.editTextPasswordConfirm);
        mSubmitButton = (Button) findViewById(R.id.buttonCreateUser);

        mSubmitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mSubmitButton.setClickable(false);
                if (mPassword.getText().toString().equals(mPasswordConf.getText().toString())) {
                    userDataHash = new HashMap<String, String>();
                    userDataHash.put("firstName", mFirstName.getText().toString());
                    userDataHash.put("lastName", mLastName.getText().toString());
                    userDataHash.put("username", mUsername.getText().toString().toLowerCase());
                    userDataHash.put("email", mEmail.getText().toString().toLowerCase());
                    userDataHash.put("password", mPassword.getText().toString());
                    userDataHash.put("password2", mPasswordConf.getText().toString());

                    usernamesRef = baseRef.child("usernames").child(userDataHash.get("username"));

                    usernamesRef.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot usernameSnapshot) {
                            if (usernameSnapshot.exists()) {
                                // username exists, error
                                mSubmitButton.setClickable(true);
                            } else {
                                baseRef.createUser(userDataHash.get("email"), userDataHash.get("password"), new Firebase.ValueResultHandler<Map<String, Object>>() {
                                    @Override
                                    public void onSuccess(Map<String, Object> result) {
                                        // user was created, store uid

                                        // log user in
                                        baseRef.authWithPassword(userDataHash.get("email"), userDataHash.get("password"), new Firebase.AuthResultHandler() {
                                            @Override
                                            public void onAuthenticated(AuthData authData) {
                                                userDataHash.put("uid", authData.getUid());

                                                usernamesRef.setValue(userDataHash.get("uid"), new Firebase.CompletionListener() {
                                                    @Override
                                                    public void onComplete(FirebaseError firebaseError, Firebase firebase) {
                                                        // added username to usernames-to-uid lookup table
                                                        if (firebaseError == null) {
                                                            // nothing got rekt
                                                            // create Map<String, String> to write to users table
                                                            Map<String, String> initUserData = new HashMap<String, String>();
                                                            initUserData.put("firstName", userDataHash.get("firstName"));
                                                            initUserData.put("lastName", userDataHash.get("lastName"));
                                                            initUserData.put("username", userDataHash.get("username"));
                                                            initUserData.put("email", userDataHash.get("email"));

                                                            baseRef.child("users").child(userDataHash.get("uid")).setValue(initUserData, new Firebase.CompletionListener() {
                                                                @Override
                                                                public void onComplete(FirebaseError firebaseError, Firebase firebase) {
                                                                    if (firebaseError == null) {
                                                                        // user is fully setup and should be logged in at this point
                                                                        Intent startMain = new Intent(SignUpActivity.this, SquadPayTabs.class);

                                                                        // android:noHistory="true" in the manifest should kill the activity when the next one starts
                                                                        startActivity(startMain);

                                                                    } else {
                                                                        // something esploded!!!!!11111
                                                                    }
                                                                }
                                                            });


                                                        } else {
                                                            //you dead, good luck debugging this
                                                        }
                                                    }
                                                });
                                            }

                                            @Override
                                            public void onAuthenticationError(FirebaseError firebaseError) {
                                                // login failed... THIS SHOULD NOT HAPPEN

                                            }
                                        });


                                    }

                                    @Override
                                    public void onError(FirebaseError firebaseError) {
                                        mSubmitButton.setClickable(true);

                                    }
                                });
                            }

                        }

                        @Override
                        public void onCancelled(FirebaseError firebaseError) {

                        }
                    });


                } else {
                    // display password does not match
                    mSubmitButton.setClickable(true);
                }
            }
        });
    }
}
