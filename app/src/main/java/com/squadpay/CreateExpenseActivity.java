package com.squadpay;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class CreateExpenseActivity extends AppCompatActivity {


    private static final String FIREBASE_URL = "https://squadpay-live.firebaseio.com";
    private Firebase firebaseRef;
    private List<String> squadNames;

    @Override
    protected void onCreate(Bundle savedInstanceState) {


        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_expense);

        firebaseRef = new Firebase(FIREBASE_URL);

        firebaseRef.child("users").child(firebaseRef.getAuth().getUid()).child("squads").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                ArrayList<String> squadList = new ArrayList<String>();
                for(DataSnapshot postSnapshot: dataSnapshot.getChildren()) {
                    squadList.add(postSnapshot.getKey().toString());
                }
                squadNames = new ArrayList<String>();
                for(String uniqueSquadName: squadList){
                    firebaseRef.child("squads").child(uniqueSquadName).child("squadname").addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            squadNames.add(dataSnapshot.getValue().toString());
                            inflateSpinner(squadNames);
                        }

                        @Override
                        public void onCancelled(FirebaseError firebaseError) {

                        }
                    });
                }



            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {

            }
        });

    }

    public void inflateSpinner(List<String> squadNames) {
        Spinner spinner = (Spinner) findViewById(R.id.spinner);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(
                getBaseContext(), android.R.layout.simple_spinner_item, squadNames);

        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
    }


}
