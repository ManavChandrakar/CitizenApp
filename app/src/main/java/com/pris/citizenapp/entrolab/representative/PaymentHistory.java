package com.pris.citizenapp.entrolab.representative;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.Toast;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.pris.citizenapp.R;
import com.pris.citizenapp.adapters.PayHistory;
import com.pris.citizenapp.adapters.PaymentAdapter;
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
 * Created by manav on 10/4/17.
 */

public class PaymentHistory extends AppCompatActivity {


    private SessionManager session;

    MaterialDialog progress;
    MaterialDialog dialog;
    LinearLayoutManager llm;
    public int LIST_SIZE;
    RecyclerView recList;
    Toolbar toolbar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment_history);

        toolbar=(Toolbar)findViewById(R.id.toolbar);
        toolbar.setTitle("Payment History");
        setSupportActionBar(toolbar);
        session  = new SessionManager(this);
        Typeface head = Typeface.createFromAsset(getAssets(), "fonts/RobotoCondensed_Bold.ttf");
        Typeface desc = Typeface.createFromAsset(getAssets(), "fonts/Roboto_Light.ttf");
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


        setTitle("Payments History");


        recList = (RecyclerView)  findViewById(R.id.recyclerView);
        recList.setHasFixedSize(true);

        llm = new LinearLayoutManager(this);
        llm.setOrientation(LinearLayoutManager.VERTICAL);
        recList.setLayoutManager(llm);



        if(session.haveNetworkConnection()){
            setupAdapter();

        }
    }



    private List<PayHistory> createList() {

        session.removeVal("infofrag");

        List<PayHistory> result = new ArrayList<PayHistory>();

        int li = 0;

        if(session.hasVal("recordsList")) {

            try {
                JSONArray places = new JSONArray(session.getStrVal("recordsList"));

                LIST_SIZE = places.length();

                if (LIST_SIZE > 0) {


                    for (int i = 0; i < places.length(); i++) {

                        li++;
                        JSONObject place = places.getJSONObject(i);
                        PayHistory ci = new PayHistory();


                        // if(place.getString("category").equals(mParam1)) {

                        ci.order_id = place.getString("order_id");
                        ci.assessment = place.getString("assessment");
                        ci.title = place.getString("title");
                        ci.taxtype = place.getString("taxtype");
                        ci.amount = place.getString("amount");
                        ci.dueyear = place.getString("dueyear");

                        ci.panchayat = place.getString("panchayat");
                        ci.timestamp = place.getString("timestamp");

                        result.add(ci);
                        //}

                    }
                }


            } catch (JSONException e) {
                e.printStackTrace();
            }

        }
        else{


            Toast.makeText(this,"No Records Found",Toast.LENGTH_SHORT).show();

        }



        //Collections.reverse(result);

        if(LIST_SIZE == 0){
            Toast.makeText(this,"No Records Found",Toast.LENGTH_SHORT).show();

        }

        return result;
    }


  /*  @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.



        if(session.isLoggedIn()){
            getMenuInflater().inflate(R.menu.login_menu, menu);


        }



        return true;
    }*/


  /*  @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if(item.getItemId() == R.id.sign_out) {

            session.logoutUser();
            Intent intent = new Intent(PaymentHistory.this, Login.class);
            startActivity(intent);

            finish();

        }

        else if(item.getItemId() == R.id.edit_profile){
            Intent intent = new Intent(PaymentHistory.this, MyProfile.class);
            startActivity(intent);

        }

        return super.onOptionsItemSelected(item);
    }*/

    private void setupAdapter(){

        if(session.haveNetworkConnection()){

            Map<String, String> params = new LinkedHashMap<String, String>();
            EditText search = (EditText) findViewById(R.id.search);

            params.put("getPaymentHistory","true");


            // params.put("search", String.valueOf(search.getText()));
           // params.put("username",session.getStrVal("username"));
            params.put("username","rajeessdf");

            if(session.hasVal("mandal"))
                params.put("mandal",session.getStrVal("mandal"));
            if(session.hasVal("division"))
                params.put("division",session.getStrVal("division"));
            if(session.hasVal("panchayat"))
                params.put("panchayat",session.getStrVal("panchayat"));
            if(session.hasVal("district"))
                params.put("district",session.getStrVal("district"));


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


            progress.show();

            super.onPreExecute();


        }

        protected void onPostExecute(String response){


            JSONObject result = null;

            Log.d("webService", "HTTP Request Result: " + response);

            try {

                result = new JSONObject(response);

                String res = result.getString("result");

                //Log.d("HTTP result filesync",response.toString());

                progress.dismiss();
                if (res.trim().equals("success")) {

                    if(result.has("getPaymentHistory")){

                        session.storeVal("recordsList",result.getString("records"));

                        PaymentAdapter ca = new PaymentAdapter(createList(),PaymentHistory.this);
                        recList.setAdapter(ca);
                        ca.notifyDataSetChanged();



                    }


                    //Log.d("DB Sync","Updating Sync status:"+ret);
                }
                else{
                    dialog.setTitle("Oops! Errors Found");
                    dialog.setContent(result.getString("error"));
                    dialog.show();
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

                response = HttpRequest.post(getResources().getString(R.string.url_taxes)).form(data).body();

            } catch (Exception e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }

            return response;



        }


    }
}
