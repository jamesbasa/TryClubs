package com.ucsd.tryclubs;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.miguelcatalan.materialsearchview.MaterialSearchView;
import com.ucsd.tryclubs.Activity.UserProfileActivity;
import com.ucsd.tryclubs.Fragment.ClublistFragment;
import com.ucsd.tryclubs.Fragment.TimelineFragment;
import com.ucsd.tryclubs.Login.LoginActivity;

/**
 * Class MainActivity sets the content to res/layout/activity_main.xml
 * and this is the TimeLine and the start screen of the App.
 *
 */
public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener{

    // top bar stuff
    private MaterialSearchView materialSearchView;
    private DrawerLayout mDrawerLayout;
    private ActionBarDrawerToggle mDrawerToggle;
    private Toolbar mMainToolBar; // maybe useful in the future, toolbar is the "Blue Bar" in the top

    // Firebase Instance
    private FirebaseAuth mAuth;

    // Fragment
    private TimelineFragment fTimelineFragment;

    // drawer content
    private NavigationView mNavigationView;
    private MenuItem mTimelineItem;
    private MenuItem mClublistsItem;
    private MenuItem mSettinsItem;
    private MenuItem mLogoutItem;
    // header content
    private View headerView;
    private ImageView mProfileView;
    private TextView mUserEmailAccount;
    private TextView mUsername;

    /**
     * OnCreate() method is the first method run automatically when
     * an Activity is called
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // get authentication from Firebase
        mAuth = FirebaseAuth.getInstance();

        // locate Toolbar
        Toolbar toolbar = (Toolbar)findViewById(R.id.main_toolbar);
        setSupportActionBar(toolbar);

        // set the timeline fragment as default
        fTimelineFragment = new TimelineFragment();
        setFragment(fTimelineFragment);

        // SEARCH BAR:
        // Library (Please Check): https://github.com/MiguelCatalan/MaterialSearchView
        // TODO - implementation
        materialSearchView = (MaterialSearchView) findViewById(R.id.search_view);

        // add auto fill in the search bar
        materialSearchView.setSuggestions(getResources().getStringArray(R.array.all_clubs_suggestions));
        materialSearchView.setOnQueryTextListener(new MaterialSearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                //Do some magic
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return true;
            }
        });

        materialSearchView.setOnSearchViewListener(new MaterialSearchView.SearchViewListener() {
            @Override
            public void onSearchViewShown() {
                //Do some magic
            }

            @Override
            public void onSearchViewClosed() {
                //Do some magic
            }
        });

        // SIDE DRAWER (aka NavigationView):
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer);
        mDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout, R.string.open, R.string.close);
        mDrawerLayout.addDrawerListener(mDrawerToggle);
        mDrawerToggle.syncState();
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mNavigationView = (NavigationView)findViewById(R.id.navigation_view);
        mNavigationView.setNavigationItemSelectedListener(this);
        // get the header stuff in side the side drawer
        headerView = mNavigationView.getHeaderView(0);
        mProfileView = (ImageView) headerView.findViewById(R.id.profileImageView);
        mProfileView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mAuth.getCurrentUser() == null) {
                    // when user is not signed in
                    goToLoginActivityHelper();
                } else {
                    // when user is signed in
                    goToUserProfileActivityHelper();
                }
            }
        });

        // locate more components
        mUsername = headerView.findViewById(R.id.usernameTextView);
        mUserEmailAccount = headerView.findViewById(R.id.emailTextView);
        mLogoutItem = mNavigationView.getMenu().getItem(3);
    }

    /**
     * onStart() will automatically call after onCreate()
     */
    @Override
    protected void onStart() {
        super.onStart();

        // get current user from Firebase
        FirebaseUser user = mAuth.getCurrentUser();

        // checking if user is signed in
        if (user == null) {
            // when user is not signed in
            mUsername.setText("Click Gary to Log In");
            mUserEmailAccount.setText("");
            mLogoutItem.setVisible(false);
        } else {
            // when user is signed in, assign their username and email to the headerView
            String userEmailAddress = user.getEmail();
            mUserEmailAccount.setText(userEmailAddress);
            mLogoutItem.setVisible(true);
            mUsername.setText(user.getDisplayName());
        }
    }

    /**
     * Override system method onBackPress() which defines the "back" button behaviour
     * in the Action Bar of Android
     */
    @Override
    public void onBackPressed() {
        if (mDrawerLayout.isDrawerOpen(GravityCompat.START)) {
            mDrawerLayout.closeDrawer(GravityCompat.START);
        } else if (materialSearchView.isSearchOpen()) {
            materialSearchView.closeSearch();
        } else {
            super.onBackPressed();
        }
    }

    /**
     * Override system method onOptionsItemSelected defines the "drawer opener" behavior
     * (change from "3 lines" to "x") located in the left-top position in the Main Page
     *
     * @param item [the item that is clicked]
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if (mDrawerToggle.onOptionsItemSelected(item)) {

            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * Override system method onCreateOptionsMenu which Inflates(replaces)
     * the ToolBar to SearchBar's View
     *
     * @param menu  [the "search" button specifically]
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.search, menu);

        MenuItem item = menu.findItem(R.id.action_search);
        materialSearchView.setMenuItem(item);

        return true;
    }

    /**
     * Override system method onNavigationItemSelected defines the behavior
     * of which an items in the drawer is clicked
     *
     * @param menuItem  [an item in the drawer menu]
     */
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
        int id = menuItem.getItemId();

        // TODO - Fill in
        if (id == R.id.timeline) {
            getSupportActionBar().setTitle("Timeline");
            setFragment(new TimelineFragment());
        } else if (id == R.id.clublist) {
            getSupportActionBar().setTitle("Clublist");
            setFragment(new ClublistFragment());
        } else if (id == R.id.settings) {

        } else if (id == R.id.logout_in_drawer) {
            userClickedLogOut();
        }

        CloseDrawerAndSearchBar();
        return true;
    }

    /**
     * method userClickedLogOut signs out the user from the app
     * and called onStart() method to change stuff in the header view
     */
    private void userClickedLogOut() {
        mAuth.signOut();
        onStart();
    }

    /**
     * Helper method which uses Intent to go to the Login Page
     */
    private void goToLoginActivityHelper() {
        Intent loginPageIntent = new Intent(getApplicationContext(), LoginActivity.class);
        startActivity(loginPageIntent);
    }

    /**
     * Helper method which uses Intent to go to the user profile page
     */
    private void goToUserProfileActivityHelper() {
        Intent userPageIntent = new Intent(getApplicationContext(), UserProfileActivity.class);
        startActivity(userPageIntent);
    }

    /**
     * Helper method which uses Intent to go back to the Main Page (Timeline)
     */
    private void goToMainActivityHelper() {
        startActivity(new Intent(getApplicationContext(), MainActivity.class));
        finish();
    }

    /**
     * Hepler method which closes the Drawer when an item is clicked
     */
    private void CloseDrawerAndSearchBar() {
        if (mDrawerLayout.isDrawerOpen(GravityCompat.START)) {
            mDrawerLayout.closeDrawer(GravityCompat.START);
        } else if (materialSearchView.isSearchOpen()) {
            materialSearchView.closeSearch();
        }
    }

    /**
     * Switching screen of the app between different fragment
     */
    private void setFragment(Fragment fragment) {
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.main_container, fragment);
        fragmentTransaction.commit();
    }
}
