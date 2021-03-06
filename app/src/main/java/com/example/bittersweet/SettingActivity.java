package com.example.bittersweet;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.bittersweet.Model.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class SettingActivity extends DrawerActivity implements View.OnClickListener {

    private static final String TAG = "SettingActivityDebug";
    private static final String COLLECTION_NAME = "User";

    private FirebaseAuth firebaseAuth;
    private FirebaseUser currentUser;
    private FirebaseFirestore db;
    private String uid;

    private Button button_delete;
    private ProgressBar delete_progress;
    AlertDialog.Builder dialog;
    private ImageView settingAvatar;
    private TextView settingUsername;
    private TextView settingEmail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        LayoutInflater inflater = (LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View contentView = inflater.inflate(R.layout.activity_setting, null, false);
        drawerLayout.addView(contentView, 0);

        firebaseAuth = FirebaseAuth.getInstance();
        currentUser = firebaseAuth.getCurrentUser();
        db = FirebaseFirestore.getInstance();
        uid = currentUser.getUid();

        button_delete = (Button) findViewById(R.id.setting_delete);
        button_delete.setOnClickListener(this);
        delete_progress = (ProgressBar) findViewById(R.id.delet_progressbar);

        setUserInfo();
    }

    private void setUserInfo() {
        settingAvatar = (ImageView) findViewById(R.id.setting_avatar);
        settingUsername = (TextView) findViewById(R.id.setting_username);
        settingEmail = (TextView) findViewById(R.id.setting_email);

        db.collection(COLLECTION_NAME).document(uid)
                .get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()) {
                            DocumentSnapshot document = task.getResult();
                            User user = document.toObject(User.class);

                            if (user.getGender() == "boy") {
                                settingAvatar.setImageDrawable(getResources().getDrawable(R.drawable.boy_avatar));
                            } else {
                                settingAvatar.setImageDrawable(getResources().getDrawable(R.drawable.girl_avatar));
                            }

                            settingUsername.setText(user.getUsername());
                            settingEmail.setText(currentUser.getEmail());
                        } else {
                            Log.d(TAG, task.getException().getMessage());
                        }
                    }
                });
    }

    @Override
    public void onClick(View view) {
        int i = view.getId();
        if (i == R.id.setting_delete) {
            showAlert();
        }
    }

    private void showAlert() {
        dialog = new AlertDialog.Builder(SettingActivity.this);
        dialog.setTitle(R.string.delete_title);
        dialog.setMessage(R.string.delete_message);
        dialog.setPositiveButton("Delete", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                // show progress bar
                delete_progress.setVisibility(View.VISIBLE);

                deleteUser();
            }
        });
        dialog.setNegativeButton("Dismiss", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        });

        AlertDialog alert = dialog.create();
        alert.show();
    }

    private void deleteUser() {
        deleteUserData();
        currentUser.delete().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                // hide progress bar
                delete_progress.setVisibility(View.GONE);

                if (task.isSuccessful()) {
                    Toast.makeText(SettingActivity.this, "Account Deleted", Toast.LENGTH_SHORT).show();

                    Intent intent = new Intent(SettingActivity.this, LoginActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                } else {
                    Toast.makeText(SettingActivity.this, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void deleteUserData() {
        String uid = currentUser.getUid();
        Log.d(TAG, uid);
        Log.d(TAG,"start deleting");

        db.collection(COLLECTION_NAME).document(uid)
                .delete()
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Log.d(TAG,"\ndeleted");
                        }
                        else {
                            Log.d(TAG, task.getException().getMessage());
                        }
                    }
                });
        Log.d(TAG,"end deleting");
    }
}
