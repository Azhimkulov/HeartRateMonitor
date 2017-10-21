package com.example.azamat.heartbeatratemanager;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.GenericTypeIndicator;
import com.google.firebase.database.ValueEventListener;

import java.util.List;

public class HistoryActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private DatabaseReference myRef;
    List<String> SerialMonitor;

    ListView ListHeartRateResult;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);

        ListHeartRateResult = (ListView)findViewById(R.id.lv_heart_rate_history);

        myRef = FirebaseDatabase.getInstance().getReference();

        FirebaseUser user = mAuth.getInstance().getCurrentUser();

        myRef.child(user.getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                try {
                    GenericTypeIndicator<List<String>> t = new GenericTypeIndicator<List<String>>(){};
                    SerialMonitor = dataSnapshot.child("HeartRate").getValue(t);

                    updateUI();
                }
                catch (Exception ex)
                {
                    startActivity(new Intent(HistoryActivity.this, PatientRoomActivity.class));
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(HistoryActivity.this, "Не прошла успешно", Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void updateUI()
    {
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getBaseContext(), android.R.layout.simple_list_item_1, SerialMonitor);
        ListHeartRateResult.setAdapter(adapter);
    }
}
