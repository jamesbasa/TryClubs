package com.ucsd.tryclubs.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.ucsd.tryclubs.Fragment.ClublistFragment;
import com.ucsd.tryclubs.Model.ClubMembers;
import com.ucsd.tryclubs.Model.Clubs;
import com.ucsd.tryclubs.Model.FollowingClubs;
import com.ucsd.tryclubs.R;
import com.ucsd.tryclubs.ViewHolder.ClubPageInfoSectionViewHolder;
import com.ucsd.tryclubs.getRandomPicture;

public class ClubProfileActivity extends AppCompatActivity {

    private static final String TAG = "ClubProfileActivity";

    private FloatingActionButton mFloatingBtn;
    private FirebaseAuth mAuth;
    private FirebaseDatabase mFirebaseDatabase;
    private DatabaseReference mThisclubRef;
    private DatabaseReference mUserRef;
    private DatabaseReference mUserFollowingClubRef;
    private DatabaseReference mThisclubMembersRef;

    private boolean isFollowed;
    private String followedClubUID = "";

    public static final String EXTRA = "club_name";

    private RecyclerView mInfoRecyclerView;
    FirebaseRecyclerOptions<ClubMembers> optionInfo;
    FirebaseRecyclerAdapter<ClubMembers, ClubPageInfoSectionViewHolder> infoAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_club_profile);

        final Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        final String clubName = getIntent().getStringExtra(EXTRA);
        mFloatingBtn = (FloatingActionButton) findViewById(R.id.club_profile_floatingbtn);
        ImageView imageView = findViewById(R.id.backdrop);
        Glide.with(this).load(getRandomPicture.getRandomCheeseDrawable()).apply(RequestOptions.centerCropTransform()).into(imageView);
        isFollowed = false;

        CollapsingToolbarLayout collapsingToolbar = findViewById(R.id.collapsing_toolbar);
        collapsingToolbar.setTitle(clubName);
        collapsingToolbar.setExpandedTitleTextAppearance(R.style.ExpandedAppBar);

        mAuth = FirebaseAuth.getInstance();
        mFirebaseDatabase = FirebaseDatabase.getInstance();
        mThisclubRef = mFirebaseDatabase.getReference().child(getApplicationContext().getString(R.string.firebase_clubs_tag)).child(clubName);
        mUserRef = mFirebaseDatabase.getReference().child(getApplicationContext().getString(R.string.firebase_users_tag)).child(mAuth.getUid());
        mUserFollowingClubRef = mFirebaseDatabase.getReference().child(getApplicationContext().getString(R.string.firebase_users_tag)).child(mAuth.getUid())
                .child(getApplicationContext().getString(R.string.firebase_following_clubs_tag));
        mThisclubMembersRef = mThisclubRef.child(getApplicationContext().getString(R.string.firebase_clubmembers_tag));

        checkIfFollowingClubsIndexExist();
        mUserFollowingClubRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                if (checkIfclubIsFollwed(clubName, dataSnapshot)) {
                    mFloatingBtn.setImageResource(R.drawable.ic_baseline_check_24px);
                    mFloatingBtn.setBackgroundTintList(getResources().getColorStateList(R.color.greenEnd));
                    isFollowed = true;
                } else {
                    mFloatingBtn.setImageResource(R.drawable.ic_baseline_star_rate_18px);
                    mFloatingBtn.setBackgroundTintList(getResources().getColorStateList(R.color.colorAccent));
                    isFollowed = false;
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        mFloatingBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isFollowed) {
                    // when followed, click to unfollow
                    Log.d(TAG, "Trying to unfollow this club" + followedClubUID);
                    mUserFollowingClubRef.child(followedClubUID).removeValue();
                    mFloatingBtn.setImageResource(R.drawable.ic_baseline_star_rate_18px);
                    mFloatingBtn.setBackgroundTintList(getResources().getColorStateList(R.color.colorAccent));
                    isFollowed = false;
                } else {
                    // when unfollow, click to follow
                    followthisclub();
                    mFloatingBtn.setImageResource(R.drawable.ic_baseline_check_24px);
                    mFloatingBtn.setBackgroundTintList(getResources().getColorStateList(R.color.greenEnd));
                    isFollowed = true;
                }
            }
        });

        mInfoRecyclerView = (RecyclerView) findViewById(R.id.club_profile_info_recyclerView);
        mInfoRecyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        setupInfoRecyclerView(mInfoRecyclerView);
        mInfoRecyclerView.hasFixedSize();

        final TextView purpose_textView = (TextView) findViewById(R.id.club_profile_purpose_textView);
        mThisclubRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Clubs clubs = dataSnapshot.getValue(Clubs.class);
                purpose_textView.setText(clubs.getPurpose());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void setupInfoRecyclerView(RecyclerView mInfoRecyclerView) {
        Log.d(TAG, "setupInfoRecyclerView!!!");
        Log.d(TAG, "this is the target club: " + mThisclubRef.getKey());

        optionInfo = new FirebaseRecyclerOptions.Builder<ClubMembers>()
                .setQuery(mThisclubMembersRef, ClubMembers.class)
                .build();

        infoAdapter = new FirebaseRecyclerAdapter<ClubMembers, ClubPageInfoSectionViewHolder>(optionInfo) {
            @Override
            protected void onBindViewHolder(@NonNull ClubPageInfoSectionViewHolder holder, int position, @NonNull ClubMembers model) {
                Log.d(TAG, "Making INFO onBindViewHolder..." + model.getName() + " " + model.getEmail());
                holder.mName_textView.setText(model.getName());
                holder.mEmail_textView.setText(model.getEmail());
            }

            @NonNull
            @Override
            public ClubPageInfoSectionViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
                View view = LayoutInflater.from(viewGroup.getContext())
                        .inflate(R.layout.clubpage_info_section, viewGroup, false);
                return new ClubPageInfoSectionViewHolder(view);
            }
        };

        mInfoRecyclerView.setAdapter(infoAdapter);
        infoAdapter.startListening();
    }

    public void checkIfFollowingClubsIndexExist() {
        Log.d(TAG, "checkIfFollowingClubsIndexExist");

        mUserRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (!dataSnapshot.hasChild(getApplicationContext().getString(R.string.firebase_following_clubs_tag))) {
                    mUserRef.child(getApplication().getString(R.string.firebase_following_clubs_tag)).setValue("");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    public boolean checkIfclubIsFollwed(String clubName, DataSnapshot dataSnapshot) {
        Log.d(TAG, "checkIfclubIsFollwed, club name: " + clubName);

        FollowingClubs club = new FollowingClubs();

        for (DataSnapshot ds : dataSnapshot.getChildren()) {
            Log.d(TAG, "checkIfclubIsFollwed, checking: " + ds);
            club.setClub_name(ds.getValue(FollowingClubs.class).getClub_name());
            Log.d(TAG, "checkIfclubIsFollwed, have gotten: " + club.getClub_name());

            if (club.getClub_name().equals(clubName)) {
                Log.d(TAG, "checkIfclubIsFollwed, Found Followed: " + club.getClub_name());
                Log.d(TAG, "checkIfclubIsFollwed, Found Master Key: " + club.getClub_name());
                followedClubUID = ds.getKey();
                return true;
            }
        }
        return false;
    }

    public void followthisclub() {

        mThisclubRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Log.d(TAG, "followthisclub, club name: " + dataSnapshot);
                Clubs club = dataSnapshot.getValue(Clubs.class);
                DatabaseReference reference = mUserFollowingClubRef.push();
                reference.setValue(club);
                followedClubUID = reference.getKey();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * Helper method which use Intent to go to the Clublist Page
     */
    private void goToClubListHelper() {
        startActivity(new Intent(getApplicationContext(), ClublistFragment.class));
        finish();
    }

    @Override
    public void onStart() {
        super.onStart();
        infoAdapter.startListening();
        mInfoRecyclerView.setAdapter(infoAdapter);
    }

    @Override
    public void onStop() {
        super.onStop();
        infoAdapter.stopListening();
    }
}
