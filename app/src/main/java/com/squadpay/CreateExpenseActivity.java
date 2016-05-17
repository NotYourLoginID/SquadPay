package com.squadpay;

import android.content.Context;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ServerValue;
import com.firebase.client.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CreateExpenseActivity extends AppCompatActivity {


    private static final String FIREBASE_URL = "https://squadpay-live.firebaseio.com";
    private Firebase firebaseRef;
    private List<String> squadNames;
    private Button createExpenseButton;
    private HashMap<String, String> map;

    @Override
    protected void onCreate(Bundle savedInstanceState) {


        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_expense);

        firebaseRef = new Firebase(FIREBASE_URL);
        map = new HashMap<>();

        firebaseRef.child("users").child(firebaseRef.getAuth().getUid()).child("squads").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                ArrayList<String> squadList = new ArrayList<String>();
                for(DataSnapshot postSnapshot: dataSnapshot.getChildren()) {
                    squadList.add(postSnapshot.getKey().toString());
                }
                squadNames = new ArrayList<String>();
                for(final String uniqueSquadName: squadList){
                    firebaseRef.child("squads").child(uniqueSquadName).child("squadname").addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            squadNames.add(dataSnapshot.getValue().toString());
                            map.put(dataSnapshot.getValue().toString(), uniqueSquadName);
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


        createExpenseButton = (Button) findViewById(R.id.create_expense);
        createExpenseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                final EditText name = (EditText) findViewById(R.id.name);
                final EditText amount = (EditText) findViewById(R.id.amount);
                final Double amountNum = Double.parseDouble(amount.getText().toString());
                final EditText description = (EditText) findViewById(R.id.description);

                Spinner spinner = (Spinner) findViewById(R.id.spinner);
                String squad = spinner.getSelectedItem().toString();

                final String uniqueSquadName = map.get(squad);

                final List<String> membersOfSquad = new ArrayList<String>();
                firebaseRef.child("squads").child(uniqueSquadName).child("members").addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        for(DataSnapshot postSnapshot: dataSnapshot.getChildren()) {
                            membersOfSquad.add(postSnapshot.getKey().toString());
                        }


                        Double amountDivided = amountNum / membersOfSquad.size();

                        Map<String, Object> expense = new HashMap<String, Object>();

                        expense.put("collected", "0");
                        expense.put("created", ServerValue.TIMESTAMP);
                        expense.put("description", description.getText().toString());
                        expense.put("owner", firebaseRef.getAuth().getUid());
                        expense.put("squad", uniqueSquadName);
                        expense.put("title", name.getText().toString());
                        expense.put("total", amountNum);
                        expense.put("updated", ServerValue.TIMESTAMP);

                        Map<String, Object> membersOfExpense = new HashMap<String, Object>();


                        for (String member: membersOfSquad) {
                            Map<String, Object> memberVals = new HashMap<String, Object>();
                            memberVals.put("assigned", ServerValue.TIMESTAMP);
                            memberVals.put("due", amountDivided);

                            membersOfExpense.put(member, memberVals);
                        }
                        expense.put("members", membersOfExpense);


                        Firebase pushRef = firebaseRef.child("expenses").push();
                        pushRef.setValue(expense);

                        String pushKey = pushRef.getKey();



                        firebaseRef.child("squads").child(uniqueSquadName).child("expenses").child(pushKey).setValue(true);


                        for (String member: membersOfSquad) {
                            if(member.equals(firebaseRef.getAuth().getUid())) {
                                firebaseRef.child("users").child(member).child("expenses").child("admins").child(pushKey).setValue(true);
                                firebaseRef.child("users").child(member).child("expenses").child("completed").child(pushKey).setValue(true);
                            }
                            firebaseRef.child("users").child(member).child("expenses").child("all").child(pushKey).setValue(true);
                        }

                    }

                    @Override
                    public void onCancelled(FirebaseError firebaseError) {

                    }
                });

                CreateExpenseActivity.this.finish();
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
