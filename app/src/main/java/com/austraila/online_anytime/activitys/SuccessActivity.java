package com.austraila.online_anytime.activitys;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;
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
import com.austraila.online_anytime.LocalManage.DatabaseHelper;
import com.austraila.online_anytime.LocalManage.ElementValueDatabaeHelper;
import com.austraila.online_anytime.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.util.HashMap;
import java.util.Map;

public class SuccessActivity extends AppCompatActivity {
    private SQLiteDatabase db,VDb;
    private SQLiteOpenHelper openHelper,ElementValueopenHeloer;
    RequestQueue queue;
    String Token, formid;
    TextView textView;
    RelativeLayout loading;
    HashMap<String, String> formData = new HashMap<String, String>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_success);

        getSupportActionBar().hide();

        Intent intent = getIntent();
        formData = (HashMap<String, String>)intent.getSerializableExtra("elementData");
        formid = intent.getStringExtra("FormId");
        for (Map.Entry<String, Bitmap> entry : FormActivity.elementPhotos.entrySet()) {
            String key = entry.getKey();
            Bitmap value = entry.getValue();
            String image = "data:image/png;base64," + toBase64(value);
            formData.put(key, image);
        }
//        System.out.println(formData);

        openHelper = new DatabaseHelper(this);
        ElementValueopenHeloer = new ElementValueDatabaeHelper(this);
        db = openHelper.getWritableDatabase();
        VDb = ElementValueopenHeloer.getWritableDatabase();

        final Cursor cursor = db.rawQuery("SELECT *FROM " + DatabaseHelper.TABLE_NAME,  null);
        if(cursor != null){
            if (cursor.moveToFirst()){
                do{
                    Token = cursor.getString(cursor.getColumnIndex("token"));
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
        sendcheck();
    }

    private void sendcheck() {
        String url = Common.getInstance().getSaveUrl();
        StringRequest postRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        JSONObject jsonObject = null;
                        try {
                            jsonObject = new JSONObject(response);
                            String result = jsonObject.getString("success");
                            if (result.equals("true")){
                                loading.setVisibility(View.GONE);
                                textView.setText(getResources().getString(R.string.success));
                                FormActivity.elementPhotos.clear();
                            } else {
                                loading.setVisibility(View.GONE);
                                textView.setText("false");
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
                        for (Map.Entry<String, String> entry : formData.entrySet()) {
                            String key = entry.getKey();
                            String value = entry.getValue();
                            FormActivity.elementPhotos.clear();
                            insertData(key, value, formid);
                        }
                        Toast.makeText(SuccessActivity.this, "It is currently offline.", Toast.LENGTH_LONG).show();
                    }
                }){

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> headers = new HashMap<>();
                headers.put("token", Token);
                return headers;
            }

            @Override
            protected Map<String, String> getParams()
            {
                formData.put("formId", formid);
                return formData;
            }
        };
        queue = Volley.newRequestQueue(SuccessActivity.this);
        queue.add(postRequest);
    }

    public String toBase64(Bitmap bitmap) {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream);
        byte[] byteArray = byteArrayOutputStream .toByteArray();
        return Base64.encodeToString(byteArray, Base64.DEFAULT);
    }

    private void insertData(String elementkye, String elementValue, String elementformid) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(ElementValueDatabaeHelper.VCOL_2, elementkye);
        contentValues.put(ElementValueDatabaeHelper.VCOL_3, elementValue);
        contentValues.put(ElementValueDatabaeHelper.VCOL_4, elementformid);
        VDb.insert(ElementValueDatabaeHelper.VTABLE_NAME,null,contentValues);
    }
}
