package com.ucsd.tryclubs.Activity;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.ucsd.tryclubs.Model.Clubs;
import com.ucsd.tryclubs.Model.FollowingClubs;
import com.ucsd.tryclubs.R;
import com.ucsd.tryclubs.getRandomPicture;

public class ClubProfileActivity extends AppCompatActivity {

    private static final String TAG = "ClubProfileActivity";

    private FloatingActionButton mFloatingBtn;
    private FirebaseAuth mAuth;
    private FirebaseDatabase mFirebaseDatabase;
    private DatabaseReference mThisclubRef;
    private DatabaseReference mUserFollowingClubRef;

    private boolean isFollwed;
    private String followedClubUID = "";

    public static final String EXTRA = "club_name";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_club_profile);

        final String clubName = getIntent().getStringExtra(EXTRA);
        mFloatingBtn = (FloatingActionButton) findViewById(R.id.club_profile_floatingbtn);
        ImageView imageView = findViewById(R.id.backdrop);
        Glide.with(this).load(getRandomPicture.getRandomCheeseDrawable()).apply(RequestOptions.centerCropTransform()).into(imageView);
        isFollwed = false;

        mAuth = FirebaseAuth.getInstance();
        mFirebaseDatabase = FirebaseDatabase.getInstance();
        mThisclubRef = mFirebaseDatabase.getReference().child(getApplicationContext().getString(R.string.firebase_clubs_tag)).child(clubName);
        mUserFollowingClubRef = mFirebaseDatabase.getReference().child(getApplicationContext().getString(R.string.firebase_users_tag)).child(mAuth.getUid())
        .child(getApplicationContext().getString(R.string.firebase_following_clubs_tag));

        mUserFollowingClubRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (checkIfclubIsFollwed(clubName, dataSnapshot)) {
                    mFloatingBtn.setImageResource(R.drawable.ic_baseline_check_24px);
                } else {

                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        mFloatingBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isFollwed) {
                    mUserFollowingClubRef.child(followedClubUID).removeValue();
                    mFloatingBtn.setImageResource(R.drawable.ic_baseline_star_rate_18px);
                    isFollwed = false;
                } else {
                    followthisclub();
                    mFloatingBtn.setImageResource(R.drawable.ic_baseline_star_rate_18px);
                    isFollwed = true;
                }
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
                isFollwed = true;
                Log.d(TAG, "checkIfclubIsFollwed, Found Master Key: " + club.getClub_name());
                followedClubUID = ds.getKey();
                return true;
            }

        }

        return false;
    }

    public void followthisclub() {

        mThisclubRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Log.d(TAG, "followthisclub, club name: " + dataSnapshot);
                Clubs club = dataSnapshot.getValue(Clubs.class);
                DatabaseReference reference = mUserFollowingClubRef.push();
                reference.setValue(club);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });



    }

}
