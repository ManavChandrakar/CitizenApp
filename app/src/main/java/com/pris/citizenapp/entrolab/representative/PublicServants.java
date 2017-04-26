package com.pris.citizenapp.entrolab.representative;

/**
 * Created by manav on 9/4/17.
 */
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.widget.Toast;

import com.afollestad.materialdialogs.MaterialDialog;
import com.pris.citizenapp.R;
import com.pris.citizenapp.adapters.MemberAdapter;
import com.pris.citizenapp.adapters.MemberModel;
import com.pris.citizenapp.common.SessionManager;
import com.pris.citizenapp.github.kevinsawicki.http.HttpRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by manav on 8/4/17.
 */

public class PublicServants extends AppCompatActivity {
    Toolbar toolbar;
    private SessionManager session;

    MaterialDialog progress;
    RecyclerView recList;
    LinearLayoutManager llm;
    public int LIST_SIZE;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.member_recycle);
        LIST_SIZE=0;
        toolbar=(Toolbar)findViewById(R.id.toolbar);
        toolbar.setTitle("Public Servants");
        setSupportActionBar(toolbar);

        session  = new SessionManager(this);

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
        else if(session.hasVal("publicservants")) {
            MemberAdapter ca = new MemberAdapter(createList(),this);
            recList.setAdapter(ca);
            ca.notifyDataSetChanged();

            //  progress.dismiss();
        }

    }

    private List<MemberModel> createList() {

        List<MemberModel> result = new ArrayList<MemberModel>();

        int li = 0;

        if(session.hasVal("publicservants")) {

            try {
                JSONArray places = new JSONArray(session.getStrVal("publicservants"));

                LIST_SIZE = places.length();

                if (LIST_SIZE > 0) {


                    for (int i = 0; i < places.length(); i++) {

                        li++;
                        JSONObject place = places.getJSONObject(i);
                        MemberModel ci = new MemberModel();

                        ci.unique_id = place.getString("unique_id");
                        ci.name = place.getString("name");
                        ci.address = place.getString("address");
                        ci.contact = place.getString("contact");
                        ci.designation = place.getString("designation");
                        ci.email = place.getString("email");
                        ci.image = place.getString("image");

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

            params.put("getPublicServants","true");
            params.put("panchayat",session.getStrVal("panchayat"));
//            params.put("username",session.getStrVal("username"));


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

                String res = result.getString("result");

                //Log.d("HTTP result filesync",response.toString());

                if (res.trim().equals("success")) {


                    if(result.has("publicservants")){

                        session.storeVal("publicservants",result.getString("publicservants"));

                        MemberAdapter ca = new MemberAdapter(createList(), PublicServants.this);
                        recList.setAdapter(ca);
                        ca.notifyDataSetChanged();



                    }



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

