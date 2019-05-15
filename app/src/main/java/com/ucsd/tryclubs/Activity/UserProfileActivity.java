package com.ucsd.tryclubs.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.blogspot.atifsoftwares.animatoolib.Animatoo;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.ucsd.tryclubs.MainActivity;
import com.ucsd.tryclubs.R;

/**
 * Class UserProfileActivity sets the content to res/layout/activity_user_profile.xml
 * and this is the Profile Page in the App.
 *
 */
public class UserProfileActivity extends AppCompatActivity {

    // all stuff
    private Button mChangePasswordBtn;
    private Button mDeleteAccountBtn;
    private ImageButton mGoBackBtn;
    private TextView mWelcomeTextView;
    private FirebaseAuth mAuth;
    private ImageView mGaryFace;
    private FirebaseDatabase mFirebaseDatabase;
    private DatabaseReference mBigBranchRef;

    // Easter Egg
    private int hitGary = 1;

    /**
     * OnCreate() method is the first method run automatically when
     * an Activity is called
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile);

        // Firebase stuff
        mAuth = FirebaseAuth.getInstance();
        mFirebaseDatabase = FirebaseDatabase.getInstance();
        mBigBranchRef = mFirebaseDatabase.getReference();
        mBigBranchRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (!dataSnapshot.hasChild(getApplicationContext().getString(R.string.firebase_users_tag))) {
                    mBigBranchRef.child(getApplicationContext().getString(R.string.firebase_users_tag)).setValue("");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        // getting all stuff from view
        final FirebaseUser user = mAuth.getCurrentUser();
        mChangePasswordBtn = (Button) findViewById(R.id.profilePage_changePassword_button);
        mDeleteAccountBtn = (Button) findViewById(R.id.profilePage_deleteAccount_button);
        mGoBackBtn = (ImageButton) findViewById(R.id.profilePage_cancel_imagebutton);
        mWelcomeTextView = (TextView) findViewById(R.id.profilePage_welcomeWord_textview);
        mGaryFace = (ImageView) findViewById(R.id.profilePage_gary_imageView);

        // attach username to "HELLO"
        String oldWelcome = mWelcomeTextView.getText().toString();
        String username = user.getDisplayName();
        if (!TextUtils.isEmpty(username)) {
            String newWelcome = oldWelcome + " " + username + "!";
            mWelcomeTextView.setText(newWelcome.toUpperCase());
        }

        // when the change my password is clicked
        mChangePasswordBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mAuth.sendPasswordResetEmail(user.getEmail()).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        Toast.makeText(UserProfileActivity.this, "Reset Password Email Sent", Toast.LENGTH_LONG).show();
                    }
                });
            }
        });

        // when the delete account is clicked
        mDeleteAccountBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                user.delete();

                FirebaseDatabase.getInstance().getReference().child(getApplicationContext().getString(R.string.firebase_users_tag))
                        .child(mAuth.getCurrentUser().getUid()).removeValue();


                mAuth.signOut();
                goToMainActivityHelper();
                Toast.makeText(UserProfileActivity.this, "Account Deleted!", Toast.LENGTH_LONG).show();
            }
        });

        // when the "x" button is clicked
        mGoBackBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goToMainActivityHelper();
            }
        });

        // Easter Egg - when Gary is clicked
        mGaryFace.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (hitGary <= 5) {
                    Toast.makeText(getApplicationContext(), "Gary: Ouch! Stop it! It hurts!", Toast.LENGTH_LONG).show();
                    hitGary++;
                } else {
                    Toast.makeText(getApplicationContext(), "Professionalism Point Deduction!", Toast.LENGTH_LONG).show();
                    hitGary = 0;
                }
            }
        });

    }

    /**
     * Override system method onBackPress() which defines the "back" button behaviour
     * in the Action Bar of Android
     */
    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    /**
     * onStart() will automatically call after onCreate()
     */
    @Override
    protected void onStart() {
        super.onStart();

        // get current user from Firebase
        FirebaseUser user = mAuth.getCurrentUser();

        // checking if user is signed in
        if (user == null) {
            goToMainActivityHelper();

        } else {
            // when user is signed in
        }
    }

    /**
     * Helper method which use Intent to go back to the Main Page (Timeline)
     */
    private void goToMainActivityHelper() {
        startActivity(new Intent(getApplicationContext(), MainActivity.class));
        Animatoo.animateSlideUp(this); //fire the slide up animation
        finish();
    }
}
