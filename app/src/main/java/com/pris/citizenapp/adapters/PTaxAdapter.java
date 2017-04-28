package com.pris.citizenapp.adapters;

/**
 * Created by manav on 10/4/17.
 */

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Build;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.facebook.drawee.backends.pipeline.Fresco;
import com.pris.citizenapp.R;
import com.pris.citizenapp.common.SessionManager;
import com.pris.citizenapp.entrolab.MainActivity;
import com.pris.citizenapp.entrolab.representative.PaymentHistory;
import com.pris.citizenapp.github.kevinsawicki.http.HttpRequest;
import com.pris.citizenapp.login.Register;
import com.pris.citizenapp.payments.CheckoutActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class PTaxAdapter extends RecyclerView.Adapter<PTaxAdapter.FeedViewHolder> {

    private List<PTaxEntry> contactList;
    private Context context;

    private SessionManager session;
    MaterialDialog progress;
    MaterialDialog dialog;

    public PTaxAdapter(List<PTaxEntry> contactList, Context fm)
    {


        this.contactList = contactList;
        session = new SessionManager(fm);



        try {
            context = fm;
            progress = new MaterialDialog.Builder(fm)
                    .title("Loading...")
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
    public void onBindViewHolder(final FeedViewHolder feedViewHolder, int i) {


        PTaxEntry ci = contactList.get(i);

        //   feedViewHolder.uid.setText(ci.uid);



        Typeface head = Typeface.createFromAsset(context.getAssets(), "fonts/Roboto_Light.ttf");
        Typeface desc = Typeface.createFromAsset(context.getAssets(), "fonts/Roboto-Thin.ttf");

        feedViewHolder.hid.setTypeface(head);
        feedViewHolder.assessment.setTypeface(head);
        feedViewHolder.hid_txt.setTypeface(desc);
        feedViewHolder.assessment_txt.setTypeface(desc);
        feedViewHolder.aadhar_txt.setTypeface(desc);
        feedViewHolder.aadhar.setTypeface(head);

        feedViewHolder.citizen.setTypeface(desc);
        feedViewHolder.father.setTypeface(desc);



        feedViewHolder.hid.setText(ci.hid);
        feedViewHolder.assessment.setText(ci.assessment);
        feedViewHolder.citizen.setText(ci.citizen);
        feedViewHolder.father.setText("S/W/o "+ci.father_name);
        feedViewHolder.aadhar.setText(ci.aadhar);



        try {
            JSONArray dues = new JSONArray(ci.duelist);


            if(dues.length() > 0){

                String[] dueamounts = new String[dues.length()];
                final String[] dueyears = new String[dues.length()];
                String[] duegen = new String[dues.length()];
                JSONArray jsonArray=new JSONArray();
                for(int k=0; k < dues.length();k++) {

                    JSONObject due = dues.getJSONObject(k);

                    dueamounts[k] = due.getString("pay_amount");
                    dueyears[k] =due.getString("due_year");
                    duegen[k] =due.getString("generated");
                    jsonArray.put(due.getString("due_year"));

                }

                session.storeVal("jsonArray",jsonArray.toString());


                final String[] damounts = dueamounts;
                final String[] dyears = dueyears;
                final String[] dgenerated = duegen;


                LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                View v;
                feedViewHolder.duelist.removeAllViews();
                for(int k=0; k < dues.length();k++){

                    JSONObject due = dues.getJSONObject(k);

                    View row_view = (View) inflater.inflate(R.layout.dues_layout,null);


                    CheckBox due_year = (CheckBox) row_view.findViewById(R.id.due_year);
                    TextView due_amount = (TextView) row_view.findViewById(R.id.due_amount);

                    due_year.setTypeface(desc);
                    due_amount.setTypeface(head);

                    due_year.setText(due.getString("due_year"));
                    due_amount.setText(due.getString("pay_amount"));


                    final String generated = due.getString("generated");

                    due_year.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                        @Override
                        public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                            JSONObject dues_selected = null;

                            try {
                                if (session.hasVal("dues_selected")) {
                                    dues_selected = new JSONObject(session.getStrVal("dues_selected"));
                                }
                                else{
                                    dues_selected = new JSONObject();
                                }
                                String year = String.valueOf(compoundButton.getText());




                                if (b) {

                                    Log.d("Checked", "year " + compoundButton.getText());
                                    dues_selected.put(year, getSubCode(year, damounts, dyears));
                                    feedViewHolder.final_total += Integer.parseInt(getSubCode(year, damounts, dyears));



                                }
                                else{
                                    Log.d("Checked", "year " + compoundButton.getText());

                                    if(dues_selected.has(year)) {
                                        dues_selected.remove(year);
                                        feedViewHolder.final_total -= Integer.parseInt(getSubCode(year, damounts, dyears));


                                    }

                                }

                                session.storeVal("dues_selected",dues_selected.toString());

                               // Toast.makeText(context,dues_selected.toString(),Toast.LENGTH_LONG).show();

                                if(checkDues(dueyears,dgenerated) > 0){
                                    feedViewHolder.paynow.setVisibility(View.VISIBLE);
                                  //  feedViewHolder.issue_demand.setVisibility(View.VISIBLE);

                                }
                                else{
                                    feedViewHolder.paynow.setVisibility(View.VISIBLE);
                                   // feedViewHolder.issue_demand.setVisibility(View.GONE);
                                }



                                feedViewHolder.total.setText(String.valueOf(feedViewHolder.final_total));



                            }   catch (Exception e){
                                e.printStackTrace();
                            }
                        }
                    });
                    feedViewHolder.duelist.addView(row_view);

                }

            }
            else{
                feedViewHolder.paynow.setVisibility(View.GONE);
                feedViewHolder.duelist.setVisibility(View.GONE);

                feedViewHolder.total.setVisibility(View.GONE);
                feedViewHolder.total_txt.setText("No Dues Found!");
                feedViewHolder.total_txt.setGravity(Gravity.CENTER_HORIZONTAL);
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }



        //Differentiate Taxes


        if(session.getStrVal("pay_tax").equals("1")){

            session.storeVal("pay_purpose","Property Tax for Assessment No."+ci.assessment);

        }
        else if(session.getStrVal("pay_tax").equals("2")){
            session.storeVal("pay_purpose","Kolagaaram Tax for Sanction No."+ci.assessment);
            feedViewHolder.assessment_txt.setText("Sanction No");

        }
        else if(session.getStrVal("pay_tax").equals("3")){
            session.storeVal("pay_purpose","Advertisement Tax for Sanction No."+ci.assessment);

            feedViewHolder.assessment_txt.setText("Sanction No");
        }
        else if(session.getStrVal("pay_tax").equals("4")){
            session.storeVal("pay_purpose","Trade Licence Fee for HT Assessment No."+ci.assessment);

            feedViewHolder.assessment_txt.setText("HT Assessment No");
        }
        else if(session.getStrVal("pay_tax").equals("5")){
            session.storeVal("pay_purpose","Auctions amount for Item No."+ci.assessment);

            feedViewHolder.assessment_txt.setText("Item No");
        }
        else if(session.getStrVal("pay_tax").equals("6")){
            session.storeVal("pay_purpose","Private Tap Fee for Assessment No."+ci.assessment);


        }





        final PTaxEntry dat = ci;
        feedViewHolder.paynow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(feedViewHolder.final_total > 0){


                    session.storeVal("pay_amount",String.valueOf(feedViewHolder.final_total));
                    session.storeVal("pay_name",dat.citizen);
                    session.storeVal("pay_email",dat.aadhar);
                    session.storeVal("pay_mobile",dat.mobile);
                    session.storeVal("pay_assessment",dat.assessment);
                    session.storeVal("pay_hid",dat.hid);
                    session.storeVal("father",dat.father_name);


                    Intent i = new Intent(context, CheckoutActivity.class);
                    i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    context.startActivity(i);

                }
                else{
                    Toast.makeText(context,"Please select atleast one Due to pay",Toast.LENGTH_SHORT).show();
                }
            }
        });



        feedViewHolder.issue_demand.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(feedViewHolder.final_total > 0){

                    Map<String, String> params = new LinkedHashMap<String, String>();

                    params.put("issueDemand","true");
                    params.put("pay_assessment",dat.assessment);
                    params.put("pay_hid",dat.hid);
                    params.put("pay_mobile",dat.mobile);
                    params.put("pay_email",dat.aadhar);
                    params.put("pay_name",dat.citizen);
                    params.put("dues_selected",session.getStrVal("dues_selected"));
                    params.put("username",session.getStrVal("username"));
                    params.put("pay_tax",session.getStrVal("pay_tax"));

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

                    new  webService().execute(params);



                }
                else{
                    Toast.makeText(context,"Please select atleast one year to issue demand",Toast.LENGTH_SHORT).show();
                }
            }
        });




    }


    public int checkDues(String[] year,String[] gen){


        int generate = 0;
        if(session.hasVal("dues_selected"))

            try {
                JSONObject dues = new JSONObject(session.getStrVal("dues_selected"));

                for(int j=0;j < year.length;j++){
                    if(dues.has(year[j])){

                        if(gen[j].equals("0")){

                            generate++;
                        }

                    }

                }
            } catch (JSONException e) {
                e.printStackTrace();
            }


        return generate;

    }

    public String getSubCode(String code, String[] subcodes, String [] subs) {
        int i = -1;
        for (String cc: subs) {
            i++;
            if (cc.equals(code))
                break;
        }
        return subcodes[i];
    }

    @Override
    public FeedViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {

        Fresco.initialize(viewGroup.getContext());



        View itemView = LayoutInflater.
                from(viewGroup.getContext()).
                inflate(R.layout.tax_card, viewGroup, false);


        return new FeedViewHolder(itemView);
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
        protected TextView total;
        protected TextView total_txt;

        protected LinearLayout duelist;
        protected TextView paynow;

        protected int final_total;

        protected TextView issue_demand;


        protected RelativeLayout fullView;


        public FeedViewHolder(View v) {
            super(v);

            //     uid = (TextView)  v.findViewById(R.id.uid);
            hid = (TextView)  v.findViewById(R.id.hid);
            hid_txt = (TextView)  v.findViewById(R.id.hid_txt);
            assessment = (TextView)  v.findViewById(R.id.assessment);
            assessment_txt = (TextView)  v.findViewById(R.id.assessment_txt);
            citizen = (TextView)  v.findViewById(R.id.citizen);


            final_total = 0;
            father = (TextView)  v.findViewById(R.id.father_name);
            aadhar = (TextView)  v.findViewById(R.id.aadhar);
            aadhar_txt = (TextView)  v.findViewById(R.id.aadhar_txt);
            total = (TextView)  v.findViewById(R.id.total);
            total_txt = (TextView)  v.findViewById(R.id.total_txt);

            duelist = (LinearLayout)  v.findViewById(R.id.duelist);
            paynow = (TextView)  v.findViewById(R.id.pay_now);
            issue_demand = (TextView)  v.findViewById(R.id.issue_demand);



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

                    if(result.has("issueDemand")){

                        dialog.setTitle("Demand Issued");
                        dialog.setContent(result.getString("message"));
                        dialog.show();
                        dialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
                            @Override
                            public void onDismiss(DialogInterface dialogInterface) {
                                Intent i = new Intent(context, MainActivity.class);
                                i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

                                context.startActivity(i);
                            }
                        });


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

                response = HttpRequest.post(context.getResources().getString(R.string.url_taxes)).form(data).body();

            } catch (Exception e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }

            return response;



        }


    }

}
