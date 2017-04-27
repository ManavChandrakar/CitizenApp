package com.pris.citizenapp.entrolab.Application;

import android.graphics.Typeface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.TextView;

import com.afollestad.materialdialogs.MaterialDialog;
import com.facebook.drawee.backends.pipeline.Fresco;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.pris.citizenapp.R;
import com.pris.citizenapp.adapters.GrevienceDetailAdapter;
import com.pris.citizenapp.adapters.GrievancedAdapter;
import com.pris.citizenapp.adapters.Replies;
import com.pris.citizenapp.common.SessionManager;

import java.util.ArrayList;

/**
 * Created by manav on 13/4/17.
 */

public class Grevancedetails extends AppCompatActivity implements OnMapReadyCallback {

    private GoogleMap mMap; // Might be null if Google Play services APK is not available.
    private SessionManager session;
    MaterialDialog popalert;
    private double latitude_v;
    private double longitude_v;
    TextView loc;
    TextView gpstxt;
    RecyclerView recList;
    LinearLayoutManager llm;
    public int LIST_SIZE;
    TextView tv1,tv2;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Fresco.initialize(this);
        setContentView(R.layout.grievence_details);
        setTitle("Grevience Details");
        session = new SessionManager(this);
        Typeface head = Typeface.createFromAsset(getAssets(), "fonts/Roboto_Light.ttf");

        loc=(TextView)findViewById(R.id.location);
        gpstxt=(TextView)findViewById(R.id.gps);
        setUpMapIfNeeded();
        ArrayList<Replies> challenge = this.getIntent().getExtras().getParcelableArrayList("arraydetails");
        recList = (RecyclerView)  findViewById(R.id.myrecyclerView);
        recList.setHasFixedSize(true);
        tv1=(TextView)findViewById(R.id.location_txt);
        tv1.setTypeface(head);
        tv2=(TextView)findViewById(R.id.gps_location);
        tv2.setTypeface(head);

        llm = new LinearLayoutManager(this);
        llm.setOrientation(LinearLayoutManager.VERTICAL);
        recList.setLayoutManager(llm);

        popalert = new MaterialDialog.Builder(this)
                .title("Errors Found!")
                .titleColor(getResources().getColor(R.color.appcolor))
                .content("No Replies Found")
                .positiveText("Ok").build();

       if(challenge.size()==0)
       {
           popalert.show();
       }
       GrevienceDetailAdapter ca = new GrevienceDetailAdapter(challenge,this);
        recList.setAdapter(ca);
        ca.notifyDataSetChanged();
        loc.setText(session.getStrVal("currentlocation"));
        gpstxt.setText(session.getStrVal("currentlat") + "," + session.getStrVal("currentlng"));


    }

    private void setUpMapIfNeeded() {
        if (mMap == null) {
            // Try to obtain the map from the SupportMapFragment.
            //mMap = ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map));
            SupportMapFragment supportMapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
            supportMapFragment.getMapAsync(this);
        }
    }

    private void setUpMap() {
        //  mMap.addMarker(new MarkerOptions().position(new LatLng(0, 0)).title("Marker"));

        // mMap.setMyLocationEnabled(true);
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(Double.parseDouble(session.getStrVal("currentlat")), Double.parseDouble(session.getStrVal("currentlng"))), 16));

        mMap.setPadding(0, 0, 20, 20);


        MarkerOptions markerOptions = new MarkerOptions().position(new LatLng(Double.parseDouble(session.getStrVal("currentlat")), Double.parseDouble(session.getStrVal("currentlng")))).draggable(false);
        mMap.addMarker(markerOptions);

    }

    @Override
    public void onMapReady(GoogleMap googleMap) {

        mMap=googleMap;
        mMap.getUiSettings().setScrollGesturesEnabled(false);
        if (mMap != null) {
            setUpMap();
        }
    }
}
