package com.austraila.online_anytime.activitys;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.austraila.online_anytime.Common.Common;
import com.austraila.online_anytime.LocalManage.DatabaseHelper;
import com.austraila.online_anytime.LocalManage.FormDatabaeHelper;
import com.austraila.online_anytime.R;
import com.austraila.online_anytime.adapter.CustomAdapter;
import com.austraila.online_anytime.model.Listmodel;
import com.google.android.material.navigation.NavigationView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;



public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener, SearchView.OnQueryTextListener {
    private DrawerLayout drawer;
    private NavigationView navigation;
    private SQLiteDatabase db,Db;
    private SQLiteOpenHelper openHelper,FormopenHelper;
    ListView listView;
    ArrayList Listitem=new ArrayList<>();
    JSONArray ApiList = new JSONArray();
    SearchView searchView;
    String useremail, result;
    CustomAdapter myAdapter;
    RequestQueue queue;
    ArrayList<String> listdata = new ArrayList<String>();

    ArrayAdapter<String> adapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        getSupportActionBar().hide();

        openHelper = new DatabaseHelper(this);
        FormopenHelper = new FormDatabaeHelper(this);
        db = openHelper.getWritableDatabase();
        Db = FormopenHelper.getWritableDatabase();
        final Cursor fcursor = Db.rawQuery("SELECT *FROM " + FormDatabaeHelper.FORMTABLE_NAME,  null);

        //defile the element
        listView = findViewById(R.id.mainListView);
        searchView = findViewById(R.id.search_view);

        //Connect the Api
        String url = Common.getInstance().getMainItemUrl();
        StringRequest postRequest = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
                @Override
            public void onResponse(String response) {
                JSONObject jsonObject = null;
                try {
                    jsonObject = new JSONObject(response);
                    result = jsonObject.getString("success");
                    if (result.equals("true")){
                        ApiList = jsonObject.getJSONArray("forms");
                        System.out.println(fcursor.getCount());
                        System.out.println(ApiList);
                        if(fcursor.equals(null)){
                            for(int i = 0; i < ApiList.length(); i++){
                                insertData(ApiList.getJSONObject(i).getString("form_name"),ApiList.getJSONObject(i).getString("form_id"));
                            }
                            init();
                            sideMenu_mangement();
                        }
                        if(ApiList.length() != fcursor.getCount()){
                            Db.execSQL("delete from "+ FormDatabaeHelper.FORMTABLE_NAME);
                            for(int i = 0; i < ApiList.length(); i++){
                                insertData(ApiList.getJSONObject(i).getString("form_name"),ApiList.getJSONObject(i).getString("form_id"));
                            }
                            init();
                            sideMenu_mangement();
                        }
                        init();
                        sideMenu_mangement();
                    } else {
                        init();
                        sideMenu_mangement();
                        Toast.makeText(MainActivity.this, "Oops, can't login! please try to login again.", Toast.LENGTH_LONG).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    init();
                    sideMenu_mangement();
                    System.out.println(error);
                    Toast.makeText(MainActivity.this, "It is currently offline.", Toast.LENGTH_LONG).show();
                }
            }){
        };
        queue = Volley.newRequestQueue(MainActivity.this);
        queue.add(postRequest);

        listView.setTextFilterEnabled(true);
        setupSearchView();
    }

    private void sideMenu_mangement() {
        final Cursor cursor = db.rawQuery("SELECT *FROM " + DatabaseHelper.TABLE_NAME,  null);
        if(cursor != null){
            if (cursor.moveToFirst()){
                do{
                    useremail = cursor.getString(cursor.getColumnIndex("Gmail"));
                }while(cursor.moveToNext());
            }
            cursor.close();
        }

        TextView sidemenu_main = findViewById(R.id.sidemenu_main);
        sidemenu_main.setText(useremail);

        drawer = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        findViewById(R.id.menu_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                drawer.openDrawer(Gravity.LEFT);
            }
        });

        navigation = findViewById(R.id.nav_view);
        navigation.setItemIconTintList(null);
        navigation.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.sidebar_course:
                        Toast.makeText(MainActivity.this, "clicked course", Toast.LENGTH_LONG).show();
                        return true;

                    case R.id.sidebar_setting:
                        Intent intent_setting = new Intent(MainActivity.this, SettingActivity.class);
                        startActivity(intent_setting);
                        Toast.makeText(MainActivity.this, "clicked setting", Toast.LENGTH_LONG).show();
                        return true;

                    case R.id.sidebar_logout:
                        Intent intent_logout = new Intent(MainActivity.this, LoginActivity.class);
                        startActivity(intent_logout);
                        Toast.makeText(MainActivity.this, "clicked logout", Toast.LENGTH_LONG).show();
                        return true;
                }
                return false;
            }
        });
    }

    private void init() {
        final Cursor fcursor = Db.rawQuery("SELECT *FROM " + FormDatabaeHelper.FORMTABLE_NAME,  null);
        System.out.println(fcursor.getColumnIndex("form_name"));
        if (fcursor.moveToFirst()){
            do{
                System.out.println(fcursor.getColumnIndex("Ftitle"));
                String data = fcursor.getString(fcursor.getColumnIndex("Ftitle"));
//                Log.e("aaaaaaaa", data );
                Listitem.add(new Listmodel(data));
            }while(fcursor.moveToNext());
        }
        fcursor.close();

        myAdapter=new CustomAdapter(MainActivity.this, Listitem);
        listView.setAdapter(myAdapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent,
                                    View v, int position, long id){
                if(position == 0){
                    Intent intent = new Intent(MainActivity.this, FormActivity.class);
                    startActivity(intent);
                }
            }
        });
    }

    private void setupSearchView()
    {
        searchView.setIconifiedByDefault(false);
        searchView.setOnQueryTextListener(this);
        searchView.setSubmitButtonEnabled(true);
        searchView.setQueryHint("Search Here");
    }

    @Override
    public boolean onQueryTextChange(String newText)
    {

        if (TextUtils.isEmpty(newText)) {
            listView.clearTextFilter();
        } else {
            listView.setFilterText(newText);
        }
        return true;
    }

    @Override
    public boolean onQueryTextSubmit(String query)
    {
        return false;
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
        return false;
    }

    public void insertData(String ftitle,String ftitle_id){
        ContentValues contentValues = new ContentValues();
        contentValues.put(FormDatabaeHelper.FCOL_2, ftitle);
        contentValues.put(FormDatabaeHelper.FCOL_3, ftitle_id);
        Db.insert(FormDatabaeHelper.FORMTABLE_NAME,null,contentValues);
    }

}
