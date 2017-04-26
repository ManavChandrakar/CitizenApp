package com.pris.citizenapp.entrolab;

import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import com.pris.citizenapp.Fragments.Calculator;
import com.pris.citizenapp.Fragments.FragementAdvertisement;
import com.pris.citizenapp.Fragments.FragmentKolagaaram;
import com.pris.citizenapp.R;
import com.pris.citizenapp.adapters.Pager;

/**
 * Created by manav on 11/4/17.
 */

public class AdvertisementTax extends AppCompatActivity implements TabLayout.OnTabSelectedListener {

    TabLayout tabLayout;

    //This is our viewPager
    ViewPager viewPager;
    Toolbar toolbar;
    Pager adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.tab_activity);

        //Adding toolbar to the activity
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        //Initializing the tablayout
        tabLayout = (TabLayout) findViewById(R.id.tabLayout);
        viewPager = (ViewPager) findViewById(R.id.pager);

        //Adding the tabs using addTab() method
        /*tabLayout.addTab(tabLayout.newTab().setText("Tab1"));
        tabLayout.addTab(tabLayout.newTab().setText("Tab2"));
        tabLayout.setTabGravity(TabLayout.GRAVITY_FILL);*/
        setTitle("Advertisement Tax");
        //Initializing viewPager

        //Creating our pager adapter
        adapter = new Pager(getSupportFragmentManager());

        adapter.addFragemnt(new FragementAdvertisement(),"Advertisement Tax");
        adapter.addFragemnt(new Calculator(),"Calculator");
        tabLayout.setupWithViewPager(viewPager);
        //Adding adapter to pager
        viewPager.setAdapter(adapter);

        //Adding onTabSelectedListener to swipe views
        tabLayout.setOnTabSelectedListener(this);
    }
    @Override
    public void onTabSelected(TabLayout.Tab tab) {

    }

    @Override
    public void onTabUnselected(TabLayout.Tab tab) {

    }

    @Override
    public void onTabReselected(TabLayout.Tab tab) {

    }
}
