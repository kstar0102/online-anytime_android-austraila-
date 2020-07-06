package com.austraila.online_anytime.activitys.LoginDepartment;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;

import com.austraila.online_anytime.R;

public class CodeCheckActivity extends AppCompatActivity {
    EditText num_1, num_2, num_3, num_4;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_codecheck);

        getSupportActionBar().hide();

        num_1 = findViewById(R.id.number_1);
        num_2 = findViewById(R.id.number_2);
        num_3 = findViewById(R.id.number_3);
        num_4 = findViewById(R.id.number_4);

        Button continueBtn = findViewById(R.id.code_continue_btn);
        continueBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(CodeCheckActivity.this, NewRegisterActivity.class);
                startActivity(intent);
            }
        });
    }
}