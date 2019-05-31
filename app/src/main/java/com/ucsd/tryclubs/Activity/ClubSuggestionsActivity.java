package com.ucsd.tryclubs.Activity;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.like.LikeButton;
import com.ucsd.tryclubs.MainActivity;
import com.ucsd.tryclubs.Model.Category;
import com.ucsd.tryclubs.Model.Clubs;
import com.ucsd.tryclubs.R;
import com.ucsd.tryclubs.getRandom;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * class ClubSuggestionsActivity is the "Club Suggestions" page after Discover page in the App.
 */
public class ClubSuggestionsActivity extends AppCompatActivity {

    public static final String EXTRA = "selected_category";
    private static final String TAG = "ClubSuggestionsActivity";

    private FirebaseAuth mAuth;
    private FirebaseDatabase mFirebase;
    private DatabaseReference mAllClubsRef;
    private DatabaseReference mUserRef;
    private ArrayList<String> potentialClubs = new ArrayList<>();
    private RecyclerView mRecyclerView;

    SuggestionAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_club_suggestions);

        final String selectedString = getIntent().getStringExtra(EXTRA);

        if (selectedString.isEmpty()) {
            startActivity(new Intent(getApplicationContext(), MainActivity.class));
            finish();
        }

        Toolbar toolbar = findViewById(R.id.club_suggestion_toolbar);
        toolbar.setTitle("Club Suggestions");
        setSupportActionBar(toolbar);

        mAuth = FirebaseAuth.getInstance();
        mFirebase = FirebaseDatabase.getInstance();
        mAllClubsRef = mFirebase.getReference().child("clubs");
        mUserRef = mFirebase.getReference().child(getApplicationContext().getString(R.string.firebase_users_tag)).child(mAuth.getCurrentUser().getUid());

        final String[] split = selectedString.split(",");

        mAllClubsRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot ds : dataSnapshot.getChildren()) {
                    if (ds.hasChild("category")) {
                        Category category = new Category();
                        category.setCategory(ds.getValue(Category.class).getCategory());
                        for (String i : split) {
                            if (i.equals(category.getCategory())) {
                                potentialClubs.add(ds.getKey());
                            }
                        }
                    }
                }
                adapter = new SuggestionAdapter(getApplicationContext(),potentialClubs);
                mRecyclerView.setAdapter(adapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        mRecyclerView = findViewById(R.id.club_suggestion_recyclerView);
        mRecyclerView.hasFixedSize();
        setupRecyclerView();

    }

    public void setupRecyclerView() {
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
    }

    /**
     * Override onOptionsItemSelected to define the behavior of the "check" button
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.club_suggestion_done) {
            checkIfFollowingClubsIndexExist();
            int count = mRecyclerView.getLayoutManager().getChildCount();
            for (int i = 0; i < count; i++) {
                View v = mRecyclerView.getChildAt(i);
                if (v != null) {
                    LikeButton star = v.findViewById(R.id.club_suggestion_star);
                    TextView textView = v.findViewById(R.id.club_suggestion_clubdescription);
                    if (star.isLiked()) {
                        followthisclub(textView.getText().toString().trim());
                    }
                }
            }
            startActivity(new Intent(getApplicationContext(), MainActivity.class));
            finish();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.club_suggestion_menu, menu);
        return true;
    }

    /**
     * Firebase adapter of the club list
     */
    public static class SuggestionAdapter extends RecyclerView.Adapter<SuggestionAdapter.ViewHolder> {

        Context mContext;
        private List<String> mValue;

        public static class ViewHolder extends RecyclerView.ViewHolder {
            public View mView;
            public CircleImageView mCircleImage;
            public TextView mName;
            public LikeButton mStar;


            public ViewHolder(@NonNull View itemView) {
                super(itemView);
                mView = itemView;
                mCircleImage = itemView.findViewById(R.id.club_suggestion_circleImageView);
                mName = itemView.findViewById(R.id.club_suggestion_clubdescription);
                mStar = itemView.findViewById(R.id.club_suggestion_star);
            }
        }

        public SuggestionAdapter(Context context, List<String> items) {
            this.mContext = context;
            mValue = items;
        }

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
            View view = LayoutInflater.from(viewGroup.getContext())
                    .inflate(R.layout.clubsuggestions_list_layout, viewGroup, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull final ViewHolder viewHolder, int i) {
            viewHolder.mName.setText(mValue.get(i));
            viewHolder.mView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (viewHolder.mStar.isLiked()) {
                        viewHolder.mStar.setLiked(false);
                    } else {
                        viewHolder.mStar.setLiked(true);
                    }
                }
            });


            RequestOptions requestoptions = new RequestOptions();
            Glide.with(viewHolder.mCircleImage.getContext())
                    .load(getRandom.getRandomUCSDDrawable())
                    .apply(requestoptions.fitCenter())
                    .into(viewHolder.mCircleImage);
        }

        @Override
        public int getItemCount() {
            return mValue.size();
        }
    }

    /**
     * method checkIfFollowingClubsIndexExist checks if the following clubs index exists under user's Database
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
     * method followthisclub add the corresponding club under user's following clubs
     *
     * @param clubName  [the corresponding club name]
     */
    public void followthisclub(final String clubName) {

        mAllClubsRef.child(clubName).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Log.d(TAG, "followthisclub, club name: " + dataSnapshot);
                Clubs club = dataSnapshot.getValue(Clubs.class);
                mUserRef.child(getApplicationContext().getString(R.string.firebase_following_clubs_tag)).child(clubName).setValue(club);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}
