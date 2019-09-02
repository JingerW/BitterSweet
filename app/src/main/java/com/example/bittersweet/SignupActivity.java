package com.example.bittersweet;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Rect;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.bittersweet.Model.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

public class SignupActivity extends AppCompatActivity implements View.OnClickListener {

    private static final String TAG = "SignupActivityDebug";

    private static final String PREFS_NAME = "preferences";
    private static final String COLLECTION_NAME = "User";

    private String username, email, password, passConf;

    private TextView login_link;
    private Button signup_button;
    private EditText signup_username;
    private EditText signup_email;
    private EditText signup_password;
    private EditText signup_password_confirm;

    private ProgressDialog progressDialog;

    private FirebaseAuth firebaseAuth;
    private FirebaseUser currentUser;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        progressDialog = new ProgressDialog(this);
        firebaseAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        login_link = (TextView) findViewById(R.id.signup_login);
        signup_button = (Button) findViewById(R.id.signup_button);
        signup_username = (EditText) findViewById(R.id.signup_username);
        signup_email = (EditText) findViewById(R.id.signup_email);
        signup_password = (EditText) findViewById(R.id.signup_password);
        signup_password_confirm = (EditText) findViewById(R.id.signup_password_confirm);

        signup_button.setOnClickListener(this);

        setLoginLink();
    }

    @Override
    public void onClick(View view) {
        int i = view.getId();
        if (i == R.id.signup_button) {
            signupUser();
        }
    }

    private void signupUser() {
        email = signup_email.getText().toString();
        password = signup_password.getText().toString();
        passConf = signup_password_confirm.getText().toString();
        username = signup_username.getText().toString();

        String vali = validate(email, password, passConf);
        if (vali != "") {
            Toast.makeText(this, vali, Toast.LENGTH_SHORT).show();
            return;
        } else {
            // validation finished, show progress bar to the user
            progressDialog.setMessage("Signing up...");
            progressDialog.show();

            // firebaseAuth create user with email and password
            firebaseAuth.createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                // successfully create user
                                // transfer to home page

                                // save preferences for next use
                                savePreferences();
                                savePreferencesSignup();

                                // message alert
                                progressDialog.cancel();
                                Toast.makeText(SignupActivity.this, "Sign up successfully", Toast.LENGTH_SHORT).show();
                                Log.d(TAG, "Signup successful, proceed to save data into database");
                                currentUser = firebaseAuth.getCurrentUser();
                                // add user info into database
                                String uid = currentUser.getUid();
                                Log.d(TAG, uid);
                                User user = new User();
                                user.setUsername(username);
                                Log.d(TAG, user.showUser());
                                Log.d(TAG, "start saving");
                                db.collection(COLLECTION_NAME).document(uid)
                                        .set(user)
                                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                if (task.isSuccessful()) {
                                                    Log.d(TAG,"\nadded");
                                                }
                                                else {
                                                    Log.d(TAG, task.getException().getMessage());
                                                }
                                            }
                                        });
                                Log.d(TAG, "end saving");

                                startActivity(new Intent(SignupActivity.this, UserInfoActivity.class));
                            } else {
                                Toast.makeText(SignupActivity.this, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                                progressDialog.cancel();
                            }
                        }
                    });
        }
    }

    private String validate(String email, String password, String passConf) {

        String mes = "";

        // check inputs are not empty
        if (TextUtils.isEmpty(email)) {
            // email address is empty
            mes = "Please enter your email address";
            return mes;
        }

        if (TextUtils.isEmpty(password)) {
            // password is empty
            mes = "Please enter your password";
            return mes;
        } else {
            if (password.length() < 10) {
                // password length is less than 10
                mes = "Password length cannot be less than 10";
                return mes;
            }
            if (TextUtils.isEmpty(passConf)) {
                mes = "Please confirm your password";
                return mes;
            } else {
                if (!passConf.equals(password)) {
                    mes = "Password confirmation failed. Please try again";
                    return mes;
                }
            }
        }
        return mes;
    }

    private void setLoginLink() {
        // text link back to login page
        String signin_text = getResources().getString(R.string.signup_login);
        SpannableString ss = new SpannableString(signin_text);
        ClickableSpan clickableSpan = new ClickableSpan() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(SignupActivity.this, LoginActivity.class));
            }
        };
        ss.setSpan(clickableSpan, 25, 35, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        login_link.setText(ss);
        login_link.setMovementMethod(LinkMovementMethod.getInstance());
    }

    private void savePreferences() {
        SharedPreferences prefs = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();

        // save and commit
        email = signup_email.getText().toString();
        password = signup_password.getText().toString();
        Log.d(TAG, email + ", " + password);
        editor.putString("email", email);
        editor.putString("password", password);
        editor.commit();
    }

    private void savePreferencesSignup() {
        SharedPreferences prefs = getSharedPreferences("signupPreference", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();

        // save and commit
        username = signup_username.getText().toString();
        email = signup_email.getText().toString();
        password = signup_password.getText().toString();
        Log.d(TAG, "save: "+username+", "+email+", "+password);
        editor.putString("username", username);
        editor.putString("email", email);
        editor.putString("password", password);
        editor.commit();
    }

    private void loadPreferencesSignup() {
        SharedPreferences prefs = getSharedPreferences("signupPreference", Context.MODE_PRIVATE);

        // load preferences
        username = prefs.getString("username", "");
        email = prefs.getString("email", "");
        password = prefs.getString("password", "");
        signup_username.setText(username);
        signup_email.setText(email);
        signup_password.setText(password);
        signup_password_confirm.setText(password);
        Log.d(TAG, "load: "+username+", "+email+", "+password);
    }

    @Override
    protected void onPause() {
        super.onPause();
        savePreferences();
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadPreferencesSignup();
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            View v = getCurrentFocus();
            if ( v instanceof EditText) {
                Rect outRect = new Rect();
                v.getGlobalVisibleRect(outRect);
                if (!outRect.contains((int)event.getRawX(), (int)event.getRawY())) {
                    v.clearFocus();
                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
                }
            }
        }
        return super.dispatchTouchEvent( event );
    }

}
