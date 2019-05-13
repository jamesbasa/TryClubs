package com.ucsd.tryclubs.Fragment;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.ucsd.tryclubs.Model.FollowingClubs;
import com.ucsd.tryclubs.R;
import com.ucsd.tryclubs.ViewHolder.FollowingClubsViewHolder;

/**
 * A simple {@link Fragment} subclass.
 */
public class ClublistFragment extends Fragment {

    RecyclerView mRecyclerView;
    DatabaseReference databaseReference;
    FirebaseRecyclerOptions<FollowingClubs> options;
    FirebaseRecyclerAdapter<FollowingClubs, FollowingClubsViewHolder> adapter;
    FirebaseAuth mAuth;
    FirebaseUser user;

    public ClublistFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mAuth = FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser();

        mRecyclerView = (RecyclerView) inflater.inflate(R.layout.fragment_clublist,container,false);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(mRecyclerView.getContext()));

        databaseReference = FirebaseDatabase.getInstance().getReference().child("Users").child(user.getUid()).child("Following");
        return mRecyclerView;
    }

}
