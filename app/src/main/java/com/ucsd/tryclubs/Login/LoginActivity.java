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
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.blogspot.atifsoftwares.animatoolib.Animatoo;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.ucsd.tryclubs.MainActivity;
import com.ucsd.tryclubs.R;
import com.ucsd.tryclubs.Activity.UserProfileActivity;

/**
 * class LoginActivity is the "Log in" page in the app in the App.
 */
public class LoginActivity extends AppCompatActivity {

    // define all stuff
    private TextView mSignUpTextView;
    private TextView mGuestTextView;
    private TextView mNoaccountTextView;
    private TextView mForgottenPasswordTextView;
    private EditText mEmailText;
    private EditText mPasswordText;
    private Button mLoginButton;
    private ProgressBar mLoginProgressbar;
    private ImageButton mVisibleImBtn;
    private ImageButton mInvisibleImBtn;

    // Firebase stuff
    private FirebaseAuth mAuth;

    /**
     * OnCreate() method is the first method run automatically when
     * an Activity is called
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // getting authentication from Firebase
        mAuth = FirebaseAuth.getInstance();

        // getting all stuff from the view
        mLoginButton = (Button) findViewById(R.id.login_button);
        mLoginProgressbar = (ProgressBar) findViewById(R.id.progressBar);
        mSignUpTextView = (TextView) findViewById(R.id.signup_textView);
        mGuestTextView = (TextView) findViewById(R.id.guest_textView);
        mNoaccountTextView = (TextView) findViewById(R.id.noAccount_textView);
        mEmailText = (EditText) findViewById(R.id.email_editText);
        mPasswordText = (EditText) findViewById(R.id.password_editText);
        mForgottenPasswordTextView = (TextView) findViewById(R.id.forgot_passoword_textView);
        mLoginProgressbar.setVisibility(View.INVISIBLE);

        // showing and hiding password btn
        mVisibleImBtn = (ImageButton) findViewById(R.id.login_visible_imageButton);
        mInvisibleImBtn = (ImageButton) findViewById(R.id.login_invisible_imageButton);
        mInvisibleImBtn.setVisibility(View.INVISIBLE);

        // when the login btn is clicked, go back to Main Page if login in is successful
        mLoginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // set some button visibility
                mLoginProgressbar.setVisibility(View.VISIBLE);
                mLoginButton.setVisibility(View.INVISIBLE);
                mSignUpTextView.setVisibility(View.INVISIBLE);
                mGuestTextView.setVisibility(View.INVISIBLE);
                mNoaccountTextView.setVisibility(View.INVISIBLE);

                // get user's email and password
                String user_input_email = mEmailText.getText().toString().trim();
                String user_input_password = mPasswordText.getText().toString();

                // when user input something
                if (!TextUtils.isEmpty(user_input_email) && !TextUtils.isEmpty(user_input_password)) {

                    mAuth.signInWithEmailAndPassword(user_input_email, user_input_password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {

                            if (task.isSuccessful()) {
                                // login success - send the user to the main page
                                goToMainActivityHelper();
                            } else {
                                // login fails
                                String loginError = task.getException().getMessage();
                                //Toast.makeText(LoginActivity.this, "Error: " + loginError, Toast.LENGTH_LONG).show();

                                Snackbar sn = Snackbar.make(findViewById(android.R.id.content),  "Error: " + loginError, Snackbar.LENGTH_LONG);
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
                            // set some button visibility
                            mLoginProgressbar.setVisibility(View.INVISIBLE);
                            mLoginButton.setVisibility(View.VISIBLE);
                            mSignUpTextView.setVisibility(View.VISIBLE);
                            mGuestTextView.setVisibility(View.VISIBLE);
                            mNoaccountTextView.setVisibility(View.VISIBLE);

                        }
                    });
                } else {
                    Snackbar sn = Snackbar.make(findViewById(android.R.id.content),  "Please enter an Email or Password", Snackbar.LENGTH_LONG);
                    View view = sn.getView();
                    TextView tv = (TextView) view.findViewById(android.support.design.R.id.snackbar_text);
                    tv.setTextColor(Color.parseColor("#FFD700"));
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
                        tv.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
                    } else {
                        tv.setGravity(Gravity.CENTER_HORIZONTAL);
                    }
                    sn.show();
                    mLoginProgressbar.setVisibility(View.INVISIBLE);
                    mLoginButton.setVisibility(View.VISIBLE);
                    mSignUpTextView.setVisibility(View.VISIBLE);
                    mGuestTextView.setVisibility(View.VISIBLE);
                    mNoaccountTextView.setVisibility(View.VISIBLE);
                }

            }
        });

        // showing password when the visible button is pressed
        mVisibleImBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mPasswordText.setTransformationMethod(HideReturnsTransformationMethod.getInstance());

                mVisibleImBtn.setVisibility(View.INVISIBLE);
                mInvisibleImBtn.setVisibility(View.VISIBLE);
            }
        });

        // hiding password when the invisible button is pressed
        mInvisibleImBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mPasswordText.setTransformationMethod(PasswordTransformationMethod.getInstance());

                mVisibleImBtn.setVisibility(View.VISIBLE);
                mInvisibleImBtn.setVisibility(View.INVISIBLE);
            }
        });

        // when the sign up btn is clicked, go to signed up btn
        mSignUpTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), RegistrationActivity.class));
            }
        });

        // when the "continue as a guest" is clicked, go back to timeline
        mGuestTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goToMainActivityHelper();
            }
        });

        // when the forgot password is clicked, go to reset page
        mForgottenPasswordTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), ForgottenPasswordActivity.class));
            }
        });

        mPasswordText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if ((actionId == EditorInfo.IME_NULL && event.getAction() == KeyEvent.ACTION_DOWN)|| (actionId == EditorInfo.IME_ACTION_DONE)){
                    View view = getCurrentFocus();
                    if (view != null) {
                        InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
                        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
                    }
                    mLoginButton.performClick();
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

        // get current user from Firebase
        FirebaseUser user = mAuth.getCurrentUser();

        if (user != null) {
            // if the user is signed in
            finish();
        }
    }


    /**
     * Override system method onBackPress() which defines the "back" button behaviour
     * in the Action Bar of Android
     */
    @Override
    public void onBackPressed(){
        super.onBackPressed();
        Animatoo.animateSlideUp(this); //fire the slide up animation
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
     * Helper method which uses Intent to go to the user profile page
     */
    private void goToUserProfileActivityHelper() {
        Intent userPageIntent = new Intent(getApplicationContext(), UserProfileActivity.class);
        startActivity(userPageIntent);
        finish();
    }

}
