package com.example.bittersweet;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.core.Tag;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener {

    private static final String PREFS_NAME = "preferences";
    private static final String PREFS_EMAIL = "email";
    private static final String PREFS_PASS = "password";

    private final String DefaultEmail = "";
    private String email;
    private final String DefaultPass = "";
    private String password;

    private TextView signup_link;
    private FirebaseAuth firebaseAuth;
    private FirebaseUser currentUser;
    private EditText login_email, login_password;
    private Button login_button;
    private ProgressDialog progressDialog;
    private FirebaseApp ref;
    private CheckBox remember;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        progressDialog = new ProgressDialog(this);
        firebaseAuth = FirebaseAuth.getInstance();

        signup_link = (TextView) findViewById(R.id.login_signup);
        login_email = (EditText) findViewById(R.id.login_email);
        login_password = (EditText) findViewById(R.id.login_password);
        login_button = (Button) findViewById(R.id.login_button);

        login_button.setOnClickListener(this);

        setSignupLink();
    }

    @Override
    protected void onStart() {
        super.onStart();
        currentUser = firebaseAuth.getCurrentUser();
        if (currentUser != null) {
            startActivity(new Intent(LoginActivity.this, MainActivity.class));
            this.finish();
        }
    }

    @Override
    public void onClick(View view) {
        int i = view.getId();
        if (i == R.id.login_button) {
            loginUser();
        }
    }

    private void loginUser() {
        email = login_email.getText().toString();
        password = login_password.getText().toString();

        String vali = validate(email, password);
        if (vali != "") {
            Toast.makeText(this, vali, Toast.LENGTH_SHORT).show();
            return;
        } else {
            // validation finished, show progress bar
            progressDialog.setMessage("Logging in...");
            progressDialog.show();

            // firebaseAuth signin user with email and password
            firebaseAuth.signInWithEmailAndPassword(email, password)
                    .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                Log.d("message: ", "Login successfully");
                                progressDialog.cancel();
                                currentUser = firebaseAuth.getCurrentUser();
                                startActivity(new Intent(LoginActivity.this, MainActivity.class));
                            } else {
                                Log.w("Erro message: ", "Login failed", task.getException());
                                progressDialog.cancel();
                                Toast.makeText(LoginActivity.this, "Login failed. Please check your email or password is correct", Toast.LENGTH_SHORT).show();
                                return;
                            }
                        }
                    });
        }
    }

    private String validate(String email, String password) {

        String mes = "";

        // check inputs are not empty
        if (TextUtils.isEmpty(email)) {
            // email address is empty
            mes = "Please enter your email address";
            return mes;
        }

        if (TextUtils.isEmpty(password)) {
            //password is empty
            mes = "Please enter your password";
            return mes;
        }

        return mes;
    }

    private void setSignupLink() {
        // text link back to login page
        String signin_text = getResources().getString(R.string.login_signup);
        SpannableString ss = new SpannableString(signin_text);
        ClickableSpan clickableSpan = new ClickableSpan() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(LoginActivity.this, SignupActivity.class));
            }
        };
        ss.setSpan(clickableSpan, 24, 36, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        signup_link.setText(ss);
        signup_link.setMovementMethod(LinkMovementMethod.getInstance());
    }

    private void savePreferences() {
        SharedPreferences prefs = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();

        // save and commit
        email = login_email.getText().toString();
        password = login_password.getText().toString();
        Log.d("saved preferences", email+", "+password);
        editor.putString("email", email);
        editor.putString("password", password);
        editor.commit();
    }

    private void loadPreferences() {
        SharedPreferences prefs = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);

        // load preferences
        email = prefs.getString(PREFS_EMAIL, DefaultEmail);
        password = prefs.getString(PREFS_PASS, DefaultPass);
        login_email.setText(email);
        login_password.setText(password);
        Log.d("loaded preferences", email+","+password);
    }

    private void removePreferences() {
        SharedPreferences prefs = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();

        // remove saved preference
        editor.remove(PREFS_EMAIL);
        editor.remove(PREFS_PASS);
        editor.commit();
        Log.d("removed preferences", "Removed");
    }

    @Override
    protected void onPause() {
        super.onPause();
        savePreferences();
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadPreferences();
    }
}
