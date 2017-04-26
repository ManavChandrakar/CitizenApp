package com.pris.citizenapp.login;

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

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.pris.citizenapp.Fragments.LinkProperty;
import com.pris.citizenapp.R;
import com.pris.citizenapp.adapters.Linkpptadapter;
import com.pris.citizenapp.adapters.PropertyDatamodel;
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
 * Created by manav on 22/4/17.
 */

public class LinkedProperty extends AppCompatActivity {

    Toolbar toolbar;

    private SessionManager session;

    MaterialDialog progress;
    MaterialDialog dialog;
    LinearLayoutManager llm;
    public int LIST_SIZE;
    RecyclerView recList;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        toolbar=(Toolbar)findViewById(R.id.toolbar);
        toolbar.setTitle("Linked Properties");
        setSupportActionBar(toolbar);

        session = new SessionManager(this);
        setupAdapter();

        progress = new MaterialDialog.Builder(this)
                .title("Loading...")
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
                .contentColor(getResources().getColor(R.color.appcolor)).build();


        //setTitle("Property Tax");

        //TextView notifications = (TextView) view.findViewById(R.id.notifications);
        recList = (RecyclerView)findViewById(R.id.recyclerView);
        recList.setHasFixedSize(true);

        llm = new LinearLayoutManager(this);
        llm.setOrientation(LinearLayoutManager.VERTICAL);
        recList.setLayoutManager(llm);
    }

    private List<PropertyDatamodel> createList() {

        session.removeVal("infofrag");

        List<PropertyDatamodel> result = new ArrayList<PropertyDatamodel>();

        int li = 0;

        if (session.hasVal("recordsList")) {

            try {
                JSONArray places = new JSONArray(session.getStrVal("recordsList"));

                LIST_SIZE = places.length();

                if (LIST_SIZE > 0) {


                    for (int i = 0; i < places.length(); i++) {

                        li++;
                        JSONObject place = places.getJSONObject(i);
                        PropertyDatamodel ci = new PropertyDatamodel();


                        // if(place.getString("category").equals(mParam1)) {

                        ci.hid = place.getString("hid");
                        ci.citizen = place.getString("full_name");
                        ci.father_name = place.getString("father_name");
                        ci.aadhar = place.getString("aadhar");
                        ci.panchayat = place.getString("panchayat");
                        ci.assessment_no = place.getString("assessment_no");
                        ci.door_no=place.getString("door_no");
                        ci.mandal=place.getString("mandal");
                        result.add(ci);
                        //}

                    }
                }


            } catch (JSONException e) {
                e.printStackTrace();
            }

        } else {


            Toast.makeText(this, "No Records Found", Toast.LENGTH_SHORT).show();

        }


        //Collections.reverse(result);

        if (LIST_SIZE == 0) {
            Toast.makeText(this, "No Records Found", Toast.LENGTH_SHORT).show();

        }

        return result;
    }


    private void setupAdapter() {

        if (session.haveNetworkConnection()) {

            Map<String, String> params = new LinkedHashMap<String, String>();
            params.put("getLinkedProperties", "true");
            params.put("username", session.getStrVal("username"));
            params.put("panchayat", session.getStrVal("panchayat"));
            
            String deviceParams = "Device:" + Build.DEVICE;
            deviceParams += " - Display:" + Build.DISPLAY;
            deviceParams += " - Manufacturer:" + Build.MANUFACTURER;
            deviceParams += " - Model:" + Build.MODEL;


            params.put("deviceinfo", deviceParams);

            new webService().execute(params);

        } else {
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


            progress.show();

            super.onPreExecute();


        }

        protected void onPostExecute(String response) {


            JSONObject result = null;

            Log.d("webService", "HTTP Request Result: " + response);

            try {

                result = new JSONObject(response);

                String res = result.getString("result");

                //Log.d("HTTP result filesync",response.toString());
                progress.dismiss();
                if (res.trim().equals("success")) {

                    if (result.has("searchProperty")) {

                        session.storeVal("recordsList", result.getString("records"));

                        Linkpptadapter ca = new Linkpptadapter(createList(), LinkedProperty.this);
                        recList.setAdapter(ca);
                        ca.notifyDataSetChanged();


                    }


                    //Log.d("DB Sync","Updating Sync status:"+ret);
                } else {
                    dialog.setTitle("Oops! Errors Found");
                    dialog.setContent(result.getString("error"));
                    dialog.show();
                }


            } catch (JSONException e) {
                e.printStackTrace();
            }


        }

        protected void onProgressUpdate(Integer... progress) {
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
