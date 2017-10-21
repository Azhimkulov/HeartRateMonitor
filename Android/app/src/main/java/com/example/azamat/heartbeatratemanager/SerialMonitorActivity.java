package com.example.azamat.heartbeatratemanager;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseListAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;

public class SerialMonitorActivity extends AppCompatActivity {
    //    Firebase
    private DatabaseReference myRef;
    FirebaseAuth mAuth;
    FirebaseUser user = mAuth.getInstance().getCurrentUser();
    FirebaseListAdapter mAdapter;

//    GraphView
    GraphView graph;
    LineGraphSeries<DataPoint> series;
    private double lastXPoint = 0;


    ListView heartHistory;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_serial_monitor);
        heartHistory = (ListView)findViewById(R.id.heart_history_list);
        graph = (GraphView)findViewById(R.id.pulse_history_graph);

        series = new LineGraphSeries<>(new DataPoint[]{});
        graph.addSeries(series);

        graph.getViewport().setScalable(true);

        String date = getIntent().getStringExtra("date");

        myRef = FirebaseDatabase.getInstance().getReference();

        mAdapter = new FirebaseListAdapter <Integer>(this, Integer.class, android.R.layout.simple_list_item_1, myRef.child(user.getUid()).child("HeartRate").child(date)) {
            @Override
            protected void populateView(View v, Integer pulseRate, int position) {
                TextView textView =  (TextView)v.findViewById(android.R.id.text1);
                textView.setText(String.valueOf(pulseRate));
                series.appendData(new DataPoint(lastXPoint, pulseRate), false, 1023);
                lastXPoint++;
            }
        };

        heartHistory.setAdapter(mAdapter);


    }
}
