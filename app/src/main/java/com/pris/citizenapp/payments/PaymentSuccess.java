package com.pris.citizenapp.payments;

import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.pris.citizenapp.R;
import com.pris.citizenapp.common.SessionManager;
import com.pris.citizenapp.entrolab.FooterMain;
import com.pris.citizenapp.github.kevinsawicki.http.HttpRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Created by manav on 26/4/17.
 */

public class PaymentSuccess extends AppCompatActivity {


    private MaterialDialog progress;
    private SessionManager session;
    public MaterialDialog dialog;
    LinearLayout due_list;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment_success);

        session  = new SessionManager(this);

        Typeface head = Typeface.createFromAsset(getAssets(), "fonts/Roboto_Light.ttf");
        Typeface desc = Typeface.createFromAsset(getAssets(), "fonts/Roboto-Thin.ttf");

        setTitle("Payment Successful");

        due_list=(LinearLayout)findViewById(R.id.duelist);
        TextView success_msg = (TextView) findViewById(R.id.success_message);
        success_msg.setTypeface(head);
        TextView order_id = (TextView) findViewById(R.id.transaction);
        order_id.setTypeface(head);
        TextView bank_transaction = (TextView) findViewById(R.id.bank_transaction);
        bank_transaction.setTypeface(head);
        TextView amount = (TextView) findViewById(R.id.amount);
        amount.setTypeface(head);
        TextView name = (TextView) findViewById(R.id.billing_name);
        name.setTypeface(head);
        TextView email = (TextView) findViewById(R.id.billing_email);
        email.setTypeface(head);
        TextView mobile = (TextView) findViewById(R.id.billing_mobile);
        mobile.setTypeface(head);
        TextView purpose = (TextView) findViewById(R.id.billing_purpose);
        purpose.setTypeface(head);

        Button gohome = (Button) findViewById(R.id.button_back);
        gohome.setTypeface(head);

        gohome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(PaymentSuccess.this, FooterMain.class);
                startActivity(intent);
                finish();


            }
        });
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



        //inflate the layout for yearwise billdetails
        LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View v;

        try {
            JSONObject jsonObject=new JSONObject(session.getStrVal("dues_selected"));
            JSONArray jsonArray=new JSONArray(session.getStrVal("jsonArray"));

            for(int i=0;i<jsonArray.length();i++)
            {
                if(jsonObject.has(jsonArray.getString(i)))
                {

                    View row_view = (View) inflater.inflate(R.layout.linflate_payment_success,null);


                    TextView due_year = (TextView) row_view.findViewById(R.id.due_year);
                    TextView due_amount = (TextView) row_view.findViewById(R.id.due_amount);
                    due_amount.setTypeface(head);


                    due_year.setText(jsonArray.getString(i));
                    due_year.setTypeface(head);
                    due_amount.setText(jsonObject.getString(jsonArray.getString(i)));
                    due_list.addView(row_view);
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }


        order_id.setText(session.getStrVal("MerchantTransactionIdentifier"));
        bank_transaction.setText(session.getStrVal("BankReferenceIdentifier"));

        amount.setText(session.getStrVal("pay_amount"));
        name.setText(session.getStrVal("pay_name"));
        email.setText(session.getStrVal("payment_mail"));
        mobile.setText(session.getStrVal("pay_mobile"));
        purpose.setText(session.getStrVal("pay_purpose"));

        success_msg.setText("Payment is Successful!\n Payment Transaction ID: #"+session.getStrVal("MerchantTransactionIdentifier"));



        if(session.haveNetworkConnection()){

            Map<String, String> params = new LinkedHashMap<String, String>();

            params.put("confirmPayment","true");
            //Tax Type Here
            params.put("pay_tax",session.getStrVal("pay_tax"));
            params.put("pay_mobile", session.getStrVal("pay_mobile"));
            params.put("pay_amount", session.getStrVal("pay_amount"));
            params.put("pay_email", session.getStrVal("pay_email"));
            params.put("pay_purpose", session.getStrVal("pay_purpose"));
            params.put("pay_name", session.getStrVal("pay_name"));
            params.put("pay_assessment",session.getStrVal("pay_assessment"));
            params.put("pay_hid",session.getStrVal("pay_hid"));

            params.put("MerchantTransactionIdentifier",session.getStrVal("MerchantTransactionIdentifier"));
            params.put("BankReferenceIdentifier",session.getStrVal("BankReferenceIdentifier"));
            params.put("BankSelectionCode",session.getStrVal("BankSelectionCode"));
            params.put("RefundIdentifier",session.getStrVal("RefundIdentifier"));
            params.put("pay_date",session.getStrVal("DateTime"));
            params.put("pay_type",session.getStrVal("pay_type"));
            params.put("dues_selected",session.getStrVal("dues_selected"));
            params.put("InstrumentAliasName",session.getStrVal("InstrumentAliasName"));
            params.put("username",session.getStrVal("username"));

/*            if(session.hasVal("mandal"))
                params.put("mandal",session.getStrVal("mandal"));
            if(session.hasVal("division"))
                params.put("division",session.getStrVal("division"));
            if(session.hasVal("panchayat"))
                params.put("panchayat",session.getStrVal("panchayat"));
            if(session.hasVal("district"))
                params.put("district",session.getStrVal("district"));*/

         //check at the time od deployment

            session.removeVal("dues_selected");
            session.removeVal("jsonArray");


            String deviceParams = "Device:"+ Build.DEVICE;
            deviceParams+=" - Display:"+Build.DISPLAY;
            deviceParams+=" - Manufacturer:"+Build.MANUFACTURER;
            deviceParams+=" - Model:"+ Build.MODEL;


            params.put("deviceinfo",deviceParams);

     //       new webService().execute(params);

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

                if (res.trim().equals("success")) {


                    progress.dismiss();


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

                response = HttpRequest.post(getResources().getString(R.string.app_name)).form(data).body();

            } catch (Exception e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }

            return response;

        }

    }
}