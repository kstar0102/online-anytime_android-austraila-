package com.austraila.online_anytime.activitys;

import android.content.Intent;
import android.os.Bundle;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.UiThread;
import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;

import com.austraila.online_anytime.R;
import com.austraila.online_anytime.adapter.CustomAdapter;
import com.austraila.online_anytime.model.Listmodel;
import com.google.android.material.navigation.NavigationView;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    private DrawerLayout drawer;
    private NavigationView navigation;
    ListView listView;
    ArrayList Listitem=new ArrayList<>();
    SearchView searchView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        getSupportActionBar().hide();

        searchView = findViewById(R.id.search_view);
        searchView.setQueryHint("Search");

        listView = findViewById(R.id.mainListView);

        Listitem.add(new Listmodel("Test Form element"));

        CustomAdapter myAdapter=new CustomAdapter(MainActivity.this, R.layout.mainlist_item, Listitem);
        listView.setAdapter(myAdapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent,
                                    View v, int position, long id){

                Toast.makeText(MainActivity.this, "myPos "+position, Toast.LENGTH_LONG).show();
                if(position == 0){
                    Intent intent = new Intent(MainActivity.this, FormActivity.class);
                    startActivity(intent);
                }
            }
        });

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

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
        return false;
    }
}
