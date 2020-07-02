package com.austraila.online_anytime.activitys;

import androidx.appcompat.app.AppCompatActivity;

import android.content.ContentValues;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

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

public class LoginActivity extends AppCompatActivity{
    private SQLiteDatabase db;
    private SQLiteOpenHelper openHelper;
    EditText email, pass;
    Button loginBtn;
    String Email, Pass, userfullname, result, user_id;
    RequestQueue queue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        getSupportActionBar().hide();

        openHelper = new DatabaseHelper(this);
        db = openHelper.getWritableDatabase();

        email = findViewById(R.id.login_email);
        pass = findViewById(R.id.login_pass);

        loginBtn = findViewById(R.id.login_btn);
        loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Email = email.getText().toString().trim();
                Pass = pass.getText().toString().trim();
                if (Email.isEmpty() || Pass.isEmpty()) {
                    Toast.makeText(LoginActivity.this, "Enter your Email and Password to login", Toast.LENGTH_SHORT).show();
                    return;
                }else {
//                    String url = "http://192.168.107.90:89/login?api_key=54d0a2c6b96b514cb47c3645714f7ce8";
                    String url = Common.getInstance().getBaseURL() + Common.getInstance().getApiKey();
                    StringRequest postRequest = new StringRequest(Request.Method.POST, url,
                            new Response.Listener<String>() {
                                @Override
                                public void onResponse(String response) {
                                    System.out.println(response);
                                    JSONObject jsonObject = null;
                                    try {
                                        jsonObject = new JSONObject(response);
                                        result = jsonObject.getString("success");
                                        userfullname = jsonObject.getString("user_fullname");
                                        user_id = jsonObject.getString("user_id");
                                        if (result.equals("true")){
                                            insertData(Email, Pass, userfullname);
                                            Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                                            startActivity(intent);
                                        } else {
                                            Toast.makeText(LoginActivity.this, "Oops, can't login! please try to login again.", Toast.LENGTH_LONG).show();
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
                                    Toast.makeText(LoginActivity.this, "It is currently offline.", Toast.LENGTH_LONG).show();
                                }
                            }){
                        @Override
                        protected Map<String, String> getParams()
                        {
                            Map<String, String> params = new HashMap<String, String>();
                            params.put("email", Email);
                            params.put("password", Pass);
                            return params;
                        }
                    };
                    queue = Volley.newRequestQueue(LoginActivity.this);
                    queue.add(postRequest);
                }
            }
        });
    }

    public void foregetClick(View view) {
        Intent intent = new Intent(LoginActivity.this, ForgetActivity.class);
        startActivity(intent);
    }

    public void insertData(String fGmail,String fPassword, String fusername){
        ContentValues contentValues = new ContentValues();
        contentValues.put(DatabaseHelper.COL_2,fGmail);
        contentValues.put(DatabaseHelper.COL_3,fPassword);
        contentValues.put(DatabaseHelper.COL_4, fusername);
        db.insert(DatabaseHelper.TABLE_NAME,null,contentValues);
    }
}
