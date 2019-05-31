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

/**
 * Class RegistrationActivity is the "Registration" page in the App.
 */
public class RegistrationActivity extends AppCompatActivity {

    // all stuff in the view
    private EditText mUserNameEditText;
    private EditText mPasswordEditText;
    private EditText mConfirmPassEditText;
    private Button mSignUPBtn;
    private ProgressBar mProgressBar;
    private TextView mHaveAccountAlready;
    private TextView mLoginTextView;
    private ImageButton mVisibleImBtn;
    private ImageButton mInvisibleImBtn;

    // Firebase stuff
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);

        // Firebase stuff
        mAuth = FirebaseAuth.getInstance();

        // all other stuff in the view
        mUserNameEditText = (EditText) findViewById(R.id.register_username_editText);
        mPasswordEditText = (EditText) findViewById(R.id.register_password_editText);
        mConfirmPassEditText = (EditText) findViewById(R.id.register_password_confirm_editText);
        mSignUPBtn = (Button) findViewById(R.id.register_signup_button);
        mLoginTextView = (TextView) findViewById(R.id.register_login_textView);
        mHaveAccountAlready = (TextView) findViewById(R.id.already_have_account_textView);
        mProgressBar = (ProgressBar) findViewById(R.id.progressBar);
        mProgressBar.setVisibility(View.INVISIBLE);

        mVisibleImBtn = (ImageButton) findViewById(R.id.login_visible_imageButton);
        mInvisibleImBtn = (ImageButton) findViewById(R.id.login_invisible_imageButton);
        mInvisibleImBtn.setVisibility(View.INVISIBLE);

        // when the sign up btn is clicked, sign it up
        mSignUPBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // set some button visibility
                mProgressBar.setVisibility(View.VISIBLE);
                mSignUPBtn.setVisibility(View.INVISIBLE);
                mHaveAccountAlready.setVisibility(View.INVISIBLE);
                mLoginTextView.setVisibility(View.INVISIBLE);

                // get user's email and password
                String user_input_email = mUserNameEditText.getText().toString().trim();
                String user_input_password = mPasswordEditText.getText().toString();
                String user_input_confirm_password = mConfirmPassEditText.getText().toString();

                if (!TextUtils.isEmpty(user_input_email) && !TextUtils.isEmpty(user_input_password) && !TextUtils.isEmpty(user_input_confirm_password) && user_input_email.toLowerCase().endsWith("@ucsd.edu")) {
                    if (user_input_password.equals(user_input_confirm_password)) {
                        mAuth.createUserWithEmailAndPassword(user_input_email, user_input_password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if (task.isSuccessful()) {
                                    //goToMainActivityHelper();
                                    goToSetUpAccountActivityHelper();
                                } else {
                                    String errormsg = task.getException().getMessage();
                                    //Toast.makeText(RegistrationActivity.this, "Error: " + errormsg, Toast.LENGTH_LONG).show();
                                    Snackbar sn = Snackbar.make(findViewById(android.R.id.content),  "Error: " + errormsg, Snackbar.LENGTH_LONG);
                                    View view = sn.getView();
                                    TextView tv = (TextView) view.findViewById(android.support.design.R.id.snackbar_text);
                                    tv.setTextColor(Color.parseColor("#FFD700"));
                                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
                                        tv.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
                                    } else {
                                        tv.setGravity(Gravity.CENTER_HORIZONTAL);
                                    }
                                    sn.show();
                                    // set some button visibility
                                    mProgressBar.setVisibility(View.INVISIBLE);
                                    mSignUPBtn.setVisibility(View.VISIBLE);
                                    mHaveAccountAlready.setVisibility(View.VISIBLE);
                                    mLoginTextView.setVisibility(View.VISIBLE);
                                }
                            }
                        });

                    } else {

                        if (!TextUtils.isEmpty(user_input_email) && !user_input_email.toLowerCase().endsWith("@ucsd.edu")) {
                            //Toast.makeText(RegistrationActivity.this, "Please make sure you use @ucsd.edu email", Toast.LENGTH_LONG).show();
                            Snackbar sn = Snackbar.make(findViewById(android.R.id.content),  "Please make sure you use @ucsd.edu email", Snackbar.LENGTH_LONG);
                            View view = sn.getView();
                            TextView tv = (TextView) view.findViewById(android.support.design.R.id.snackbar_text);
                            tv.setTextColor(Color.parseColor("#FFD700"));
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
                                tv.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
                            } else {
                                tv.setGravity(Gravity.CENTER_HORIZONTAL);
                            }
                            sn.show();
                        } else {
                            //Toast.makeText(RegistrationActivity.this, "Password and Confirm Password don't match", Toast.LENGTH_LONG).show();
                            Snackbar sn = Snackbar.make(findViewById(android.R.id.content),  "Password and Confirm Password don't match", Snackbar.LENGTH_LONG);
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
                        mProgressBar.setVisibility(View.INVISIBLE);
                        mSignUPBtn.setVisibility(View.VISIBLE);
                        mHaveAccountAlready.setVisibility(View.VISIBLE);
                        mLoginTextView.setVisibility(View.VISIBLE);
                    }
                } else {

                    if (!TextUtils.isEmpty(user_input_email)&& !user_input_email.endsWith("@ucsd.edu")) {
                        //Toast.makeText(RegistrationActivity.this, "Please make sure you use @ucsd.edu email", Toast.LENGTH_LONG).show();
                        Snackbar sn = Snackbar.make(findViewById(android.R.id.content),  "Please make sure you use @ucsd.edu email", Snackbar.LENGTH_LONG);
                        View view = sn.getView();
                        TextView tv = (TextView) view.findViewById(android.support.design.R.id.snackbar_text);
                        tv.setTextColor(Color.parseColor("#FFD700"));
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
                            tv.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
                        } else {
                            tv.setGravity(Gravity.CENTER_HORIZONTAL);
                        }
                        sn.show();
                    } else {
                        //Toast.makeText(RegistrationActivity.this, "Please fill all fields", Toast.LENGTH_LONG).show();
                        Snackbar sn = Snackbar.make(findViewById(android.R.id.content),  "Please fill all fields", Snackbar.LENGTH_LONG);
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
                    mProgressBar.setVisibility(View.INVISIBLE);
                    mSignUPBtn.setVisibility(View.VISIBLE);
                    mHaveAccountAlready.setVisibility(View.VISIBLE);
                    mLoginTextView.setVisibility(View.VISIBLE);
                }
            }
        });

        // when the login text is clicked, go back to login in
        mLoginTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goToLoginInActivityHelper();
            }
        });

        // showing password when the visible button is pressed
        mVisibleImBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mPasswordEditText.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                mConfirmPassEditText.setTransformationMethod(HideReturnsTransformationMethod.getInstance());

                mVisibleImBtn.setVisibility(View.INVISIBLE);
                mInvisibleImBtn.setVisibility(View.VISIBLE);
            }
        });

        // hiding password when the invisible button is pressed
        mInvisibleImBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mPasswordEditText.setTransformationMethod(PasswordTransformationMethod.getInstance());
                mConfirmPassEditText.setTransformationMethod(PasswordTransformationMethod.getInstance());

                mVisibleImBtn.setVisibility(View.VISIBLE);
                mInvisibleImBtn.setVisibility(View.INVISIBLE);
            }
        });

        mConfirmPassEditText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if ((actionId == EditorInfo.IME_NULL && event.getAction() == KeyEvent.ACTION_DOWN)|| (actionId == EditorInfo.IME_ACTION_DONE)){
                    View view = getCurrentFocus();
                    if (view != null) {
                        InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
                        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
                    }
                    mSignUPBtn.performClick();
                }
                return true;
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

        FirebaseUser user = mAuth.getCurrentUser();
        if (user != null) {

        } else {

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

    /**
     * Helper method which use Intent to go to the Login Page
     */
    private void goToLoginInActivityHelper() {
        Intent goToLoginInActivity = new Intent(getApplicationContext(), LoginActivity.class);
        goToLoginInActivity.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(goToLoginInActivity);
        finish();
    }

    /**
     * Helper method which use Intent to go to the set up account page
     */
    private void goToSetUpAccountActivityHelper() {
        startActivity(new Intent(getApplicationContext(), SetUpAccountActivity.class));
        finish();
    }
}
