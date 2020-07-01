package com.austraila.online_anytime.activitys;

import android.content.DialogInterface;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.icu.util.Calendar;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.austraila.online_anytime.LocalManage.DatabaseHelper;
import com.austraila.online_anytime.R;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class SettingActivity extends AppCompatActivity {
    private SQLiteDatabase db;
    private SQLiteOpenHelper openHelper;
    TextView setting_name, setting_email,setting_time;
    ImageView side_menu_setting;
    Button sync_btn;
    String useremail;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);

        getSupportActionBar().hide();

        openHelper = new DatabaseHelper(this);
        db = openHelper.getWritableDatabase();

        final Cursor cursor = db.rawQuery("SELECT *FROM " + DatabaseHelper.TABLE_NAME,  null);

//        Log.e("asdfasdfadsf", String.valueOf(cursor.getString(cursor.getColumnIndex("COL_2"))));
//        useremail = cursor.getString(cursor.getColumnIndex("COL_2"));

        //define element
        setting_name = findViewById(R.id.setting_username);
        setting_email = findViewById(R.id.setting_email);
        setting_time = findViewById(R.id.setting_time);
        side_menu_setting = findViewById(R.id.menu_btn_setting);
        sync_btn = findViewById(R.id.setting_submit);

        setting_email.setText(useremail);
        sync_btn.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onClick(View v) {
                Date date = Calendar.getInstance().getTime();
                DateFormat dateFormat = new SimpleDateFormat("yyyy-mm-dd hh:mm:ss");
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
    }
}
