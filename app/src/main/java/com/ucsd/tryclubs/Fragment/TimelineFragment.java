package com.ucsd.tryclubs.Fragment;


import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.widget.NestedScrollView;
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
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.like.LikeButton;
import com.like.OnLikeListener;
import com.ucsd.tryclubs.Activity.ClubProfileActivity;
import com.ucsd.tryclubs.Activity.NewPostActivity;
import com.ucsd.tryclubs.Model.Post;
import com.ucsd.tryclubs.R;
import com.ucsd.tryclubs.ViewHolder.PostViewHolder;
import com.ucsd.tryclubs.getRandom;

public class TimelineFragment extends Fragment {

    private FloatingActionButton mAddPost;
    private RecyclerView mRecyclerView;
    private LinearLayoutManager mLayoutManager;
    private NestedScrollView mNestedScroll;

    private FirebaseAuth mAuth;
    private FirebaseDatabase mFirebaseDatabase;
    private DatabaseReference mEventsRef;
    private DatabaseReference mUserRef;

    private static final String TAG = "TimelineFragment";

    FirebaseRecyclerOptions<Post> option;
    FirebaseRecyclerAdapter<Post, PostViewHolder> adapter;

    View v;

    public TimelineFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             final Bundle savedInstanceState) {

        Log.d(TAG, "this is me");

        mAuth = FirebaseAuth.getInstance();
        mFirebaseDatabase = FirebaseDatabase.getInstance();
        if (mAuth.getCurrentUser() != null) {
            mUserRef = mFirebaseDatabase.getReference().child(getActivity().getString(R.string.firebase_users_tag)).child(mAuth.getCurrentUser().getUid());
        }
        mEventsRef = mFirebaseDatabase.getReference().child(getActivity().getString(R.string.firebase_events_tag));
        Log.d(TAG, "setupTimelineRecyclerView!!!");
        option = new FirebaseRecyclerOptions.Builder<Post>()
                .setQuery(mEventsRef, Post.class)
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

                holder.mClubName.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent clubProfilePage = new Intent(getContext(), ClubProfileActivity.class);
                        Log.d(TAG, "Timeline.setOnClickListener Intent Extra: " + holder.mClubName.getText().toString());
                        clubProfilePage.putExtra(ClubProfileActivity.EXTRA, holder.mClubName.getText().toString().trim());
                        startActivity(clubProfilePage);
                    }
                });

                FirebaseUser user = mAuth.getCurrentUser();
                if (user == null) {
                    holder.mLike.setVisibility(View.INVISIBLE);
                } else {
                    holder.mLike.setVisibility(View.VISIBLE);
                    mUserRef.child(getActivity().getString(R.string.firebase_following_events_tag)).addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            holder.mLike.setLiked(false);
                            if (dataSnapshot.hasChildren()) {
                                for (DataSnapshot ds : dataSnapshot.getChildren()) {
                                    Post post = ds.getValue(Post.class);
                                    Log.d(TAG, "checkIfEventIsFollwed, have gotten: " + post.getEname());
                                    if (post.getEname().equals(model.getEname())) {
                                        Log.d(TAG, "checkIfEventIsFollwed, Found Followed: " + post.getEname());
                                        holder.mLike.setLiked(true);
                                    }
                                }
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {
                        }
                    });

                    holder.mLike.setOnLikeListener(new OnLikeListener() {
                        @Override
                        public void liked(LikeButton likeButton) {
                            mUserRef.child(getActivity().getString(R.string.firebase_following_events_tag)).child(model.getEname()).setValue(model);
                        }

                        @Override
                        public void unLiked(LikeButton likeButton) {
                            mUserRef.child(getActivity().getString(R.string.firebase_following_events_tag)).child(model.getEname()).removeValue();
                        }
                    });
                }
            }

            @NonNull
            @Override
            public PostViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
                View view = LayoutInflater.from(viewGroup.getContext())
                        .inflate(R.layout.post_layout, viewGroup, false);
                return new PostViewHolder(view);
            }
        };

        v = inflater.inflate(R.layout.fragment_timeline, container, false);
        mRecyclerView = (RecyclerView) v.findViewById(R.id.timeline_recyclerView);
        mRecyclerView.hasFixedSize(); //for performance issues apparently...
        mAddPost = (FloatingActionButton) v.findViewById(R.id.buttonAddPost);
        mNestedScroll = v.findViewById(R.id.timeline_nestedsv);
        mAddPost.hide();

        if (mAuth.getCurrentUser() != null) {
            mUserRef.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if (dataSnapshot.hasChild("my_clubs")) {
                        if (dataSnapshot.child("my_clubs").getChildrenCount() > 0) {
                            mAddPost.show();
                            mNestedScroll.setOnScrollChangeListener(new NestedScrollView.OnScrollChangeListener() {
                                @Override
                                public void onScrollChange(NestedScrollView nestedScrollView, int scrollX, int scrollY, int oldScrollX, int oldScrollY) {
                                    if (nestedScrollView.getChildAt(nestedScrollView.getChildCount() - 1) != null) {
                                        if ((scrollY >= (nestedScrollView.getChildAt(nestedScrollView.getChildCount() - 1).getMeasuredHeight() - v.getMeasuredHeight())) &&
                                                scrollY > oldScrollY) {
                                            mAddPost.hide();
                                        } else {
                                            mAddPost.show();
                                        }

                                    }
                                }
                            });
                        }
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        }

        /*
        mRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
            }

            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);

                if (dy < 0) mAddPost.show();
                else if (dy > 0){
                    mAddPost.hide();
                }
            }
        });
        */

        mAddPost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getActivity(), NewPostActivity.class));
            }
        });
        mLayoutManager = new LinearLayoutManager(getContext());
        mLayoutManager.setReverseLayout(true);
        mLayoutManager.setStackFromEnd(true);
        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.setAdapter(adapter);
        adapter.startListening();
        return v;
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
