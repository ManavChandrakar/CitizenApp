package com.pris.citizenapp.Fragments;

import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.pris.citizenapp.R;
import com.pris.citizenapp.adapters.Notice;
import com.pris.citizenapp.adapters.NoticeAdapter;
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
 * Created by manav on 16/4/17.
 */

public class Notification extends Fragment {
   Toolbar toolbar;
    private SessionManager session;

    MaterialDialog progress;
    RecyclerView recList;
    LinearLayoutManager llm;
    public int LIST_SIZE;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v=inflater.inflate(R.layout.myrecycleone,container,false);
        toolbar=(Toolbar)v.findViewById(R.id.toolbar);
        ((AppCompatActivity)getActivity()).setSupportActionBar(toolbar);
        getActivity().setTitle("Notification");
        return v;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        session  = new SessionManager(view.getContext());
        progress = new MaterialDialog.Builder(view.getContext())
                .title("Fetching Data")
                .content("...")
                .progress(true, 0).build();
        recList = (RecyclerView)  view.findViewById(R.id.myrecyclerView);
        recList.setHasFixedSize(true);

        llm = new LinearLayoutManager(view.getContext());
        llm.setOrientation(LinearLayoutManager.VERTICAL);
        recList.setLayoutManager(llm);

        if(session.haveNetworkConnection()){
            setupAdapter();

        }
        else if(session.hasVal("noticeList")) {
            NoticeAdapter ca = new NoticeAdapter(createList(),getActivity().getApplicationContext());
            recList.setAdapter(ca);
            ca.notifyDataSetChanged();

            //  progress.dismiss();
        }

    }



    private List<Notice> createList() {

        session.removeVal("infofrag");

        List<Notice> result = new ArrayList<Notice>();

        int li = 0;

        if(session.hasVal("noticeList")) {

            try {
                JSONArray places = new JSONArray(session.getStrVal("noticeList"));

                LIST_SIZE = places.length();

                if (LIST_SIZE > 0) {


                    for (int i = 0; i < places.length(); i++) {

                        li++;
                        JSONObject place = places.getJSONObject(i);
                        Notice ci = new Notice();

                        Log.d("Contact req", " TITLE: " + place.getString("title")+" date: " + place.getString("timestamp"));

                        // if(place.getString("category").equals(mParam1)) {

                        ci.uid = place.getString("unique_id");
                        ci.timestamp = place.getString("timestamp");
                        ci.title = place.getString("title");
                        ci.description = place.getString("description");
                        ci.link = place.getString("link");
                      //  ci.tag=place.getString("tag");
                      //  ci.download = place.getString("download");
                      //  ci.Iurl=place.getString("Image_url");
                        result.add(ci);
                        //}

                    }
                }


            } catch (JSONException e) {
                e.printStackTrace();
            }

        }
        else{


            Toast.makeText(getActivity().getApplicationContext(),"No Notifications Found",Toast.LENGTH_SHORT).show();

        }

        if(li == 0){
            new MaterialDialog.Builder(getActivity().getApplicationContext())
                    .title("Oops")
                    .content("No Notifications Found")
                    .positiveText("Go To Home")
                    .onPositive(new MaterialDialog.SingleButtonCallback() {
                        @Override
                        public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                            dialog.dismiss();
                            getActivity().finish();
                        }
                    }).show();

        }

        //Collections.reverse(result);

        return result;
    }

    private void setupAdapter() {

        if(session.haveNetworkConnection()){

            Map<String, String> params = new LinkedHashMap<String, String>();

            params.put("getNotices","true");
            params.put("username",session.getStrVal("username"));
            String deviceParams = "Device:"+ Build.DEVICE;
            deviceParams+=" - Display:"+Build.DISPLAY;
            deviceParams+=" - Manufacturer:"+Build.MANUFACTURER;
            deviceParams+=" - Model:"+ Build.MODEL;


            params.put("deviceinfo",deviceParams);

            new webService().execute(params);

        }
        else{
            Toast.makeText(getActivity().getApplicationContext(), "Please make sure that you are connected to an Active Internet connection!", Toast.LENGTH_SHORT).show();
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
            //  progress.show();
        }

        protected void onPostExecute(String response){


            JSONObject result = null;

            Log.d("webService", "HTTP Request Result: " + response);

            try {

                result = new JSONObject(response);

                String res = result.getString("result");

                //Log.d("HTTP result filesync",response.toString());

                if (res.trim().equals("success")) {


                    if(result.has("getNotices")){

                        session.storeVal("noticeList",result.getString("notices"));

                        NoticeAdapter ca = new NoticeAdapter(createList(),getActivity().getApplicationContext());
                        recList.setAdapter(ca);
                        ca.notifyDataSetChanged();

                    }
                    //Log.d("DB Sync","Updating Sync status:"+ret);
                }

                //   progress.dismiss();


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
