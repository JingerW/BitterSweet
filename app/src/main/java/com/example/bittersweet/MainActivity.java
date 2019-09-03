package com.example.bittersweet;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.print.PageRange;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.example.bittersweet.Model.BloodGlucose;
import com.example.bittersweet.Model.User;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.listener.OnChartGestureListener;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.example.bittersweet.Model.User;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import lecho.lib.hellocharts.model.Axis;
import lecho.lib.hellocharts.model.AxisValue;
import lecho.lib.hellocharts.model.Line;
import lecho.lib.hellocharts.model.LineChartData;
import lecho.lib.hellocharts.model.PointValue;
import lecho.lib.hellocharts.model.Viewport;
import lecho.lib.hellocharts.view.LineChartView;


public class MainActivity extends DrawerActivity {

    private static final String TAG = "MainActivityDebug";
    private static final String COLLECTION_NAME = "User";
    private static final String SUB_COLLECTION_NAME = "BloodGlucoseRecord";

    private Button addUserinfo;
    private Button deleteUserinfo;
    private Button updateUserinfo;

    private Button addRecord;
    private Button getRecord;

    private LineChartView helloChart;
    private ArrayList<BloodGlucose> records;

    private FirebaseAuth firebaseAuth;
    private FirebaseUser currentUser;
    private FirebaseFirestore db;
    private String uid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        LayoutInflater inflater = (LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View contentView = inflater.inflate(R.layout.activity_main, null, false);
        drawerLayout.addView(contentView, 0);

        setFireStore();

        setAddRecord();

        setGetRecord();

        setHelloLineChart();

    }

    private void setFireStore() {
        firebaseAuth = FirebaseAuth.getInstance();
        currentUser = firebaseAuth.getCurrentUser();
        db = FirebaseFirestore.getInstance();
        uid = currentUser.getUid();
    }

    private void setHelloLineChart() {
        helloChart = (LineChartView) findViewById(R.id.hello_chart);

        int[] yAxisData = {50, 20, 15, 30, 20, 60, 15, 40, 45, 10, 90, 18};

        List<AxisValue> xaxisLabel = new ArrayList<>();
        int labelCount = 24;
        int totalunits = 24*60;
        int interval = 60;
        for (int i=0;i<labelCount;i++) {
            int n = (i*interval)/interval;
            int r = (i*interval)%interval;
            xaxisLabel.add(new AxisValue(i).setLabel(String.format("%02d:%02d",n,r)));
        }

        List xValues = new ArrayList();
        List yValues = new ArrayList();
        Line line = new Line(yValues);
        line.setColor(Color.GRAY);

        for (int i = 0; i < yAxisData.length; i++){
            yValues.add(new PointValue(i, yAxisData[i]));
        }

        List lines = new ArrayList();
        lines.add(line);

        LineChartData data = new LineChartData();
        data.setLines(lines);

        // set x axis
        Axis xaxis = new Axis();
        xaxis.setValues(xaxisLabel);
        xaxis.setTextSize(16);
        data.setAxisXBottom(xaxis);

        // set y axis
        Axis yaxis = new Axis();
        data.setAxisYLeft(yaxis);

        Viewport v = new Viewport(helloChart.getMaximumViewport());
        v.bottom = 0;
        v.top = 100;
        v.left = 0;
        v.right = labelCount;
        helloChart.setMaximumViewport(v);
        helloChart.setCurrentViewport(v);
        helloChart.setLineChartData(data);

    }

    private void setAddRecord() {
        addRecord = (Button) findViewById(R.id.add_record);
        addRecord.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MainActivity.this, AddRecordActivity.class));
            }
        });
    }

    private void setGetRecord() {
        getRecord = (Button) findViewById(R.id.get_record);
        getRecord.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                db.collection(COLLECTION_NAME).document(uid).collection(SUB_COLLECTION_NAME)
                        .get()
                        .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                if (task.isSuccessful()){
                                    records = new ArrayList<BloodGlucose>();
                                    for (QueryDocumentSnapshot document : task.getResult()) {
                                        Log.d(TAG, document.getId() + " => " + document.getData());
                                        records.add(document.toObject(BloodGlucose.class));
                                    }
                                } else {
                                    Log.d(TAG, task.getException().getMessage());
                                    Toast.makeText(MainActivity.this, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
            }
        });


    }


//    private void addUserinfo() {
//        addUserinfo = (Button) findViewById(R.id.add_userinfo);
//        addUserinfo.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                String uid = currentUser.getUid();
//                Log.d(TAG, uid);
//
//                User userinfo = new User();
//                Log.d(TAG, userinfo.showUser());
//
//                db.collection(COLLECTION_NAME).document(uid)
//                        .set(userinfo)
//                        .addOnCompleteListener(new OnCompleteListener<Void>() {
//                            @Override
//                            public void onComplete(@NonNull Task<Void> task) {
//                                if (task.isSuccessful()) {
//                                    Log.d(TAG,"\nadded");
//                                }
//                                else {
//                                    Log.d(TAG, task.getException().getMessage());
//                                }
//                            }
//                        });
//            }
//        });
//
//    }
//
//    private void deleteUserinfo() {
//        deleteUserinfo = (Button) findViewById(R.id.delete_userinfo);
//        deleteUserinfo.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                String uid = currentUser.getUid();
//                Log.d(TAG, uid);
//
//                db.collection(COLLECTION_NAME).document(uid)
//                        .delete()
//                        .addOnCompleteListener(new OnCompleteListener<Void>() {
//                            @Override
//                            public void onComplete(@NonNull Task<Void> task) {
//                                if (task.isSuccessful()) {
//                                    Log.d(TAG,"\ndeleted");
//                                }
//                                else {
//                                    Log.d(TAG, task.getException().getMessage());
//                                }
//                            }
//                        });
//            }
//        });
//    }
//
//    private void updateUserinfo(final Map<String, Object> updateinfo) {
//        updateUserinfo = (Button) findViewById(R.id.update_userinfo);
//        updateUserinfo.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                String uid = currentUser.getUid();
//                Log.d(TAG, uid);
//
//                db.collection(COLLECTION_NAME).document(uid)
//                        .update(updateinfo)
//                        .addOnCompleteListener(new OnCompleteListener<Void>() {
//                            @Override
//                            public void onComplete(@NonNull Task<Void> task) {
//                                if (task.isSuccessful()) {
//                                    Log.d(TAG,"\nupdated");
//                                }
//                                else {
//                                    Log.d(TAG, task.getException().getMessage());
//                                }
//                            }
//                        });
//            }
//        });
//    }

}