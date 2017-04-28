package com.pris.citizenapp.Certificates;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.NotificationCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.afollestad.materialdialogs.MaterialDialog;
import com.pris.citizenapp.R;
import com.pris.citizenapp.common.SessionManager;
import com.pris.citizenapp.entrolab.Application.TapOtp;
import com.pris.citizenapp.entrolab.FooterMain;
import com.pris.citizenapp.github.kevinsawicki.http.HttpRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Created by manav on 17/4/17.
 */

public class CertificateOtp extends AppCompatActivity {
    private MaterialDialog progDialog;
    private SessionManager session;
    public MaterialDialog popAlert;
    Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_otp);
        toolbar=(Toolbar)findViewById(R.id.toolbar);
        toolbar.setTitle("Certificate Otp");
        setSupportActionBar(toolbar);
        session = new SessionManager(this);

        popAlert = new MaterialDialog.Builder(this)
                .title("Errors Found!")
                .content("Please check your data!")
                .positiveText("Ok").build();

        progDialog = new MaterialDialog.Builder(this)
                .title("Contacting server...")
                .content("...")
                .progress(true, 0).build();
        Typeface rl = Typeface.createFromAsset(getAssets(), "fonts/Roboto_Light.ttf");

        TextView otptxt = (TextView) findViewById(R.id.otptxt);

        final TextView resend = (TextView) findViewById(R.id.button_resend);
        resend.setTypeface(rl);


        otptxt.setTypeface(rl);

        Button otp_btn = (Button) findViewById(R.id.button_otp);
         otp_btn.setTypeface(rl);

        otp_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                verifyOtp();
            }
        });


        resend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                resend.setClickable(false);
                resend.setEnabled(false);
                new Handler().postDelayed(new Runnable()
                {
                    public void run()
                    {
                        resend.setClickable(true);
                        resend.setEnabled(true);
                    }
                }, 9000);
                Map<String, String> params = new LinkedHashMap<String, String>();

                params.put("resendProCertificateOtp", "true");
                params.put("panchayat", session.getStrVal("certificatepanchayat"));
                params.put("hid", session.getStrVal("hidcertificates"));
                params.put("username", session.getStrVal("username"));

                String deviceParams = "Device:" + Build.DEVICE;
                deviceParams += " - Display:" + Build.DISPLAY;
                deviceParams += " - Manufacturer:" + Build.MANUFACTURER;
                deviceParams += " - Model:" + Build.MODEL;

                params.put("deviceinfo", deviceParams);

                new webService().execute(params);

            }
        });
    }


    public void verifyOtp() {

        EditText otp = (EditText) findViewById(R.id.otp);
        String otp_string = otp.getText().toString();

        if (otp_string.length() == 0) {
            popAlert.setContent("OTP not entered");
            popAlert.show();
        } else if (otp_string.equals(session.getStrVal("otpcertificate"))) {

            if (session.haveNetworkConnection()) {

                Map<String, String> params = new LinkedHashMap<String, String>();

                params.put("verifyProCertificateOtp", "true");
                params.put("panchayat", session.getStrVal("certificatepanchayat"));
                params.put("hid", session.getStrVal("hidcertificates"));
                params.put("username", session.getStrVal("username"));


                String deviceParams = "Device:" + Build.DEVICE;
                deviceParams += " - Display:" + Build.DISPLAY;
                deviceParams += " - Manufacturer:" + Build.MANUFACTURER;
                deviceParams += " - Model:" + Build.MODEL;


                params.put("deviceinfo", deviceParams);

                new webService().execute(params);

            }
            else {
                popAlert.setContent("Invalid OTP");
                popAlert.show();

            }


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

        protected void onPostExecute(String response) {

            progDialog.dismiss();

            JSONObject result = null;


            try {

                Log.d("Otp", "OTP gen: " + response);
                result = new JSONObject(response);
                String res = result.getString("result");

                if (res.trim().equals("success")) {


                    if (result.has("verifyProCertificateOtp")) {

                        session.removeVal("otpcertificate");
                        if(result.has("download"))
                        {
                            if(result.getString("download").equals("no"))
                            {
                              /*popAlert.setTitle("Message");
                              popAlert.setContent(result.getString("link"));
                                popAlert.show();*/
                                AlertDialog.Builder builder = new AlertDialog.Builder(CertificateOtp.this);
                                builder.setCancelable(false);
                                builder.setTitle("Message");
                                builder.setMessage(result.getString("link"));
                                builder.setInverseBackgroundForced(true);

                                final JSONObject finalResult = result;
                                builder.setNegativeButton("ok",
                                        new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog,
                                                                int which) {
                                              finish();
                                            }
                                        });
                                AlertDialog alert = builder.create();
                                alert.show();

                            }
                            else if(result.getString("download").equals("yes"))
                            {
                                AlertDialog.Builder builder = new AlertDialog.Builder(CertificateOtp.this);
                                builder.setCancelable(false);
                                builder.setTitle("Message");
                                builder.setMessage("Click on download button to download Certificate");
                                builder.setInverseBackgroundForced(true);

                                final String lnk = result.getString("link");
                                builder.setNegativeButton("download",
                                        new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog,
                                                                int which) {
                                               /* Intent intent=new Intent(CertificateOtp.this, FooterMain.class);
                                                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                                startActivity(intent);*/
                                                Intent intent = null;
                                                try {
                                                    intent = new Intent(Intent.ACTION_VIEW, Uri.parse(lnk));
                                                    startActivity(intent);
                                                } catch (Exception e) {
                                                    e.printStackTrace();
                                                }
                                                finish();
                                            }
                                        });
                                AlertDialog alert = builder.create();
                                alert.show();
                            }
                        }


                    }


                    } else if (result.has("resendProCertificateOtp")) {

                        session.storeVal("otpcertificate", result.getString("otp"));
                        popAlert.setTitle("OTP Sent");
                        popAlert.setContent("A New OTP has been sent to your Registered Mobile");
                        popAlert.show();
                    }
                 else {

                    session.removeVal("otptap");
                    popAlert.setTitle("Oops! Errors Found");
                    popAlert.setContent(result.getString("error"));
                    popAlert.show();
                }


            } catch (JSONException e) {
                e.printStackTrace();
            }


        }

        protected void onProgressUpdate(Integer... progress) {

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