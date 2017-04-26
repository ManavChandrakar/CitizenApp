package com.pris.citizenapp.entrolab.Application;

import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.facebook.drawee.view.SimpleDraweeView;
import com.pris.citizenapp.R;
import com.pris.citizenapp.common.ImageLoadingUtils;
import com.pris.citizenapp.common.SessionManager;
import com.pris.citizenapp.github.kevinsawicki.http.HttpRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Created by manav on 11/4/17.
 */

public class RTIForm extends AppCompatActivity {
    EditText name,mobile,mail,fat_hus,inforamtion,address;
    CheckBox cb1,cb2;
    MaterialDialog popAlert;
    MaterialDialog progress;
    MaterialDialog dialog;
    Button save;
    Toolbar toolbar;
    private SessionManager session;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.rti_form);
        toolbar=(Toolbar)findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        setTitle("RTI Form");
        name=(EditText)findViewById(R.id.Name);
        mobile=(EditText)findViewById(R.id.mobile);
        mail=(EditText)findViewById(R.id.mail);
        fat_hus=(EditText)findViewById(R.id.father_husband);
        address=(EditText)findViewById(R.id.editdetails);
        inforamtion=(EditText)findViewById(R.id.landmark);
        cb1=(CheckBox)findViewById(R.id.check1);
        cb2=(CheckBox)findViewById(R.id.check2);
        save = (Button) findViewById(R.id.submit);

        session = new SessionManager(this);
        progress = new MaterialDialog.Builder(this)
                .title("Fetching Data")
                .content("...")
                .cancelable(false)
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
                .build();

        popAlert = new MaterialDialog.Builder(this)
                .title("Errors Found!")
                .content("Please check your data!")
                .positiveText("Ok").build();


        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                boolean res = validate();

                if (res) {


                    if (session.haveNetworkConnection()) {
                        Map<String, String> params = new LinkedHashMap<String, String>();

                        params.put("newRtiApplications", "true");

                        String deviceParams = "Device:" + Build.DEVICE;
                        deviceParams += " - Display:" + Build.DISPLAY;
                        deviceParams += " - Manufacturer:" + Build.MANUFACTURER;
                        deviceParams += " - Model:" + Build.MODEL;


                        params.put("deviceinfo", deviceParams);
                        params.put("panchayat", session.getStrVal("panchayat"));
                        params.put("name", name.getText().toString());
                        params.put("mobile", mobile.getText().toString());
                        params.put("email", mail.getText().toString());
                        params.put("father_husband",fat_hus.getText().toString());
                        params.put("address",address.getText().toString());
                        params.put("information",inforamtion.getText().toString());
                        //  params.put("aadhar", session.getStrVal("myaadhar"));
                        params.put("username", session.getStrVal("username"));
                        new webServiceaddgrevience().execute(params);


                    }
                }
            }
        });


    }

    private boolean validate() {

        int error = 0;
        String errorTxt ="error";

      if(!cb1.isChecked())
      {
          errorTxt = "Check the checkboxes";
          error++;
      }

        if(!cb2.isChecked())
        {
            errorTxt = "Check the checkboxes";
            error++;
        }


        if(address.getText().toString().length()==0)
        {
            errorTxt = "Enter Address";
            error++;
        }

        if(inforamtion.getText().toString().length()==0)
        {
            errorTxt = "Enter information field";
            error++;
        }

        String mobilePattern = "^([7-8-9]{1}[0-9]{9})+$";
        if (error == 0) {
            if (mobile.getText().toString().trim().length() == 0 ) {
                errorTxt = "Mobile number Cannot be empty";
                error++;
            }
            else if (mobile.getText().toString().trim().length()< 10 || (!mobile.getText().toString().matches(mobilePattern))) {
                errorTxt = "Enter valid 10 digit mobile number";
                error++;
            }
        }

        if(mail.getText().toString().length() > 0 && error == 0){

            if( !Patterns.EMAIL_ADDRESS.matcher(mail.getText().toString()).matches()) {

                errorTxt = "Invalid Email";
                error++;
            }
        }

        if (error == 0) {
            if (name.getText().toString().trim().length() == 0) {
                errorTxt = "Full Name Mandatory";
                error++;
            }

        }

        if (error == 0) {
            if (fat_hus.getText().toString().trim().length() == 0) {
                errorTxt = "Father/Husband Name Mandatory";
                error++;
            }

        }

        if (error > 0) {
            error = 0;

            popAlert.setTitle("Oops! Errors Found");
            popAlert.setContent(errorTxt);
            popAlert.show();

            return false;

        }

        return true;
    }


    private class webServiceaddgrevience extends AsyncTask<Map, Integer, String>
    {


        @Override
        protected String doInBackground(Map... params) {

            return postData(params[0]);
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            //   progDialog.show();

        }

        protected void onPostExecute(String response){

            //     progDialog.dismiss();

            JSONObject result = null;

            Log.d("result",response);

            try {
                result = new JSONObject(response);
                String res = result.getString("result");

                if(res.trim().equals("success")) {
                    Log.d("webService", "HTTP Request Result: " + response);


                    if (result.has("rti_id")) {

                   /*     popAlert.setTitle("Grevience id");
                        popAlert.setContent(result.getString("message"));
                        popAlert.show();*/

                        new MaterialDialog.Builder(RTIForm.this)
                                .title("RTI Id")
                                .titleColor(getResources().getColor(R.color.appcolor))
                                .content(result.getString("message"))
                                .contentColor(getResources().getColor(R.color.appcolor))
                                .positiveText("Ok").show();
                    }

                }
                else{
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

            String response = "";

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
