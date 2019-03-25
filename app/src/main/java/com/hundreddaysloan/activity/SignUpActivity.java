package com.hundreddaysloan.activity;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.github.ybq.android.spinkit.SpinKitView;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.hundreddaysloan.R;

public class SignUpActivity extends AppCompatActivity {
    int exitcount = 0;
    Dialog alrtDialog;
    private EditText email, password,mobileNo;
    FirebaseAuth firebaseAuth;
    DatabaseReference rootReference;
    SpinKitView signup_progress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
        ScrollView login_scrollvw = findViewById(R.id.login_scrollvw);
        login_scrollvw.setVerticalScrollBarEnabled(false);
        login_scrollvw.setHorizontalScrollBarEnabled(false);
        TextView signin_txtview = (TextView) findViewById(R.id.signin_txtview);
        signin_txtview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                gotoSignup();
            }
        });
        email = (EditText) findViewById(R.id.email);
        password = (EditText) findViewById(R.id.password);
        mobileNo = (EditText) findViewById(R.id.mobile_no);

        firebaseAuth = FirebaseAuth.getInstance();
        rootReference = FirebaseDatabase.getInstance().getReference();

        signup_progress = findViewById(R.id.signup_progress);

        Button mEmailSignInButton = (Button) findViewById(R.id.email_sign_in_button);
        mEmailSignInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                hideSoftKeyboard();
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        attemptLogin();
                    }
                },100);
            }
        });
    }

    private void gotoSignup() {
        Intent newIntent = new Intent(getApplicationContext(),
                LoginActivity.class);
        newIntent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        newIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(newIntent);
        finish();
    }


    private void attemptLogin() {

        String strEmail = email.getText().toString();
        String strPassword = password.getText().toString();
        String strMobile = mobileNo.getText().toString();

        if (TextUtils.isEmpty(strEmail)) {
            Toast.makeText(getApplicationContext(), "Please enter email", Toast.LENGTH_SHORT).show();
            return;
        } else if (!strEmail.contains("@")) {
            Toast.makeText(getApplicationContext(), "Please enter valid email", Toast.LENGTH_SHORT).show();
            return;
        }
        if (TextUtils.isEmpty(strPassword)) {
            Toast.makeText(getApplicationContext(), "Please enter password", Toast.LENGTH_SHORT).show();
            return;
        }

        if (strPassword.length() < 6) {
            Toast.makeText(getApplicationContext(), "Password must be at least 6 characters", Toast.LENGTH_SHORT).show();
            return;
        }

        signup_progress.setVisibility(View.VISIBLE);

        firebaseAuth.createUserWithEmailAndPassword(strEmail, strPassword)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful())
                        {
                            final FirebaseUser user = firebaseAuth.getCurrentUser();
                            LoginToDB myUserDetails = new LoginToDB(email.getText().toString(),password.getText().toString(),mobileNo.getText().toString());

                            rootReference.child(firebaseAuth.getUid()).setValue(myUserDetails)
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if(task.isSuccessful())
                                            {
                                                Toast.makeText(getApplicationContext(), "Registered", Toast.LENGTH_SHORT).show();
                                                finish();
                                                Intent intent = new Intent(getApplicationContext(),HomeActivity.class);
                                                startActivity(intent);
                                            }
                                        }
                                    });

                            if (user != null) {
                                signup_progress.setVisibility(View.GONE);
                                // Name, email address, and profile photo Url
                                String name = user.getDisplayName();
                                String email = user.getEmail();
                                Uri photoUrl = user.getPhotoUrl();

                                // Check if user's email is verified
                                boolean emailVerified = user.isEmailVerified();

                                // The user's ID, unique to the Firebase project. Do NOT use this value to
                                // authenticate with your backend server, if you have one. Use
                                // FirebaseUser.getIdToken() instead.
                                String uid = user.getUid();

                                if (!emailVerified) {

                                    user.sendEmailVerification()
                                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                @Override
                                                public void onComplete(@NonNull Task<Void> task) {
                                                    if (task.isSuccessful()) {
                                                        alertMessage("Email sent to registered mail. Please verify and login.");
                                                    } else {
                                                        alertMessage("Please try again later, error while signup");
                                                    }
                                                }
                                            });
                                }
                            }
                        } else {
                            Log.e("SignupRes", "signInWithEmail:failure", task.getException());
                            signup_progress.setVisibility(View.GONE);
                            Toast.makeText(getApplicationContext(), "Check you have entered valid E-mail", Toast.LENGTH_SHORT).show();
                            if (task.getException().getClass().getCanonicalName().contains("UserCollisionException")) {
                                Toast.makeText(SignUpActivity.this, "The email address is already registered.",
                                        Toast.LENGTH_LONG).show();
                            } else {
                                Toast.makeText(SignUpActivity.this, "Check you have entered valid E-mail. ",
                                        Toast.LENGTH_SHORT).show();
                                Toast.makeText(SignUpActivity.this, ""+task.getException(),
                                        Toast.LENGTH_LONG).show();
                            }
                        }
                    }
                });
    }

    private void alertMessage(final String msg) {

        alrtDialog = new Dialog(SignUpActivity.this);
        alrtDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        alrtDialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        alrtDialog.setContentView(R.layout.message_alert);
        alrtDialog.setCancelable(false);
        final TextView err_msg = alrtDialog.findViewById(R.id.err_msg);
        final Button btn_cancel = alrtDialog.findViewById(R.id.btn_cancel);
        btn_cancel.setVisibility(View.GONE);
        final Button btn_ok = alrtDialog.findViewById(R.id.btn_ok);
        err_msg.setText(msg);
        btn_cancel.setText("OK");
        btn_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alrtDialog.dismiss();
            }
        });

        btn_ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alrtDialog.dismiss();
                Intent newIntent = new Intent(getApplicationContext(),
                        LoginActivity.class);
                newIntent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                newIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(newIntent);
                finish();
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
        if(getCurrentFocus()!=null) {
            InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
            if (inputMethodManager != null) {
                inputMethodManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
            }
        }
    }
}
