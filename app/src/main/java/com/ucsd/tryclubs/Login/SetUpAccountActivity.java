package com.ucsd.tryclubs.Login;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.blogspot.atifsoftwares.animatoolib.Animatoo;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.FirebaseDatabase;
import com.ucsd.tryclubs.Activity.UserProfileActivity;
import com.ucsd.tryclubs.MainActivity;
import com.ucsd.tryclubs.Module.User;
import com.ucsd.tryclubs.R;

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
    private String usernameField;
    private String userEmail;

    static final String admit = "ADMIT";
    static final String student = "STUDENT";

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
                                createUserClassAndAddtoFirebase(usernameField,userEmail);
                            } else {
                                Toast.makeText(getApplicationContext(), task.getException().getMessage(),Toast.LENGTH_LONG).show();
                            }
                        }
                    });
                    goToDiscoverActivityHelper();
                } else {
                    goToLoginActivityHelper();
                }
            }
        });
    }

    private void createUserClassAndAddtoFirebase(String username, String userEmail) {
        User user = new User(username, userEmail, student);
        FirebaseDatabase.getInstance().getReference("Users").child(mAuth.getCurrentUser().getUid())
                .setValue(user);

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
