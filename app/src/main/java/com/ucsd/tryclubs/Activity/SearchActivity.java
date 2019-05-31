package com.ucsd.tryclubs.Activity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.design.button.MaterialButton;
import android.support.design.chip.Chip;
import android.support.design.chip.ChipGroup;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
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
import com.ucsd.tryclubs.Model.Clubs;
import com.ucsd.tryclubs.Model.FollowingClubs;
import com.ucsd.tryclubs.Model.Tags;
import com.ucsd.tryclubs.R;
import com.ucsd.tryclubs.getRandom;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * class SearchActivity is the "Search" page in the App.
 */
public class SearchActivity extends AppCompatActivity {

    private MaterialButton mSearchBtn;
    private AutoCompleteTextView mSearchField;
    private RecyclerView mRecyclerView;
    private ChipGroup mTagsChipGroup;
    private TextView mWelcomeHint;

    private FirebaseAuth mAuth;
    private FirebaseDatabase mFirebaseDatabase;
    private DatabaseReference mClubRef;

    private static final String TAG = "SearchActivity";

    private boolean initialize = true;

    private ArrayList<String> potentialClubs = new ArrayList<>();
    TagsSearchRecyclerViewAdapter tagsAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        mAuth = FirebaseAuth.getInstance();
        mFirebaseDatabase = FirebaseDatabase.getInstance();
        mClubRef = mFirebaseDatabase.getReference().child(getApplicationContext().getString(R.string.firebase_clubs_tag));

        mSearchBtn = (MaterialButton) findViewById(R.id.search_page_materialButton);
        mSearchField = (AutoCompleteTextView) findViewById(R.id.search_page_autoCompleteTextView);
        mSearchField.setInputType(InputType.TYPE_CLASS_TEXT);
        mRecyclerView = (RecyclerView) findViewById(R.id.search_page_recyclerView);
        mRecyclerView.hasFixedSize();
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mTagsChipGroup = (ChipGroup) findViewById(R.id.search_tag_group);
        mWelcomeHint = (TextView) findViewById(R.id.search_page_marquee);
        mWelcomeHint.setSelected(true);

        final String[] tags;
        tags = getResources().getStringArray(R.array.all_custom_tags);

        mSearchField.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if ((!mSearchField.getText().toString().trim().startsWith("#") && actionId == EditorInfo.IME_NULL
                        && event.getAction() == KeyEvent.ACTION_DOWN) || (!mSearchField.getText().toString().trim().startsWith("#") && actionId == EditorInfo.IME_ACTION_NEXT)) {
                    if (mTagsChipGroup.getChildCount() > 0) {
                        mTagsChipGroup.removeAllViews();
                        return true;
                    }
                    String search = mSearchField.getText().toString().trim();
                    firebaseSearch(search);
                } else if ((mSearchField.getText().toString().trim().startsWith("#") && actionId == EditorInfo.IME_NULL
                        && event.getAction() == KeyEvent.ACTION_DOWN) || (mSearchField.getText().toString().trim().startsWith("#") && actionId == EditorInfo.IME_ACTION_NEXT)) {

                    String[] t = mSearchField.getText().toString().toLowerCase().trim().split(" ");
                    mSearchField.setText(null);
                    for (String a : t) {
                        if (Arrays.asList(tags).contains(a)) {
                            addChipToGroup(a, mTagsChipGroup);
                        } else {
                            Snackbar sn = Snackbar.make(findViewById(android.R.id.content), a + " does not exist", Snackbar.LENGTH_LONG);
                            View view = sn.getView();
                            TextView tv = (TextView) view.findViewById(android.support.design.R.id.snackbar_text);
                            tv.setTextColor(Color.parseColor("#FFD700"));
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                                tv.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
                            } else {
                                tv.setGravity(Gravity.CENTER_HORIZONTAL);
                            }
                            sn.show();
                            continue;
                        }
                    }

                }
                return true;
            }
        });

        mSearchBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mSearchField.getText().toString().trim().startsWith("#")) {
                    //String temp = mSearchField.getText().toString().toLowerCase().trim();
                    String[] t = mSearchField.getText().toString().toLowerCase().trim().split(" ");
                    mSearchField.setText(null);
                    for (String a : t) {
                        if (Arrays.asList(tags).contains(a)) {
                            addChipToGroup(a, mTagsChipGroup);
                        } else {
                            Snackbar sn = Snackbar.make(findViewById(android.R.id.content), a + " does not exist", Snackbar.LENGTH_LONG);
                            View view = sn.getView();
                            TextView tv = (TextView) view.findViewById(android.support.design.R.id.snackbar_text);
                            tv.setTextColor(Color.parseColor("#FFD700"));
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                                tv.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
                            } else {
                                tv.setGravity(Gravity.CENTER_HORIZONTAL);
                            }
                            sn.show();
                            continue;
                        }
                    }
                } else {
                    if ((mTagsChipGroup.getChildCount() > 0 && !mSearchField.getText().toString().trim().isEmpty()) || (mTagsChipGroup.getChildCount() > 0 && mSearchField.getText().toString().equals(" "))) {
                        mTagsChipGroup.removeAllViews();
                        return;
                    }
                    String search = mSearchField.getText().toString().trim();
                    firebaseSearch(search);
                }
            }
        });

        if (initialize) {
            mSearchBtn.performClick();
            initialize = false;
        }

        mSearchField.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (!s.toString().trim().startsWith("#")) {
                    mSearchBtn.performClick();
                }
            }
        });

        firebaseTagSearch();

        final ArrayAdapter<String> autoFillAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, tags);
        mSearchField.setAdapter(autoFillAdapter);
        mSearchField.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                mSearchField.setText(null);
                String selected = parent.getItemAtPosition(position).toString();
                Log.d(TAG, "adding chip" + selected);
                addChipToGroup(selected, mTagsChipGroup);
            }
        });
    }

    /**
     * method firebaseSearch enables search by Text
     *
     * @param search [the input searching text]
     */
    private void firebaseSearch(String search) {

        Query firebaseQuery = mClubRef.orderByChild(getApplicationContext().getString(R.string.firebase_lower_case_club_name_tag)).startAt(search.toLowerCase()).endAt(search.toLowerCase() + "\uf88f");

        FirebaseRecyclerOptions searchOption = new FirebaseRecyclerOptions.Builder<Clubs>()
                .setQuery(firebaseQuery, Clubs.class).build();

        FirebaseRecyclerAdapter<Clubs, SearchListViewHolder> firebaseRecyclerAdapter;
        firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<Clubs, SearchListViewHolder>(searchOption) {
            @Override
            protected void onBindViewHolder(@NonNull final SearchListViewHolder holder, int position, @NonNull final Clubs model) {
                RequestOptions requestoptions = new RequestOptions();
                Glide.with(holder.mCircleImage.getContext())
                        .load(getRandom.getRandomUCSDDrawable())
                        .apply(requestoptions.fitCenter())
                        .into(holder.mCircleImage);

                holder.mName.setText(model.getClub_name());

                FirebaseUser user = mAuth.getCurrentUser();

                holder.mView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent clubProfilePage = new Intent(getApplicationContext(), ClubProfileActivity.class);
                        clubProfilePage.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                        Log.d(TAG, "SearchViewItem.setOnClickListener Intent Extra: " + holder.mName.getText().toString());
                        clubProfilePage.putExtra(ClubProfileActivity.EXTRA, holder.mName.getText().toString().trim());
                        startActivity(clubProfilePage);
                    }
                });

                if (user == null) {
                    holder.mStar.setVisibility(View.INVISIBLE);
                } else {
                    holder.mStar.setVisibility(View.VISIBLE);
                    final DatabaseReference mUserRef = mFirebaseDatabase.getReference().child(getApplicationContext().getString(R.string.firebase_users_tag)).child(user.getUid());
                    mUserRef.child(getApplicationContext().getString(R.string.firebase_following_clubs_tag)).addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            holder.mStar.setLiked(false);
                            if (dataSnapshot.hasChildren()) {
                                for (DataSnapshot ds : dataSnapshot.getChildren()) {
                                    FollowingClubs followingClubs = ds.getValue(FollowingClubs.class);
                                    if (followingClubs.getClub_name().equals(model.getClub_name())) {
                                        holder.mStar.setLiked(true);
                                    }
                                }
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });

                    holder.mStar.setOnLikeListener(new OnLikeListener() {
                        @Override
                        public void liked(LikeButton likeButton) {
                            mUserRef.child(getApplicationContext().getString(R.string.firebase_following_clubs_tag)).child(model.getClub_name()).setValue(model);
                        }

                        @Override
                        public void unLiked(LikeButton likeButton) {
                            mUserRef.child(getApplicationContext().getString(R.string.firebase_following_clubs_tag)).child(model.getClub_name()).removeValue();
                        }
                    });
                }
            }

            @NonNull
            @Override
            public SearchListViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
                View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.clubsuggestions_list_layout, viewGroup, false);

                return new SearchListViewHolder(view);
            }
        };

        mRecyclerView.setAdapter(firebaseRecyclerAdapter);
        firebaseRecyclerAdapter.startListening();
    }

    /**
     * method firebaseTagSearch enables search by Tags
     */
    private void firebaseTagSearch() {
        mTagsChipGroup.setOnHierarchyChangeListener(new ViewGroup.OnHierarchyChangeListener() {
            @Override
            public void onChildViewAdded(View parent, View child) {
                Log.d(TAG, "new child added...");
                String selectedTag = "";
                if (mTagsChipGroup.getChildCount() > 0) {
                    for (int i = 0; i < mTagsChipGroup.getChildCount(); i++) {
                        String temp = ((Chip) mTagsChipGroup.getChildAt(i)).getText().toString().toLowerCase() + ",";
                        Log.d(TAG, "new child added... " + temp);
                        selectedTag = selectedTag + temp;
                    }
                    if (!selectedTag.isEmpty()) {
                        selectedTag = selectedTag.substring(0, selectedTag.lastIndexOf(","));
                    }
                    Log.d(TAG, "new child added... " + selectedTag);

                    final String[] split = selectedTag.split(",");

                    mClubRef.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            for (DataSnapshot ds : dataSnapshot.getChildren()) {
                                if (ds.hasChild("tags")) {
                                    Tags tag = ds.child("tags").getValue(Tags.class);
                                    int matchedCount = 0;
                                    ArrayList<String> temp = tag.allTrue();
                                    for (String i : split) {
                                        if (temp.contains(i)) {
                                            Log.d(TAG, "checking if " + ds.getKey() + " contains " + i);
                                            matchedCount++;
                                        }
                                    }
                                    if (matchedCount == mTagsChipGroup.getChildCount())
                                        potentialClubs.add(ds.getKey());
                                }
                            }
                            tagsAdapter = new TagsSearchRecyclerViewAdapter(getApplicationContext(), potentialClubs);
                            mRecyclerView.setAdapter(tagsAdapter);
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });
                }
                potentialClubs.clear();
            }

            @Override
            public void onChildViewRemoved(View parent, View child) {
                if (mTagsChipGroup.getChildCount() == 0) {
                    String search = mSearchField.getText().toString().trim();
                    firebaseSearch(search);
                } else {
                    String selectedTag = "";
                    if (mTagsChipGroup.getChildCount() > 0) {
                        for (int i = 0; i < mTagsChipGroup.getChildCount(); i++) {
                            String temp = ((Chip) mTagsChipGroup.getChildAt(i)).getText().toString().toLowerCase() + ",";
                            Log.d(TAG, "new child added... " + temp);
                            selectedTag = selectedTag + temp;
                        }
                        if (!selectedTag.isEmpty()) {
                            selectedTag = selectedTag.substring(0, selectedTag.lastIndexOf(","));
                        }
                        Log.d(TAG, "new child added... " + selectedTag);

                        final String[] split = selectedTag.split(",");

                        mClubRef.addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                for (DataSnapshot ds : dataSnapshot.getChildren()) {
                                    if (ds.hasChild("tags")) {
                                        Tags tag = ds.child("tags").getValue(Tags.class);
                                        int matchedCount = 0;
                                        ArrayList<String> temp = tag.allTrue();
                                        for (String i : split) {
                                            if (temp.contains(i)) {
                                                Log.d(TAG, "checking if " + ds.getKey() + " contains " + i);
                                                matchedCount++;
                                            }
                                        }
                                        if (matchedCount == mTagsChipGroup.getChildCount())
                                            potentialClubs.add(ds.getKey());
                                    }
                                }
                                tagsAdapter = new TagsSearchRecyclerViewAdapter(getApplicationContext(), potentialClubs);
                                mRecyclerView.setAdapter(tagsAdapter);
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {

                            }
                        });
                    }
                    potentialClubs.clear();
                }
            }
        });
    }

    /**
     * ViewHolder of each item (each club)
     */
    public class SearchListViewHolder extends RecyclerView.ViewHolder {
        public View mView;
        public CircleImageView mCircleImage;
        public TextView mName;
        public LikeButton mStar;

        public SearchListViewHolder(View itemView) {
            super(itemView);
            mView = itemView;
            mCircleImage = itemView.findViewById(R.id.club_suggestion_circleImageView);
            mName = itemView.findViewById(R.id.club_suggestion_clubdescription);
            mStar = itemView.findViewById(R.id.club_suggestion_star);
        }

    }


    /**
     * method addChipToGroup adds a new chip to the ChipGroup
     *
     * @param tagsName  [the new input tag name]
     * @param chipGroup [the ChipGroup in the View]
     */
    private void addChipToGroup(String tagsName, final ChipGroup chipGroup) {
        boolean existed = false;
        if (mTagsChipGroup.getChildCount() > 0) {
            for (int i = 0; i < mTagsChipGroup.getChildCount(); i++) {
                if (((Chip) mTagsChipGroup.getChildAt(i)).getText().toString().equals(tagsName)) {
                    existed = true;
                    mRecyclerView.setAdapter(tagsAdapter);
                }
            }
        }

        if (!existed) {
            final Chip chip = new Chip(this);
            chip.setText(tagsName);
            chip.setClickable(true);
            chip.setCloseIconVisible(true);
            chip.setTextColor(Color.parseColor(getRandom.getRandomColor()));
            chipGroup.addView(chip);
            chip.setOnCloseIconClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    chipGroup.removeView(chip);
                }
            });
        }
    }

    /**
     * Firebase Adapter when search by Tags
     */
    public class TagsSearchRecyclerViewAdapter extends RecyclerView.Adapter<TagsSearchRecyclerViewAdapter.ViewHolder> {

        Context mContext;
        private List<String> mValue;

        public class ViewHolder extends RecyclerView.ViewHolder {
            public View mView;
            public CircleImageView mCirleImage;
            public TextView mName;
            public LikeButton mStar;

            public ViewHolder(View itemView) {
                super(itemView);
                mView = itemView;
                mCirleImage = itemView.findViewById(R.id.club_suggestion_circleImageView);
                mName = itemView.findViewById(R.id.club_suggestion_clubdescription);
                mStar = itemView.findViewById(R.id.club_suggestion_star);
            }
        }

        public TagsSearchRecyclerViewAdapter(Context context, List<String> items) {
            this.mContext = context;
            mValue = items;
        }

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
            View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.clubsuggestions_list_layout, viewGroup, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull final ViewHolder viewHolder, int i) {
            RequestOptions requestoptions = new RequestOptions();
            Glide.with(viewHolder.mCirleImage.getContext())
                    .load(getRandom.getRandomUCSDDrawable())
                    .apply(requestoptions.fitCenter())
                    .into(viewHolder.mCirleImage);

            viewHolder.mName.setText(mValue.get(i));

            FirebaseAuth aAuth = FirebaseAuth.getInstance();
            FirebaseUser user = aAuth.getCurrentUser();
            final FirebaseDatabase aFirebaseDatabase = FirebaseDatabase.getInstance();

            viewHolder.mView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent clubProfilePage = new Intent(getApplicationContext(), ClubProfileActivity.class);
                    clubProfilePage.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                    Log.d(TAG, "SearchViewItem.setOnClickListener Intent Extra: " + viewHolder.mName.getText().toString());
                    clubProfilePage.putExtra(ClubProfileActivity.EXTRA, viewHolder.mName.getText().toString().trim());
                    startActivity(clubProfilePage);
                }
            });

            if (user == null) {
                viewHolder.mStar.setVisibility(View.INVISIBLE);
            } else {
                viewHolder.mStar.setVisibility(View.VISIBLE);
                final DatabaseReference mUserRef = aFirebaseDatabase.getReference().child("users").child(user.getUid());
                mUserRef.child("following_clubs").addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        viewHolder.mStar.setLiked(false);
                        if (dataSnapshot.hasChildren() && viewHolder.getAdapterPosition() >= 0) {
                            for (DataSnapshot ds : dataSnapshot.getChildren()) {
                                FollowingClubs followingClubs = ds.getValue(FollowingClubs.class);
                                if (followingClubs.getClub_name().equals(mValue.get(viewHolder.getAdapterPosition()))) {
                                    viewHolder.mStar.setLiked(true);
                                }
                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });

                viewHolder.mStar.setOnLikeListener(new OnLikeListener() {
                    @Override
                    public void liked(LikeButton likeButton) {
                        DatabaseReference mClub = aFirebaseDatabase.getReference().child("clubs").child(mValue.get(viewHolder.getAdapterPosition()));
                        mClub.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                FollowingClubs model = dataSnapshot.getValue(FollowingClubs.class);
                                mUserRef.child("following_clubs").child(mValue.get(viewHolder.getAdapterPosition())).setValue(model);
                                viewHolder.mStar.setLiked(true);
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {

                            }
                        });
                    }

                    @Override
                    public void unLiked(LikeButton likeButton) {
                        DatabaseReference mClub = aFirebaseDatabase.getReference().child("clubs").child(mValue.get(viewHolder.getAdapterPosition()));
                        mClub.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                FollowingClubs model = dataSnapshot.getValue(FollowingClubs.class);
                                mUserRef.child("following_clubs").child(model.getClub_name()).removeValue();
                                viewHolder.mStar.setLiked(false);
                                onResume();
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {

                            }
                        });
                    }
                });
            }
        }

        @Override
        public int getItemCount() {
            return mValue.size();
        }

        public void removeItem(int position) {
            mValue.remove(position);
            notifyItemRemoved(position);
        }

        public void restoreItem(String name, int position) {
            mValue.add(position, name);
            notifyItemInserted(position);
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

    /*
    public void onResume() {
        super.onResume();
        Log.d(TAG, "resume");
        if (mTagsChipGroup.getChildCount() > 0) {
            firebaseTagSearch();
        } else {
            firebaseSearch(mSearchField.getText().toString());
        }
    }
    */
}
