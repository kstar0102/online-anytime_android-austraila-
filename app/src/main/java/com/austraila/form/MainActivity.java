package com.austraila.form;

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
import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;

import com.austraila.form.model.Listmodel;
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

        Listitem.add(new Listmodel("G1-RIIRIS301D(Practical Assessment)Copy"));
        Listitem.add(new Listmodel("Enrolment Form"));
        Listitem.add(new Listmodel("Working at Heights - Enrolment form"));
        Listitem.add(new Listmodel("G2 - RIIRIS402D (Practical Assessment)"));
        Listitem.add(new Listmodel("My simple test form"));
        Listitem.add(new Listmodel("MyFileupload"));
        Listitem.add(new Listmodel("MyTestForm"));
        Listitem.add(new Listmodel("Theory Assessment Cover Page"));
        Listitem.add(new Listmodel("Confirmation of Student Contact Visit"));
        Listitem.add(new Listmodel("Enter and work in confined spaces - 2nd Assessment"));
        Listitem.add(new Listmodel("Enter and work in confined spaces - 1st Assessment"));

        CustomAdapter myAdapter=new CustomAdapter(MainActivity.this, R.layout.mainlist_item, Listitem);
        listView.setAdapter(myAdapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent,
                                    View v, int position, long id){
                // Send intent to SingleViewActivity
                Intent i = new Intent(MainActivity.this, EachItemActivity.class);
                // Pass image index
                i.putExtra("id", position);
                startActivity(i);
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
                        Toast.makeText(MainActivity.this, "clicked setting", Toast.LENGTH_LONG).show();
                        return true;

                    case R.id.sidebar_logout:
                        Toast.makeText(MainActivity.this, "clicked logout", Toast.LENGTH_LONG).show();
                        return true;
//
//
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
