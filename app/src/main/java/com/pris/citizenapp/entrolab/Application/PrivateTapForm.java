package com.pris.citizenapp.entrolab.Application;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.afollestad.materialdialogs.MaterialDialog;
import com.pris.citizenapp.R;
import com.pris.citizenapp.VerhoeffAlgorithm;
import com.pris.citizenapp.common.SessionManager;
import com.pris.citizenapp.entrolab.PrivateTap;
import com.pris.citizenapp.github.kevinsawicki.http.HttpRequest;
import com.pris.citizenapp.login.Login;
import com.pris.citizenapp.login.Otp;
import com.pris.citizenapp.login.Register;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.regex.Pattern;

/**
 * Created by manav on 11/4/17.
 */

public class PrivateTapForm extends AppCompatActivity {

    Toolbar toolbar;
    EditText assessNo,connecttion;
    Button button;
    TextView noofconn,tv1;
    public MaterialDialog popAlert;
    public MaterialDialog progDialog;
    public SessionManager session;
    ArrayList<String> al;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
       setContentView(R.layout.tapform);
        toolbar=(Toolbar)findViewById(R.id.toolbar);
        toolbar.setTitle("Private Tap Form");
        setSupportActionBar(toolbar);
        Typeface head = Typeface.createFromAsset(getAssets(), "fonts/Roboto_Light.ttf");

        session=new SessionManager(this);
        noofconn=(TextView)findViewById(R.id.noofconn);
        noofconn.setTypeface(head);
        tv1=(TextView)findViewById(R.id.tv1);
        tv1.setTypeface(head);
        assessNo=(EditText)findViewById(R.id.assesno);
        connecttion=(EditText)findViewById(R.id.connection);
        button=(Button)findViewById(R.id.btnsubmit);
        button.setTypeface(head);
        al=new ArrayList<String>();
        al.add("1");
        al.add("2");
        al.add("3");
        al.add("4");
        al.add("5");
        al.add("6");

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

            noofconn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    new MaterialDialog.Builder(PrivateTapForm.this)
                            .title("Connections")
                            .titleColor(getResources().getColor(R.color.appcolor))
                            .items(al)
                            .itemsColor(getResources().getColor(R.color.appcolor))
                            .positiveColor(getResources().getColor(R.color.appcolor))
                            .itemsCallbackSingleChoice(-1, new MaterialDialog.ListCallbackSingleChoice() {
                                @Override
                                public boolean onSelection(MaterialDialog dialog, View view, int which, CharSequence text) {

                                    Log.d("Selected", String.valueOf(text) + " - " + which);
                                    String selected = String.valueOf(text);
                                    connecttion.setText(String.valueOf(text));

                                    dialog.hide();
                                    return true;
                                }
                            })
                            .positiveText("Ok").show();
                }
            });


        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                boolean res=validate();
                if(res)
                {
                    if(session.haveNetworkConnection()) {
                        Map<String, String> params = new LinkedHashMap<String, String>();

                        params.put("newPrivateTap", "true");
                        params.put("panchayat", session.getStrVal("panchayat"));
                        params.put("search",assessNo.getText().toString());
                        params.put("taps",connecttion.getText().toString());
                        params.put("username",session.getStrVal("username"));
                        new webService().execute(params);
                    }

                    else{
                        Toast.makeText(PrivateTapForm.this, "Please make sure that you are connected to an Active Internet connection!", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });

    }

    private boolean validate() {
        int error = 0;
        String errorTxt ="error";


        if(connecttion.getText().toString().length()==0 ||connecttion.getText().toString().equals("null") )
        {
            errorTxt = "Select number of connections";
            error++;
        }



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



                        session.storeVal("tappanchayat",result.getString("panchayat"));
                        session.storeVal("otptap",result.getString("otp"));
                        session.storeVal("hid",result.getString("hid"));
                        session.storeVal("taps",result.getString("taps"));

                        // session.createLoginSession(result);

                        Intent intent = new Intent(PrivateTapForm.this, TapOtp.class);
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
