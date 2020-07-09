package com.austraila.online_anytime.activitys.LoginDepartment;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.austraila.online_anytime.Common.Common;
import com.austraila.online_anytime.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class CodeCheckActivity extends AppCompatActivity {
    EditText num_1, num_2, num_3, num_4, num_5, num_6;
    String email,result, opt, token, deviceOTP;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_codecheck);

        getSupportActionBar().hide();

        Intent intent = getIntent();
        email = intent.getStringExtra("Email");

        num_1 = findViewById(R.id.number_1);
        num_2 = findViewById(R.id.number_2);
        num_3 = findViewById(R.id.number_3);
        num_4 = findViewById(R.id.number_4);
        num_5 = findViewById(R.id.number_5);
        num_6 = findViewById(R.id.number_6);

        String url = Common.getInstance().getForgetUrl() ;
        StringRequest postRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        System.out.println(response);
                        JSONObject jsonObject = null;
                        try {
                            jsonObject = new JSONObject(response);
                            result = jsonObject.getString("success");
                            if (result.equals("true")){
                                opt = jsonObject.getString("opt");
                                token = jsonObject.getString("token");
                                Button continueBtn = findViewById(R.id.code_continue_btn);
                                continueBtn.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        deviceOTP = num_1.getText().toString() + num_2.getText().toString()+ num_3.getText().toString()
                                                + num_4.getText().toString()+ num_5.getText().toString()+ num_6.getText().toString();

                                        String url = Common.getInstance().getCodeUrl();
                                        StringRequest postRequest = new StringRequest(Request.Method.POST, url,
                                                new Response.Listener<String>() {
                                                    @Override
                                                    public void onResponse(String response) {
                                                        System.out.println(response);
                                                        JSONObject jsonObject = null;
                                                        try {
                                                            jsonObject = new JSONObject(response);
                                                            result = jsonObject.getString("success");
                                                            if (result.equals("true")){
                                                                String keytoken = jsonObject.getString("token");
                                                                Intent intent = new Intent(CodeCheckActivity.this, NewRegisterActivity.class);
                                                                intent.putExtra("token", keytoken);
                                                                startActivity(intent);
                                                            } else {
                                                                Toast.makeText(CodeCheckActivity.this, "Entered Otp is failed. Please retry.", Toast.LENGTH_LONG).show();
                                                                num_1.setText("");
                                                                num_2.setText("");
                                                                num_3.setText("");
                                                                num_4.setText("");
                                                                num_5.setText("");
                                                                num_6.setText("");
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
                                                        Toast.makeText(CodeCheckActivity.this, "It is currently offline.", Toast.LENGTH_LONG).show();
                                                    }
                                                }){

                                            @Override
                                            public Map<String, String> getHeaders() throws AuthFailureError {
                                                Map<String, String> headers = new HashMap<>();
                                                headers.put("token", token);
                                                return headers;
                                            }

                                            @Override
                                            protected Map<String, String> getParams()
                                            {
                                                Map<String, String> params = new HashMap<String, String>();
                                                params.put("key", opt);
                                                return params;
                                            }
                                        };
                                        RequestQueue queue = Volley.newRequestQueue(CodeCheckActivity.this);
                                        queue.add(postRequest);
                                    }
                                });
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
                        Toast.makeText(CodeCheckActivity.this, "It is currently offline.", Toast.LENGTH_LONG).show();
                    }
                }){
            @Override
            protected Map<String, String> getParams()
            {
                Map<String, String> params = new HashMap<String, String>();
                params.put("email", email);
                return params;
            }
        };
        RequestQueue queue = Volley.newRequestQueue(CodeCheckActivity.this);
        queue.add(postRequest);
    }
}
