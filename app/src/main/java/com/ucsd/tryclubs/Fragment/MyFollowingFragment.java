package com.ucsd.tryclubs.Fragment;


import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.like.LikeButton;
import com.like.OnLikeListener;
import com.ucsd.tryclubs.Model.Post;
import com.ucsd.tryclubs.R;
import com.ucsd.tryclubs.ViewHolder.PostViewHolder;
import com.ucsd.tryclubs.getRandom;

public class MyFollowingFragment extends Fragment {

    private static final String TAG = "MyFollowingFragment";

    private FirebaseAuth mAuth;
    private FirebaseDatabase mFirebaseDatabase;
    private DatabaseReference mUserRef;
    private DatabaseReference mUserFollowingEvetsRef;

    View v;
    FirebaseRecyclerOptions<Post> option;
    FirebaseRecyclerAdapter<Post, PostViewHolder> adapter;

    private RecyclerView mRecyclerView;
    private LinearLayoutManager mLayoutManager;
    private TextView mNoFollowingTextView;

    public MyFollowingFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        mAuth = FirebaseAuth.getInstance();
        mFirebaseDatabase = FirebaseDatabase.getInstance();
        mUserRef = mFirebaseDatabase.getReference().child(getActivity().getString(R.string.firebase_users_tag)).child(mAuth.getCurrentUser().getUid());
        checkIfFollowingEventsIndexExist();
        mUserFollowingEvetsRef = mUserRef.child(getActivity().getString(R.string.firebase_following_events_tag));

        Log.d(TAG, "setupTimelineRecyclerView!!!" );
        option = new FirebaseRecyclerOptions.Builder<Post>()
                .setQuery(mUserFollowingEvetsRef, Post.class)
                .build();
        adapter = new FirebaseRecyclerAdapter<Post, PostViewHolder>(option) {
            @Override
            protected void onBindViewHolder(@NonNull final PostViewHolder holder, int position, @NonNull final Post model) {
                RequestOptions requestoptions = new RequestOptions();
                Glide.with(holder.mTimelineImg.getContext())
                        .load(getRandom.getRandomUCSDDrawable())
                        .apply(requestoptions.fitCenter())
                        .into(holder.mTimelineImg);

                holder.mClubName.setText(model.getHosts());
                holder.mDescription.setText(model.getDescription());
                holder.mTitle.setText(model.getEname());
                holder.mLocation.setText(model.getLocation());
                String date = model.getDate();
                String time = model.getTime();
                String dateAndTime = date + " " + time;
                holder.mDateTime.setText(dateAndTime);
                holder.mLike.setLiked(true);

                holder.mLike.setOnLikeListener(new OnLikeListener() {
                    @Override
                    public void liked(LikeButton likeButton) {
                    }

                    @Override
                    public void unLiked(LikeButton likeButton) {
                        mUserRef.child(getActivity().getString(R.string.firebase_following_events_tag)).child(model.getEname()).removeValue();


                            String eventNmae = model.getEname();
                            final Post deletedItem = adapter.getItem(holder.getAdapterPosition());
                            adapter.notifyItemRemoved(holder.getAdapterPosition());

                            Snackbar snackbar = Snackbar.make(getActivity().findViewById(android.R.id.content),  eventNmae.toUpperCase() + " removed from Following Events List", Snackbar.LENGTH_LONG);
                            snackbar.setAction("UNDO", new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    mUserRef.child(getActivity().getString(R.string.firebase_following_events_tag)).child(model.getEname()).setValue(deletedItem);
                                }
                            });
                            snackbar.setActionTextColor(Color.YELLOW);
                            snackbar.show();

                    }
                });

            }

            @NonNull
            @Override
            public PostViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
                View view = LayoutInflater.from(viewGroup.getContext())
                        .inflate(R.layout.post_layout, viewGroup, false);
                return new PostViewHolder(view);
            }
        };

        v = inflater.inflate(R.layout.fragment_my_following, container, false);
        mNoFollowingTextView = (TextView) v.findViewById(R.id.my_following_no_following_textView);
        mRecyclerView = (RecyclerView) v.findViewById(R.id.my_following_recyclerView);
        mRecyclerView.hasFixedSize();
        mLayoutManager = new LinearLayoutManager(getContext());
        mLayoutManager.setReverseLayout(true);
        mLayoutManager.setStackFromEnd(true);
        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.setAdapter(adapter);
        adapter.startListening();

        return v;
    }

    private void checkIfFollowingEventsIndexExist() {
        Log.d(TAG, "checkIfFollowingEventsIndexExist");

        mUserRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (!dataSnapshot.hasChild("following_events")) {
                    mUserRef.child("following_events").setValue("");
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        mUserRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.hasChild("following_events")) {
                    if (dataSnapshot.child("following_events").getChildrenCount() > 0) {
                        mNoFollowingTextView.setVisibility(View.INVISIBLE);
                    } else {
                        mNoFollowingTextView.setVisibility(View.VISIBLE);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
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
