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
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.ucsd.tryclubs.Activity.ClubCreationActivity;
import com.ucsd.tryclubs.Activity.ClubProfileActivity;
import com.ucsd.tryclubs.Activity.SearchActivity;
import com.ucsd.tryclubs.Activity.UserProfileActivity;
import com.ucsd.tryclubs.Fragment.FavoringClubsFragment;
import com.ucsd.tryclubs.Fragment.FollowingEventsFragment;
import com.ucsd.tryclubs.Fragment.TimelineFragment;
import com.ucsd.tryclubs.Login.LoginActivity;
import com.ucsd.tryclubs.Model.Clubs;

/**
 * class MainActivity is the starter class in the App.
 * It sets the default view as the TimeLine Fragment.
 */
public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private static final String TAG = "MainActivity";

    // top bar stuff
    private DrawerLayout mDrawerLayout;
    private ActionBarDrawerToggle mDrawerToggle;
    private Toolbar toolbar; // maybe useful in the future, toolbar is the "Blue Bar" in the top

    // Firebase Instance
    private FirebaseAuth mAuth;
    private FirebaseDatabase mFirebaseDatabase;
    private DatabaseReference mUserRef;
    private DatabaseReference mUserMyClubRef;
    private DatabaseReference mClubs;

    // Fragment
    private TimelineFragment fTimelineFragment;

    // drawer content
    private NavigationView mNavigationView;
    private MenuItem mTimelineItem;
    private MenuItem mClublistsItem;
    private MenuItem mMyFollowing;
    private MenuItem mCreateAclub;
    private MenuItem mManageMyClub;
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
        setContentView(R.layout.drawer_layout);

        // set the timeline fragment as default
        fTimelineFragment = new TimelineFragment();
        setFragment(fTimelineFragment);

        // get authentication from Firebase
        mAuth = FirebaseAuth.getInstance();
        // locate Toolbar
        toolbar = (Toolbar) findViewById(R.id.main_toolbar);
        setSupportActionBar(toolbar);

        // SIDE DRAWER (aka NavigationView):
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer);
        mDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout, R.string.open, R.string.close);
        mDrawerLayout.addDrawerListener(mDrawerToggle);
        mDrawerToggle.syncState();
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mNavigationView = (NavigationView) findViewById(R.id.navigation_view);
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
                    CloseDrawer();
                } else {
                    // when user is signed in
                    goToUserProfileActivityHelper();
                    CloseDrawer();
                }
            }
        });

        // locate more components
        mUsername = headerView.findViewById(R.id.usernameTextView);
        mUserEmailAccount = headerView.findViewById(R.id.emailTextView);
        mTimelineItem = mNavigationView.getMenu().getItem(0);
        mClublistsItem = mNavigationView.getMenu().getItem(1);
        mMyFollowing = mNavigationView.getMenu().getItem(2);
        mCreateAclub = mNavigationView.getMenu().getItem(3);
        mManageMyClub = mNavigationView.getMenu().getItem(4);
        mLogoutItem = mNavigationView.getMenu().getItem(5);

        mTimelineItem.setChecked(true);
        getSupportActionBar().setTitle("Timeline");
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
            mClublistsItem.setVisible(false);
            mMyFollowing.setVisible(false);
            mCreateAclub.setVisible(false);
            mManageMyClub.setVisible(false);
            mLogoutItem.setVisible(false);
        } else {
            // when user is signed in, assign their username and email to the headerView
            String userEmailAddress = user.getEmail();
            mUserEmailAccount.setText(userEmailAddress);
            mClublistsItem.setVisible(true);
            mMyFollowing.setVisible(true);
            mCreateAclub.setVisible(true);
            mManageMyClub.setVisible(true);
            mLogoutItem.setVisible(true);
            mUsername.setText(user.getDisplayName());

            mFirebaseDatabase = FirebaseDatabase.getInstance();
            mUserRef = mFirebaseDatabase.getReference().child(getApplicationContext().getString(R.string.firebase_users_tag)).child(mAuth.getCurrentUser().getUid());
            mUserRef.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if (checkIfMyClubExist(dataSnapshot)) {
                        // when my_club exist
                        mManageMyClub.setVisible(true);
                        mCreateAclub.setVisible(false);
                    } else {
                        mManageMyClub.setVisible(false);
                        mCreateAclub.setVisible(true);
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
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

        if (item.getItemId() == R.id.action_search) {
            Intent SearchPage = new Intent(getApplicationContext(), SearchActivity.class);
            Log.d(TAG, "Going into Search Page");
            startActivity(SearchPage);
        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * Override system method onCreateOptionsMenu which Inflates(replaces)
     * the ToolBar to SearchBar's View
     *
     * @param menu [the "search" button specifically]
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.search, menu);
        return true;
    }

    /**
     * Override system method onNavigationItemSelected defines the behavior
     * of which an items in the drawer is clicked
     *
     * @param menuItem [an item in the drawer menu]
     */
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
        int id = menuItem.getItemId();

        if (id == R.id.drawer_timeline) { // timeline
            getSupportActionBar().setTitle("Timeline");
            setFragment(new TimelineFragment());
        } else if (id == R.id.drawer_favoring_clubs) { // favoring clubs
            getSupportActionBar().setTitle("Favoring Clubs");
            setFragment(new FavoringClubsFragment());
        } else if (id == R.id.drawer_my_following) { // following events
            getSupportActionBar().setTitle("Following Events");
            setFragment(new FollowingEventsFragment());
        } else if (id == R.id.drawer_create_club) { // create a club
            startActivity(new Intent(getApplicationContext(), ClubCreationActivity.class));
        } else if (id == R.id.drawer_my_club) { // manage my club
            Query q = mUserRef.child(getApplicationContext().getString(R.string.firebase_my_club_tag)).limitToFirst(1);
            q.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    DataSnapshot ds = dataSnapshot.getChildren().iterator().next();
                    Clubs club = ds.getValue(Clubs.class);
                    String cn = club.getClub_name();
                    Intent clubProfilePage = new Intent(getApplicationContext(), ClubProfileActivity.class);
                    clubProfilePage.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                    Log.d(TAG, "ManageMyClub Menu Item Intent Extra: " + cn);
                    clubProfilePage.putExtra(ClubProfileActivity.EXTRA, cn);
                    startActivity(clubProfilePage);
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        } else if (id == R.id.drawer_logout) { // log out
            userClickedLogOut();
        }

        CloseDrawer();
        return true;
    }

    /**
     * method userClickedLogOut signs out the user from the app
     * and called onStart() method to change stuff in the header view
     */
    private void userClickedLogOut() {
        mAuth.signOut();
        recreate();
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
    private void CloseDrawer() {
        if (mDrawerLayout.isDrawerOpen(GravityCompat.START)) {
            mDrawerLayout.closeDrawer(GravityCompat.START);
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

    private void setFragmentWithBackStack(Fragment fragment) {
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction().addToBackStack(null);
        fragmentTransaction.replace(R.id.main_container, fragment);
        fragmentTransaction.commit();
    }

    /**
     * method checkIfMyClubExist checks if the user has it own club
     */
    public boolean checkIfMyClubExist(DataSnapshot dataSnapshot) {
        Log.d(TAG, "checkIfMyClubExist");

        return dataSnapshot.hasChild(getApplicationContext()
                .getString(R.string.firebase_my_club_tag));
    }
}
