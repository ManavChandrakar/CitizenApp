package com.pris.citizenapp.login;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

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

import static com.pris.citizenapp.common.SessionManager.USER_NAME;

/**
 * Created by manav on 20/4/17.
 */

public class ChangePassword extends AppCompatActivity {
    EditText oldpass,newpass,confirmpass;
    Button btnchnage;
    SessionManager session;
    public MaterialDialog popAlert;
    public MaterialDialog progDialog;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.change_password);
        session=new SessionManager(this);
        oldpass=(EditText)findViewById(R.id.oldpass);
        newpass=(EditText)findViewById(R.id.new_pass);
        confirmpass=(EditText)findViewById(R.id.confirm_pass);
        btnchnage=(Button)findViewById(R.id.btnchangepass);
        setTitle("Update Password");

        popAlert = new MaterialDialog.Builder(this)
                .title("Errors Found!")
                .titleColor(getResources().getColor(R.color.appcolor))
                .content("Please check your data!")
                .positiveColor(getResources().getColor(R.color.appcolor))
                .positiveText("Ok").build();


        progDialog = new MaterialDialog.Builder(this)
                .title("Updating Password")
                .titleColor(getResources().getColor(R.color.appcolor))
                .content("...")
                .progress(true, 0).build();



        btnchnage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                boolean res = validate();
                if (res) {
                    if (session.haveNetworkConnection()) {
                        Map<String, String> params = new LinkedHashMap<String, String>();
                        params.put("changePassword", "true");
                        params.put("username", session.getStrVal(USER_NAME));
                        params.put("password", newpass.getText().toString().trim());
                        params.put("old_password", oldpass.getText().toString().trim());


                        new webService().execute(params);
                    } else {
                        Toast.makeText(ChangePassword.this, "Please make sure that you are connected to an Active Internet connection!", Toast.LENGTH_SHORT).show();
                    }

                }
            }

        });
    }

    private boolean validate() {

        int error = 0;
        String errorTxt = "error";
        if(error==0) {
            if (oldpass.getText().toString().trim().length() < 6 || newpass.getText().toString().trim().length() < 6 || confirmpass.getText().toString().trim().length() < 6)
            {
                errorTxt="Enter six digit password without space";
                error++;
            }
        }

        if(error==0) {
            if (!(newpass.getText().toString().equals(confirmpass.getText().toString())))
            {
                errorTxt="Password Mismatch";
                error++;
            }
        }

        if(error==0) {
            if (oldpass.getText().toString().equals(newpass.getText().toString()))
            {
                errorTxt="New Password Same as Old Password";
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

        protected void onPostExecute(String response) {

            progDialog.dismiss();

            JSONObject result = null;

            Log.d("result", response);

            try {
                result = new JSONObject(response);
                String res = result.getString("result");

                if (res.trim().equals("success")) {
                    Log.d("webService", "HTTP Request Result: " + response);
                    AlertDialog.Builder builder = new AlertDialog.Builder(ChangePassword.this);
                    builder.setCancelable(false);
                    builder.setTitle("Message");
                    builder.setMessage("Your password has been updated click on ok to login with new password");
                    builder.setInverseBackgroundForced(true);

                    builder.setNegativeButton("Ok",
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog,
                                                    int which) {
                                    Intent intent=new Intent(ChangePassword.this, Login.class);
                                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                    startActivity(intent);
                                }
                            });
                    AlertDialog alert = builder.create();
                    alert.show();


                }  else {
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
                //add url here
                response = HttpRequest.post(getResources().getString(R.string.url_district)).form(data).body();

            } catch (Exception e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }

            return response;

        }
    }
}
