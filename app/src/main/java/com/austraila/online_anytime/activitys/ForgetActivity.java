package com.austraila.online_anytime.activitys;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

import com.austraila.online_anytime.R;

public class ForgetActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forget);
        getSupportActionBar().hide();
    }

    public void loginPageClick (View view) {
        Intent intent = new Intent(ForgetActivity.this, LoginActivity.class);
        startActivity(intent);
    }
}
