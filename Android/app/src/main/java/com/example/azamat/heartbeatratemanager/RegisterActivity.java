package com.example.azamat.heartbeatratemanager;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class RegisterActivity extends AppCompatActivity {

    private EditText ET_email, ET_password, ET_password_confirm;
    private Button Btn_next;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        ET_email = (EditText)findViewById(R.id.et_email_register);
        ET_password = (EditText)findViewById(R.id.et_password_register);
        ET_password_confirm = (EditText)findViewById(R.id.et_password_confirm);
        Btn_next = (Button)findViewById(R.id.btn_next_to_person_date);

        Btn_next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = ET_email.getText().toString();
                if (ET_password.getText().toString().equals(ET_password_confirm.getText().toString()))
                {
                    String password = ET_password.getText().toString();
                    Intent intent = new Intent(RegisterActivity.this, RegisterPersonDateActivity.class);
                    intent.putExtra("Email", email);
                    intent.putExtra("Password", password);
                    startActivity(intent);
                }
            }
        });
    }
}
