package com.example.azamat.heartbeatratemanager;

import android.bluetooth.BluetoothAdapter;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.GenericTypeIndicator;
import com.google.firebase.database.ValueEventListener;

public class PatientRoomActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private DatabaseReference myRef;

    private TextView TV_surname, TV_name, TV_lastname;
    private Button Btn_bt_connect, Btn_serial_monitor, Btn_sign_out;

    boolean result;

    private static final int REQUEST_ENABLE_BT_FOR_GRAPH = 1;
    private static final int REQUEST_ENABLE_BT_FOR_SERIAL = 2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_patient_room);

        TV_surname = (TextView)findViewById(R.id.tv_surname_proom);
        TV_name = (TextView)findViewById(R.id.tv_name_proom);
        TV_lastname = (TextView)findViewById(R.id.tv_lastname_proom);

        Btn_bt_connect = (Button)findViewById(R.id.btn_bt_connect_proom);
        Btn_serial_monitor = (Button)findViewById(R.id.btn_serial_monitor_proom);
        Btn_sign_out = (Button)findViewById(R.id.btn_sign_out_proom);

        myRef = FirebaseDatabase.getInstance().getReference();

        FirebaseUser user = mAuth.getInstance().getCurrentUser();

        myRef.child(user.getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                GenericTypeIndicator<String> typeIndicator = new GenericTypeIndicator<String>() {};
                String name = dataSnapshot.child("PersonDate").child("name").getValue(typeIndicator);
                String surname = dataSnapshot.child("PersonDate").child("surname").getValue(typeIndicator);
                String lastname = dataSnapshot.child("PersonDate").child("lastname").getValue(typeIndicator);
                TV_name.setText("Vorname: "+name);
                TV_surname.setText("Nachname: "+surname);
                TV_lastname.setText("Vatersname: "+lastname);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        Btn_bt_connect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean check = CheckBluetooth(REQUEST_ENABLE_BT_FOR_GRAPH);
                if (check) {
                    startActivity(new Intent(PatientRoomActivity.this, GraphicActivity.class));
                }

            }
        });

        Btn_serial_monitor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(PatientRoomActivity.this, MeasuresDateList.class));
            }
        });

        Btn_sign_out.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mAuth.getInstance().signOut();
                startActivity(new Intent(PatientRoomActivity.this, AuthActivity.class));
            }
        });
    }

    public boolean CheckBluetooth(int REQUEST_CODE)
    {

        BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (bluetoothAdapter!=null)
        {
            if (bluetoothAdapter.isEnabled())
            {
                result = true;
            }
            else
            {
                Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                startActivityForResult(enableBtIntent, REQUEST_CODE);
                result = false;
            }
        }

        return result;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode==REQUEST_ENABLE_BT_FOR_GRAPH)
        {
            if (resultCode==RESULT_OK)
            {
                startActivity(new Intent(PatientRoomActivity.this, GraphicActivity.class));
            }
        }
        else if (requestCode==REQUEST_ENABLE_BT_FOR_SERIAL)
        {
            if (resultCode==RESULT_OK)
            {
//                startActivity(new Intent(PatientRoomActivity.this, SerialMonitorActivity.class));
            }
        }
    }
}


