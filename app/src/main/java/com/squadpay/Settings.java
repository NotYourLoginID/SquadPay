package com.squadpay;

import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;

import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;

public class Settings extends AppCompatActivity {
    private Firebase ref;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.settings);

        final EditText email_ = (EditText) findViewById(R.id.email);
        final EditText old_Password = (EditText) findViewById(R.id.oldPassword);
        final EditText new_Password = (EditText) findViewById(R.id.newPassword);
        final EditText confirmPassword = (EditText) findViewById(R.id.confirmPassword);
        ref = new Firebase("https://squadpay-live.firebaseio.com");
        final RelativeLayout rl = (RelativeLayout) findViewById(R.id.relView);

        Button createSquadButton = (Button) findViewById(R.id.pwd_button);

        createSquadButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(new_Password.getText().toString().equals(confirmPassword.getText().toString())){
                    ref.changePassword(email_.getText().toString(), old_Password.getText().toString(),
                            new_Password.getText().toString(), new Firebase.ResultHandler() {
                                @Override
                                public void onSuccess() {
                                    Snackbar snackbar = Snackbar.make(rl, "Password successfully changed!", Snackbar.LENGTH_LONG);
                                    snackbar.show();
                                }

                                @Override
                                public void onError(FirebaseError firebaseError) {
                                    Snackbar snackbar = Snackbar.make(rl, "Password change not successful", Snackbar.LENGTH_LONG);
                                    snackbar.show();
                                }
                            });
                }else{
                    Snackbar snackbar = Snackbar.make(rl, "Passwords do not match", Snackbar.LENGTH_LONG);
                    snackbar.show();
                }
            }
        });


    }
}
