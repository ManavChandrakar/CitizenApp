package com.pris.citizenapp.entrolab.Application;

import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.view.ViewCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.afollestad.materialdialogs.MaterialDialog;
import com.pris.citizenapp.R;
import com.pris.citizenapp.adapters.GrievanceModel;
import com.pris.citizenapp.adapters.GrievancedAdapter;
import com.pris.citizenapp.adapters.MemberAdapter;
import com.pris.citizenapp.adapters.MemberModel;
import com.pris.citizenapp.adapters.Replies;
import com.pris.citizenapp.common.SessionManager;
import com.pris.citizenapp.entrolab.representative.ElectedWing;
import com.pris.citizenapp.github.kevinsawicki.http.HttpRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by manav on 11/4/17.
 */

public class Grievance extends AppCompatActivity {

    private SessionManager session;

    MaterialDialog progress;
    RecyclerView recList;
    LinearLayoutManager llm;
    public int LIST_SIZE;
    Toolbar toolbar;
    FloatingActionButton fab;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fab);
        LIST_SIZE=0;
        toolbar=(Toolbar)findViewById(R.id.toolbar);
        toolbar.setTitle("Greviences");
        setSupportActionBar(toolbar);

        session  = new SessionManager(this);
        fab=(FloatingActionButton)findViewById(R.id.fab);

        if (Build.VERSION.SDK_INT == Build.VERSION_CODES.LOLLIPOP) {
            fab.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#eb6e04")));
        } else {
            ViewCompat.setBackgroundTintList(fab,ColorStateList.valueOf(Color.parseColor("#eb6e04")));
        }

        progress = new MaterialDialog.Builder(this)
                .title("Fetching Data")
                .content("...")
                .progress(true, 0).build();

        recList = (RecyclerView)  findViewById(R.id.myrecyclerView);
        recList.setHasFixedSize(true);

        llm = new LinearLayoutManager(this);
        llm.setOrientation(LinearLayoutManager.VERTICAL);
        recList.setLayoutManager(llm);


        if(session.haveNetworkConnection()){
            setupAdapter();

        }
        else if(session.hasVal("issues")) {
            GrievancedAdapter ca = new GrievancedAdapter (createList(),this);
            recList.setAdapter(ca);
            ca.notifyDataSetChanged();

            //  progress.dismiss();
        }

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent =new Intent(Grievance.this,GrivencelocationImage.class);
                startActivity(intent);
            }
        });

    }


    private List<GrievanceModel> createList() {

        List<GrievanceModel> result = new ArrayList<GrievanceModel>();

        int li = 0;

        if(session.hasVal("issues")) {

            try {
                JSONArray places = new JSONArray(session.getStrVal("issues"));

                LIST_SIZE = places.length();

                if (LIST_SIZE > 0) {


                    for (int i = 0; i < places.length(); i++) {

                        li++;
                        JSONObject place = places.getJSONObject(i);
                        GrievanceModel ci = new GrievanceModel();

                        ci.grievance_id = place.getString("grievance_id");
                        ci.name = place.getString("name");
                        ci.category = place.getString("category");
                        ci.sub_category = place.getString("sub_category");
                        ci.timestamp = place.getString("timestamp");
                        ci.email = place.getString("email");
                        ci.details= place.getString("details");
                        ci.landmark=place.getString("landmark");
                        ci.lat=place.getString("lat");
                        ci.lng=place.getString("lng");
                        ci.images=place.getString("images");
                        ci.panchayat=place.getString("panchayat");
                        ci.status=place.getString("status");
                        ci.mobile=place.getString("mobile");
                        ci.location=place.getString("location");

                        JSONArray arr =place.getJSONArray("replies");
                        ArrayList<Replies> midres = new ArrayList<Replies>();

                        for(int j=0;j<arr.length();j++)
                        {
                            JSONObject json=arr.getJSONObject(j);
                            Replies rep=new Replies();
                            rep.grievance_id=json.getString("grievance_id");
                            rep.id=json.getString("id");
                            rep.reply=json.getString("reply");
                            rep.username=json.getString("username");
                            rep.status=json.getString("status");
                            rep.timestamp=json.getString("timestamp");
                            midres.add(rep);
                        }
                        ci.replies=midres;

                        result.add(ci);

                    }
                }


            } catch (JSONException e) {
                e.printStackTrace();
            }

        }
        else{


            Toast.makeText(this,"No Updates Found",Toast.LENGTH_SHORT).show();

        }

        if(li == 0){
            Toast.makeText(this,"No Updates Found",Toast.LENGTH_SHORT).show();

        }

        //Collections.reverse(result);

        return result;
    }

    private void setupAdapter() {
        if(session.haveNetworkConnection()){

            Map<String, String> params = new LinkedHashMap<String, String>();

            params.put("getGrievances","true");
            params.put("panchayat",session.getStrVal("panchayat"));
            params.put("username",session.getStrVal("username"));


            String deviceParams = "Device:"+ Build.DEVICE;
            deviceParams+=" - Display:"+Build.DISPLAY;
            deviceParams+=" - Manufacturer:"+Build.MANUFACTURER;
            deviceParams+=" - Model:"+ Build.MODEL;


            params.put("deviceinfo",deviceParams);

            new webService().execute(params);

        }
        else{
            Toast.makeText(this, "Please make sure that you are connected to an Active Internet connection!", Toast.LENGTH_SHORT).show();
        }
    }


    private class webService extends AsyncTask<Map, Integer, String>

    {


        @Override
        protected String doInBackground(Map... params) {

            return postData(params[0]);
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            progress.show();
        }

        protected void onPostExecute(String response){


            JSONObject result = null;

            Log.d("webService", "HTTP Request Result: " + response);

            try {

                result = new JSONObject(response);

               // String res = result.getString("result");

                //Log.d("HTTP result filesync",response.toString());

               //

                    if(result.has("issues")){

                        session.storeVal("issues",result.getString("issues"));

                        GrievancedAdapter ca = new GrievancedAdapter(createList(),Grievance.this);
                        recList.setAdapter(ca);
                        ca.notifyDataSetChanged();




                    //Log.d("DB Sync","Updating Sync status:"+ret);
                }

                progress.dismiss();


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
