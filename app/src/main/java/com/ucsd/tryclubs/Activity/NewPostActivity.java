package com.ucsd.tryclubs.Activity;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.graphics.Color;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.design.button.MaterialButton;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputEditText;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.TimePicker;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.ucsd.tryclubs.Model.Clubs;
import com.ucsd.tryclubs.Model.Post;
import com.ucsd.tryclubs.R;

import java.text.DateFormatSymbols;
import java.util.Calendar;

/**
 * class NewPostActivity is the "Post Creation" page in the App.
 */
public class NewPostActivity extends AppCompatActivity implements View.OnClickListener {

    private static final String TAG = "NewPostActivity";

    private TextInputEditText mClubName;
    private TextInputEditText mDate;
    private TextInputEditText mTime;
    private TextInputEditText mLocation;
    private TextInputEditText mDescription;
    private TextInputEditText mEventName;
    private ImageView mExit;
    private MaterialButton mDoneBtn;

    private FirebaseAuth mAuth;
    private FirebaseDatabase mFirebaseDatabase;
    private DatabaseReference mUserRef;
    private DatabaseReference mMyClubRef;

    private String clubName = "";
    private int mYear, mMonth, mDay, mHour, mMinute;

    @Override
    protected void onCreate(Bundle savedInstanceState)

    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_post);

        mAuth = FirebaseAuth.getInstance();
        mFirebaseDatabase = FirebaseDatabase.getInstance();
        mUserRef = mFirebaseDatabase.getReference().child(getApplicationContext().getString(R.string.firebase_users_tag)).child(mAuth.getCurrentUser().getUid());
        mMyClubRef = mUserRef.child(getApplicationContext().getString(R.string.firebase_my_club_tag));

        mClubName = findViewById(R.id.post_creation_clubname);
        mDate = findViewById(R.id.post_creation_date);
        mTime = findViewById(R.id.post_creation_time);
        mLocation = findViewById(R.id.post_creation_location);
        mLocation.setInputType(InputType.TYPE_CLASS_TEXT);
        mDescription = findViewById(R.id.post_creation_description);
        mExit = findViewById(R.id.post_creation_exit_imageView);
        mDoneBtn = findViewById(R.id.post_creation_done_MaterialButton);
        mEventName = findViewById(R.id.post_creation_event_name);
        mEventName.setInputType(InputType.TYPE_CLASS_TEXT);

        mMyClubRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                DataSnapshot ds = dataSnapshot.getChildren().iterator().next();
                Clubs club = new Clubs();
                club.setClub_name(ds.getValue(Clubs.class).getClub_name());
                clubName = club.getClub_name();
                Log.d(TAG, club.getClub_name());
                Log.d(TAG, club.getClub_name());
                mClubName.setText(clubName);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        mDate.setKeyListener(null);
        mTime.setKeyListener(null);
        mDate.setOnClickListener(this);
        mTime.setOnClickListener(this);

        mExit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        mDoneBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mEventName.getText().toString().trim().equals(".") || mEventName.getText().toString().trim().equals("#") || mEventName.getText().toString().trim().equals("$")
                        || mEventName.getText().toString().trim().equals("[") || mEventName.getText().toString().trim().equals("]")) {
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
                    return;
                }
                if (!mClubName.getText().toString().trim().isEmpty() && !mDate.getText().toString().trim().isEmpty() && !mTime.getText().toString().trim().isEmpty() && !mLocation.getText().toString().trim().isEmpty() && !mDescription.getText().toString().trim().isEmpty() && !mEventName.getText().toString().trim().isEmpty()) {
                    addPost();
                } else {
                    //Toast.makeText(getApplicationContext(), "Please Make Sure All Text Fields Are Filled.", Toast.LENGTH_LONG).show();
                    Snackbar sn = Snackbar.make(findViewById(android.R.id.content),  "Please Make Sure All Text Fields Are Filled.", Snackbar.LENGTH_LONG);
                    View view = sn.getView();
                    TextView tv = (TextView) view.findViewById(android.support.design.R.id.snackbar_text);
                    tv.setTextColor(Color.parseColor("#FFD700"));
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
                        tv.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
                    } else {
                        tv.setGravity(Gravity.CENTER_HORIZONTAL);
                    }
                    sn.show();
                }
            }
        });
    }

    /**
     * method addPost adds the new post under the club, user's following events, and events
     */
    private void addPost() {
        DatabaseReference mEventRef = mFirebaseDatabase.getReference().child(getApplicationContext().getString(R.string.firebase_events_tag));
        DatabaseReference mClubRef = mFirebaseDatabase.getReference().child(getApplicationContext().getString(R.string.firebase_clubs_tag)).child(clubName);

        mEventRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.hasChild(mEventName.getText().toString().trim())) {
                    Snackbar sn = Snackbar.make(findViewById(android.R.id.content), "Event name conflicted. Choose another name.", Snackbar.LENGTH_LONG);
                    View view = sn.getView();
                    TextView tv = (TextView) view.findViewById(android.support.design.R.id.snackbar_text);
                    tv.setTextColor(Color.parseColor("#FFD700"));
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        tv.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
                    } else {
                        tv.setGravity(Gravity.CENTER_HORIZONTAL);
                    }
                    sn.show();
                } else {
                    DatabaseReference mEventRef = mFirebaseDatabase.getReference().child(getApplicationContext().getString(R.string.firebase_events_tag));
                    DatabaseReference mClubRef = mFirebaseDatabase.getReference().child(getApplicationContext().getString(R.string.firebase_clubs_tag)).child(clubName);
                    String d = String.format("%02d", mDay);
                    String m = String.format("%02d", mMonth);
                    String y = Integer.toString(mYear);
                    String t = y+m+d;
                    Post post = new Post(mEventName.getText().toString().trim(),
                            mClubName.getText().toString().trim(),mLocation.getText().toString().trim(),
                            mDate.getText().toString().trim(),mTime.getText().toString().trim(),
                            mDescription.getText().toString().trim(), Long.parseLong(t));

                    mFirebaseDatabase.getReference().child("clubs").child(mClubName.getText().toString().trim()).child(getApplicationContext().getString(R.string.firebase_events_tag)).child(post.getEname()).setValue(post);
                    //mClubRef.child(getApplicationContext().getString(R.string.firebase_events_tag)).child(post.getEname()).setValue(post);
                    mEventRef.child(post.getEname()).setValue(post);
                    mUserRef.child(getApplicationContext().getString(R.string.firebase_following_events_tag)).child(post.getEname()).setValue(post);
                    //mEventRef.child(post.getEname()).child("sort_date").setValue(Long.parseLong(t));
                    onBackPressed();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }


    /**
     * Override onClick to define the behavior of clicking the Date or Time Picker
     */
    @Override
    public void onClick(View v) {
        if (v == mDate) {
            // Get Current Date
            final Calendar c = Calendar.getInstance();
            mYear = c.get(Calendar.YEAR);
            mMonth = c.get(Calendar.MONTH);
            mDay = c.get(Calendar.DAY_OF_MONTH);


            DatePickerDialog datePickerDialog = new DatePickerDialog(this,
                    new DatePickerDialog.OnDateSetListener() {

                        @Override
                        public void onDateSet(DatePicker view, int year,
                                              int month, int day) {
                            String[] shortMonths = new DateFormatSymbols().getShortMonths();
                            String result = shortMonths[month] + " " + day + ", " + year;

                            mDate.setText(result);

                            mYear = year;
                            mMonth = month+1;
                            mDay = day;

                        }
                    }, mYear, mMonth, mDay);
            datePickerDialog.getDatePicker().setMinDate(System.currentTimeMillis() - 1000);
            datePickerDialog.show();
        }
        if (v == mTime) {

            // Get Current Time
            final Calendar c = Calendar.getInstance();
            mHour = c.get(Calendar.HOUR_OF_DAY);
            mMinute = c.get(Calendar.MINUTE);

            // Launch Time Picker Dialog
            TimePickerDialog timePickerDialog = new TimePickerDialog(this,
                    new TimePickerDialog.OnTimeSetListener() {

                        @Override
                        public void onTimeSet(TimePicker view, int hourOfDay,
                                              int minute) {
                            mTime.setText(String.format("%02d:%02d", hourOfDay, minute));
                        }
                    }, mHour, mMinute, false);
            timePickerDialog.show();
        }
    }
}