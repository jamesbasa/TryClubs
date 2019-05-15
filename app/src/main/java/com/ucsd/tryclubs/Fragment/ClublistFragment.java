package com.ucsd.tryclubs.Fragment;


import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.ucsd.tryclubs.Activity.ClubProfileActivity;
import com.ucsd.tryclubs.Model.FollowingClubs;
import com.ucsd.tryclubs.R;
import com.ucsd.tryclubs.ViewHolder.FollowingClubsViewHolder;
import com.ucsd.tryclubs.getRandomPicture;

/**
 * A simple {@link Fragment} subclass.
 */
public class ClublistFragment extends Fragment{

    private static final String TAG = "ClublistFragment";

    private FirebaseAuth mAuth;
    private FirebaseDatabase mFirebaseDatabase;
    private DatabaseReference mUserFollowingClubReference;
    private String userID;

    private View v;
    private RecyclerView mRecyclerView;

    FirebaseRecyclerOptions<FollowingClubs> options;
    FirebaseRecyclerAdapter<FollowingClubs, FollowingClubsViewHolder> adapter;

    public ClublistFragment() {

    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        mAuth = FirebaseAuth.getInstance();
        userID = mAuth.getCurrentUser().getUid();
        mFirebaseDatabase = FirebaseDatabase.getInstance();
        mUserFollowingClubReference = mFirebaseDatabase.getReference().child(getContext().getString(R.string.firebase_users_tag)).child(userID)
                .child(getContext().getString(R.string.firebase_following_clubs_tag));

        v = inflater.inflate(R.layout.fragment_clublist,container,false);
        mRecyclerView = (RecyclerView) v.findViewById(R.id.clublist_fragment_recyclerview);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        setupRecyclerView(mRecyclerView);
        mRecyclerView.hasFixedSize();
        return v;
    }

    public void setupRecyclerView(RecyclerView recyclerView) {
        Log.d(TAG, "setupRecyclerView!!!" );

        options = new FirebaseRecyclerOptions.Builder<FollowingClubs>()
                .setQuery(mUserFollowingClubReference, FollowingClubs.class)
                .build();

        adapter = new FirebaseRecyclerAdapter<FollowingClubs, FollowingClubsViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull FollowingClubsViewHolder holder, final int position, @NonNull final FollowingClubs model) {
                Log.d(TAG, "Making View: " + model.getClub_name());

                final String club_name = model.getClub_name();
                holder.mTextView.setText(model.getClub_name());
                holder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent clubProfilePage = new Intent(getContext(), ClubProfileActivity.class);
                        Log.d(TAG, "holder.itemView.setOnClickListener Intent Extra: " + club_name);
                        clubProfilePage.putExtra(ClubProfileActivity.EXTRA, club_name);
                        startActivity(clubProfilePage);
                    }
                });

                RequestOptions requestoptions = new RequestOptions();
                Glide.with(holder.mImageView.getContext())
                        .load(getRandomPicture.getRandomCheeseDrawable())
                        .apply(requestoptions.fitCenter())
                        .into(holder.mImageView);
            }

            @NonNull
            @Override
            public FollowingClubsViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
                View view = LayoutInflater.from(viewGroup.getContext())
                        .inflate(R.layout.clublist_layout, viewGroup, false);
                return new FollowingClubsViewHolder(view);
            }

        };

        recyclerView.setAdapter(adapter);
        adapter.startListening();
    }

    @Override
    public void onStart() {
        super.onStart();
        mRecyclerView.setAdapter(adapter);
        adapter.startListening();
    }

    @Override
    public void onStop() {
        super.onStop();
        adapter.stopListening();
    }
}
