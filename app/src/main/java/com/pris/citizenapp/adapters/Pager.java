package com.pris.citizenapp.adapters;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import java.util.ArrayList;

/**
 * Created by manav on 5/4/17.
 */

public class Pager extends FragmentPagerAdapter {

    //integer to count number of tabs
    ArrayList<Fragment> fragments=new ArrayList<>();
    ArrayList<String> header=new ArrayList<>();
    //Constructor to the class


    public Pager(FragmentManager fm) {
        super(fm);
        //Initializing tab count

    }

    public void addFragemnt(Fragment frag,String ttl)
    {
         this.fragments.add(frag);
         this.header.add(ttl);
    }
    //Overriding method getItem
    @Override
    public Fragment getItem(int position) {
        //Returning the current tabs
        return fragments.get(position);
    }

    //Overriden method getCount to get the number of tabs
    @Override
    public int getCount() {
        return fragments.size();
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return header.get(position);
    }
}
