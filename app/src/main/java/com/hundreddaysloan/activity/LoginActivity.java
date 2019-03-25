package com.hundreddaysloan.activity;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.github.ybq.android.spinkit.SpinKitView;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.hundreddaysloan.R;

/**
 * A login screen that offers login via email/password.
 */
public class LoginActivity extends AppCompatActivity implements GoogleApiClient.OnConnectionFailedListener {
    int exitcount = 0;
    FirebaseAuth firebaseAuth;
    GoogleApiClient mGoogleApiClient;
    FirebaseUser currentUser;
    Dialog alrtDialog;
    // UI references.
    private EditText mEmailView;
    private EditText mPasswordView;
    private SpinKitView mProgressView;
    GoogleSignInOptions gso;

    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        if (firebaseAuth == null) {
            firebaseAuth = FirebaseAuth.getInstance();
        }
        currentUser = firebaseAuth.getCurrentUser();
        updateUI(currentUser);
    }

    private void updateUI(FirebaseUser currentUser) {
        if (currentUser != null) {
            invokeHomeActivity();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        ScrollView login_scrollvw = findViewById(R.id.login_scrollvw);
        login_scrollvw.setVerticalScrollBarEnabled(false);
        login_scrollvw.setHorizontalScrollBarEnabled(false);
        firebaseAuth = FirebaseAuth.getInstance();

        gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(LoginActivity.this, this)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();

        // Set up the login form.
        mEmailView = (EditText) findViewById(R.id.email);
        mPasswordView = (EditText) findViewById(R.id.password);
        mPasswordView.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int id, KeyEvent keyEvent) {
                if (id == EditorInfo.IME_ACTION_DONE || id == EditorInfo.IME_NULL) {

                    hideSoftKeyboard();
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            attemptLogin();
                        }
                    }, 100);
                    return true;
                }
                return false;
            }
        });

        Button mEmailSignInButton = (Button) findViewById(R.id.email_sign_in_button);
        mEmailSignInButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                hideSoftKeyboard();
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        attemptLogin();
                    }
                }, 100);
            }
        });
        TextView signup_txtview = findViewById(R.id.signup_txtview);
        signup_txtview.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                gotoSignup();
            }
        });

        mProgressView = findViewById(R.id.login_progress);
        if (firebaseAuth.getCurrentUser() != null) {
            Log.e("UserDetails", "" + firebaseAuth.getCurrentUser().getDisplayName());
            Log.e("UserDetails", "" + firebaseAuth.getCurrentUser().getEmail());
            Log.e("UserDetails", "" + firebaseAuth.getCurrentUser().getPhoneNumber());
            Log.e("UserDetails", "isEmailVerified+" + firebaseAuth.getCurrentUser().isEmailVerified());

            if (firebaseAuth.getCurrentUser().isEmailVerified()) {
                invokeHomeActivity();
            } else {
                Log.e("UserDetails", "User not verified");
            }
        }
    }

    /**
     * Callback received when a permissions request has been completed.
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
//        if (requestCode == REQUEST_READ_CONTACTS) {
//            if (grantResults.length == 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
//                populateAutoComplete();
//            }
//        }
    }


    /**
     * Attempts to sign in or register the account specified by the login form.
     * If there are form errors (invalid email, missing fields, etc.), the
     * errors are presented and no actual login attempt is made.
     */
    private void attemptLogin() {
        // Reset errors.
        mEmailView.setError(null);
        mPasswordView.setError(null);

        // Store values at the time of the login attempt.
        String email = mEmailView.getText().toString();
        String password = mPasswordView.getText().toString();

        boolean cancel = false;
        View focusView = null;

        // Check for a valid password, if the user entered one.
//        if (!TextUtils.isEmpty(password) && !isPasswordValid(password)) {
//            mPasswordView.setError(getString(R.string.error_invalid_password));
//            focusView = mPasswordView;
//            cancel = true;
//        }

        if (TextUtils.isEmpty(password)) {
            mPasswordView.setError(getString(R.string.error_field_required));
            focusView = mPasswordView;
            cancel = true;
        }

        // Check for a valid email address.
        if (TextUtils.isEmpty(email)) {
            mEmailView.setError(getString(R.string.error_field_required));
            focusView = mEmailView;
            cancel = true;
        } else if (!isEmailValid(email)) {
            mEmailView.setError(getString(R.string.error_invalid_email));
            focusView = mEmailView;
            cancel = true;
        }

        if (cancel) {
            // There was an error; don't attempt login and focus the first
            // form field with an error.
            focusView.requestFocus();
        } else {
            // Show a progress spinner, and kick off a background task to
            // perform the user login attempt.
            mProgressView.setVisibility(View.VISIBLE);
            signIn(email, password);
        }
    }


    private void gotoSignup() {
        Intent newIntent = new Intent(getApplicationContext(),
                SignUpActivity.class);
        newIntent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        newIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(newIntent);
        finish();
    }

    private boolean isEmailValid(String email) {
        //TODO: Replace this with your own logic
        return email.contains("@");
    }

    private boolean isPasswordValid(String password) {
        //TODO: Replace this with your own logic
        return password.length() > 4;
    }


    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }


    private void signIn(String mEmail, String mPassword) {
        mPasswordView.setText("");
        firebaseAuth.signInWithEmailAndPassword(mEmail, mPassword)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        mProgressView.setVisibility(View.GONE);
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.e("LoginRes", "signInWithEmail:success");
                            FirebaseUser user = firebaseAuth.getCurrentUser();
                            if (user != null) {
                                if (firebaseAuth.getCurrentUser().isEmailVerified()) {
                                    updateUI(user);
                                } else {
                                    alertMessage("Your mail id not verified." +
                                            "\n Do you want us to send email to your registered mail for verification?", user);
                                }
                            } else {
                                Toast.makeText(LoginActivity.this, "User not found.",
                                        Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.e("LoginRes", "signInWithEmail:failure", task.getException());
                            Log.e("\n\n getCanonicalName", "" + task.getException().getClass().getCanonicalName());
                            if (task.getException().getClass().getCanonicalName().contains("InvalidUserException")) {
                                Log.e("\n\n message", "true");
                                Toast.makeText(LoginActivity.this, "Your mail id not registered yet.",
                                        Toast.LENGTH_SHORT).show();
                            } else {
                                Log.e("\n\n message", "false");
                                Toast.makeText(LoginActivity.this, "Authentication failed.",
                                        Toast.LENGTH_SHORT).show();
                            }
                            updateUI(null);
                        }
                    }
                });
    }

    private void invokeHomeActivity() {
        Intent newIntent = new Intent(getApplicationContext(),
                HomeActivity.class);
        newIntent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        newIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(newIntent);
    }

    private void alertMessage(String msg, final FirebaseUser user) {

        alrtDialog = new Dialog(LoginActivity.this);
        alrtDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        alrtDialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        alrtDialog.setContentView(R.layout.message_alert);
        alrtDialog.setCancelable(false);
        final TextView err_msg = alrtDialog.findViewById(R.id.err_msg);
        final Button btn_cancel = alrtDialog.findViewById(R.id.btn_cancel);
        final Button btn_ok = alrtDialog.findViewById(R.id.btn_ok);
        err_msg.setText(msg);

        btn_cancel.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                alrtDialog.dismiss();
            }
        });

        btn_ok.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (user != null) {
                    user.sendEmailVerification()
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {
                                        Log.e("Success", "Email sent.");
                                        Toast.makeText(LoginActivity.this, "Email sent to registered mail. Please verify and login.",
                                                Toast.LENGTH_SHORT).show();
                                        err_msg.setText("Email sent to registered mail. Please verify and login.");
                                        btn_ok.setVisibility(View.GONE);
                                        btn_cancel.setText("OK");
                                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                                            btn_cancel.setBackground(ContextCompat.getDrawable(getApplicationContext(), R.drawable.rounded_btn));
                                        } else {
                                            btn_cancel.setBackgroundDrawable(ContextCompat.getDrawable(getApplicationContext(), R.drawable.rounded_btn));
                                        }
                                    }
                                }
                            });
                }
            }

        });

        alrtDialog.show();

        alrtDialog.setOnKeyListener(new Dialog.OnKeyListener() {

            @Override
            public boolean onKey(DialogInterface arg0, int keyCode,
                                 KeyEvent event) {
                if (keyCode == KeyEvent.KEYCODE_BACK) {
                    if (exitcount < 2) {
                        exitcount = exitcount + 1;
                        Toast.makeText(getApplicationContext(), "Press again to exit..", Toast.LENGTH_SHORT).show();
                    } else {
                        exitcount = 0;
                        alrtDialog.dismiss();
                        finish();
                    }
                }
                return true;
            }
        });
    }

    public void hideSoftKeyboard() {
        if (getCurrentFocus() != null) {
            InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
            if (inputMethodManager != null) {
                inputMethodManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
            }
        }
    }
}

