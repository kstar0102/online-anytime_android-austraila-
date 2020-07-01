package com.austraila.online_anytime.activitys;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.austraila.online_anytime.LocalManage.DatabaseHelper;
import com.austraila.online_anytime.R;

public class SplashActivity extends AppCompatActivity {
    private SQLiteDatabase db;
    private SQLiteOpenHelper openHelper;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        getSupportActionBar().hide();
        openHelper = new DatabaseHelper(this);
        db = openHelper.getWritableDatabase();

        final Cursor cursor = db.rawQuery("SELECT *FROM " + DatabaseHelper.TABLE_NAME,  null);
        if(cursor != null){
            Log.e("TAGtotal", String.valueOf(cursor.getCount()));
            if (cursor.moveToFirst()){
                do{
                    Log.e("TAGId", cursor.getString(cursor.getColumnIndex("ID")));
                    Log.e("TAGGmail", cursor.getString(cursor.getColumnIndex("Gmail")));
                    Log.e("TAGPass", cursor.getString(cursor.getColumnIndex("Password")));
                }while(cursor.moveToNext());
            }
            cursor.close();
        }else {
            Log.e("No Data", "No cursor");
        }

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if(cursor.getCount() > 0){
                    Intent intent = new Intent(SplashActivity.this, MainActivity.class);
                    startActivity(intent);
                }else {
                    Intent intent = new Intent(SplashActivity.this, LoginActivity.class);
                    startActivity(intent);
                }
            }
        }, 3000);
    }
}
