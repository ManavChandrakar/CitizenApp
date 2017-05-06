package com.pris.citizenapp.entrolab;

import android.Manifest;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.pris.citizenapp.Certificates.BirthCft;
import com.pris.citizenapp.Certificates.DeathCft;
import com.pris.citizenapp.Certificates.MarriageCft;
import com.pris.citizenapp.Certificates.PropertyCerificate;
import com.pris.citizenapp.Certificates.TradeLicenceCft;
import com.pris.citizenapp.R;
import com.pris.citizenapp.adapters.CustomSwipeAdapter;
import com.pris.citizenapp.common.SessionManager;
import com.pris.citizenapp.entrolab.AdvertisementTax;
import com.pris.citizenapp.entrolab.Application.Grievance;
import com.pris.citizenapp.entrolab.Application.PrivateTapApply;
import com.pris.citizenapp.entrolab.Application.RTI;
import com.pris.citizenapp.entrolab.Application.VerifyAadhar;
import com.pris.citizenapp.entrolab.Auction;
import com.pris.citizenapp.entrolab.Kolagaaram;
import com.pris.citizenapp.entrolab.MainActivity;
import com.pris.citizenapp.entrolab.PrivateTap;
import com.pris.citizenapp.entrolab.PropertyTaxesParent;
import com.pris.citizenapp.entrolab.TradeLicenceTax;
import com.pris.citizenapp.entrolab.representative.ElectedWing;
import com.pris.citizenapp.entrolab.representative.News;
import com.pris.citizenapp.entrolab.representative.PublicServants;
import com.pris.citizenapp.entrolab.representative.YellowPage;
import com.pris.citizenapp.github.kevinsawicki.http.HttpRequest;
import com.pris.citizenapp.login.Login;
import com.pris.citizenapp.payments.CheckoutActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import pub.devrel.easypermissions.EasyPermissions;

import static com.pris.citizenapp.common.SessionManager.USER_FULL_NAME;

/**
 * Created by manav on 16/4/17.
 */

public class CitizenActivity extends Fragment implements EasyPermissions.PermissionCallbacks {


    //GCM
    private GoogleCloudMessaging gcm;
    private String regid;
    private String PROJECT_NUMBER = "1085132301476";


    ImageView news,Elected,publicservent,yellowPage,tax1,privatetap,kolagaaram,advertisement,trade,auction;
    ImageView grievance,rti,ptapsapply,property_certificate,marriage_certificate,birthcertificate,death_certificate,trade_certificate;
    Toolbar toolbar;
    TextView name,panchaayatname;
    LinearLayout layout;
    SessionManager session;
    ViewPager viewPager;
    CustomSwipeAdapter customSwipeAdapter;
    ArrayList<String>all;
    TextView tv1,tv2,tv3,tv4,tv01,tv02,tv03,tv04,tv05,tv06,tv07,tv08,tv09,tv10,tv11,tv12,tv13,tv14,tv15,tv16,tv17,tv18;

    final String[] perms = {Manifest.permission.INTERNET,Manifest.permission.ACCESS_NETWORK_STATE,Manifest.permission.WRITE_EXTERNAL_STORAGE,Manifest.permission.READ_EXTERNAL_STORAGE,Manifest.permission.WAKE_LOCK};


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.citizen_main,container,false);
    }

    @Override
    public void onViewCreated(View view, @Nullable final Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        session=new SessionManager(view.getContext());
        Typeface head = Typeface.createFromAsset(view.getContext().getAssets(), "fonts/Roboto_Light.ttf");

        toolbar=(Toolbar)view.findViewById(R.id.toolbar);
        tv2=(TextView)view.findViewById(R.id.tv2);
        tv3=(TextView)view.findViewById(R.id.tv3);
        tv4=(TextView)view.findViewById(R.id.tv4);
        tv01=(TextView)view.findViewById(R.id.tv01);
        tv02=(TextView)view.findViewById(R.id.tv02);
        tv03=(TextView)view.findViewById(R.id.tv03);
        tv04=(TextView)view.findViewById(R.id.tv04);
        tv05=(TextView)view.findViewById(R.id.tv05);
        tv06=(TextView)view.findViewById(R.id.tv06);
        tv07=(TextView)view.findViewById(R.id.tv07);
        tv08=(TextView)view.findViewById(R.id.tv08);
        tv09=(TextView)view.findViewById(R.id.tv09);
        tv10=(TextView)view.findViewById(R.id.tv10);
        tv11=(TextView)view.findViewById(R.id.tv11);
        tv12=(TextView)view.findViewById(R.id.tv12);
        tv13=(TextView)view.findViewById(R.id.tv13);
        tv14=(TextView)view.findViewById(R.id.tv14);
        tv15=(TextView)view.findViewById(R.id.tv15);
        tv16=(TextView)view.findViewById(R.id.tv16);
        tv17=(TextView)view.findViewById(R.id.tv17);
        tv18=(TextView)view.findViewById(R.id.tv18);

        tv2.setTypeface(head);
        tv3.setTypeface(head);
        tv4.setTypeface(head);
        tv01.setTypeface(head);
        tv02.setTypeface(head);
        tv03.setTypeface(head);
        tv04.setTypeface(head);
        tv05.setTypeface(head);
        tv06.setTypeface(head);
        tv07.setTypeface(head);
        tv08.setTypeface(head);
        tv09.setTypeface(head);
        tv10.setTypeface(head);
        tv11.setTypeface(head);
        tv12.setTypeface(head);
        tv13.setTypeface(head);
        tv14.setTypeface(head);
        tv15.setTypeface(head);
        tv16.setTypeface(head);
        tv17.setTypeface(head);
        tv18.setTypeface(head);


        //just for testing
        all=new ArrayList<String>();
        JSONArray jsonArray= null;
        try {
            jsonArray = new JSONArray(session.getStrVal("mybanner"));
            for(int i=0;i<jsonArray.length();i++)
            {
                all.add(jsonArray.getString(i));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }


      /*  all.add("http://pris.ap.gov.in/files/test.png");
        all.add("http://pris.ap.gov.in/files/test.png");
        all.add("http://pris.ap.gov.in/files/test.png");
        all.add("http://pris.ap.gov.in/files/test.png");*/
        //


        viewPager=(ViewPager)view.findViewById(R.id.pager);
        customSwipeAdapter=new CustomSwipeAdapter(view.getContext(),all);
        viewPager.setAdapter(customSwipeAdapter);

        TabLayout tabLayout = (TabLayout) view.findViewById(R.id.tab_layout);
        tabLayout.setupWithViewPager(viewPager, true);
        //setSupportActionBar(toolbar);
        //getSupportActionBar().setDisplayShowTitleEnabled(false);*/
        Elected=(ImageView)view.findViewById(R.id.elected);
        news=(ImageView)view.findViewById(R.id.news);
        publicservent=(ImageView)view.findViewById(R.id.publicservent);
        yellowPage=(ImageView)view.findViewById(R.id.yellow);
        layout=(LinearLayout)view.findViewById(R.id.ll);
        layout.setVisibility(View.VISIBLE);
        name=(TextView) view.findViewById(R.id.tooltext);
        panchaayatname=(TextView)view.findViewById(R.id.panchaayatname);
        name.setTypeface(head);
        panchaayatname.setTypeface(head);
        //set panchayat name
        panchaayatname.setText(session.getStrVal("panchayat_name"));
        panchaayatname.setTextSize(TypedValue.COMPLEX_UNIT_SP, 17);

        name.setText(session.getStrVal(USER_FULL_NAME));

        tax1=(ImageView)view.findViewById(R.id.tax1);
        privatetap=(ImageView)view.findViewById(R.id.privatetap);
        kolagaaram=(ImageView)view.findViewById(R.id.kolagaraam);
        advertisement=(ImageView)view.findViewById(R.id.advertisement);
        trade=(ImageView)view.findViewById(R.id.tradetax);
        auction=(ImageView)view.findViewById(R.id.auction);

        grievance=(ImageView)view.findViewById(R.id.grievance);
        rti=(ImageView)view.findViewById(R.id.rti);
        ptapsapply=(ImageView)view.findViewById(R.id.pvttaps);
        property_certificate=(ImageView)view.findViewById(R.id.propert_certificate);
        marriage_certificate=(ImageView)view.findViewById(R.id.marriagecft);
        birthcertificate=(ImageView)view.findViewById(R.id.birthcft);
        death_certificate=(ImageView)view.findViewById(R.id.deathcft);
        trade_certificate=(ImageView)view.findViewById(R.id.trade_licencectf);


        Elected.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i =new Intent(view.getContext(), ElectedWing.class);
                startActivity(i);
            }
        });


        news.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i =new Intent(view.getContext(), News.class);
                startActivity(i);
            }
        });

        publicservent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i =new Intent(view.getContext(), PublicServants.class);
                startActivity(i);
            }
        });

        yellowPage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i =new Intent(view.getContext(), YellowPage.class);
                startActivity(i);
            }
        });

        tax1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(view.getContext(),PropertyTaxesParent.class);
                startActivity(intent);
            }
        });

        privatetap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(view.getContext(),PrivateTap.class);
                startActivity(intent);
            }
        });

        kolagaaram.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(view.getContext(),Kolagaaram.class);
                startActivity(intent);
            }
        });

        advertisement.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(view.getContext(),AdvertisementTax.class);
                startActivity(intent);
            }
        });

        trade.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(view.getContext(),TradeLicenceTax.class);
                startActivity(intent);
            }
        });

        auction.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(view.getContext(),Auction.class);
                startActivity(intent);
            }
        });

        grievance.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent =new Intent(view.getContext(), Grievance.class);
                //Intent intent =new Intent(MainActivity.this, GrivencelocationImage.class);
                startActivity(intent);
            }
        });

        rti.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent =new Intent(view.getContext(), RTI.class);
                startActivity(intent);
            }
        });

        ptapsapply.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent =new Intent(view.getContext(), PrivateTapApply.class);
                startActivity(intent);
            }
        });

        property_certificate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(view.getContext(), PropertyCerificate.class);
                startActivity(intent);
            }
        });

        birthcertificate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(view.getContext(),BirthCft.class);
                 startActivity(intent);
            }
        });

        death_certificate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(view.getContext(),DeathCft.class);
                startActivity(intent);
            }
        });

        marriage_certificate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(view.getContext(),MarriageCft.class);
                startActivity(intent);
            }
        });

        trade_certificate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(view.getContext(),TradeLicenceCft.class);
                startActivity(intent);
            }
        });


        if(methodRequiresPermission(perms,111)) {

            getRegId();
        }
        else{
            Toast.makeText(view.getContext(),"Please Grant required app permissions!!",Toast.LENGTH_SHORT).show();
        }

    }

    public boolean methodRequiresPermission(String[] perms, int permission) {

        if (EasyPermissions.hasPermissions(getContext(), perms)) {
            // Already have permission, do the thing
            // ...
            return true;
        } else {
            // Do not have permissions, request them now
            EasyPermissions.requestPermissions(this, "Need these permissions",
                    permission, perms);
        }

        return false;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        EasyPermissions.onRequestPermissionsResult(requestCode,permissions,grantResults,this);

    }


    @Override
    public void onPermissionsGranted(int requestCode, List<String> perms) {

        if(requestCode==111)
        {
            getRegId();
        }

    }

    @Override
    public void onPermissionsDenied(int requestCode, List<String> perms) {
        Toast.makeText(getContext(), "Please Grant required app permissions!!", Toast.LENGTH_SHORT).show();

    }

    public void getRegId() {

        new AsyncTask<Void, Void, String>() {
            @Override
            protected String doInBackground(Void... params) {
                String msg = "";
                try {
                    if (gcm == null) {
                        gcm = GoogleCloudMessaging.getInstance(getActivity().getApplicationContext());
                    }
                    regid = gcm.register(PROJECT_NUMBER);
                    msg = "Device registered, registration ID=" + regid;


                    Log.i("GCM", msg);

                    session.storeVal("GCMid", regid);

                } catch (IOException ex) {
                    msg = "Error :" + ex.getMessage();

                }
                return msg;
            }

            @Override
            protected void onPostExecute(String msg) {
                Log.d("SplashScren", msg);


                if (session.haveNetworkConnection()) {

                    Map<String, String> params = new LinkedHashMap<String, String>();

                    params.put("storeGCM", "true");
                    params.put("regid", regid);
                    params.put("username", session.getStrVal("username"));

                    String deviceParams = "Device:" + Build.DEVICE;
                    deviceParams += " - Display:" + Build.DISPLAY;
                    deviceParams += " - Manufacturer:" + Build.MANUFACTURER;
                    deviceParams += " - Model:" + Build.MODEL;


                    params.put("deviceinfo", deviceParams);

                    new webService().execute(params);
                }
            }
        }.execute(null, null, null);
    }

    private class webService extends AsyncTask<Map, Integer, String>

    {


        @Override
        protected String doInBackground(Map... params) {

            return postData(params[0]);
        }

        @Override
        protected void onPreExecute() {


            //  progress.show();

            super.onPreExecute();


        }

        protected void onPostExecute(String response){


            JSONObject result = null;

            Log.d("webService", "HTTP Request Result: " + response);

            try {

                result = new JSONObject(response);

                String res = result.getString("result");

                //Log.d("HTTP result filesync",response.toString());

                if (res.trim().equals("success")) {


                    //  progress.dismiss();


                    //Log.d("DB Sync","Updating Sync status:"+ret);
                }



            } catch (JSONException e) {
                e.printStackTrace();
            }


        }
        protected void onProgressUpdate(Integer... progress){
        }

        public String postData(Map data) {

            String response = "{\"result\":\"failed\"}";

            try {

                response = HttpRequest.post(getResources().getString(R.string.url_district)).form(data).body();

            } catch (Exception e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }

            return response;



        }


    }


}

