package com.pris.citizenapp.Certificates;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.afollestad.materialdialogs.MaterialDialog;
import com.pris.citizenapp.R;
import com.pris.citizenapp.common.SessionManager;
import com.pris.citizenapp.github.kevinsawicki.http.HttpRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Created by manav on 21/4/17.
 */

public class MarriageCft extends AppCompatActivity {

    Toolbar toolbar;
    EditText assessNo;
    Button button;
    public MaterialDialog popAlert;
    public MaterialDialog progDialog;
    public SessionManager session;
    ArrayList<String> al;
    TextView tv1;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.certificate_ppt);
        toolbar=(Toolbar)findViewById(R.id.toolbar);
        toolbar.setTitle("Marriage Certificate");
        setSupportActionBar(toolbar);
        Typeface head = Typeface.createFromAsset(getAssets(), "fonts/Roboto_Light.ttf");

        session=new SessionManager(this);
        assessNo=(EditText)findViewById(R.id.assesno);
        assessNo.setHint("Enter aadhar number/mobile number");
        button=(Button)findViewById(R.id.btnsubmit);
        tv1=(TextView)findViewById(R.id.tv1);
        tv1.setText("Aadhar Number/Mobile Number");
        tv1.setTypeface(head);
        button.setTypeface(head);

        //initialise popup
        popAlert = new MaterialDialog.Builder(this)
                .title("Errors Found!")
                .content("Please check your data!")
                .positiveText("Ok").build();


        progDialog = new MaterialDialog.Builder(this)
                .title("Submitting data")
                .titleColor(getResources().getColor(R.color.appcolor))
                .content("...")
                .progress(true, 0).build();


        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                boolean res=validate();
                if(res)
                {
                    if(session.haveNetworkConnection()) {
                        Map<String, String> params = new LinkedHashMap<String, String>();

                        params.put("propertyCertificate", "true");
                        params.put("panchayat", session.getStrVal("panchayat"));
                        params.put("search",assessNo.getText().toString());
                        params.put("username",session.getStrVal("username"));
                        new webService().execute(params);
                    }

                    else{
                        Toast.makeText(MarriageCft.this, "Please make sure that you are connected to an Active Internet connection!", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });

    }

    private boolean validate() {
        int error = 0;
        String errorTxt ="error";


        if (error == 0) {
            if (assessNo.getText().toString().trim().length() == 0) {
                errorTxt = "Assesment no. / Door number Mandatory";
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
                Log.d("webService", "HTTP Request Result: " + response);

                result = new JSONObject(response);
                String res = result.getString("result");

                if(res.trim().equals("success")){



                    session.storeVal("certificatepanchayat",result.getString("panchayat"));
                    session.storeVal("otpcertificate",result.getString("otp"));
                    session.storeVal("hidcertificates",result.getString("hid"));

                    // session.createLoginSession(result);

                    Intent intent = new Intent(MarriageCft.this, CertificateOtp.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                    finish();
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

