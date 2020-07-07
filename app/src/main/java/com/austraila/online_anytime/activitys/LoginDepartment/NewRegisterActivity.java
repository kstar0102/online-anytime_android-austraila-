package com.austraila.online_anytime.activitys.LoginDepartment;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.austraila.online_anytime.Common.Common;
import com.austraila.online_anytime.R;

import java.util.HashMap;
import java.util.Map;

public class NewRegisterActivity extends AppCompatActivity {
    EditText register_name, register_email, register_password, register_confirm;
    String username, useremail, userpass, userconfirm;
    Button register_btn;
    ProgressDialog mProgressDialog;

    String url;
    RequestQueue queue;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        getSupportActionBar().hide();

        register_btn = findViewById(R.id.register_btn);
        register_name = findViewById(R.id.register_name);
        register_email = findViewById(R.id.register_email);
        register_password = findViewById(R.id.register_password);
        register_confirm = findViewById(R.id.register_confirm);

        register_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signupProcessing();
            }

        });

    }

    private void signupProcessing() {
        username = register_name.getText().toString();
        useremail = register_email.getText().toString();
        userpass = register_password.getText().toString();
        userconfirm = register_confirm.getText().toString();

        if (TextUtils.isEmpty(username)) {
            Toast.makeText(NewRegisterActivity.this, "User name Field is Empty!", Toast.LENGTH_SHORT).show();
            return;
        }

        if (TextUtils.isEmpty(useremail)) {
            Toast.makeText(NewRegisterActivity.this, "User emal Field is Empty!", Toast.LENGTH_SHORT).show();
            return;
        }

        if (TextUtils.isEmpty(userpass)) {
            Toast.makeText(NewRegisterActivity.this, "User password Field is Empty!", Toast.LENGTH_SHORT).show();
            return;
        }

        if (TextUtils.isEmpty(userconfirm)) {
            Toast.makeText(NewRegisterActivity.this, "User password Field is Empty!", Toast.LENGTH_SHORT).show();
            return;
        }

        if (!userpass.equals(userconfirm)){
            Toast.makeText(NewRegisterActivity.this, "Invalid password!", Toast.LENGTH_SHORT).show();
            return;
        }

        RegisterUser(username, useremail, userpass);
    }

    private void RegisterUser(final String username, final String useremail, final String userpass) {
        mProgressDialog = new ProgressDialog(NewRegisterActivity.this);
        mProgressDialog.setTitle("Signing...");
        mProgressDialog.setIndeterminate(false);
        mProgressDialog.show();

        register_password.setEnabled(false);
        register_email.setEnabled(false);
        register_name.setEnabled(false);
        register_confirm.setEnabled(false);

        url = Common.getInstance().getRegisterUrl();
        StringRequest postRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        System.out.println(response);
                        mProgressDialog.dismiss();
                        Intent i = new Intent(getApplicationContext(), LoginActivity.class);
                        startActivity(i);
//                        finish();
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {

                    }
                }){
            @Override
            protected Map<String, String> getParams()
            {
                Map<String, String> params = new HashMap<String, String>();
                params.put("email", useremail);
                params.put("name", username);
                params.put("password", userpass);

                return params;
            }
        };
        queue = Volley.newRequestQueue(NewRegisterActivity.this);
        queue.add(postRequest);
    }
}
