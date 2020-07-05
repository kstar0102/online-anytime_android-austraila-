package com.austraila.online_anytime.activitys.LoginDepartment;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;

import com.austraila.online_anytime.R;

public class NewRegisterActivity extends AppCompatActivity {
    EditText register_name, register_email, register_password;
    Button register_btn;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        getSupportActionBar().hide();

        register_btn = findViewById(R.id.register_btn);
        register_name = findViewById(R.id.register_name);
        register_email = findViewById(R.id.register_email);
        register_password = findViewById(R.id.register_password);

        register_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(NewRegisterActivity.this, LoginActivity.class);
                startActivity(intent);
            }
        });

    }
}
