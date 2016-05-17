package com.squadpay;


import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
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

import java.util.ArrayList;

public class Feed extends Fragment {
    protected FloatingActionButton floatingActionButton;
    protected RecyclerView expenseRecycler;
    protected LinearLayoutManager linLayoutManager;
    protected ExpenseAdapter expenseAdapter;
    protected Firebase fbRef;
    protected ArrayList feedDataset;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        fbRef = new Firebase(getString(R.string.FIREBASE_URL));
        feedDataset = new ArrayList<String>();
        setupDataset();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {


        View view = inflater.inflate(R.layout.feed, container, false);

        expenseAdapter = new ExpenseAdapter(feedDataset);

        expenseRecycler = (RecyclerView) view.findViewById(R.id.feed_recycler);
        linLayoutManager = new LinearLayoutManager(getActivity());
        expenseRecycler.setAdapter(expenseAdapter);
        expenseRecycler.setLayoutManager(linLayoutManager);


        return view;
    }

    private void setupDataset() {
        Firebase feed = fbRef.child("feed");
        feed.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                feedDataset = new ArrayList<String>();
                for(DataSnapshot feedSnapshot: dataSnapshot.getChildren()) {
                    feedDataset.add(feedSnapshot.getValue());
                    expenseAdapter.setDataSet(feedDataset);
                }
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {

            }
        });
    }


    class ExpenseAdapter extends RecyclerView.Adapter<ExpenseAdapter.StoryHolder>{
        private ArrayList<String> expenseData;

        public ExpenseAdapter() {
            expenseData = new ArrayList<String>();
        }

        public ExpenseAdapter(ArrayList<String> data) {
            expenseData = data;
        }

        public ArrayList<String> getDataSet() {
            return expenseData;
        }

        public void setDataSet(ArrayList<String> data) {
            expenseData = data;
            notifyDataSetChanged();
        }

        public void addToDataSet(String str){
            expenseData.add(str);
            notifyDataSetChanged();
        }

        @Override
        public int getItemCount() {
            return expenseData.size();
        }

        @Override
        public StoryHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            // set layout
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.feed_row, parent, false);
            StoryHolder storyHolder = new StoryHolder(view);
            return storyHolder;
        }

        @Override
        public void onBindViewHolder(StoryHolder holder, int position) {

            holder.mMsgText.setText(expenseData.get(position));


        }

        public class StoryHolder extends RecyclerView.ViewHolder {
            TextView mMsgText;


            public StoryHolder(View v) {
                super(v);
                this.mMsgText = (TextView) v.findViewById(R.id.textViewStory);
            }
        }
    }
}
