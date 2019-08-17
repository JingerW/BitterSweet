package com.example.bittersweet;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;


public class MainActivity extends DrawerActivity implements View.OnClickListener{

    private static final String TAG = "MainActivityDebug";
    private static final String COLLECTION_NAME = "User";

    private Button test;
    private Button test_delete;

    private FirebaseAuth firebaseAuth;
    private FirebaseUser currentUser;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        LayoutInflater inflater = (LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View contentView = inflater.inflate(R.layout.activity_main, null, false);
        drawerLayout.addView(contentView, 0);

        firebaseAuth = FirebaseAuth.getInstance();
        currentUser = firebaseAuth.getCurrentUser();
        db = FirebaseFirestore.getInstance();

        test = (Button) findViewById(R.id.main_test);
        test.setOnClickListener(this);

        test_delete = (Button) findViewById(R.id.main_delete_test);
        test_delete.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        int i = view.getId();
        if (i == R.id.main_test) {
            String uid = currentUser.getUid();
            Log.d(TAG, uid);

            User userinfo = new User();
            Log.d(TAG, userinfo.showUser());

            db.collection(COLLECTION_NAME).document(uid)
                    .set(userinfo)
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
        } else if (i == R.id.main_delete_test) {
            String uid = currentUser.getUid();
            Log.d(TAG, uid);

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
        }
    }


}