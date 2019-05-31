package com.ucsd.tryclubs.Login;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.blogspot.atifsoftwares.animatoolib.Animatoo;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.ucsd.tryclubs.Activity.UserProfileActivity;
import com.ucsd.tryclubs.MainActivity;
import com.ucsd.tryclubs.Model.User;
import com.ucsd.tryclubs.R;

/**
 * Class SetUpAccountActivity is the "set up account" page in the App.
 */
public class SetUpAccountActivity extends AppCompatActivity {

    // Firebase stuff
    private FirebaseAuth mAuth;
    private String uid;
    private FirebaseDatabase mFirebaseDatabase;
    private DatabaseReference mDatabaseReference;

    // all stuff in the view
    private EditText mEnterUserNameEditText;
    private Button mNextButton;
    private String usernameField;
    private String userEmail;

    /**
     * OnCreate() method is the first method run automatically when
     * an Activity is called
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_set_up_account);

        // Firebase stuff
        mAuth = FirebaseAuth.getInstance();
        uid = mAuth.getCurrentUser().getUid();
        mFirebaseDatabase = FirebaseDatabase.getInstance();
        mDatabaseReference = mFirebaseDatabase.getReference();

        // all stuff in the view
        mEnterUserNameEditText = (EditText) findViewById(R.id.setup_account_username_editText);
        mNextButton = (Button) findViewById(R.id.setup_account_next_button);

        // when the next btn is clicked, update username and go to Discover page
        mNextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseUser user = mAuth.getCurrentUser();
                usernameField = mEnterUserNameEditText.getText().toString().trim();
                userEmail = user.getEmail();
                if (TextUtils.isEmpty(usernameField)) {
                    usernameField = userEmail.substring(0,userEmail.indexOf("@"));
                }
                if (user != null) {
                    // add user name to the account
                    UserProfileChangeRequest addUserName = new UserProfileChangeRequest.Builder()
                            .setDisplayName(usernameField).build();
                    user.updateProfile(addUserName).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                createUserClassAndAddtoFirebase(usernameField,userEmail, uid);
                                goToDiscoverActivityHelper();
                            } else {
                                //Toast.makeText(getApplicationContext(), task.getException().getMessage(),Toast.LENGTH_LONG).show();
                                Snackbar sn = Snackbar.make(findViewById(android.R.id.content),  task.getException().getMessage(), Snackbar.LENGTH_LONG);
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
                } else {
                    goToLoginActivityHelper();
                }
            }
        });

        mEnterUserNameEditText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if ((actionId == EditorInfo.IME_NULL && event.getAction() == KeyEvent.ACTION_DOWN)|| (actionId == EditorInfo.IME_ACTION_DONE)){
                    View view = getCurrentFocus();
                    if (view != null) {
                        InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
                        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
                    }
                    mNextButton.performClick();
                }
                return true;
            }
        });
    }

    private void createUserClassAndAddtoFirebase(String username, String userEmail, String uid) {
        User user = new User(username, userEmail, uid, "", "");

        mDatabaseReference.child(getApplicationContext().getString(R.string.firebase_users_tag))
                .child(uid)
                .setValue(user);

    }

    /**
     * onStart() will automatically call after onCreate()
     */
    @Override
    protected void onStart() {
        super.onStart();

        FirebaseUser user = mAuth.getCurrentUser();

        if (user != null) {
            // when the user is signed in
        } else {
            // not signed in - go back to login in page
            goToLoginActivityHelper();
        }
    }

    /**
     * Override system method onBackPress() which defines the "back" button behaviour
     * in the Action Bar of Android
     */
    @Override
    public void onBackPressed() {
        // NO GO BACK IS ALLOWED!
    }

    /**
     * Helper method which use Intent to go to the Login Page
     */
    private void goToLoginActivityHelper() {
        Intent loginPageIntent = new Intent(getApplicationContext(), LoginActivity.class);
        startActivity(loginPageIntent);
        finish();
    }

    /**
     * Helper method which use Intent to go to the Discover Page
     */
    private void goToDiscoverActivityHelper() {
        startActivity(new Intent(getApplicationContext(), DiscoverActivity.class));
        finish();
    }

    /**
     * Helper method which use Intent to go back to the Main Page (Timeline)
     */
    private void goToMainActivityHelper() {
        startActivity(new Intent(getApplicationContext(), MainActivity.class));
        Animatoo.animateSlideUp(this); //fire the slide up animation
        finish();
    }

    /**
     * Helper method which use Intent to go to the profile page
     */
    private void goToProfilePageActivityHelper() {
        startActivity(new Intent(getApplicationContext(), UserProfileActivity.class));
        finish();
    }
}
