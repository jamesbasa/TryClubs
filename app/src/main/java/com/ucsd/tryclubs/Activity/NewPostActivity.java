package com.ucsd.tryclubs.Activity;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.support.annotation.NonNull;
import android.support.design.button.MaterialButton;
import android.support.design.widget.TextInputEditText;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.ucsd.tryclubs.Model.Clubs;
import com.ucsd.tryclubs.Model.Post;
import com.ucsd.tryclubs.R;

import org.w3c.dom.Text;

import java.sql.Date;
import java.text.DateFormat;
import java.text.DateFormatSymbols;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.Locale;

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
        mDescription = findViewById(R.id.post_creation_description);
        mExit = findViewById(R.id.post_creation_exit_imageView);
        mDoneBtn = findViewById(R.id.post_creation_done_MaterialButton);
        mEventName = findViewById(R.id.post_creation_event_name);

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
                if (!mClubName.getText().toString().isEmpty() && !mDate.getText().toString().isEmpty() && !mTime.getText().toString().isEmpty() && !mLocation.getText().toString().isEmpty() && !mDescription.getText().toString().isEmpty() && !mEventName.getText().toString().isEmpty()) {
                    addPost();
                } else {
                    Toast.makeText(getApplicationContext(), "Please Make Sure All Text Fields Are Filled.", Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    private void addPost() {
        DatabaseReference mEventRef = mFirebaseDatabase.getReference().child(getApplicationContext().getString(R.string.firebase_events_tag));
        DatabaseReference mClubRef = mFirebaseDatabase.getReference().child(getApplicationContext().getString(R.string.firebase_clubs_tag)).child(clubName);

        Post post = new Post(mEventName.getText().toString().trim(),
                clubName,mLocation.getText().toString().trim(),
                mDate.getText().toString().trim(),mTime.getText().toString().trim(),
                mDescription.getText().toString().trim());
        DatabaseReference postClubRef = mClubRef.child(getApplicationContext().getString(R.string.firebase_events_tag)).push();
        postClubRef.setValue(post);

        DatabaseReference postEventRef = mEventRef.push();
        postEventRef.setValue(post);
        onBackPressed();
    }


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
                        }
                    }, mYear, mMonth, mDay);
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