package com.pris.citizenapp.entrolab.Application;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.afollestad.materialdialogs.MaterialDialog;
import com.pris.citizenapp.R;
import com.pris.citizenapp.common.SessionManager;
import com.pris.citizenapp.entrolab.FooterMain;
import com.pris.citizenapp.entrolab.MainActivity;
import com.pris.citizenapp.entrolab.PrivateTap;
import com.pris.citizenapp.github.kevinsawicki.http.HttpRequest;
import com.pris.citizenapp.login.Otp;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Created by manav on 17/4/17.
 */

public class TapOtp extends AppCompatActivity {
    private MaterialDialog progDialog;
    private SessionManager session;
    public MaterialDialog popAlert;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_otp);

        session = new SessionManager(this);

        popAlert = new MaterialDialog.Builder(this)
                .title("Errors Found!")
                .content("Please check your data!")
                .positiveText("Ok").build();

        progDialog = new MaterialDialog.Builder(this)
                .title("Registering your account")
                .content("...")
                .progress(true, 0).build();
        Typeface rl = Typeface.createFromAsset(getAssets(), "fonts/Roboto_Light.ttf");

        TextView otptxt = (TextView) findViewById(R.id.otptxt);

        TextView resend = (TextView) findViewById(R.id.button_resend);


        otptxt.setTypeface(rl);

        Button otp_btn = (Button) findViewById(R.id.button_otp);


        otp_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                verifyOtp();
            }
        });


        resend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Map<String, String> params = new LinkedHashMap<String, String>();

                params.put("resendPtapOtp","true");
                params.put("panchayat",session.getStrVal("tappanchayat"));
                params.put("hid",session.getStrVal("hid"));
                params.put("taps",session.getStrVal("taps"));
                params.put("username",session.getStrVal("tapname"));

                String deviceParams = "Device:"+ Build.DEVICE;
                deviceParams+=" - Display:"+Build.DISPLAY;
                deviceParams+=" - Manufacturer:"+Build.MANUFACTURER;
                deviceParams += " - Model:" + Build.MODEL;

                params.put("deviceinfo",deviceParams);

                new webService().execute(params);

            }
        });
    }


    public  void verifyOtp(){

        EditText otp = (EditText) findViewById(R.id.otp);
        String otp_string = otp.getText().toString();

        if(otp_string.length() == 0){
            popAlert.setContent("OTP not entered");
            popAlert.show();
        }

        else if(otp_string.equals(session.getStrVal("otptap"))){

            if(session.haveNetworkConnection()){

                Map<String, String> params = new LinkedHashMap<String, String>();

                params.put("verifyPtapOtp","true");
                params.put("panchayat",session.getStrVal("tappanchayat"));
                params.put("hid",session.getStrVal("hid"));
                params.put("taps",session.getStrVal("taps"));
                params.put("username",session.getStrVal("username"));


                String deviceParams = "Device:"+ Build.DEVICE;
                deviceParams+=" - Display:"+Build.DISPLAY;
                deviceParams+=" - Manufacturer:"+Build.MANUFACTURER;
                deviceParams += " - Model:" + Build.MODEL;


                params.put("deviceinfo",deviceParams);

                new webService().execute(params);

            }
            else{
                popAlert.setTitle("Connectivity Issues");
                popAlert.setContent("Please make sure that you are connected to an Active Internet connection to complete registration!");
                popAlert.show();
            }



        }
        else{
            popAlert.setContent("Invalid OTP");
            popAlert.show();

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
            progDialog.show();

        }

        protected void onPostExecute(String response){

            progDialog.dismiss();

            JSONObject result = null;


            try {

                Log.d("Otp","OTP gen: "+response);
                result = new JSONObject(response);
                String res = result.getString("result");

                if(res.trim().equals("success")) {


                    if (result.has("verifyPtapOtp")) {

                        session.removeVal("otptap");

/*
                        session.createLoginSession(result);
*/
                        if(result.has("privatetap_id"))
                        {
                           /* popAlert.setTitle("Tap id");
                            popAlert.setContent("your private Tap id is " +result.get("privatetap_id"));*/
                            AlertDialog.Builder builder = new AlertDialog.Builder(TapOtp.this);
                            builder.setCancelable(false);
                            builder.setTitle("Message");
                            builder.setMessage("your private Tap id is " +result.get("privatetap_id"));
                            builder.setInverseBackgroundForced(true);

                            builder.setNegativeButton("Ok",
                                    new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog,
                                                            int which) {
                                            Intent intent=new Intent(TapOtp.this, FooterMain.class);
                                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                            startActivity(intent);
                                        }
                                    });
                            AlertDialog alert = builder.create();
                            alert.show();


                        }


                    } else if (result.has("resendPtapOtp")) {

                        session.storeVal("otptap",result.getString("otp"));
                        popAlert.setTitle("OTP Sent");
                        popAlert.setContent("A New OTP has been sent to your Registered Mobile");
                        popAlert.show();
                    }
                }
                else{

                    session.removeVal("otptap");
                    popAlert.setTitle("Oops! Errors Found");
                    popAlert.setContent(result.getString("error"));
                    popAlert.show();
                }


            } catch (JSONException e) {
                e.printStackTrace();
            }


        }
        protected void onProgressUpdate(Integer... progress){

            //  progDialog.setProgress(progress[0]);
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