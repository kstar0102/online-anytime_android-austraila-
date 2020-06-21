package com.austraila.form.G1_RiiRis301D;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.austraila.form.R;

public class G1DetailActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_g1_detail);
        getSupportActionBar().hide();
    }
}
