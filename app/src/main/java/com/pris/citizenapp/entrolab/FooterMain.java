package com.pris.citizenapp.entrolab;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;

import com.pris.citizenapp.Fragments.LinkProperty;
import com.pris.citizenapp.R;
import com.pris.citizenapp.Fragments.Notification;
import com.pris.citizenapp.Fragments.Profile;

/**
 * Created by manav on 16/4/17.
 */

public class FooterMain extends AppCompatActivity {
    Toolbar toolbar;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        final CitizenActivity c=new CitizenActivity();
        BottomNavigationView bottomNavigationView = (BottomNavigationView) findViewById(R.id.bottomnvigate);
        BottomNavigationViewHelper.disableShiftMode(bottomNavigationView);
        getSupportFragmentManager().beginTransaction().replace(R.id.frame,c).commit();
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.home:
                        getSupportFragmentManager().beginTransaction().replace(R.id.frame,c).commit();
                        break;

                    case R.id.notification:
                        Notification f=new Notification();
                        getSupportFragmentManager().beginTransaction().replace(R.id.frame,f).commit();
                        break;


                    case R.id.linkppt:
                        LinkProperty l=new LinkProperty();
                        getSupportFragmentManager().beginTransaction().replace(R.id.frame,l).commit();
                        break;


                    case R.id.profile:
                         Profile p=new Profile();
                        getSupportFragmentManager().beginTransaction().replace(R.id.frame,p).commit();
                        break;

                }
              return true;
            }
        });
    }
}

