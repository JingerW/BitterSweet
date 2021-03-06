package com.example.bittersweet;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.bittersweet.Model.Record;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import lecho.lib.hellocharts.model.Axis;
import lecho.lib.hellocharts.model.AxisValue;
import lecho.lib.hellocharts.model.Line;
import lecho.lib.hellocharts.model.LineChartData;
import lecho.lib.hellocharts.model.PointValue;
import lecho.lib.hellocharts.model.Viewport;
import lecho.lib.hellocharts.view.LineChartView;

public class MainActivity extends DrawerActivity implements View.OnClickListener{

    private static final String TAG = "MainActivityDebug";
    private static final String COLLECTION_NAME = "User";
    private static final String SUB_COLLECTION_NAME = "BloodGlucoseRecord";

    private Button addRecord;

    private LineChartView helloChart;
    private ArrayList<Record> records;
    private ArrayList<Float> yAxisData;
    private ArrayList<Float> xAxisData;
    private List<AxisValue> xaxisLabel;
    private List<AxisValue> yaxisLabel;

    private FirebaseAuth firebaseAuth;
    private FirebaseUser currentUser;
    private FirebaseFirestore db;
    private String uid;
    private CollectionReference ref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        LayoutInflater inflater = (LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View contentView = inflater.inflate(R.layout.activity_main, null, false);
        drawerLayout.addView(contentView, 0);

        setFireStore();

        getRecords();

        setButtons();

    }

    private void setFireStore() {
        // set up fire store
        firebaseAuth = FirebaseAuth.getInstance();
        // get current user
        currentUser = firebaseAuth.getCurrentUser();
        // get this user's database
        db = FirebaseFirestore.getInstance();
        // get user id
        uid = currentUser.getUid();
        // get reference
        ref = db.collection(COLLECTION_NAME).document(uid).collection(SUB_COLLECTION_NAME);
    }

    private void getRecords() {

        String dateString = getCurrentDateString();
        // fetch data from fire store and then store into an array list
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);
        Date currentDate = cal.getTime();
        cal.setTime(currentDate);
        cal.add(Calendar.DATE, 1);
        Date nextDate = cal.getTime();
        Log.d(TAG, "start date : "+currentDate);
        Log.d(TAG, "end date : "+nextDate);
//        Query query = ref.orderBy("dateTime");
        Query query = ref.orderBy("dateTime")
                .whereGreaterThanOrEqualTo("dateTime", currentDate)
                .whereLessThan("dateTime", nextDate);
        query.addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot snapshots, @Nullable FirebaseFirestoreException error) {
                if (error != null) {Log.w(TAG, "Listen failed.", error);return;}

                if (snapshots != null && !snapshots.isEmpty()) {
                    Log.d(TAG, "record size: "+snapshots.size());
                    // create new blood glucose level record list
                    records = new ArrayList<Record>();
                    for (QueryDocumentSnapshot snapshot: snapshots) {
                        Record record = snapshot.toObject(Record.class);
                        records.add(record);
                        record.showBloodGlucose(TAG);
                    }

                    setHelloLineChart(records);
                } else {
                    Log.d(TAG, "record data is null");
                }

            }
        });
    }

    private void setHelloLineChart(ArrayList<Record> records) {
        Log.d(TAG, "record size: "+records.size());

        // find chart layer by id
        helloChart = (LineChartView) findViewById(R.id.main_display_chart);

        // x axis labels
        xaxisLabel = new ArrayList<>();
        int xlabelCount = 24;
        int xinterval = 60;
        for (int i=0;i<xlabelCount*xinterval;i++) {
            if (i%xinterval == 0) {
                int count = i/xinterval;
                int n = (count*xinterval)/xinterval;
                int r = (count*xinterval)%xinterval;
//                Log.d(TAG, "X LABEL: "+i+" with "+String.format("%02d:%02d",n,r));
                xaxisLabel.add(new AxisValue(i).setLabel(String.format("%02d:%02d",n,r)));
            }
        }

        // y axis labels
        yaxisLabel = new ArrayList<>();
        int ylabelCount = 10;
        int yinterval = 2;
        for (int i=0;i<ylabelCount*yinterval;i++) {
            if (i%yinterval == 0) {
                int count = i/yinterval;
                int n = count*yinterval;
                yaxisLabel.add(new AxisValue(i).setLabel(Integer.toString(n)));
            } else {
                yaxisLabel.add(new AxisValue(i).setLabel(""));
            }

        }

        // set data
        List xyValues = new ArrayList();

        // blood glucose records
        yAxisData = new ArrayList();
        xAxisData = new ArrayList();
        if (records.size()>0) {
            for (Record record : records) {
                double bg = record.getBloodGlucose();
                String date = record.getDate();
                String time = record.getTime();
                String[] t = time.split(":");
                float h = Float.parseFloat(t[0]);
                float m = Float.parseFloat(t[1]);
                float x = h * xinterval + m;
                Log.d(TAG, bg+" at "+date+" , "+time);
                xAxisData.add(x);
                yAxisData.add((float) bg);
                Log.d(TAG, "x axis: "+x+", y axis: "+bg);
            }

            // x and y axis data
            for (int i=0;i<records.size();i++) {
                xyValues.add(new PointValue(xAxisData.get(i), yAxisData.get(i)));
                Log.d(TAG, "X AND Y AXIS DATA: "+xAxisData.get(i)+", "+yAxisData.get(i));
            }

        } else {
            int[] emptyData = {0,0,0,0,0};
            for (int i=0;i<emptyData.length;i++) {
                xyValues.add(new PointValue(i, emptyData[i]));
            }
        }

        // set data into chart
        Line line = new Line(xyValues);
        line.setColor(Color.GRAY);

        List lines = new ArrayList();
        lines.add(line);

        LineChartData data = new LineChartData();
        data.setLines(lines);


        // configure x axis
        Axis xaxis = new Axis();
        xaxis.setValues(xaxisLabel);
        xaxis.setTextSize(12);
        xaxis.setTextColor(Color.DKGRAY);
        data.setAxisXBottom(xaxis);

        // configure y axis
        Axis yaxis = new Axis();
        yaxis.setValues(yaxisLabel);
        yaxis.setTextSize(12);
        yaxis.setTextColor(Color.DKGRAY);
        yaxis.setHasLines(true);
        yaxis.setLineColor(Color.GRAY);
        data.setAxisYLeft(yaxis);

        // configure view ports
        helloChart.setViewportCalculationEnabled(false);
        helloChart.setBackgroundColor(Color.WHITE);
        Viewport v = new Viewport(helloChart.getMaximumViewport());
        v.bottom = 0;
        v.top = ylabelCount*yinterval;
        v.left = 0;
        v.right = xlabelCount*xinterval;
        helloChart.setMaximumViewport(v);
        helloChart.setCurrentViewport(v);
        helloChart.setLineChartData(data);

    }

    private void setButtons() {

        addRecord = (Button) findViewById(R.id.add_record);
        addRecord.setOnClickListener(this);

    }

    public void onClick(View view) {
        switch (view.getId()){
            case R.id.add_record:
                startActivity(new Intent(MainActivity.this, AddRecordActivity.class));
        }
    }

    private String getCurrentDateString() {
        // get current day, month and year from Calendar
        final Calendar c = Calendar.getInstance();
        int year = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH);
        int day = c.get(Calendar.DAY_OF_MONTH);
        DateFormat format = new SimpleDateFormat("DD/MM/YYY");

        final String[] months = getResources().getStringArray(R.array.months);
        // Store them into an array list and return it
        String dateString = String.format(months[month] + " %02d, %d", day, year);
        Log.d(TAG, "Current date: "+dateString);

        return dateString;
    }

}