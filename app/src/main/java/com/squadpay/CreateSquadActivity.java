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
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.util.HashMap;

/**
 * The CreateSquadActivity allows the user to create their own squad based off of the current users
 * in the (soon to have) database. This activity utilizes a RecyclerView to display possible squad
 * members. (Max B.)
 */
public class CreateSquadActivity extends AppCompatActivity {

    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    /**
     * Keeps track of selected data. Keys are from myDataset. Values are booleans (true -> selected)
     */
    private HashMap<String, Boolean> map = new HashMap<>();
    // Hardcoded data until we figure the database out, **index 0 is always user's position**
    String[] myDataset = {"My Name (selected by default)", "Bob", "Pete", "Alice", "Joe", "Fetty Wap"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_squad);

        mRecyclerView = (RecyclerView) findViewById(R.id.my_recycler_view);

        // use this setting to improve performance if you know that changes
        // in content do not change the layout size of the RecyclerView
        mRecyclerView.setHasFixedSize(true);

        // use a linear layout manager
        mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLayoutManager);

        // specify an adapter
        initData(); // All other members are not selected by default
        mAdapter = new PersonDataAdapter(myDataset);
        mRecyclerView.setAdapter(mAdapter);

    }

    // Method for hardcoded data until we get the database figured out. Temporary method.
    private void initData() {
        for (String x : myDataset) {
            map.put(x, false);
        }
        map.put(myDataset[0], true);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_create_squad, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        } else if (id == R.id.add_squad) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * This method decides if a user has been selected and responses to changes in the background
     * of selected TextViews. True is selected. False is not selected.
     *
     * @param v
     */
    public void personSelection(View v) {
        TextView tv = (TextView) v;
        if (tv != null) {
            // myDataset[0] is the active user and they are always selected
            if (map.get(tv.getText().toString()) == false || myDataset[0].equals(tv.getText().toString()) == true) {
                tv.setBackgroundColor(Color.LTGRAY);
                map.put(tv.getText().toString(), true);
            } else {
                tv.setBackgroundColor(Color.WHITE);
                map.put(tv.getText().toString(), false);
            }
        }
    }

    /**
     * This method selects all users. If all users are already selected this method
     * will deselect all users. See UI.
     *
     * @param v
     */
    public void personSelectAll(View v) {
        View v1;

        if (selectedAll() == false) {
            for (int i = 1; i < myDataset.length; i++) {
                map.put(myDataset[i], false);
            }
        } else {
            for (int i = 1; i < myDataset.length; i++) {
                map.put(myDataset[i], true);
            }
        }
        for (int i = 1; i < myDataset.length; i++) {
            v1 = getItem(i);
            personSelection(v1);
        }
    }

    // Return true if all users are selected
    private boolean selectedAll() {
        for (int i = 1; i < myDataset.length; i++) {
            if (map.get(myDataset[i]) == false) {
                return false;
            }
        }
        return true;
    }

    // Returns true if all users are not selected
    private boolean selectedNone() {
        for (int i = 1; i < myDataset.length; i++) {
            if (map.get(myDataset[i]) == true) {
                return false;
            }
        }
        return true;
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
    public void createNewSquad(MenuItem item) {
        TextView tv = (TextView) findViewById(R.id.editText);
        String s = null;
        if (tv != null) {
            s = tv.getText().toString(); // Squad name here
            s = s.trim(); // cut whitespace
        }
        if (selectedNone() == true || s == null || (s.equals("")) || s.length() > 20) {
            Toast.makeText(this, "Please make sure you have entered a title less than 21 characters " +
                    "and have at least one other person selected for your squad", Toast.LENGTH_LONG).show();
        } else {
            // This is where the squad will be added to the database eventually
            // If selected then add to database, etc.
            Toast.makeText(this, "You have created a new Squad!", Toast.LENGTH_LONG).show();
        }
    }

}
/** Adapter class for the RecyclerView. This adapter is currently used to take data from
 *  myDataset to post to the RecyclerView, created in the onCreate method of the CreateSquadActivity.
 *  This adapter has an inner ViewHolder class that maintains all the views for each RecyclerView item.
 *  Currently this adapter only supports TextViews. It also is responsible for initially highlighting
 *  the active users name in the RecyclerView. (Max B.)
 */
class PersonDataAdapter extends RecyclerView.Adapter<PersonDataAdapter.ViewHolder> {
    private String[] mDataset;

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
            v.setTextSize(16f);
            mTextView = v;
        }
    }

    // Provide a suitable constructor (depends on the kind of dataset)
    public PersonDataAdapter(String[] myDataset) {
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
        holder.mTextView.setText(mDataset[position]);
        // inital set of active users background color (highlight)
        if (position == 0) {holder.mTextView.setBackgroundColor(Color.LTGRAY);}
    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return mDataset.length;
    }
}