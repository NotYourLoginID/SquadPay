package com.squadpay;

import android.content.Intent;
import android.support.design.widget.NavigationView;
import android.support.design.widget.TabLayout;
import android.support.v4.content.ContextCompat;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.AdapterView;
import android.widget.TextView;

public class SquadPayTabs extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    /**
     * The {@link android.support.v4.view.PagerAdapter} that will provide
     * fragments for each of the sections. We use a
     * {@link FragmentPagerAdapter} derivative, which will keep every
     * loaded fragment in memory. If this becomes too memory intensive, it
     * may be best to switch to a
     * {@link android.support.v4.app.FragmentStatePagerAdapter}.
     */
    private SectionsPagerAdapter mSectionsPagerAdapter;

    /**
     * The {@link ViewPager} that will host the section contents.
     */
    private ViewPager mViewPager;

    DrawerLayout drawer;
    NavigationView navigationView;
    ActionBarDrawerToggle toggle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_squad_pay_tabs);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // Chris - attempting to make a nav button
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_menu);

        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.
        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager) findViewById(R.id.container);
        mViewPager.setAdapter(mSectionsPagerAdapter);

        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(mViewPager);

        drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        toggle = new ActionBarDrawerToggle(this, drawer, toolbar,
                R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        // Because I (Max B.) changed the background color of this activity in the XML to emulate
        // dividers between view items in the recyclerview, we have to implement a runtime change
        // of the status bar to keep it in sync with the overall theme. This is a sloppy way of
        // doing things, and if I have time I will change it.
        Window window = getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(ContextCompat.getColor(this,R.color.colorPrimary));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_squad_pay_tabs, menu);
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
        }
        else if (id == R.id.add_squad) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    /** Create a new squad. Clicking the action button (defined in the tabs menu xml file)
     *  will open the CreateSquadActivity. (Max B.)
     */
    public void createNewSquad(MenuItem item) {
        Intent intent = new Intent(this, CreateSquadActivity.class);
        startActivity(intent);
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    public void displayView(int viewId) {

        Fragment fragment = null;
        String title = getString(R.string.app_name);

        switch (viewId) {
            case R.id.nav_account_info:
                Intent intent = new Intent(this, AccountInfo.class);
                startActivity(intent);
                break;
            case R.id.nav_settings:
                Intent intent2 = new Intent(this, Settings.class);
                startActivity(intent2);
                break;

        }





        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);

    }

    public boolean onNavigationItemSelected(MenuItem item) {
        displayView(item.getItemId());
        return true;
    }


    /**
     * A placeholder fragment containing a simple view.
     */
    public static class PlaceholderFragment extends Fragment {
        /**
         * The fragment argument representing the section number for this
         * fragment.
         */
        private static final String ARG_SECTION_NUMBER = "section_number";
        private RecyclerView mRecyclerView; // Each fragment will have its own recycler view
        private TabDataAdapter mAdapter;    // Each fragment will have its own TabDataAdapter
        private LinearLayoutManager mLayoutManager; // Same here, too

        public PlaceholderFragment() {
        }
        /**
         * Returns a new instance of this fragment for the given section
         * number.
         */
        public static PlaceholderFragment newInstance(int sectionNumber) {
            PlaceholderFragment fragment = new PlaceholderFragment();
            Bundle args = new Bundle();
            args.putInt(ARG_SECTION_NUMBER, sectionNumber);
            fragment.setArguments(args);
            return fragment;
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View view = inflater.inflate(R.layout.fragment_squad_pay_tabs, container, false);
            mRecyclerView = (RecyclerView) view.findViewById(R.id.tabs_recycler_view);

            // use this setting to improve performance if you know that changes
            // in content do not change the layout size of the RecyclerView
            mRecyclerView.setHasFixedSize(true);

            // use a linear layout manager
            mLayoutManager = new LinearLayoutManager(getActivity().getBaseContext());
            mRecyclerView.setLayoutManager(mLayoutManager);

            // The next few lines of code will change heavily after firebase implementation
            // This is just a placeholder to show what data might look like
            String[] data = new String[100];
            if (getArguments().getInt(ARG_SECTION_NUMBER) == 3) {
                for (int i = 0; i < 100; i++) {
                    data[i] = "Item name" + "\n" + "Squad involved" + "\n" + "Paid status" + "\n" + "$Amount";
                }

            } else if (getArguments().getInt(ARG_SECTION_NUMBER) == 2) {
                for (int i = 0; i < 100; i++) {
                    data[i] = "Name of Squad" + "\n" + "Number of members" + "\n" + "Creation date";
                }
            } else if (getArguments().getInt(ARG_SECTION_NUMBER) == 1){
                for (int i = 0; i < 100; i++) {
                    data[i] = "Brief recent activity";
                }
            }
            // specify the adapter
            mAdapter = new TabDataAdapter(data);
            mRecyclerView.setAdapter(mAdapter);
            return view;
        }
    }

    /**
     * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
     * one of the sections/tabs/pages.
     */
    public class SectionsPagerAdapter extends FragmentPagerAdapter {
        final private int PAGE_COUNT = 3;
        final private String tabTitles[] = new String[] {"FEED", "SQUADS", "EXPENSES"};

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            // getItem is called to instantiate the fragment for the given page.
            // Return a PlaceholderFragment (defined as a static inner class below).
            return PlaceholderFragment.newInstance(position + 1);
        }

        @Override
        public int getCount() { return PAGE_COUNT; }

        @Override
        public CharSequence getPageTitle(int position) { return tabTitles[position]; }
    }
}
/** Adapter class for the RecyclerView. This adapter is currently used to take data from
 *  mDataset to post to the RecyclerView, created in the onCreate method of the SquadPayTabs Activity.
 *  This adapter has an inner ViewHolder class that maintains all the views for each RecyclerView item.
 *  Currently this adapter supports TextViews in a Framelayout.(Max B.)
 */
class TabDataAdapter extends RecyclerView.Adapter<TabDataAdapter.ViewHolder> {
    private String[] mDataset;
    // Provide a reference to the views for each data item
    // Complex data items may need more than one view per item, and
    // you provide access to all the views for a data item in a view holder
    public class ViewHolder extends RecyclerView.ViewHolder {
        public TextView mTextView;
        public FrameLayout mFrameLayout;
        public ViewHolder(FrameLayout v) {
            super(v);
            // still need to figure out this part below before I can add other views
            // to this recycler view. This might not be necessary right now
            if (v.getParent() != null) { ((ViewGroup) v.getParent()).removeView(v); }
            mFrameLayout = v;
            mTextView = (TextView) v.findViewById(R.id.textViewTabs);
            mTextView.setTextSize(16f);
        }
    }
    // Provide a suitable constructor (depends on the kind of dataset)
    public TabDataAdapter(String[] myDataset) {
        mDataset = myDataset;
    }

    // Create new views (invoked by the layout manager)
    @Override
    public TabDataAdapter.ViewHolder onCreateViewHolder(ViewGroup parent,
                                                           int viewType) {
        // create a new view
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.recyclerview_tabs, parent, false);
        // set the view's size, margins, paddings and layout parameters here
        // ...
        ViewHolder vh = new ViewHolder((FrameLayout) v.findViewById(R.id.frameLayoutTabs));
        return vh;
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        // - get element from your dataset at this position
        // - replace the contents of the view with that element
        holder.mTextView.setText(mDataset[position]);
    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return mDataset.length;
    }
}
