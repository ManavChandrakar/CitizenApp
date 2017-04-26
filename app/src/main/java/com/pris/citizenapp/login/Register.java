package com.pris.citizenapp.login;

import android.Manifest;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatRadioButton;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import java.text.SimpleDateFormat;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.pris.citizenapp.R;
import com.pris.citizenapp.VerhoeffAlgorithm;
import com.pris.citizenapp.common.SessionManager;
import com.pris.citizenapp.entrolab.MainActivity;
import com.pris.citizenapp.github.kevinsawicki.http.HttpRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.regex.Pattern;

import pub.devrel.easypermissions.EasyPermissions;

/**
 * Created by manav on 5/4/17.
 */

public class Register extends AppCompatActivity implements EasyPermissions.PermissionCallbacks{

    Toolbar toolbar;
    TextView date_cdd,ClickDis,ClickDiv,ClickMan,ClickPan;
    DatePickerDialog fromDatePickerDialog;
    SimpleDateFormat dateFormatter;
    private SessionManager session;
    MaterialDialog progress;
    MaterialDialog dialog;

    AppCompatRadioButton radiores;
    EditText name,mail,number,aadhar,district,division,mandal,panchayat;
    RadioGroup radioGroup;
    String gender="1";
    Button submit;

    private MaterialDialog progDialog;
    //private SessionManager session;
    public MaterialDialog popAlert;

    ArrayList<String> dist;
    ArrayList<String> div;
    ArrayList<String> Mand;
    ArrayList<String> panch;
    HashMap<String,String> mapdist;
    HashMap<String,String> mapdiv;
    HashMap<String,String> mapMand;
    HashMap<String,String> mappach;

    final String[] perms = {Manifest.permission.INTERNET, Manifest.permission.ACCESS_NETWORK_STATE, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE,Manifest.permission.ACCESS_COARSE_LOCATION,Manifest.permission.ACCESS_FINE_LOCATION};


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //session
        session = new SessionManager(Register.this);

       /* if(session.isLoggedIn()){

            Intent intent = new Intent(this,MainActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            finish();
        }
        else if(session.hasVal("otp")){
            Intent intent = new Intent(this,Otp.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            finish();

        }*/


        setContentView(R.layout.register);
       /* toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);*/
        setTitle("Register");
        // getSupportActionBar().setDisplayShowTitleEnabled(true);
        date_cdd = (TextView) findViewById(R.id.date_ccd);
        radioGroup = (RadioGroup) findViewById(R.id.radioSex);

        submit = (Button) findViewById(R.id.submit);
        name = (EditText) findViewById(R.id.Name);
        mail = (EditText) findViewById(R.id.mail);
        number = (EditText) findViewById(R.id.Number);
        aadhar = (EditText) findViewById(R.id.AadharNumber);
        district = (EditText) findViewById(R.id.district);
        division = (EditText) findViewById(R.id.division);
        mandal = (EditText) findViewById(R.id.Mandal);
        panchayat = (EditText) findViewById(R.id.Panchayat);
        ClickDis = (TextView) findViewById(R.id.clickdis);
        ClickDiv = (TextView) findViewById(R.id.clickdiv);
        ClickMan = (TextView) findViewById(R.id.clickman);
        ClickPan = (TextView) findViewById(R.id.clickpan);


        //initialsing the dropdown
        dist = new ArrayList<>();
        div = new ArrayList<>();
        Mand = new ArrayList<>();
        panch = new ArrayList<>();

        //initialise popup
        popAlert = new MaterialDialog.Builder(this)
                .title("Errors Found!")
                .content("Please check your data!")
                .positiveText("Ok").build();

        //progress
        progress = new MaterialDialog.Builder(this)
                .title("Loading...")
                .content("...")
                .progress(true, 0).build();

        //dialog
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


        //progress dialog
        progDialog = new MaterialDialog.Builder(this)
                .title("Registering your account")
                .content("...")
                .progress(true, 0).build();

        //default date set

        dateFormatter = new SimpleDateFormat("dd-MM-yyyy", Locale.US);
        Date current_date = new Date();
        final String date = new SimpleDateFormat("dd-MM-yyyy").format(current_date);
        date_cdd.setText(date);

        //radio button checkchange
        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {

                if (checkedId == R.id.male) {
                    gender = "1";
                } else if (checkedId == R.id.Female) {
                    gender = "0";
                }
            }
        });

        //setDOBField;
        date_cdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Calendar newCalendar = Calendar.getInstance();
                fromDatePickerDialog = new DatePickerDialog(Register.this, new DatePickerDialog.OnDateSetListener() {

                    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                        Calendar newDate = Calendar.getInstance();
                        newDate.set(year, monthOfYear, dayOfMonth);
                        date_cdd.setText(dateFormatter.format(newDate.getTime()));
                    }

                }, newCalendar.get(Calendar.YEAR), newCalendar.get(Calendar.MONTH), newCalendar.get(Calendar.DAY_OF_MONTH));


                fromDatePickerDialog.show();
            }
        });

        //form submission
        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                boolean valid = validate();
                if (valid) {
                    //method to start service
                    registerUser();
                }
            }
        });

        //Async task for fetching all district;
       /* if(session.haveNetworkConnection()){

            Map<String, String> params = new LinkedHashMap<String, String>();
           *//* EditText search = (EditText) findViewById(R.id.search);*//*

          *//*  params.put("getTaxDues","true");
            //Tax Type Here
            params.put("pay_tax","3");
            session.storeVal("pay_tax","3");

            params.put("searchby",session.getStrVal("search_by"));
            params.put("search", String.valueOf(search.getText()));
            params.put("username",session.getStrVal("username"));

            if(session.hasVal("mandal"))
                params.put("mandal",session.getStrVal("mandal"));
            if(session.hasVal("division"))
                params.put("division",session.getStrVal("division"));
            if(session.hasVal("panchayat"))
                params.put("panchayat",session.getStrVal("panchayat"));
            if(session.hasVal("district"))
                params.put("district",session.getStrVal("district"));*//*


            String deviceParams = "Device:"+ Build.DEVICE;
            deviceParams+=" - Display:"+Build.DISPLAY;
            deviceParams+=" - Manufacturer:"+Build.MANUFACTURER;
            deviceParams+=" - Model:"+ Build.MODEL;


            params.put("deviceinfo",deviceParams);

            new webService().execute(params);

        }
        else{
            Toast.makeText(this, "Please make sure that you are connected to an Active Internet connection!", Toast.LENGTH_SHORT).show();
        }

    */
        try {
            ClickDis.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    new MaterialDialog.Builder(Register.this)
                            .title("District")
                            .items(dist)
                            .positiveColor(getResources().getColor(R.color.appcolor))
                            .itemsCallbackSingleChoice(-1, new MaterialDialog.ListCallbackSingleChoice() {
                                @Override
                                public boolean onSelection(MaterialDialog dialog, View view, int which, CharSequence text) {

                                    Log.d("Selected", String.valueOf(text) + " - " + which);
                                    String selected = String.valueOf(text);
                                    district.setText(String.valueOf(text));
                                    division.setText("");
                                    division.setHint("Division");
                                    mandal.setText("");
                                    mandal.setHint("Mandal");
                                    panchayat.setText("");
                                    panchayat.setHint("Panchayat");
                                    String uid = mapdist.get(selected);
                                    session.storeVal("district", uid);

                                    if (selected.equals("null")) {
                                        Toast.makeText(Register.this, "select a valid field", Toast.LENGTH_LONG).show();
                                    } else {
                                        Map<String, String> params1 = new LinkedHashMap<String, String>();
                                        params1.put("getDivisions", "true");
                                        params1.put("district", uid);
                                        new webService().execute(params1);
                                    }

                                    dialog.hide();
                                    return true;
                                }
                            })
                            .positiveText("Ok").show();
                }
            });
        } catch (Exception e) {
            Toast.makeText(Register.this, "selct above fields", Toast.LENGTH_LONG).show();
        }

        try {
            ClickDiv.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    if (!(district.getText().toString().equals("null"))) {

                        new MaterialDialog.Builder(Register.this)
                                .title("Division")
                                .items(div)
                                .positiveColor(getResources().getColor(R.color.appcolor))
                                .itemsCallbackSingleChoice(-1, new MaterialDialog.ListCallbackSingleChoice() {
                                    @Override
                                    public boolean onSelection(MaterialDialog dialog, View view, int which, CharSequence text) {

                                        Log.d("Selected", String.valueOf(text) + " - " + which);
                                        String selected = String.valueOf(text);
                                        division.setText(String.valueOf(text));
                                        mandal.setText("");
                                        mandal.setHint("Mandal");
                                        panchayat.setText("");
                                        panchayat.setHint("Panchayat");
                                        String uid = mapdiv.get(selected);
                                        session.storeVal("division", uid);


                                        if (session.haveNetworkConnection()) {
                                            if (selected.equals("null")) {

                                                Toast.makeText(Register.this, "select a valid field", Toast.LENGTH_LONG).show();
                                            } else {
                                                Map<String, String> params2 = new LinkedHashMap<String, String>();
                                                params2.put("getMandals", "true");
                                                params2.put("district", session.getStrVal("district"));
                                                params2.put("division", uid);
                                                new webService().execute(params2);
                                            }
                                        }
                                        dialog.hide();
                                        return true;
                                    }
                                })
                                .positiveText("Ok").show();
                    } else {
                        Toast.makeText(Register.this, "selct above fields", Toast.LENGTH_LONG).show();

                    }
                }

            });
        } catch (Exception e) {
            Toast.makeText(Register.this, "selct above fields", Toast.LENGTH_LONG).show();
        }


        try {
            ClickMan.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    if (!((division.getText().toString().equals("null"))|| division.getText().toString().equals(""))) {
                        new MaterialDialog.Builder(Register.this)
                                .title("Mandal")
                                .items(Mand)
                                .positiveColor(getResources().getColor(R.color.appcolor))
                                .itemsCallbackSingleChoice(-1, new MaterialDialog.ListCallbackSingleChoice() {
                                    @Override
                                    public boolean onSelection(MaterialDialog dialog, View view, int which, CharSequence text) {

                                        Log.d("Selected", String.valueOf(text) + " - " + which);
                                        String selected = String.valueOf(text);
                                        mandal.setText(String.valueOf(text));
                                        panchayat.setText("");
                                        panchayat.setHint("Panchayat");
                                        String uid = mapMand.get(selected);
                                        session.storeVal("mandal", uid);

                                        dialog.hide();
                                        if (session.haveNetworkConnection()) {
                                            if (selected.equals("null")) {
                                                Toast.makeText(Register.this, "select a valid field", Toast.LENGTH_LONG).show();
                                            } else {
                                                Map<String, String> params3 = new LinkedHashMap<String, String>();
                                                params3.put("getPanchayats", "true");
                                                params3.put("district", session.getStrVal("district"));
                                                params3.put("division", session.getStrVal("division"));
                                                params3.put("mandal", uid);
                                                new webService().execute(params3);
                                            }
                                        }
                                        return true;
                                    }
                                })
                                .positiveText("Ok").show();

                    }
                    else
                    {
                        Toast.makeText(Register.this, "selct above fields", Toast.LENGTH_LONG).show();

                    }
                }
            });
        } catch (Exception e) {
            Toast.makeText(Register.this, "selct above fields", Toast.LENGTH_LONG).show();

        }

        try {
            ClickPan.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (! ((mandal.getText().toString().equals("null")) ||(mandal.getText().toString().equals(""))) ) {
                        new MaterialDialog.Builder(Register.this)
                                .title("Panchayat")
                                .items(panch)
                                .positiveColor(getResources().getColor(R.color.appcolor))
                                .itemsCallbackSingleChoice(-1, new MaterialDialog.ListCallbackSingleChoice() {
                                    @Override
                                    public boolean onSelection(MaterialDialog dialog, View view, int which, CharSequence text) {

                                        Log.d("Selected", String.valueOf(text) + " - " + which);
                                        String selected = String.valueOf(text);
                                        panchayat.setText(String.valueOf(text));
                                        String uid = mappach.get(selected);
                                        session.storeVal("panchayat", uid);

                                        dialog.hide();
                                        return true;
                                    }
                                })
                                .positiveText("Ok").show();

                    }
                    else
                    {
                        Toast.makeText(Register.this, "selct above fields", Toast.LENGTH_LONG).show();
                    }
                }

            });
        }
        catch (Exception e) {
            Toast.makeText(Register.this, "selct above fields", Toast.LENGTH_LONG).show();

        }


        try
        {
            if (session.haveNetworkConnection()) {


                if (methodRequiresPermission(perms, 111)) {
                    Map<String, String> params = new LinkedHashMap<String, String>();
                    params.put("getDistricts", "true");
                    new webService().execute(params);

                } else
                    Toast.makeText(this, "grant permission", Toast.LENGTH_SHORT).show();

            }

        }

        catch (Exception e)
        {
            Toast.makeText(Register.this,"selct above fields",Toast.LENGTH_LONG).show();

        }
    }

    //validate all form field
    private boolean validate() {

        int error = 0;
        String errorTxt ="error";


        if(panchayat.getText().toString().length()==0 ||panchayat.getText().toString().equals("null") )
        {
            errorTxt = "Select Panchayat";
            error++;
        }


        if(mandal.getText().toString().length()==0 || mandal.getText().toString().equals("null"))
        {
            errorTxt = "Select Mandal";
            error++;
        }


        if(division.getText().toString().length()==0 || division.getText().toString().equals("null"))
        {
            errorTxt = "Select Division";
            error++;
        }

        if(district.getText().toString().length()==0 || district.getText().toString().equals("null"))
        {
            errorTxt = "Select District";
            error++;
        }


        String mobilePattern = "^([7-8-9]{1}[0-9]{9})+$";
        if (error == 0) {
            if (number.getText().toString().trim().length() == 0 ) {
                errorTxt = "Mobile number Cannot be empty";
                error++;
            }
            else if (number.getText().toString().trim().length()< 10 || (!number.getText().toString().matches(mobilePattern))) {
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

        //aadhar validation
        if(aadhar.getText().length()>0) {
            Pattern aadharPattern = Pattern.compile("\\d{12}");
            boolean isValidAadhar = aadharPattern.matcher(aadhar.getText().toString()).matches();
            if (isValidAadhar) {
                isValidAadhar = VerhoeffAlgorithm.validateVerhoeff(aadhar.getText().toString());
            }

            if (!isValidAadhar) {
                errorTxt = "Invalid AadharNumber";
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


    //register the users

    public void registerUser()
    {
        if(session.haveNetworkConnection()){

            Map<String, String> paramsreg = new LinkedHashMap<String, String>();

            paramsreg.put("registration","true");
            paramsreg.put("name",name.getText().toString());
            paramsreg.put("mobile",number.getText().toString());
            paramsreg.put("dob",date_cdd.getText().toString());
            paramsreg.put("email",mail.getText().toString());
            paramsreg.put("aadhar",aadhar.getText().toString());
            paramsreg.put("gender",gender);
            paramsreg.put("district",session.getStrVal("district"));
            paramsreg.put("division",session.getStrVal("division"));
            paramsreg.put("mandal",session.getStrVal("mandal"));
            paramsreg.put("panchayat",session.getStrVal("panchayat"));



          /*  String deviceParams = "Device:"+ Build.DEVICE;
            deviceParams+=" - Display:"+Build.DISPLAY;
            deviceParams+=" - Manufacturer:"+Build.MANUFACTURER;
            deviceParams += " - Model:" + Build.MODEL;

            params.put("deviceinfo",deviceParams);*/

             new webServiceRegister().execute(paramsreg);

        }
        else{
            popAlert.setTitle("Connectivity Issues");
            popAlert.setContent("Please make sure that you are connected to an Active Internet connection to complete registration!");
            popAlert.show();
        }
    }

    @Override
    public void onPermissionsGranted(int requestCode, List<String> perms) {
        if(requestCode==111) {

            Map<String, String> params = new LinkedHashMap<String, String>();
            params.put("getDistricts", "true");
            new webService().execute(params);
        }
    }

    @Override
    public void onPermissionsDenied(int requestCode, List<String> perms) {
       // finish();
        Toast.makeText(this,"grant all the premission", Toast.LENGTH_LONG);
        Log.d("permissions",perms.toString());

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


                    if (result.has("districts")) {

                        JSONArray array = result.getJSONArray("districts");
                        mapdist = new HashMap<String, String>();
                        for (int i = 0; i < array.length(); i++) {
                            JSONObject obj = array.getJSONObject(i);
                            String d = obj.getString("district");
                            String uid = obj.getString("uid");
                            dist.add(obj.getString("district"));
                            mapdist.put(d, uid);
                        }
                        Log.d("ARRAYLIST", String.valueOf(mapdist));

                    }

                    if (result.has("divisions")) {

                        JSONArray array = result.getJSONArray("divisions");
                        mapdiv = new HashMap<String, String>();
                        for (int i = 0; i < array.length(); i++) {
                            JSONObject obj = array.getJSONObject(i);
                            String d = obj.getString("division");
                            String uid = obj.getString("uid");
                            div.add(d);
                            mapdiv.put(d, uid);
                        }
                        Log.d("ARRAYLIST", String.valueOf(mapdiv));

                    }

                    if (result.has("mandals")) {

                        JSONArray array = result.getJSONArray("mandals");
                        mapMand = new HashMap<String, String>();
                        for (int i = 0; i < array.length(); i++) {
                            JSONObject obj = array.getJSONObject(i);
                            String d = obj.getString("mandal");
                            String uid = obj.getString("uid");
                            Mand.add(d);
                            mapMand.put(d, uid);
                        }
                        Log.d("ARRAYLIST", String.valueOf(mapMand));

                    }

                    if (result.has("panchayats")) {

                        JSONArray array = result.getJSONArray("panchayats");
                        mappach= new HashMap<String, String>();
                        for (int i = 0; i < array.length(); i++) {
                            JSONObject obj = array.getJSONObject(i);
                            String d = obj.getString("panchayat");
                            String uid = obj.getString("panchayat_id");
                            panch.add(d);
                            mappach.put(d, uid);
                        }
                        Log.d("ARRAYLIST", String.valueOf(mappach));

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

    private class webServiceRegister extends AsyncTask<Map, Integer, String>
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

                    if (result.has("registration")) {


                        if(result.has("otp"))
                            session.storeVal("otp",String.valueOf(result.getString("otp")));
                            session.storeVal("tmpmobile",result.getString("mobile"));
                            session.storeVal("tmpemail",result.getString("email"));
                            session.storeVal("tmpname",result.getString("name"));

                           // session.createLoginSession(result);

                        Intent intent = new Intent(Register.this, Otp.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(intent);
                        finish();
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


    public boolean methodRequiresPermission(String[] perms, int permission) {

        if (EasyPermissions.hasPermissions(Register.this, perms)) {
            // Already have permission, do the thing
            // ...
            return true;
        } else {
            // Do not have permissions, request them now
            EasyPermissions.requestPermissions(this, "Need these permissions",

                   permission, perms);


        }

        return false;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        EasyPermissions.onRequestPermissionsResult(requestCode,permissions,grantResults,this);
    }
}
