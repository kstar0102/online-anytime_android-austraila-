package com.austraila.online_anytime.activitys;

import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.icu.util.Calendar;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;

import com.austraila.online_anytime.LocalManage.DatabaseHelper;
import com.austraila.online_anytime.R;
import com.google.android.material.navigation.NavigationView;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class SettingActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener  {
    private SQLiteDatabase db;
    private SQLiteOpenHelper openHelper;
    DrawerLayout settinglayout;
    private NavigationView navigation;
    TextView setting_name, setting_email,setting_time,sidemenu_email,setting_username;
    ImageView side_menu_setting;
    Button sync_btn;
    String useremail, username;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);

        getSupportActionBar().hide();

        openHelper = new DatabaseHelper(this);
        db = openHelper.getWritableDatabase();

        final Cursor cursor = db.rawQuery("SELECT *FROM " + DatabaseHelper.TABLE_NAME,  null);
        if(cursor != null){
            Log.e("TAGtotal", String.valueOf(cursor.getCount()));
            if (cursor.moveToFirst()){
                do{
                    useremail = cursor.getString(cursor.getColumnIndex("Gmail"));
                    username = cursor.getString(cursor.getColumnIndex("username"));
                }while(cursor.moveToNext());
            }
            cursor.close();
        }

        //define element
        setting_name = findViewById(R.id.setting_username);
        setting_email = findViewById(R.id.setting_email);
        setting_time = findViewById(R.id.setting_time);
        side_menu_setting = findViewById(R.id.menu_btn_setting);
        sync_btn = findViewById(R.id.setting_submit);
        sidemenu_email = findViewById(R.id.sidemenu_email);

        sidemenu_email.setText(useremail);

        sync_btn.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onClick(View v) {
                setting_email.setText(useremail);
                setting_name.setText(username);

                Date date = Calendar.getInstance().getTime();
                DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
                String strDate = dateFormat.format(date);
                setting_time.setText(strDate);

                AlertDialog alertDialog = new AlertDialog.Builder(SettingActivity.this).create();
                alertDialog.setTitle("Notivce");
                alertDialog.setMessage("Data was transferred successfully.");
                alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        });
                alertDialog.show();
            }
        });

        settinglayout = findViewById(R.id.setting_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        findViewById(R.id.menu_btn_setting).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                settinglayout.openDrawer(Gravity.LEFT);
            }
        });

        navigation = findViewById(R.id.nav_view);
        navigation.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.sidebar_course:
                        Intent intent_main = new Intent(SettingActivity.this, MainActivity.class);
                        startActivity(intent_main);
                        return true;

                    case R.id.sidebar_setting:
                        Intent intent_setting = new Intent(SettingActivity.this, SettingActivity.class);
                        startActivity(intent_setting);
                        return true;

                    case R.id.sidebar_logout:
                        Intent intent_logout = new Intent(SettingActivity.this, LoginActivity.class);
                        startActivity(intent_logout);
                        return true;
                }
                return false;
            }
        });
        navigationView.setItemIconTintList(null);
    }


    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
        return false;
    }
}
