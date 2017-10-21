package com.example.azamat.heartbeatratemanager;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class RegisterPersonDateActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private DatabaseReference myRef;
    private FirebaseAuth.AuthStateListener mAuthListener;

    private EditText ET_name, ET_surname, ET_lastname;
    private Button Btn_ready_registration;

    String email;
    String password;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_person_date);
        email = getIntent().getStringExtra("Email");
        password = getIntent().getStringExtra("Password");

        ET_name = (EditText)findViewById(R.id.et_name_register);
        ET_surname = (EditText)findViewById(R.id.et_surname_register);
        ET_lastname = (EditText)findViewById(R.id.et_lastname_confirm);

        Btn_ready_registration = (Button)findViewById(R.id.btn_add_person_date);

        myRef = FirebaseDatabase.getInstance().getReference();

        mAuth = FirebaseAuth.getInstance();

        Btn_ready_registration.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                email = email.trim();
                password = password.trim();
                mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(RegisterPersonDateActivity.this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful())
                        {
                            FirebaseUser user = mAuth.getInstance().getCurrentUser();
                            myRef.child(user.getUid()).child("PersonDate").child("name").setValue(ET_name.getText().toString());
                            myRef.child(user.getUid()).child("PersonDate").child("surname").setValue(ET_surname.getText().toString());
                            myRef.child(user.getUid()).child("PersonDate").child("lastname").setValue(ET_lastname.getText().toString());
//                            myRef.child(user.getUid()).push().setValue(ET_name.getText().toString()+ET_surname.getText().toString());
                            startActivity(new Intent(RegisterPersonDateActivity.this, PatientRoomActivity.class));
                        }
                    }
                });
            }
        });

    }
}
