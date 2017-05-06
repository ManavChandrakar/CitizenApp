package com.pris.citizenapp.adapters;

import android.content.Context;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Build;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.facebook.drawee.backends.pipeline.Fresco;
import com.pris.citizenapp.R;
import com.pris.citizenapp.common.SessionManager;
import com.pris.citizenapp.github.kevinsawicki.http.HttpRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by manav on 22/4/17.
 */

public class LinkedpptRemoveAdapter extends RecyclerView.Adapter<LinkedpptRemoveAdapter.FeedViewHolder> {

    private List<PropertyDatamodel> contactList;
    private Context context;

    private SessionManager session;
    MaterialDialog progress;
    MaterialDialog dialog;

    public LinkedpptRemoveAdapter(List<PropertyDatamodel> contactList, Context fm)
    {


        this.contactList = contactList;
        session = new SessionManager(fm);



        try {
            context = fm;
            progress = new MaterialDialog.Builder(fm)
                    .title("please wait...")
                    .content("...")
                    .progress(true, 0).build();


            dialog = new MaterialDialog.Builder(fm)
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

        }
        catch (NullPointerException e){
            e.printStackTrace();
        }
    }


    @Override
    public int getItemCount() {
        return contactList.size();
    }

    @Override
    public void onBindViewHolder(final LinkedpptRemoveAdapter.FeedViewHolder feedViewHolder, final int i) {

        final PropertyDatamodel ci = contactList.get(i);
        Typeface head = Typeface.createFromAsset(context.getAssets(), "fonts/Roboto_Light.ttf");
        Typeface desc = Typeface.createFromAsset(context.getAssets(), "fonts/Roboto-Thin.ttf");

        feedViewHolder.add.setText("Remove form linked properties list");
        feedViewHolder.add.setTypeface(desc);

        feedViewHolder.hid.setText(ci.hid);
        feedViewHolder.aadhar.setText(ci.aadhar);
        feedViewHolder.assessment.setText(ci.assessment_no);
        feedViewHolder.panchayat.setText(ci.panchayat);
        feedViewHolder.mandal.setText(ci.mandal);
        feedViewHolder.citizen.setText(ci.citizen);
        feedViewHolder.father.setText(ci.father_name);

        feedViewHolder.hid.setTypeface(head);
        feedViewHolder.assessment.setTypeface(head);
        feedViewHolder.hid_txt.setTypeface(desc);
        feedViewHolder.assessment_txt.setTypeface(desc);
        feedViewHolder.aadhar_txt.setTypeface(desc);
        feedViewHolder.aadhar.setTypeface(head);
        feedViewHolder.mandal_txt.setTypeface(desc);
        feedViewHolder.mandal.setTypeface(head);
        feedViewHolder.panchayat_txt.setTypeface(desc);
        feedViewHolder.panchayat.setTypeface(head);
        feedViewHolder.citizen.setTypeface(desc);
        feedViewHolder.father.setTypeface(desc);
        feedViewHolder.imvremove.setImageResource(R.drawable.ic_delete);

        feedViewHolder.imvremove.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (session.haveNetworkConnection()) {

                    Map<String, String> params = new LinkedHashMap<String, String>();

                    params.put("removeLinkedProperty", "true");
                    params.put("assessment_no", ci.assessment_no);
                    params.put("hid", ci.hid);
                    params.put("username", session.getStrVal("username"));
                    params.put("panchayat",session.getStrVal("panchayat"));


                    String deviceParams = "Device:" + Build.DEVICE;
                    deviceParams += " - Display:" + Build.DISPLAY;
                    deviceParams += " - Manufacturer:" + Build.MANUFACTURER;
                    deviceParams += " - Model:" + Build.MODEL;


                    params.put("deviceinfo", deviceParams);

                    new webService().execute(params);

                } else {
                    Toast.makeText(context, "Please make sure that you are connected to an Active Internet connection!", Toast.LENGTH_SHORT).show();
                }
            }
        });

    }

    @Override
    public LinkedpptRemoveAdapter.FeedViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {

        Fresco.initialize(viewGroup.getContext());



        View itemView = LayoutInflater.
                from(viewGroup.getContext()).
                inflate(R.layout.link, viewGroup, false);


        return new LinkedpptRemoveAdapter.FeedViewHolder(itemView);
    }

    public static class FeedViewHolder extends RecyclerView.ViewHolder {

        //   protected TextView uid;
        protected TextView hid;
        protected TextView hid_txt;

        protected TextView assessment;
        protected TextView assessment_txt;

        protected TextView citizen;
        protected TextView father;
        protected TextView aadhar;
        protected TextView aadhar_txt;
        protected TextView mandal_txt;
        protected TextView mandal;
        protected TextView panchayat_txt;
        protected TextView panchayat;
        protected TextView add;
        protected ImageView imvremove;

        protected RelativeLayout fullView;


        public FeedViewHolder(View v) {
            super(v);

            //     uid = (TextView)  v.findViewById(R.id.uid);
            hid = (TextView)  v.findViewById(R.id.hid);
            hid_txt = (TextView)  v.findViewById(R.id.hid_txt);
            assessment = (TextView)  v.findViewById(R.id.assessment);
            assessment_txt = (TextView)  v.findViewById(R.id.assessment_txt);
            citizen = (TextView)  v.findViewById(R.id.citizen);
            father = (TextView)  v.findViewById(R.id.father_name);
            aadhar = (TextView)  v.findViewById(R.id.aadhar);
            aadhar_txt = (TextView)  v.findViewById(R.id.aadhar_txt);
            panchayat_txt = (TextView)  v.findViewById(R.id.panchayat_txt);
            panchayat = (TextView)  v.findViewById(R.id.pptpanchayat);
            mandal = (TextView)  v.findViewById(R.id.pptmandal);
            mandal_txt = (TextView)  v.findViewById(R.id.mandal_txt);
            add=(TextView)v.findViewById(R.id.add);
            imvremove=(ImageView)v.findViewById(R.id.imvadd);
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


            //       progress.show();

            super.onPreExecute();


        }

        protected void onPostExecute(String response){


            JSONObject result = null;

            Log.d("webService", "HTTP Request Result: " + response);

            try {

                result = new JSONObject(response);

                String res = result.getString("result");

                //Log.d("HTTP result filesync",response.toString());
                //         progress.dismiss();
                if (res.trim().equals("success")) {

                    Toast.makeText(context,"Linked property to propertylist successfully",Toast.LENGTH_LONG).show();

                }
                //Log.d("DB Sync","Updating Sync status:"+ret);
                else{
                    Toast.makeText(context,result.getString("error"),Toast.LENGTH_LONG).show();
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

                response = HttpRequest.post(context.getResources().getString(R.string.url_district)).form(data).body();

            } catch (Exception e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }

            return response;



        }


    }

}

