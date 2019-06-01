package com.ucsd.tryclubs.Activity;

import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.chip.Chip;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
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
import com.ucsd.tryclubs.Model.ClubMembers;
import com.ucsd.tryclubs.Model.Clubs;
import com.ucsd.tryclubs.Model.Post;
import com.ucsd.tryclubs.Model.Tags;
import com.ucsd.tryclubs.R;
import com.ucsd.tryclubs.ViewHolder.ClubPageInfoSectionViewHolder;
import com.ucsd.tryclubs.ViewHolder.PostViewHolder;
import com.ucsd.tryclubs.getRandom;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * class ClubProfileActivity is the "Club Profile" page in the App.
 */
public class ClubProfileActivity extends AppCompatActivity {

    private static final String TAG = "ClubProfileActivity";

    private FloatingActionButton mFloatingBtn;
    private FirebaseAuth mAuth;
    private FirebaseDatabase mFirebaseDatabase;
    private DatabaseReference mThisclubRef;
    private DatabaseReference mUserRef;
    private DatabaseReference mUserFollowingClubRef;
    private DatabaseReference mThisclubMembersRef;
    private DatabaseReference mThisclubTagsRef;

    private boolean isFollowed;

    public static final String EXTRA = "club_name";

    private RecyclerView mInfoRecyclerView;
    FirebaseRecyclerOptions<ClubMembers> optionInfo;
    FirebaseRecyclerAdapter<ClubMembers, ClubPageInfoSectionViewHolder> infoAdapter;

    private RecyclerView mEventRecyclerView;
    FirebaseRecyclerOptions<Post> optionEvents;
    FirebaseRecyclerAdapter<Post, PostViewHolder> eventsAdapter;

    ArrayList<String> allTags = new ArrayList<String>();
    TagsRecyclerViewAdapter tagsAdapter;

    private RecyclerView mTagsRecyclerView;
    private TextView purpose_textView;
    private TextView mNoEventTextView;
    private TextView mAddMemberTextView;

    String clubNameInHere = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_club_profile);

        final Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        final String clubName = getIntent().getStringExtra(EXTRA);
        clubNameInHere = clubName;
        mFloatingBtn = (FloatingActionButton) findViewById(R.id.club_profile_floatingbtn);
        ImageView imageView = findViewById(R.id.backdrop);
        Glide.with(this).load(getRandom.getRandomUCSDDrawable()).apply(RequestOptions.centerCropTransform()).into(imageView);
        isFollowed = false;

        CollapsingToolbarLayout collapsingToolbar = findViewById(R.id.collapsing_toolbar);
        collapsingToolbar.setTitle(clubName);
        collapsingToolbar.setExpandedTitleTextAppearance(R.style.ExpandedAppBar);

        mAuth = FirebaseAuth.getInstance();
        mFirebaseDatabase = FirebaseDatabase.getInstance();
        mThisclubRef = mFirebaseDatabase.getReference().child(getApplicationContext().getString(R.string.firebase_clubs_tag)).child(clubName);
        mFloatingBtn.hide();

        if (mAuth.getCurrentUser() != null) {
            mFloatingBtn.show();
            mUserRef = mFirebaseDatabase.getReference().child(getApplicationContext().getString(R.string.firebase_users_tag)).child(mAuth.getUid());
            mUserFollowingClubRef = mFirebaseDatabase.getReference().child(getApplicationContext().getString(R.string.firebase_users_tag)).child(mAuth.getCurrentUser().getUid())
                    .child(getApplicationContext().getString(R.string.firebase_following_clubs_tag));

            checkIfFollowingClubsIndexExist();
            mUserFollowingClubRef.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if (checkIfclubIsFollwed(clubName, dataSnapshot)) {
                        mFloatingBtn.setImageResource(R.drawable.ic_baseline_check_24px);
                        mFloatingBtn.setBackgroundTintList(getResources().getColorStateList(R.color.greenEnd));
                        isFollowed = true;
                    } else {
                        mFloatingBtn.setImageResource(R.drawable.ic_baseline_star_24px);
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
                        Log.d(TAG, "Trying to unfollow this club: " + clubName);
                        mUserFollowingClubRef.child(clubName).removeValue();
                        mFloatingBtn.setImageResource(R.drawable.ic_baseline_star_24px);
                        mFloatingBtn.setBackgroundTintList(getResources().getColorStateList(R.color.colorAccent));
                        isFollowed = false;
                    } else {
                        // when unfollow, click to follow
                        followthisclub();
                        mFloatingBtn.setImageResource(R.drawable.ic_baseline_check_24px);
                        mFloatingBtn.setBackgroundTintList(getResources().getColorStateList(R.color.greenEnd));
                        isFollowed = true;
                    }
                    mFloatingBtn.hide();
                    mFloatingBtn.show();
                }
            });
        }

        mThisclubMembersRef = mThisclubRef.child(getApplicationContext().getString(R.string.firebase_clubmembers_tag));
        mThisclubTagsRef = mThisclubRef.child(getApplicationContext().getString(R.string.firebase_tags_tag));

        mThisclubTagsRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Tags tags = dataSnapshot.getValue(Tags.class);
                ArrayList<String> temp = tags.allTrue();
                allTags.addAll(temp);
                tagsAdapter = new TagsRecyclerViewAdapter(getApplicationContext(), allTags);
                mTagsRecyclerView.setAdapter(tagsAdapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        checkIfClubsEventsIndexExist();
        mInfoRecyclerView = (RecyclerView) findViewById(R.id.club_profile_info_recyclerView);
        mInfoRecyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        setupInfoRecyclerView(mInfoRecyclerView);
        mInfoRecyclerView.hasFixedSize();

        mNoEventTextView = (TextView) findViewById(R.id.club_profile_events_no_event_TextView);
        mEventRecyclerView = (RecyclerView) findViewById(R.id.club_profile_events_recyclerView);
        mAddMemberTextView = (TextView) findViewById(R.id.club_profile_add_member);
        mAddMemberTextView.setVisibility(View.INVISIBLE);
        mAddMemberTextView.setText(getApplicationContext().getString(R.string.claimClub));
        mEventRecyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        setupEventRecyclerView(mEventRecyclerView);
        mEventRecyclerView.hasFixedSize();

        purpose_textView = (TextView) findViewById(R.id.club_profile_purpose_textView);
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

        if (mAuth.getCurrentUser() != null) {
            mUserRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if (dataSnapshot.hasChild(getApplicationContext().getString(R.string.firebase_my_club_tag))) {
                        if (dataSnapshot.child(getApplicationContext().getString(R.string.firebase_my_club_tag)).hasChildren()) {
                            DataSnapshot ds = dataSnapshot.child(getApplicationContext().getString(R.string.firebase_my_club_tag)).getChildren().iterator().next();
                            Clubs club = ds.getValue(Clubs.class);
                            Log.d(TAG, "has children" + club.getClub_name());
                            Log.d(TAG, "has children" + clubName);
                            if (club.getClub_name().equals(clubName)) {
                                purpose_textView.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        showPurposeEditDialog();
                                    }
                                });

                                mAddMemberTextView.setVisibility(View.VISIBLE);
                                mAddMemberTextView.setText(getApplicationContext().getString(R.string.add));
                                mAddMemberTextView.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        showAddMemberDialog();
                                    }
                                });
                            }
                        }
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        }

        if (mAddMemberTextView.getText().toString().equals(getApplicationContext().getString(R.string.claimClub)) && mAuth.getCurrentUser() != null) {
            mAddMemberTextView.setVisibility(View.VISIBLE);
            mAddMemberTextView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    showClaimDialog();
                }
            });
        }

        mTagsRecyclerView = (RecyclerView) findViewById(R.id.club_profile_tags_recyclerView);
        mTagsRecyclerView.hasFixedSize();
        setupTagRecyclerView(mTagsRecyclerView);
    }

    /**
     * method setupEventRecyclerView sets up the recycler view in the Events session
     */
    private void setupEventRecyclerView(RecyclerView mEventRecyclerView) {
        Log.d(TAG, "setupEventRecyclerView!!!");

        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
        Query firebaseQuery = mThisclubRef.child("events").orderByChild("sort_date").startAt(Long.parseLong(sdf.format(new Date())));

        optionEvents = new FirebaseRecyclerOptions.Builder<Post>()
                .setQuery(firebaseQuery, Post.class)
                .build();

        eventsAdapter = new FirebaseRecyclerAdapter<Post, PostViewHolder>(optionEvents) {
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

                FirebaseUser user = mAuth.getCurrentUser();
                if (user == null) {
                    holder.mLike.setVisibility(View.INVISIBLE);
                } else {
                    holder.mLike.setVisibility(View.VISIBLE);
                    mUserRef.child(getApplicationContext().getString(R.string.firebase_following_events_tag)).addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
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
                            mUserRef.child(getApplicationContext().getString(R.string.firebase_following_events_tag)).child(model.getEname()).setValue(model);
                        }

                        @Override
                        public void unLiked(LikeButton likeButton) {
                            mUserRef.child(getApplicationContext().getString(R.string.firebase_following_events_tag)).child(model.getEname()).removeValue();
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

        mEventRecyclerView.setAdapter(eventsAdapter);
        eventsAdapter.startListening();
    }

    /**
     * method updatePurposeinFirebase updates the purpose of the club
     *
     * @param newPurpose    [the new purpose of the club]
     */
    private void updatePurposeinFirebase(String newPurpose) {
        mThisclubRef.child(getApplicationContext().getString(R.string.firebase_purpose_tag)).setValue(newPurpose);
        String name = mThisclubRef.getKey();
        mUserRef.child(getApplicationContext().getString(R.string.firebase_following_clubs_tag)).child(name).child(getApplicationContext().getString(R.string.firebase_purpose_tag)).setValue(newPurpose);
        mUserRef.child(getApplicationContext().getString(R.string.firebase_my_club_tag)).child(name).child(getApplicationContext().getString(R.string.firebase_purpose_tag)).setValue(newPurpose);
    }

    /**
     * method updateInfoinFirebase updates the principle members info of the club
     *
     * @param name  [the new name]
     * @param email [the new email]
     * @param oldName   [the old name]
     */
    private void updateInfoinFirebase(String name, String email, String oldName) {
        ClubMembers clubMembers = new ClubMembers(name, email);

        // remove old value
        mThisclubRef.child(getApplicationContext().getString(R.string.firebase_clubmembers_tag)).child(oldName).removeValue();
        mUserRef.child(getApplicationContext().getString(R.string.firebase_my_club_tag)).child(clubNameInHere).child(getApplicationContext().getString(R.string.firebase_clubmembers_tag)).child(oldName).removeValue();

        mThisclubRef.child(getApplicationContext().getString(R.string.firebase_clubmembers_tag)).child(name).setValue(clubMembers);
        mUserRef.child(getApplicationContext().getString(R.string.firebase_my_club_tag)).child(clubNameInHere).child(getApplicationContext().getString(R.string.firebase_clubmembers_tag)).child(name).setValue(clubMembers);
    }

    /**
     * method addNewMemberFirebase adds new principle members to the club
     *
     * @param name  [the new members name]
     * @param email [the new members email]
     */
    private void addNewMemberFirebase(String name, String email) {
        ClubMembers clubMembers = new ClubMembers(name, email);
        mThisclubRef.child(getApplicationContext().getString(R.string.firebase_clubmembers_tag)).child(name).setValue(clubMembers);
        mUserRef.child(getApplicationContext().getString(R.string.firebase_my_club_tag)).child(clubNameInHere).child(getApplicationContext().getString(R.string.firebase_clubmembers_tag)).child(name).setValue(clubMembers);
    }

    /**
     * method showClaimDialog opens the "Claim" Dialog
     */
    private void showClaimDialog() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(this)
                .setTitle("Claiming: " + "\"" + clubNameInHere + "\"")
                .setMessage("Are you sure you want to claim \"" + clubNameInHere + "\"? By clicking YES, our team will contact you within 2 business days.")
                .setPositiveButton("yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Snackbar snackbar = Snackbar.make(findViewById(android.R.id.content),  "Claiming \"" + clubNameInHere + "\" request sent", Snackbar.LENGTH_LONG);
                        View view = snackbar.getView();
                        TextView tv = (TextView) view.findViewById(android.support.design.R.id.snackbar_text);
                        tv.setTextColor(Color.parseColor("#FFD700"));
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
                            tv.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
                        } else {
                            tv.setGravity(Gravity.CENTER_HORIZONTAL);
                        }
                        snackbar.show();
                        dialog.dismiss();
                    }
                }).setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });
        builder.show();
    }

    /**
     * method showAddMemberDialog opens the "ADD" Dialog
     */
    private void showAddMemberDialog() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View view = LayoutInflater.from(getApplicationContext()).inflate(R.layout.add_new_member_dialog, (ViewGroup) findViewById(android.R.id.content), false);
        final EditText dialogNameTextView = view.findViewById(R.id.club_new_member_dialog_edit_name_editText);
        final EditText dialogEmailTextView = view.findViewById(R.id.club_new_member_dialog_edit_email_editText);
        builder.setView(view).setTitle("Add Principle Member")
                .setNegativeButton("cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                }).setPositiveButton("update", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (dialogNameTextView.getText().toString().trim().equals(".") || dialogNameTextView.getText().toString().trim().equals("#") || dialogNameTextView.getText().toString().trim().equals("$")
                        || dialogNameTextView.getText().toString().trim().equals("[") || dialogNameTextView.getText().toString().trim().equals("]")) {
                    Snackbar snackbar = Snackbar.make(findViewById(android.R.id.content), "Invalid symbol", Snackbar.LENGTH_SHORT);
                    View view = snackbar.getView();
                    TextView tv = (TextView) view.findViewById(android.support.design.R.id.snackbar_text);
                    tv.setTextColor(Color.parseColor("#FFD700"));
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
                        tv.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
                    } else {
                        tv.setGravity(Gravity.CENTER_HORIZONTAL);
                    }
                    snackbar.show();
                    dialog.dismiss();
                    return;
                }
                if (TextUtils.isEmpty(dialogEmailTextView.getText().toString().trim()) ||  TextUtils.isEmpty(dialogNameTextView.getText().toString().trim())) {
                    Snackbar snackbar = Snackbar.make(findViewById(android.R.id.content), "Fields cannot be empty", Snackbar.LENGTH_SHORT);
                    View view = snackbar.getView();
                    TextView tv = (TextView) view.findViewById(android.support.design.R.id.snackbar_text);
                    tv.setTextColor(Color.parseColor("#FFD700"));
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
                        tv.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
                    } else {
                        tv.setGravity(Gravity.CENTER_HORIZONTAL);
                    }
                    snackbar.show();
                    dialog.dismiss();
                    return;
                }
                String newName = dialogNameTextView.getText().toString().trim();
                String newEmail = dialogEmailTextView.getText().toString().trim();
                addNewMemberFirebase(newName, newEmail);
                Log.d(TAG, "onClick: TAGGGGGGGGG");
                dialog.dismiss();
            }
        });
        builder.show();
    }

    /**
     * method showInfoEditDialog opens the "Edit Principle Members" Dialog
     *
     * @param miniView  [the view holder of the corresponding principle members]
     */
    private void showInfoEditDialog(View miniView) {
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View view = LayoutInflater.from(getApplicationContext()).inflate(R.layout.edit_info_dialog, (ViewGroup) findViewById(android.R.id.content), false);
        final EditText dialogNameTextView = view.findViewById(R.id.club_info_dialog_edit_name_editText);
        final EditText dialogEmailTextView = view.findViewById(R.id.club_info_dialog_edit_email_editText);

        final TextView oldNameTextView = miniView.findViewById(R.id.club_profile_info_name_textView);
        final TextView oldEmailTextView = miniView.findViewById(R.id.club_profile_info_email_textView);
        final String oldName = oldNameTextView.getText().toString();

        dialogNameTextView.setText(oldNameTextView.getText().toString());
        dialogEmailTextView.setText(oldEmailTextView.getText().toString());

        builder.setView(view).setTitle("Edit Info")
                .setNegativeButton("cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                }).setPositiveButton("update", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (dialogNameTextView.getText().toString().trim().equals(".") || dialogNameTextView.getText().toString().trim().equals("#") || dialogNameTextView.getText().toString().trim().equals("$")
                || dialogNameTextView.getText().toString().trim().equals("[") || dialogNameTextView.getText().toString().trim().equals("]")) {
                    Snackbar snackbar = Snackbar.make(findViewById(android.R.id.content), "Invalid symbol", Snackbar.LENGTH_SHORT);
                    View view = snackbar.getView();
                    TextView tv = (TextView) view.findViewById(android.support.design.R.id.snackbar_text);
                    tv.setTextColor(Color.parseColor("#FFD700"));
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
                        tv.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
                    } else {
                        tv.setGravity(Gravity.CENTER_HORIZONTAL);
                    }
                    snackbar.show();
                    dialog.dismiss();
                    return;
                }
                if (TextUtils.isEmpty(dialogEmailTextView.getText().toString().trim()) ||  TextUtils.isEmpty(dialogNameTextView.getText().toString().trim())) {
                    Snackbar snackbar = Snackbar.make(findViewById(android.R.id.content), "Fields cannot be empty", Snackbar.LENGTH_SHORT);
                    View view = snackbar.getView();
                    TextView tv = (TextView) view.findViewById(android.support.design.R.id.snackbar_text);
                    tv.setTextColor(Color.parseColor("#FFD700"));
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
                        tv.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
                    } else {
                        tv.setGravity(Gravity.CENTER_HORIZONTAL);
                    }
                    snackbar.show();
                    dialog.dismiss();
                    return;
                }
                String newName = dialogNameTextView.getText().toString().trim();
                String newEmail = dialogEmailTextView.getText().toString().trim();
                oldNameTextView.setText(newName);
                oldEmailTextView.setText(newEmail);
                updateInfoinFirebase(newName, newEmail, oldName);
                dialog.dismiss();
            }
        });
        builder.show();
    }

    /**
     * method showPurposeEditDialog shows the "Purpose Edit" Dialog
     */
    private void showPurposeEditDialog() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View view = LayoutInflater.from(getApplicationContext()).inflate(R.layout.edit_purpose_dialog, (ViewGroup) findViewById(android.R.id.content), false);
        final EditText dialogPurposeTextView = view.findViewById(R.id.edit_purpose_editText);
        dialogPurposeTextView.setText(purpose_textView.getText().toString());
        builder.setView(view).setTitle("Edit Club Purpose")
                .setNegativeButton("cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                }).setPositiveButton("update", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (TextUtils.isEmpty(dialogPurposeTextView.getText().toString().trim())) {
                    Snackbar snackbar = Snackbar.make(findViewById(android.R.id.content), "Fields cannot be empty", Snackbar.LENGTH_SHORT);
                    View view = snackbar.getView();
                    TextView tv = (TextView) view.findViewById(android.support.design.R.id.snackbar_text);
                    tv.setTextColor(Color.parseColor("#FFD700"));
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
                        tv.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
                    } else {
                        tv.setGravity(Gravity.CENTER_HORIZONTAL);
                    }
                    snackbar.show();
                    dialog.dismiss();
                    return;
                }

                String newPurpose = dialogPurposeTextView.getText().toString().trim();
                purpose_textView.setText(newPurpose);
                updatePurposeinFirebase(newPurpose);
                dialog.dismiss();
            }
        });
        builder.show();
    }

    private void setupTagRecyclerView(RecyclerView mTagsRecyclerView) {
        mTagsRecyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
    }

    /**
     * Firebase adapter of showing Tags of the club
     */
    public static class TagsRecyclerViewAdapter extends RecyclerView.Adapter<TagsRecyclerViewAdapter.ViewHolder> {

        Context mContext;
        private List<String> mValue;

        public static class ViewHolder extends RecyclerView.ViewHolder {
            public View mView;
            public Chip mTag_name;


            public ViewHolder(@NonNull View itemView) {
                super(itemView);
                mView = itemView;
                mTag_name = itemView.findViewById(R.id.club_page_tags_chip);
                mTag_name.setTextColor(Color.parseColor(getRandom.getRandomColor()));
            }
        }

        public TagsRecyclerViewAdapter(Context context, List<String> items) {
            this.mContext = context;
            mValue = items;
        }

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
            View view = LayoutInflater.from(viewGroup.getContext())
                    .inflate(R.layout.club_page_tags_list_layout, viewGroup, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolder viewHolder, int i) {
            viewHolder.mTag_name.setText(mValue.get(i));
        }

        @Override
        public int getItemCount() {
            return mValue.size();
        }
    }

    /**
     * Firebase adapter of the info session of the club
     */
    private void setupInfoRecyclerView(RecyclerView mInfoRecyclerView) {
        Log.d(TAG, "setupInfoRecyclerView!!!");
        Log.d(TAG, "setupInfoRecyclerView!!! - this is the target club: " + mThisclubRef.getKey());

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
                final View view = LayoutInflater.from(viewGroup.getContext())
                        .inflate(R.layout.clubpage_info_section, viewGroup, false);

                if (mAuth.getCurrentUser() != null) {
                    mUserRef.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            if (dataSnapshot.hasChild(getApplicationContext().getString(R.string.firebase_my_club_tag))) {
                                if (dataSnapshot.child(getApplicationContext().getString(R.string.firebase_my_club_tag)).hasChildren()) {
                                    DataSnapshot ds = dataSnapshot.child(getApplicationContext().getString(R.string.firebase_my_club_tag)).getChildren().iterator().next();
                                    Clubs club = ds.getValue(Clubs.class);
                                    if (club.getClub_name().equals(clubNameInHere)) {
                                        view.setOnClickListener(new View.OnClickListener() {
                                            @Override
                                            public void onClick(View v) {
                                                showInfoEditDialog(v);
                                            }
                                        });
                                    }

                                }
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });
                }
                return new ClubPageInfoSectionViewHolder(view);
            }
        };

        mInfoRecyclerView.setAdapter(infoAdapter);
        infoAdapter.startListening();
    }

    /**
     * method checkIfFollowingClubsIndexExist checks if the following_clubs index exists under user's index
     */
    private void checkIfFollowingClubsIndexExist() {
        Log.d(TAG, "checkIfFollowingClubsIndexExist");

        mUserRef.addListenerForSingleValueEvent(new ValueEventListener() {
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

    /**
     * method checkIfclubIsFollwed checked if this club has been followed
     *
     * @param clubName  [this club name]
     * @param dataSnapshot  [all following clubs of the user]
     * @return  true if followed, false if unfollowed
     */
    public boolean checkIfclubIsFollwed(String clubName, DataSnapshot dataSnapshot) {
        Log.d(TAG, "checkIfclubIsFollwed, club name: " + clubName);

        if (dataSnapshot.hasChild(clubName)) {
            Log.d(TAG, "Already Followed " + clubName);
            return true;
        }
        return false;

        /*
        FollowingClubs club = new FollowingClubs();

        for (DataSnapshot ds : dataSnapshot.getChildren()) {
            Log.d(TAG, "checkIfclubIsFollwed, checking: " + ds);
            club.setClub_name(ds.getValue(FollowingClubs.class).getClub_name());
            Log.d(TAG, "checkIfclubIsFollwed, have gotten: " + club.getClub_name());

            if (club.getClub_name().equals(clubName)) {
                Log.d(TAG, "checkIfclubIsFollwed, Found Followed: " + club.getClub_name());
                Log.d(TAG, "checkIfclubIsFollwed, Found Master Key: " + club.getClub_name());
                return true;
            }
        }
        return false;
        */
    }

    /**
     * method followthisclub follows this club
     */
    public void followthisclub() {

        mThisclubRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Log.d(TAG, "followthisclub, club name: " + dataSnapshot);
                Clubs club = dataSnapshot.getValue(Clubs.class);
                mUserFollowingClubRef.child(club.getClub_name()).setValue(club);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    /**
     * method checkIfClubsEventsIndexExist checks if this club has events index
     */
    private void checkIfClubsEventsIndexExist() {
        Log.d(TAG, "checkIfClubsEventsIndexExist");

        mThisclubRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (!dataSnapshot.hasChild("events")) {
                    mThisclubRef.child("events").setValue("");
                }

                if (dataSnapshot.hasChild("events")) {
                    if (dataSnapshot.child("events").getChildrenCount() > 0) {
                        mNoEventTextView.setVisibility(View.INVISIBLE);
                    } else {
                        mNoEventTextView.setVisibility(View.VISIBLE);
                    }
                }

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

    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public void onStop() {
        super.onStop();
    }
}
