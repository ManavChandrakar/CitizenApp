package com.pris.citizenapp.entrolab.Application;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Typeface;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.pris.citizenapp.R;
import com.pris.citizenapp.common.GPSTracker;
import com.pris.citizenapp.common.ImageLoadingUtils;
import com.pris.citizenapp.common.SessionManager;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.List;

/**
 * Created by manav on 12/4/17.
 */

public class GrivencelocationImage extends AppCompatActivity implements OnMapReadyCallback {


    private GoogleMap mMap; // Might be null if Google Play services APK is not available.
    private SessionManager session;
    private double latitude_v;
    private double longitude_v;
    GPSTracker gps;
    TextView tv1,tv2;

    private ImageLoadingUtils utils;

    MaterialDialog progress;
    MaterialDialog dialog;

    TextView loc;
    TextView gpstxt;

    private SimpleDateFormat dateFormatter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_confirm_location);
        Typeface head = Typeface.createFromAsset(getAssets(), "fonts/Roboto_Light.ttf");

        session = new SessionManager(this);

        setTitle("Confirm Location");
        progress = new MaterialDialog.Builder(this)
                .title("Fetching Data")
                .content("...")
                .progress(true, 0).build();


        dialog = new MaterialDialog.Builder(this)
                .title("alert")
                .content("...")
                .positiveText("OK")
                .onPositive(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(MaterialDialog dialog, DialogAction which) {
                        dialog.dismiss();
                    }
                })
                .build();

        loc = (TextView) findViewById(R.id.location);
        gpstxt = (TextView) findViewById(R.id.gps);
        tv1=(TextView)findViewById(R.id.location_txt);
        tv1.setTypeface(head);
        tv2=(TextView)findViewById(R.id.gps_location);
        tv2.setTypeface(head);


        gps = new GPSTracker(this);
        if (!gps.canGetLocation()) {
            gps.showSettingsAlert();
        } else {
            gps.getLocation();
            latitude_v = gps.getLatitude();
            longitude_v = gps.getLongitude();

            gpstxt.setText(String.valueOf(latitude_v) + "," + String.valueOf(longitude_v));
            loc.setText(getAddress(latitude_v, longitude_v));
            Log.d("MapsActivity", "Loc: " + gps.getLatitude() + " - " + gps.getLongitude());
        }

        setUpMapIfNeeded();


        TextView confirm = (TextView) findViewById(R.id.confirm);


        confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                session.storeVal("clat", String.valueOf(latitude_v));
                session.storeVal("clongd", String.valueOf(longitude_v));
                Intent i = new Intent(GrivencelocationImage.this, CaptureAndConfirm.class);
                startActivity(i);

            }
        });

    }


    @Override
    protected void onResume() {
        super.onResume();

        gps = new GPSTracker(this);
        if (!gps.canGetLocation()) {
            gps.showSettingsAlert();
        } else {
            gps.getLocation();
            latitude_v = gps.getLatitude();
            longitude_v = gps.getLongitude();

            gpstxt.setText(String.valueOf(latitude_v) + "," + String.valueOf(longitude_v));
            loc.setText(getAddress(latitude_v, longitude_v));
            Log.d("MapsActivity", "Loc: " + gps.getLatitude() + " - " + gps.getLongitude());
        }
        setUpMapIfNeeded();
    }

    private void setUpMapIfNeeded() {
        // Do a null check to confirm that we have not already instantiated the map.
        if (mMap == null) {
            // Try to obtain the map from the SupportMapFragment.
            //mMap = ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map));
            SupportMapFragment supportMapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
            supportMapFragment.getMapAsync(this);
            // Check if we were successful in obtaining the map.
        }
    }


    private void setUpMap() {
        //  mMap.addMarker(new MarkerOptions().position(new LatLng(0, 0)).title("Marker"));

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        mMap.setMyLocationEnabled(true);
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(latitude_v, longitude_v), 16));

        mMap.setPadding(0, 120, 20, 20);


        MarkerOptions markerOptions = new MarkerOptions().position(new LatLng(latitude_v, longitude_v)).title(
                "GPS Pointer").draggable(true);
        mMap.addMarker(markerOptions);

        mMap.setOnMyLocationButtonClickListener(new GoogleMap.OnMyLocationButtonClickListener() {
            @Override
            public boolean onMyLocationButtonClick() {
                gps = new GPSTracker(GrivencelocationImage.this);
                if (!gps.canGetLocation()) {
                    gps.showSettingsAlert();
                } else {
                    gps.getLocation();
                    latitude_v = gps.getLatitude();
                    longitude_v = gps.getLongitude();

                    gpstxt.setText(String.valueOf(latitude_v) + "," + String.valueOf(longitude_v));
                    loc.setText(getAddress(latitude_v, longitude_v));
                    Log.d("MapsActivity", "Loc: " + gps.getLatitude() + " - " + gps.getLongitude());


                    mMap.clear();
                    mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(latitude_v, longitude_v), 16));
                    mMap.addMarker(new MarkerOptions().position(new LatLng(latitude_v, longitude_v)).title(session.getStrVal("bid")));
                    Log.d("MapsActivity", "Loc: " + gps.getLatitude() + " - " + gps.getLongitude());
                }
                return false;
            }
        });

        mMap.setOnMarkerDragListener(new GoogleMap.OnMarkerDragListener() {

            @Override
            public void onMarkerDragStart(Marker marker) {
            }

            @Override
            public void onMarkerDragEnd(Marker marker) {
                Log.d("MapsActivity", "New Co-ordinates...latitude : " + marker.getPosition().latitude + "latitude : " + marker.getPosition().longitude);
                latitude_v = marker.getPosition().latitude;
                longitude_v = marker.getPosition().longitude;

                Toast.makeText(GrivencelocationImage.this, "New Marker Location\n" + getAddress(latitude_v, longitude_v), Toast.LENGTH_SHORT).show();

                gpstxt.setText(String.valueOf(latitude_v) + "," + String.valueOf(longitude_v));

                loc.setText(getAddress(latitude_v, longitude_v));


                marker.setSnippet(String.valueOf(marker.getPosition().latitude) + "," + String.valueOf(marker.getPosition().longitude));
                mMap.animateCamera(CameraUpdateFactory.newLatLng(marker.getPosition()));

            }

            @Override
            public void onMarkerDrag(Marker marker) {
            }

        });
    }

    private String getAddress(Double lat, Double longt) {
        String address = "na";

        Geocoder geo = new Geocoder(GrivencelocationImage.this);
        try {
            List<Address> addresses = geo.getFromLocation(lat, longt, 1);

            if (addresses != null && !addresses.isEmpty()) {
                Log.d("Address", "Address: " + addresses.get(0));

                address = "";
                Address ad = addresses.get(0);
                for (int i = 0; i < ad.getMaxAddressLineIndex(); i++) {
                    address += ad.getAddressLine(i);
                }

            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return address;
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
         mMap=googleMap;
        if (mMap != null) {
            setUpMap();
        }
    }
}




