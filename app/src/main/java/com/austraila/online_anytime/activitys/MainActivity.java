package com.austraila.online_anytime.activitys;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
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
import com.austraila.online_anytime.LocalManage.ElementDatabaseHelper;
import com.austraila.online_anytime.LocalManage.FormDatabaeHelper;
import com.austraila.online_anytime.R;
import com.austraila.online_anytime.activitys.LoginDepartment.LoginActivity;
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
    private SQLiteDatabase db,Db,EDb;
    private SQLiteOpenHelper openHelper,FormopenHelper,ElementopenHelper;
    RelativeLayout loading;
    ListView listView;
    ArrayList Listitem=new ArrayList<>();
    JSONArray ApiList = new JSONArray();
    JSONArray ElemnetList = new JSONArray();
    SearchView searchView;
    String useremail, result, checksum;
    CustomAdapter myAdapter;
    RequestQueue queue;
    ArrayList<String> listFormId = new ArrayList<String>();
    ArrayList<String> listFormDes = new ArrayList<String>();
    ArrayList<String> listFormtitle = new ArrayList<String>();
    ImageView reloadBtn;
    ArrayList<String> data = new ArrayList<String>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        getSupportActionBar().hide();

        //defile the element
        listView = findViewById(R.id.mainListView);
        searchView = findViewById(R.id.search_view);
        reloadBtn = findViewById(R.id.reload_btn);
//        loading = findViewById(R.id.loadingLayout);

        //local database define
        openHelper = new DatabaseHelper(this);
        FormopenHelper = new FormDatabaeHelper(this);
        ElementopenHelper = new ElementDatabaseHelper(this);
        db = openHelper.getWritableDatabase();
        Db = FormopenHelper.getWritableDatabase();
        EDb = ElementopenHelper.getWritableDatabase();

//        loading.setVisibility(View.VISIBLE);
        init();


        reloadBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Cursor fcursor = Db.rawQuery("SELECT *FROM " + FormDatabaeHelper.FORMTABLE_NAME,  null);

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
                                String Apichecksum = jsonObject.getString("checksum");

                                if(checksum != Apichecksum){
                                    Db.execSQL("delete from "+ FormDatabaeHelper.FORMTABLE_NAME);
                                    EDb.execSQL("delete from "+ ElementDatabaseHelper.ElEMENTTABLE_NAME);
                                    for(int i = 0; i < ApiList.length(); i++){
                                        insertData(ApiList.getJSONObject(i).getString("form_name")
                                                ,ApiList.getJSONObject(i).getString("form_id")
                                                , Apichecksum
                                                ,ApiList.getJSONObject(i).getString("form_description"));
                                    }
                                    elementSave();
                                    update();
//                                    sideMenu_mangement();
                                }else {
                                    update();
//                                    sideMenu_mangement();
                                }
                            } else {
                                update();
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
                        update();
//                        sideMenu_mangement();
                        System.out.println(error);
                        Toast.makeText(MainActivity.this, "It is currently offline.", Toast.LENGTH_LONG).show();
                    }
                }){
                };
                queue = Volley.newRequestQueue(MainActivity.this);
                queue.add(postRequest);
            }
        });
        sideMenu_mangement();
    }

    private void update(){
        if (listView!= null) {
            listView.invalidateViews();
        }
        myAdapter.notifyDataSetChanged();

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent,
                                    View v, int position, long id){
                Intent intent = new Intent(MainActivity.this, FormActivity.class);
                intent.putExtra("id", listFormId.get(position));
                startActivity(intent);
            }
        });
    }

    private void elementSave() throws JSONException {
        for (int i = 0; i < ApiList.length(); i ++){
            final String formId = ApiList.getJSONObject(i).getString("form_id");
            StringRequest postRequest = new StringRequest(Request.Method.GET, Common.getInstance().getFormelementUrl() + formId + Common.getInstance().getApiKey(), new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    JSONObject jsonObject = null;
                    try {
                        jsonObject = new JSONObject(response);
                        result = jsonObject.getString("success");
                        if (result.equals("true")){
//                            loading.setVisibility(View.GONE);
                            ElemnetList = jsonObject.getJSONArray("forms");
                            for(int j = 0; j < ElemnetList.length(); j++){
                                insertElementData(ElemnetList.getJSONObject(j).getString("element_id")
                                        ,ElemnetList.getJSONObject(j).getString("element_title")
                                        ,ElemnetList.getJSONObject(j).getString("element_guidelines")
                                        ,ElemnetList.getJSONObject(j).getString("element_type")
                                        ,ElemnetList.getJSONObject(j).getString("element_position")
                                        ,ElemnetList.getJSONObject(j).getString("element_page_number")
                                        ,ElemnetList.getJSONObject(j).getString("element_default_value")
                                        ,ElemnetList.getJSONObject(j).getString("element_submit_secondary_text")
                                        ,formId);
                            }

                        } else {
//                            loading.setVisibility(View.GONE);
                            Toast.makeText(MainActivity.this, "Oops, Request failed..", Toast.LENGTH_LONG).show();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
//                    loading.setVisibility(View.GONE);
                    ListviewManagement();
//                    sideMenu_mangement();
                    System.out.println(error);
                    Toast.makeText(MainActivity.this, "It is currently offline.", Toast.LENGTH_LONG).show();
                }
            }){
            };
            queue = Volley.newRequestQueue(MainActivity.this);
            queue.add(postRequest);
        }
    }

    private void init() {
        final Cursor fcursor = Db.rawQuery("SELECT *FROM " + FormDatabaeHelper.FORMTABLE_NAME,  null);

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
                        String Apichecksum = jsonObject.getString("checksum");

                        if(checksum != Apichecksum){
                            Db.execSQL("delete from "+ FormDatabaeHelper.FORMTABLE_NAME);
                            EDb.execSQL("delete from "+ ElementDatabaseHelper.ElEMENTTABLE_NAME);
                            for(int i = 0; i < ApiList.length(); i++){
                                insertData(ApiList.getJSONObject(i).getString("form_name")
                                        ,ApiList.getJSONObject(i).getString("form_id")
                                        , Apichecksum
                                        ,ApiList.getJSONObject(i).getString("form_description"));
                            }
                            elementSave();
                            ListviewManagement();
//                            loading.setVisibility(View.GONE);
//                            sideMenu_mangement();
                        }else {
                            ListviewManagement();
//                            loading.setVisibility(View.GONE);
//                            sideMenu_mangement();
                        }
                    } else {
                        ListviewManagement();
//                        loading.setVisibility(View.GONE);
//                        sideMenu_mangement();
                        Toast.makeText(MainActivity.this, "Oops, can't login! please try to login again.", Toast.LENGTH_LONG).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
//                loading.setVisibility(View.GONE);
                ListviewManagement();
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

    private void ListviewManagement() {

        final Cursor fcursor = Db.rawQuery("SELECT *FROM " + FormDatabaeHelper.FORMTABLE_NAME,  null);

        if (fcursor.moveToFirst()){
            do{
                data.add(fcursor.getString(fcursor.getColumnIndex("Ftitle")));
                listFormId.add(fcursor.getString(fcursor.getColumnIndex("Ftitle_id")));
                listFormDes.add(fcursor.getString(fcursor.getColumnIndex("form_description")));
                listFormtitle.add(fcursor.getString(fcursor.getColumnIndex("Ftitle")));
            }while(fcursor.moveToNext());
        }
        fcursor.close();
        for(int i = 0; i < data.size(); i++){
            Listitem.add(new Listmodel(data.get(i)));
        }

        myAdapter=new CustomAdapter(MainActivity.this, Listitem);
        listView.setAdapter(myAdapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent,
                                    View v, int position, long id){
                Intent intent = new Intent(MainActivity.this, FormActivity.class);
                intent.putExtra("id", listFormId.get(position));
                intent.putExtra("des", listFormDes.get(position));
                intent.putExtra("title", listFormtitle.get(position));
                startActivity(intent);
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

    public void insertData(String ftitle,String ftitle_id, String checksum,String formDes){
        ContentValues contentValues = new ContentValues();
        contentValues.put(FormDatabaeHelper.FCOL_2, ftitle);
        contentValues.put(FormDatabaeHelper.FCOL_3, ftitle_id);
        contentValues.put(FormDatabaeHelper.FCOL_4, checksum);
        contentValues.put(FormDatabaeHelper.FCOL_5, formDes);
        Db.insert(FormDatabaeHelper.FORMTABLE_NAME,null,contentValues);
    }

    public void insertElementData(String element_id,String element_title, String element_guidelines, String element_type, String element_position, String element_page_number, String element_default_value, String element_submit_secondary_text, String formid){
        ContentValues contentValues = new ContentValues();
        contentValues.put(ElementDatabaseHelper.ECOL_2, element_id);
        contentValues.put(ElementDatabaseHelper.ECOL_3, element_title);
        contentValues.put(ElementDatabaseHelper.ECOL_4, element_guidelines);
        contentValues.put(ElementDatabaseHelper.ECOL_5, element_type);
        contentValues.put(ElementDatabaseHelper.ECOL_6, element_position);
        contentValues.put(ElementDatabaseHelper.ECOL_7, element_page_number);
        contentValues.put(ElementDatabaseHelper.ECOL_8, element_default_value);
        contentValues.put(ElementDatabaseHelper.ECOL_9, element_submit_secondary_text);
        contentValues.put(ElementDatabaseHelper.ECOL_10, formid);
        System.out.println(contentValues);
        EDb.insert(ElementDatabaseHelper.ElEMENTTABLE_NAME,null,contentValues);
    }

}
