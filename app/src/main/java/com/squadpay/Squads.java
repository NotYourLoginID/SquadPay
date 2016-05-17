package com.squadpay;


import android.content.Intent;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutCompat;
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
import java.util.HashMap;

public class Squads extends Fragment {

    protected FloatingActionButton floatingActionButton;
    protected RecyclerView squadRecycler;
    protected LinearLayoutManager linLayoutManager;
    protected SquadAdapter squadAdapter;
    protected Firebase fbRef;
    protected ArrayList<HashMap<String,Object>> squadDataset;

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        fbRef = new Firebase(getString(R.string.FIREBASE_URL));
        squadDataset = new ArrayList<HashMap<String, Object>>();
        setupDataset();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {


        View view = inflater.inflate(R.layout.squads, container, false);
        floatingActionButton = (FloatingActionButton) view.findViewById(R.id.fab);
        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), CreateSquadActivity.class);
                startActivity(intent);
            }
        });

        squadAdapter = new SquadAdapter(squadDataset);

        squadRecycler = (RecyclerView) view.findViewById(R.id.squad_recycler);
        linLayoutManager = new LinearLayoutManager(getActivity());
        squadRecycler.setAdapter(squadAdapter);
        squadRecycler.setLayoutManager(linLayoutManager);





        return view;
    }

    private void setupDataset(){
        Firebase userSquads = fbRef.child("users").child(fbRef.getAuth().getUid()).child("squads");
        userSquads.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                squadDataset = new ArrayList<HashMap<String,Object>>();
                for(DataSnapshot squadSnapshot: dataSnapshot.getChildren()){
                    String squadId = squadSnapshot.getKey();

                    Firebase squadRef = fbRef.child("squads").child(squadId);
                    squadRef.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            HashMap<String, Object> squadMap = new HashMap<String, Object>();
                            squadMap.put("squadname",dataSnapshot.child("squadname").getValue().toString());
                            squadMap.put("count",dataSnapshot.child("members").getChildrenCount());
                            squadDataset.add(squadMap);
                            squadAdapter.setDataSet(squadDataset);

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




    class SquadAdapter extends RecyclerView.Adapter<SquadAdapter.SquadHolder>{

        private ArrayList<HashMap<String, Object>> squadData;

        public SquadAdapter(){
            squadData = new ArrayList<HashMap<String, Object>>();
        }

        public SquadAdapter(ArrayList<HashMap<String, Object>> data){
            squadData = data;
        }

        public ArrayList<HashMap<String, Object>> getDataSet(){
            return squadData;
        }

        public void setDataSet(ArrayList<HashMap<String, Object>> data){
            squadData = data;
            notifyDataSetChanged();
        }

        public void addToDataSet(HashMap<String, Object> map){
            squadData.add(map);
            notifyDataSetChanged();
        }

        @Override
        public int getItemCount(){
            return squadData.size();
        }
        @Override
        public SquadHolder onCreateViewHolder(ViewGroup parent, int viewType){
            // set layout
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.squad_row, parent, false);
            SquadHolder squadHolder = new SquadHolder(view);
            return squadHolder;
        }
        @Override
        public void onBindViewHolder(SquadHolder holder, int position){

            holder.mTitleText.setText(squadData.get(position).get("squadname").toString());
            holder.mMemberCounter.setText(squadData.get(position).get("count").toString()+" Members");
        }

        public class SquadHolder extends RecyclerView.ViewHolder{
            TextView mTitleText;
            TextView mMemberCounter;

            public SquadHolder(View v){
                super(v);
                this.mTitleText = (TextView) v.findViewById(R.id.textViewTitle);
                this.mMemberCounter = (TextView) v.findViewById(R.id.textViewCount);
            }
        }
    }
}
