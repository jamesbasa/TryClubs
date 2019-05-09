package com.ucsd.tryclubs.login;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.blogspot.atifsoftwares.animatoolib.Animatoo;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.ucsd.tryclubs.MainActivity;
import com.ucsd.tryclubs.R;
import com.ucsd.tryclubs.UserProfileActivity;

/**
 * Class SetUpAccountActivity sets the content to res/layout/activity_set_up_account.xml
 * and this is page where user assigns their username after sign up as a new user.
 *
 */
public class SetUpAccountActivity extends AppCompatActivity {

    // Firebase stuff
    private FirebaseAuth mAuth;

    // all stuff in the view
    private EditText mEnterUserNameEditText;
    private Button mNextButton;

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

        // all stuff in the view
        mEnterUserNameEditText = (EditText) findViewById(R.id.setup_account_username_editText);
        mNextButton = (Button) findViewById(R.id.setup_account_next_button);

        // when the next btn is clicked, update username and go to Discover page
        mNextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseUser user = mAuth.getCurrentUser();
                String usernameField = mEnterUserNameEditText.getText().toString();
                if (TextUtils.isEmpty(usernameField)) {
                    String userEmail = user.getEmail();
                    usernameField = userEmail.substring(0,userEmail.indexOf("@"));
                }
                if (user != null) {
                    // add user name to the account
                    UserProfileChangeRequest addUserName = new UserProfileChangeRequest.Builder()
                            .setDisplayName(usernameField).build();
                    user.updateProfile(addUserName);

                    // TODO - CHANGE AFTER BUBBLE PICKER IS FINISHED
                    goToProfilePageActivityHelper();
                } else {
                    goToLoginActivityHelper();
                }
            }
        });
    }

    /**
     * onStart() will automatically call after onCreate()
     */
    @Override
    protected void onStart() {
        super.onStart();

        FirebaseUser user = mAuth.getCurrentUser();

        // TODO
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
     * TODO - CALL WHEN DISCOVER IS FINISHED
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
