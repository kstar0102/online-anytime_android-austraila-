package com.austraila.online_anytime.activitys;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.austraila.online_anytime.Common.Common;
import com.austraila.online_anytime.LocalManage.DatabaseHelper;
import com.austraila.online_anytime.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class SuccessActivity extends AppCompatActivity {
    private SQLiteDatabase db;
    private SQLiteOpenHelper openHelper;
    RequestQueue queue;
    String useremail, username, userpass;
    TextView textView;
    RelativeLayout loading;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_success);

        getSupportActionBar().hide();

        Intent intent = getIntent();
        HashMap<String, String> hashMap = (HashMap<String, String>)intent.getSerializableExtra("elementData");
        System.out.println(hashMap);

        openHelper = new DatabaseHelper(this);
        db = openHelper.getWritableDatabase();

        final Cursor cursor = db.rawQuery("SELECT *FROM " + DatabaseHelper.TABLE_NAME,  null);
        if(cursor != null){
            Log.e("TAGtotal", String.valueOf(cursor.getCount()));
            if (cursor.moveToFirst()){
                do{
                    useremail = cursor.getString(cursor.getColumnIndex("Gmail"));
                    username = cursor.getString(cursor.getColumnIndex("username"));
                    userpass = cursor.getString(cursor.getColumnIndex("Password"));
                }while(cursor.moveToNext());
            }
            cursor.close();
        }
        loading = findViewById(R.id.loadingSucees);
        textView = findViewById(R.id.successText);
        TextView back = findViewById(R.id.back_main);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SuccessActivity.this, MainActivity.class);
                startActivity(intent);
            }
        });
        testcheck();
    }

    private void testcheck() {
        String url = Common.getInstance().getBaseURL() + Common.getInstance().getApiKey();
        StringRequest postRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        System.out.println(response);
                        JSONObject jsonObject = null;
                        try {
                            jsonObject = new JSONObject(response);
                            String result = jsonObject.getString("success");
                            if (result.equals("true")){
                                loading.setVisibility(View.GONE);
                                textView.setText(getResources().getString(R.string.success));
                            } else {
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        System.out.println(error);
                        loading.setVisibility(View.GONE);
                        textView.setText("It is currently offline. All data saved local stroage.");
                        Toast.makeText(SuccessActivity.this, "It is currently offline.", Toast.LENGTH_LONG).show();
                    }
                }){
            @Override
            protected Map<String, String> getParams()
            {
                Map<String, String> params = new HashMap<String, String>();
                params.put("email", useremail);
                params.put("password", userpass);
                return params;
            }
        };
        queue = Volley.newRequestQueue(SuccessActivity.this);
        queue.add(postRequest);
    }
}
