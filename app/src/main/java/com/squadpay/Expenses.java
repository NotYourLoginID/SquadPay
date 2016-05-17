package com.squadpay;


import android.content.Intent;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.HashMap;

public class Expenses extends Fragment {
    protected FloatingActionButton floatingActionButton;
    protected RecyclerView expenseRecycler;
    protected LinearLayoutManager linLayoutManager;
    protected ExpenseAdapter expenseAdapter;
    protected Firebase fbRef;
    protected ArrayList<HashMap<String,Object>> expenseDataset;

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        fbRef = new Firebase(getString(R.string.FIREBASE_URL));
        expenseDataset = new ArrayList<HashMap<String, Object>>();
        setupDataset();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {


        View view = inflater.inflate(R.layout.expenses, container, false);
        floatingActionButton = (FloatingActionButton) view.findViewById(R.id.fab);
        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), CreateExpenseActivity.class);
                startActivity(intent);
            }
        });

        expenseAdapter = new ExpenseAdapter(expenseDataset);

        expenseRecycler = (RecyclerView) view.findViewById(R.id.expense_recycler);
        linLayoutManager = new LinearLayoutManager(getActivity());
        expenseRecycler.setAdapter(expenseAdapter);
        expenseRecycler.setLayoutManager(linLayoutManager);





        return view;
    }

    private void setupDataset(){
        Firebase userExpenses = fbRef.child("users").child(fbRef.getAuth().getUid()).child("expenses").child("all");
        userExpenses.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                expenseDataset = new ArrayList<HashMap<String,Object>>();
                for(DataSnapshot expenseSnapshot: dataSnapshot.getChildren()){
                    String expenseId = expenseSnapshot.getKey();

                    Firebase expenseRef = fbRef.child("expenses").child(expenseId);
                    expenseRef.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            HashMap<String, Object> expenseMap = new HashMap<String, Object>();
                            expenseMap.put("title",dataSnapshot.child("title").getValue().toString());
                            expenseMap.put("description",dataSnapshot.child("description").getValue().toString());
                            expenseMap.put("due",dataSnapshot.child("members").child(fbRef.getAuth().getUid()).child("due").getValue());
                            expenseDataset.add(expenseMap);
                            expenseAdapter.setDataSet(expenseDataset);

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




    class ExpenseAdapter extends RecyclerView.Adapter<ExpenseAdapter.ExpenseHolder>{

        private ArrayList<HashMap<String, Object>> expenseData;

        public ExpenseAdapter(){
            expenseData = new ArrayList<HashMap<String, Object>>();
        }

        public ExpenseAdapter(ArrayList<HashMap<String, Object>> data){
            expenseData = data;
        }

        public ArrayList<HashMap<String, Object>> getDataSet(){
            return expenseData;
        }

        public void setDataSet(ArrayList<HashMap<String, Object>> data){
            expenseData = data;
            notifyDataSetChanged();
        }

        public void addToDataSet(HashMap<String, Object> map){
            expenseData.add(map);
            notifyDataSetChanged();
        }

        @Override
        public int getItemCount(){
            return expenseData.size();
        }
        @Override
        public ExpenseHolder onCreateViewHolder(ViewGroup parent, int viewType){
            // set layout
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.expense_row, parent, false);
            ExpenseHolder expenseHolder = new ExpenseHolder(view);
            return expenseHolder;
        }
        @Override
        public void onBindViewHolder(ExpenseHolder holder, int position){

            holder.mTitleText.setText(expenseData.get(position).get("title").toString());
            holder.mDescription.setText(expenseData.get(position).get("description").toString());
            String val = expenseData.get(position).get("due").toString();
            Double due = Double.parseDouble(val);
            NumberFormat nf = NumberFormat.getCurrencyInstance();
            holder.mAmountDue.setText(nf.format(due));
        }

        public class ExpenseHolder extends RecyclerView.ViewHolder{
            TextView mTitleText;
            TextView mAmountDue;
            TextView mDescription;

            public ExpenseHolder(View v){
                super(v);
                this.mTitleText = (TextView) v.findViewById(R.id.textViewTitle);
                this.mDescription = (TextView) v.findViewById(R.id.textViewDescription);
                this.mAmountDue = (TextView) v.findViewById(R.id.textViewCharge);
            }
        }
    }
}
