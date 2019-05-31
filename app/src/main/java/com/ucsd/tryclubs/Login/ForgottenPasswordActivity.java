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
 * class ForgottenPasswordActivity is the "forgot password" page in the App.
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
                mResetPassswordBtn.setVisibility(View.INVISIBLE);

                // get user's email
                String user_input_email = mEmailInputEditText.getText().toString();

                if (!TextUtils.isEmpty(user_input_email)) {
                    mAuth.sendPasswordResetEmail(user_input_email).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                goToLoginInActivityHelper();
                                Toast.makeText(ForgottenPasswordActivity.this, "Reset Password Email Sent", Toast.LENGTH_LONG).show();
                                //Snackbar.make(findViewById(android.R.id.content),  "Reset Password Email Sent", Snackbar.LENGTH_LONG).show();
                            } else {
                                //Toast.makeText(ForgottenPasswordActivity.this, "Couldn't send reset password email", Toast.LENGTH_LONG).show();
                                //Snackbar.make(findViewById(android.R.id.content),  "Couldn't send reset password email", Snackbar.LENGTH_LONG).show();

                                String ErrorMsg = task.getException().getMessage();
                                Snackbar sn = Snackbar.make(findViewById(android.R.id.content),  "Error: " + ErrorMsg, Snackbar.LENGTH_LONG);
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
                            mProgressBar.setVisibility(View.INVISIBLE);
                            mAlreadyHaveaccountTextView.setVisibility(View.VISIBLE);
                            mLoginTextView.setVisibility(View.VISIBLE);
                            mResetPassswordBtn.setVisibility(View.VISIBLE);
                        }
                    });
                } else {
                    Snackbar sn = Snackbar.make(findViewById(android.R.id.content),  "Please Enter an Email", Snackbar.LENGTH_LONG);
                    View view = sn.getView();
                    TextView tv = (TextView) view.findViewById(android.support.design.R.id.snackbar_text);
                    tv.setTextColor(Color.parseColor("#FFD700"));
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
                        tv.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
                    } else {
                        tv.setGravity(Gravity.CENTER_HORIZONTAL);
                    }
                    sn.show();
                    mProgressBar.setVisibility(View.INVISIBLE);
                    mAlreadyHaveaccountTextView.setVisibility(View.VISIBLE);
                    mLoginTextView.setVisibility(View.VISIBLE);
                    mResetPassswordBtn.setVisibility(View.VISIBLE);
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

        mEmailInputEditText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if ((actionId == EditorInfo.IME_NULL && event.getAction() == KeyEvent.ACTION_DOWN)|| (actionId == EditorInfo.IME_ACTION_DONE)){
                    View view = getCurrentFocus();
                    if (view != null) {
                        InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
                        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
                    }
                    mResetPassswordBtn.performClick();
                }
                return true;
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
     * Helper method which use Intent to go back to the Login Page
     */
    private void goToLoginInActivityHelper() {
        Intent goToLoginInActivity = new Intent(getApplicationContext(), LoginActivity.class);
        goToLoginInActivity.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(goToLoginInActivity);
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
