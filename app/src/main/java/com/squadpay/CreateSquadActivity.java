package com.squadpay;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * The CreateSquadActivity allows the user to create their own squad based off of the current users
 * in the (soon to have) database. This activity utilizes a RecyclerView to display possible squad
 * members. (Max B.)
 */
public class CreateSquadActivity extends AppCompatActivity {

    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;
    private LinearLayoutManager mLayoutManager;
    private Firebase ref;
    /**
     * Keeps track of selected data. Keys are from myDataset. Values are booleans (true -> selected)
     */
    String newSquadName = "";
    ArrayList<String> squadMembers = new ArrayList();
    ArrayList<String> uid = new ArrayList();
    String addUser;
    String myId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_squad);
        ref = new Firebase("https://squadpay-live.firebaseio.com");

        mRecyclerView = (RecyclerView) findViewById(R.id.my_recycler_view);
        // TRying to find current user here
        myId = ref.getAuth().getUid();


        // use this setting to improve performance if you know that changes
        // in content do not change the layout size of the RecyclerView
        mRecyclerView.setHasFixedSize(true);

        // use a linear layout manager
        mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLayoutManager);

        // specify an adapter

        mAdapter = new PersonDataAdapter(squadMembers);
        mRecyclerView.setAdapter(mAdapter);


        Button createSquadButton = (Button) findViewById(R.id.create_squad);

        createSquadButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createNewSquad();
            }

        });

    }

    // Return a specific View from our RecyclerView by index of position (Max B.)
    private View getItem(int pos) {
        return mRecyclerView.getLayoutManager().findViewByPosition(pos);
    }

    /**
     * This method should eventually push new squads onto the database.
     * Only selected users will be added to a squad and the current user
     * must obey the input constraints required to make a squad.
     *
     * @param item
     */
    public void createNewSquad() {
        TextView tv = (TextView) findViewById(R.id.editText);
        String s = null;
        if (tv != null) {
            s = tv.getText().toString(); // Squad name here
            s = s.trim(); // cut whitespace
        }
        if (s == null || (s.equals("")) || s.length() > 20 || squadMembers.size() == 0) {
            Toast.makeText(this, "Please make sure you have entered a title less than 21 characters " +
                    "and have at least one other person selected for your squad", Toast.LENGTH_LONG).show();
        } else {
            // This is where the squad will be added to the database eventually
            // If selected then add to database, etc.
            newSquadName = s;
            Toast.makeText(this, "You have created a new Squad!", Toast.LENGTH_SHORT).show();

            uid.add(myId);

            Map<String, Object> squad = new HashMap<String, Object>();

            squad.put("squadname", newSquadName);

            Map<String, Object> members = new HashMap<String, Object>();
            for(String member: uid) {
                members.put(member, true);
            }
            squad.put("members", members);

            Firebase pushRef = ref.child("squads").push();
            pushRef.setValue(squad);

            final String pushKey = pushRef.getKey();

            ref.child("users").addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    for(DataSnapshot postSnapshot: dataSnapshot.getChildren()) {
                        String tUID = postSnapshot.getKey().toString();
                        if(uid.contains(tUID)) {
                            ref.child("users").child(tUID).child("squads").child(pushKey).setValue(true);
                        }
                    }
                }

                @Override
                public void onCancelled(FirebaseError firebaseError) {

                }
            });

            onBackPressed(); // go back
        }
    }
    public void findUser(View v) {
        final EditText editText = (EditText) findViewById(R.id.editText1);
        addUser = editText.getText().toString();

        Firebase emailRef = ref.child("usernames").child(addUser); // path
        emailRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (addUser.equals("")) { return; }
                String p = (String) dataSnapshot.getValue();
                if (p != null && !p.equals("")) {
                    if (squadMembers.contains(addUser)) { return; } // if the user exists don't add
                    squadMembers.add(addUser);
                    mAdapter.notifyDataSetChanged(); // update recyclerview
                    uid.add(p); // put uid into list
                    editText.setText(""); // clear the text if you found a person
                } else {
                    Toast.makeText(getApplicationContext(), "This user does not exist!", Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {
                // Nothing
            }
        });
    }
}
/** Adapter class for the RecyclerView. This adapter is currently used to take data from
 *  myDataset to post to the RecyclerView, created in the onCreate method of the CreateSquadActivity.
 *  This adapter has an inner ViewHolder class that maintains all the views for each RecyclerView item.
 *  Currently this adapter only supports TextViews. It also is responsible for initially highlighting
 *  the active users name in the RecyclerView. (Max B.)
 */
class PersonDataAdapter extends RecyclerView.Adapter<PersonDataAdapter.ViewHolder> {
    private ArrayList<String> mDataset;

    // Provide a reference to the views for each data item
    // Complex data items may need more than one view per item, and
    // you provide access to all the views for a data item in a view holder
    public class ViewHolder extends RecyclerView.ViewHolder {
        // each data item is just a string in this case
        public TextView mTextView;
        public ViewHolder(TextView v) {
            super(v);
            // still need to figure out this part below before I can add other views
            // to this recycler view. This might not be necessary right now
            if (v.getParent() != null) { ((ViewGroup) v.getParent()).removeView(v); }
            v.setTextSize(15f);
            mTextView = v;
        }
    }

    // Provide a suitable constructor (depends on the kind of dataset)
    public PersonDataAdapter(ArrayList<String> myDataset) {
        mDataset = myDataset;
    }

    // Create new views (invoked by the layout manager)
    @Override
    public PersonDataAdapter.ViewHolder onCreateViewHolder(ViewGroup parent,
                                                           int viewType) {
        // create a new view
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.recyclerview_squad_create, parent, false);
        // set the view's size, margins, paddings and layout parameters here
        // ...
        ViewHolder vh = new ViewHolder((TextView) v.findViewById(R.id.textView1));
        return vh;
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        // - get element from your dataset at this position
        // - replace the contents of the view with that element
        holder.mTextView.setText(mDataset.get(position));
        // inital set of active users background color (highlight)
    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return mDataset.size();
    }
}