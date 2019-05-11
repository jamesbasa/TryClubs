package com.ucsd.tryclubs.Login;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.blogspot.atifsoftwares.animatoolib.Animatoo;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.ucsd.tryclubs.MainActivity;
import com.ucsd.tryclubs.R;

/**
 * Class ForgottenPasswordActivity sets the content to res/layout/activity_forgotten_password.xml
 * and this is the forgot password page.
 *
 */
public class ForgottenPasswordActivity extends AppCompatActivity {

    // all stuff in the view
    private EditText mEmailInputEditText;
    private TextView mAlreadyHaveaccountTextView;
    private TextView mLoginTextView;
    private Button mResetPassswordBtn;
    private ProgressBar mProgressBar;

    // Firebase stuff
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgotten_password);

        // all stuff in the view
        mEmailInputEditText = (EditText) findViewById(R.id.reset_email_editText);
        mAlreadyHaveaccountTextView = (TextView) findViewById(R.id.reset_alreadyhaveaccount_textView);
        mLoginTextView = (TextView) findViewById(R.id.reset_login_textView);
        mResetPassswordBtn = (Button) findViewById(R.id.resetPassword_button);
        mProgressBar = (ProgressBar) findViewById(R.id.progressBar);
        mProgressBar.setVisibility(View.INVISIBLE);

        // Firebase stuff
        mAuth = FirebaseAuth.getInstance();

        // when the reset password is clicked, sent an email
        mResetPassswordBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mProgressBar.setVisibility(View.VISIBLE);
                mAlreadyHaveaccountTextView.setVisibility(View.INVISIBLE);
                mLoginTextView.setVisibility(View.INVISIBLE);

                // get user's email
                String user_input_email = mEmailInputEditText.getText().toString();

                if (!TextUtils.isEmpty(user_input_email)) {
                    mAuth.sendPasswordResetEmail(user_input_email).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                goToLoginInActivityHelper();
                                Toast.makeText(ForgottenPasswordActivity.this, "Reset Password Email Sent", Toast.LENGTH_LONG).show();
                            } else {
                                // TODO
                            }
                            mProgressBar.setVisibility(View.INVISIBLE);
                            mAlreadyHaveaccountTextView.setVisibility(View.VISIBLE);
                            mLoginTextView.setVisibility(View.VISIBLE);
                        }
                    });
                }
            }
        });

        // when the login in text is clicked, go back to login page
        mLoginTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goToLoginInActivityHelper();
            }
        });
    }

    /**
     * onStart() will automatically call after onCreate()
     */
    @Override
    protected void onStart() {
        super.onStart();

        // get the current user
        FirebaseUser user = mAuth.getCurrentUser();

        if (user != null) {
            // when the user is signed in
        } else {
            // if user is not signed in
        }
    }

    /**
     * Override system method onBackPress() which defines the "back" button behaviour
     * in the Action Bar of Android
     */
    @Override
    public void onBackPressed(){
        super.onBackPressed();
    }

    /**
     * Helper method which use Intent to go back to the Login Page (Timeline)
     */
    private void goToLoginInActivityHelper() {
        startActivity(new Intent(getApplicationContext(), LoginActivity.class));
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
}
