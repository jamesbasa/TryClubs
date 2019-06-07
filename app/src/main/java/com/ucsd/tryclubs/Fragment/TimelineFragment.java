package com.ucsd.tryclubs.Fragment;

import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.ContextMenu;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuItem;
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
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.like.LikeButton;
import com.like.OnLikeListener;
import com.ucsd.tryclubs.Activity.ClubProfileActivity;
import com.ucsd.tryclubs.Activity.NewPostActivity;
import com.ucsd.tryclubs.Model.Post;
import com.ucsd.tryclubs.R;
import com.ucsd.tryclubs.ViewHolder.PostViewHolder;
import com.ucsd.tryclubs.getRandom;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * class TimelineFragment is the "Timeline" page in the App.
 */
public class TimelineFragment extends Fragment {

    private FloatingActionButton mAddPost;
    private RecyclerView mRecyclerView;
    private LinearLayoutManager mLayoutManager;
    private NestedScrollView mNestedScroll;
    private TextView mSortTextView;

    private TextView mWhatsNew;

    private FirebaseAuth mAuth;
    private FirebaseDatabase mFirebaseDatabase;
    private DatabaseReference mEventsRef;
    private DatabaseReference mUserRef;

    private static final String TAG = "TimelineFragment";
    private static final String BUILD_VERSION = "Current Build Version: 1.1.1";

    FirebaseRecyclerOptions<Post> option;
    FirebaseRecyclerAdapter<Post, PostViewHolder> adapter;
    Query firebaseQuery;

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

        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
        firebaseQuery = mEventsRef.orderByChild("sort_date").startAt(Long.parseLong(sdf.format(new Date())));

        Log.d(TAG, "setupTimelineRecyclerView!!!");
        setUpTimeLine(firebaseQuery);

        v = inflater.inflate(R.layout.fragment_timeline, container, false);
        mRecyclerView = (RecyclerView) v.findViewById(R.id.timeline_recyclerView);
        mRecyclerView.hasFixedSize(); //for performance issues apparently...
        mAddPost = (FloatingActionButton) v.findViewById(R.id.buttonAddPost);
        mNestedScroll = v.findViewById(R.id.timeline_nestedsv);
        mSortTextView = v.findViewById(R.id.timeline_sort_TextView);
        registerForContextMenu(mSortTextView);
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
        mWhatsNew = (TextView) v.findViewById(R.id.whatsnew);
        mWhatsNew.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Snackbar snackbar = Snackbar.make(getActivity().findViewById(android.R.id.content), BUILD_VERSION, Snackbar.LENGTH_SHORT);
                View view = snackbar.getView();
                TextView tv = (TextView) view.findViewById(android.support.design.R.id.snackbar_text);
                tv.setTextColor(Color.parseColor("#FFD700"));
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
                    tv.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
                } else {
                    tv.setGravity(Gravity.CENTER_HORIZONTAL);
                }
                snackbar.show();
            }
        });

        mAddPost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getActivity(), NewPostActivity.class));
            }
        });
        mLayoutManager = new LinearLayoutManager(getContext());
        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.setAdapter(adapter);
        adapter.startListening();
        return v;
    }

    /**
     * method setUpTimeLine use the query point to parse all posts that with dates later than today(inclusive) in the firebase
     * @param query
     */
    private void setUpTimeLine(Query query) {
        option = new FirebaseRecyclerOptions.Builder<Post>()
                .setQuery(query, Post.class)
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
                        clubProfilePage.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
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
    }

    /**
     * Override onCreateContextMenu defines the behavior of "sort" text
     */
    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        getActivity().getMenuInflater().inflate(R.menu.sort_menu, menu);
    }

    /**
     * Override onContextItemSelected defines the behavior of the pop up window after hitting "sort"
     */
    @Override
    public boolean onContextItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            default:
                return super.onContextItemSelected(item);
            case R.id.sort_by_time:
                SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
                firebaseQuery = mEventsRef.orderByChild("sort_date").startAt(Long.parseLong(sdf.format(new Date())));
                setUpTimeLine(firebaseQuery);
                mRecyclerView.setAdapter(adapter);
                adapter.startListening();
                return true;
            case R.id.sort_by_name:
                firebaseQuery = mEventsRef.orderByChild("hosts");
                setUpTimeLine(firebaseQuery);
                mRecyclerView.setAdapter(adapter);
                adapter.startListening();
                mLayoutManager.setReverseLayout(false);
                mLayoutManager.setStackFromEnd(false);
                return true;
        }
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public void onStop() {
        super.onStop();
    }


}
