package com.example.bittersweet;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.List;

public class SignupActivity extends AppCompatActivity implements View.OnClickListener {

    private TextView login_link;
    private Button signup_button;
    private EditText signup_email;
    private EditText signup_password;
    private EditText signup_password_confirm;
    private ProgressDialog progressDialog;
    private FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        progressDialog = new ProgressDialog(this);
        firebaseAuth = FirebaseAuth.getInstance();

        login_link = findViewById(R.id.signup_login);
        signup_button = findViewById(R.id.signup_button);
        signup_email = findViewById(R.id.signup_email);
        signup_password = findViewById(R.id.signup_password);
        signup_password_confirm = findViewById(R.id.signup_password_confirm);

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
        String email = signup_email.getText().toString();
        String password = signup_password.getText().toString();
        String passConf = signup_password_confirm.getText().toString();

        String vali = validate(email,password,passConf);
        if (vali != "") {
            Toast.makeText(this,vali,Toast.LENGTH_SHORT).show();
            return;
        }
        else {
            // validation finished, show progress bar to the user
            progressDialog.setMessage("Signing up...");
            progressDialog.show();

            // firebaseAuth create user with email and password
            firebaseAuth.createUserWithEmailAndPassword(email,password)
                    .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                // successfully create user
                                // transfer to home page
                                progressDialog.cancel();
                                Toast.makeText(SignupActivity.this,"Sign up successfully", Toast.LENGTH_SHORT).show();
                                FirebaseUser currentUser = firebaseAuth.getCurrentUser();
                                startActivity(new Intent(SignupActivity.this, MainActivity.class));
                            } else {
                                Toast.makeText(SignupActivity.this,"Sign up failed, please try again", Toast.LENGTH_SHORT).show();
                                progressDialog.cancel();
                            }
                        }
                    });
        }
    }

    private String  validate(String email, String password, String passConf) {

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
        }
        else {
            if (password.length()<10) {
                // password length is less than 10
                mes = "Password length cannot be less than 10";
                return mes;
            }
            if (TextUtils.isEmpty(passConf)) {
                mes = "Please confirm your password";
                return mes;
            }
            else {
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
        ss.setSpan(clickableSpan,25,35, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE );
        login_link.setText(ss);
        login_link.setMovementMethod(LinkMovementMethod.getInstance());
    }
}
