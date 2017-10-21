package com.example.azamat.heartbeatratemanager;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseListAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class MeasuresDateList extends AppCompatActivity {

//    Firebase
    private DatabaseReference myRef;
    FirebaseAuth mAuth;
    FirebaseUser user = mAuth.getInstance().getCurrentUser();
    FirebaseListAdapter mAdapter;

    ListView dateList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_measures_date_list);
        dateList = (ListView)findViewById(R.id.date_list);

        myRef = FirebaseDatabase.getInstance().getReference();

        mAdapter = new FirebaseListAdapter <String>(this, String.class, android.R.layout.simple_list_item_1, myRef.child(user.getUid()).child("Measure Dates")) {
            @Override
            protected void populateView(View v, String date, int position) {
                TextView textView =  (TextView)v.findViewById(android.R.id.text1);
                date = date.replace("Q", "/");
                textView.setText(date);
            }
        };

        dateList.setAdapter(mAdapter);

        dateList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                TextView textView = (TextView)view;
                String date = textView.getText().toString();
                date = date.replace("/", "Q");
                Intent intent = new Intent(MeasuresDateList.this, SerialMonitorActivity.class);
                intent.putExtra("date", date);
                startActivity(intent);
            }
        });
    }
}
