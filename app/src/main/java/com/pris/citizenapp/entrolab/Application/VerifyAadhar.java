package com.pris.citizenapp.entrolab.Application;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.afollestad.materialdialogs.MaterialDialog;
import com.pris.citizenapp.R;
import com.pris.citizenapp.VerhoeffAlgorithm;
import com.pris.citizenapp.common.SessionManager;
import com.pris.citizenapp.entrolab.MainActivity;
import com.pris.citizenapp.github.kevinsawicki.http.HttpRequest;
import com.pris.citizenapp.login.Otp;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.regex.Pattern;

/**
 * Created by manav on 12/4/17.
 */

public class VerifyAadhar extends AppCompatActivity {
    private MaterialDialog progDialog;
    private SessionManager session;
    public MaterialDialog popAlert;
    private MaterialDialog forgot;
    String aadharstr="";
    String s="";
    Toolbar toolbar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.verify_aadhar);
        toolbar=(Toolbar)findViewById(R.id.toolbar);
        toolbar.setTitle("Verify Aadhar");
        setSupportActionBar(toolbar);
        session = new SessionManager(this);
        Intent call=getIntent();
         s=call.getStringExtra("flag");
        popAlert = new MaterialDialog.Builder(this)
                .title("Errors Found!")
                .content("Please check your data!")
                .positiveText("Ok").build();

        progDialog = new MaterialDialog.Builder(this)
                .title("Verifying Aadhar")
                .content("...")
                .progress(true, 0).build();
        Typeface rl = Typeface.createFromAsset(getAssets(), "fonts/Roboto_Light.ttf");

        boolean wrapInScrollView = true;
        forgot = new MaterialDialog.Builder(this)
                .title("Enter Otp")
                .titleColor(getResources().getColor(R.color.appcolor))
                .customView(R.layout.enter_otp_dialog, wrapInScrollView)
                .positiveText("Submit")
                .positiveColor(getResources().getColor(R.color.appcolor))
                .typeface("Roboto_Medium.ttf", "Roboto_Light.ttf")
                .cancelable(false)
                .callback(new MaterialDialog.ButtonCallback() {
                    @Override
                    public void onPositive(MaterialDialog dialog) {
                        EditText myotpone = (EditText) dialog.findViewById(R.id.entr_otp);
                        String user =myotpone.getText().toString();
                        if (session.haveNetworkConnection()) {

                            Map<String, String> params = new LinkedHashMap<String, String>();

                            params.put("authenticateAadhar", "1");
                            params.put("aadhar", session.getStrVal("myaadhar"));
                            params.put("otp",user);



                            String deviceParams = "Device:" + Build.DEVICE;
                            deviceParams += " - Display:" + Build.DISPLAY;
                            deviceParams += " - Manufacturer:" + Build.MANUFACTURER;
                            deviceParams += " - Model:" + Build.MODEL;


                            params.put("deviceinfo", deviceParams);

                            new VerifyAadhar.verifyotp().execute(params);
                        }

                        else {
                            popAlert.setTitle("Connectivity Issues");
                            popAlert.setContent("Please make sure that you are connected to an Active Internet connection to complete registration!");
                            popAlert.show();
                        }

                    }

                    @Override
                    public void onNegative(MaterialDialog dialog) {
                        //dialog.dismiss();
                    }
                }).build();

        TextView otptxt = (TextView) findViewById(R.id.otptxt);


        otptxt.setTypeface(rl);


        Button otp_btn = (Button) findViewById(R.id.button_otp);
        otp_btn.setTypeface(rl);


        otp_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
              verifyAadhar();
            }
        });



    }


    public void verifyAadhar() {

        EditText Aadhar = (EditText) findViewById(R.id.aadhar);
        aadharstr = Aadhar.getText().toString();
        String errorTxt = "";
        int error = 0;

        if (aadharstr.length() > 0) {
            Pattern aadharPattern = Pattern.compile("\\d{12}");
            boolean isValidAadhar = aadharPattern.matcher(aadharstr).matches();
            if (isValidAadhar) {
                isValidAadhar = VerhoeffAlgorithm.validateVerhoeff(aadharstr);
            }

            if (!isValidAadhar) {
                errorTxt = "Invalid AadharNumber";
                error++;
            }


        } else {
            errorTxt = "Enter Aadhar Number";
            error++;
        }


        if (error > 0) {

            popAlert.setTitle("Oops! Errors Found");
            popAlert.setContent(errorTxt);

            popAlert.show();
        }

        else {
            if (session.haveNetworkConnection()) {

                Map<String, String> params = new LinkedHashMap<String, String>();

                params.put("authenticateAadhar", "true");
                params.put("aadhar", aadharstr);
                /*params.put("otp",session.getStrVal("otp"));
                params.put("userotp",otp_string);*/


                String deviceParams = "Device:" + Build.DEVICE;
                deviceParams += " - Display:" + Build.DISPLAY;
                deviceParams += " - Manufacturer:" + Build.MANUFACTURER;
                deviceParams += " - Model:" + Build.MODEL;


                params.put("deviceinfo", deviceParams);

                new VerifyAadhar.webService().execute(params);
            }

            else {
                popAlert.setTitle("Connectivity Issues");
                popAlert.setContent("Please make sure that you are connected to an Active Internet connection to complete registration!");
                popAlert.show();
            }

        }
    }

      public class webService extends AsyncTask<Map, Integer, String>

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
                    JSONObject jsonObject=result.getJSONObject("json");
                    String rs=jsonObject.getString("value");

                    if (rs.trim().equals("100/Otp generation Success")) {
                           session.storeVal("myaadhar",aadharstr);
                            forgot.show();
                    }
                    else
                    {
                        popAlert.setContent("otp generation failed");
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

                    response = HttpRequest.post(getResources().getString(R.string.gen_otp)).form(data).body();

                } catch (Exception e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }

                return response;


            }


        }

    public class verifyotp extends AsyncTask<Map, Integer, String>

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
                //String res = result.getString("result");
                JSONObject jsonObject=result.getJSONObject("json");

                if(jsonObject.has("auth_status"))
                {
                    String auth=jsonObject.getString("auth_status");
                    if(auth.equals("100"))
                    {
                        if(jsonObject.has("name"))
                        {
                            session.storeVal("myname",jsonObject.getString("name"));
                        }
                        forgot.dismiss();
                        if(s.equals("G")) {
                            Intent intent = new Intent(VerifyAadhar.this, GrivencelocationImage.class);
                            startActivity(intent);
                        }

                       else if(s.equals("R")) {
                            Intent intent = new Intent(VerifyAadhar.this, RTIForm.class);
                            startActivity(intent);
                        }
                       /* else if(s.equals("P")) {
                        Intent intent = new Intent(VerifyAadhar.this, RTI.class);
                        startActivity(intent);
                    }*/

                    }
                    else if(auth.equals("101"))
                    {
                        popAlert.setContent("validation failed");
                        popAlert.show();
                    }
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

                response = HttpRequest.post(getResources().getString(R.string.validate_otp)).form(data).body();

            } catch (Exception e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }

            return response;


        }


    }

}
