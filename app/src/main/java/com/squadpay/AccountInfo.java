package com.squadpay;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.widget.TextView;

import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;

public class AccountInfo extends AppCompatActivity {
    private Firebase ref;
    private String myID;
    private String username;
    private String displayname;
    private String temp;
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.account_info);

        ref = new Firebase("https://squadpay-live.firebaseio.com/users");
        myID = ref.getAuth().getUid();

        ref.child(myID).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                    System.out.println(postSnapshot.toString());
                    if (postSnapshot.getKey().toString().equals("displayname")){
                        displayname = postSnapshot.getValue().toString();
                        final TextView textViewToChange = (TextView) findViewById(R.id.account_name);
                        textViewToChange.setText("Display Name:  " + displayname);
                    }
                    if (postSnapshot.getKey().toString().equals("username")){
                        username = postSnapshot.getValue().toString();
                        final TextView textViewToChange2 = (TextView) findViewById(R.id.user_name);
                        textViewToChange2.setText("User Name:  " +username);
                    }

                }
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {

            }
        });
    }
}
