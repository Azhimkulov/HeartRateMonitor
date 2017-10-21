package com.example.azamat.heartbeatratemanager;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class AuthActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;

    private EditText ETemail;
    private EditText ETpassword;

    private Button Btn_sign_in;
    private Button Btn_register;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_auth);

        mAuth = FirebaseAuth.getInstance();

        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user != null) {

                    startActivity(new Intent(AuthActivity.this, PatientRoomActivity.class));
                } else {


                }

            }
        };

        ETemail = (EditText)findViewById(R.id.et_email);
        ETpassword = (EditText)findViewById(R.id.et_password);

        Btn_sign_in = (Button)findViewById(R.id.btn_sign_in);
        Btn_register = (Button)findViewById(R.id.btn_register);

        Btn_sign_in.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String email = ETemail.getText().toString().trim();
                String password = ETpassword.getText().toString().trim();

                mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(AuthActivity.this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful())
                        {
                            Intent intent = new Intent(AuthActivity.this, PatientRoomActivity.class);
                            startActivity(intent);
                        }
                        else
                        {
                            Toast.makeText(AuthActivity.this, "Авторизация провалена", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        });

        Btn_register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(AuthActivity.this, RegisterActivity.class);
                startActivity(intent);
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(mAuthListener);
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (mAuthListener != null) {
            mAuth.removeAuthStateListener(mAuthListener);
        }
    }
}
