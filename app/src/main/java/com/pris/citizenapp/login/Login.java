package com.pris.citizenapp.login;

import android.Manifest;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.afollestad.materialdialogs.MaterialDialog;
import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.Profile;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.pris.citizenapp.R;
import com.pris.citizenapp.common.SessionManager;
import com.pris.citizenapp.entrolab.FooterMain;
import com.pris.citizenapp.entrolab.MainActivity;
import com.pris.citizenapp.github.kevinsawicki.http.HttpRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import pub.devrel.easypermissions.EasyPermissions;

/**
 * Created by manav on 5/4/17.
 */

public class Login  extends AppCompatActivity implements GoogleApiClient.OnConnectionFailedListener,EasyPermissions.PermissionCallbacks {

    Toolbar toolbar;
    EditText num, pass;
    Button login;
    ImageButton GoogleLogin, FacebookLogin;
    TextView tv, register, forgotpass,tvx;

    //
    String mymobile="";

    //google signin
    String Gname = "null";
    String Gmail = "null";


    String Fname = "null";
    String Fmail = "null";
    private GoogleApiClient googleapiclient;
    private static final int REQ_CODE = 9001;

    //Facebook
    CallbackManager callbackManager;

    private MaterialDialog progDialog;
    private SessionManager session;
    public MaterialDialog popAlert;
    private MaterialDialog forgot;
    final String[] perms = {Manifest.permission.INTERNET, Manifest.permission.ACCESS_NETWORK_STATE, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE
    ,Manifest.permission.ACCESS_COARSE_LOCATION,Manifest.permission.ACCESS_FINE_LOCATION,Manifest.permission.LOCATION_HARDWARE,Manifest.permission.CAMERA};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        session = new SessionManager(this);
        FacebookSdk.sdkInitialize(getApplicationContext());
        setContentView(R.layout.login);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("Login");
        setSupportActionBar(toolbar);

        Typeface head = Typeface.createFromAsset(getAssets(), "fonts/Roboto_Light.ttf");

        tv=(TextView)findViewById(R.id.tv);
        tv.setTypeface(head);
        tvx=(TextView)findViewById(R.id.tvx);
        tvx.setTypeface(head);
        num = (EditText) findViewById(R.id.editnum);
        GoogleLogin = (ImageButton) findViewById(R.id.googleLogin);
        FacebookLogin = (ImageButton) findViewById(R.id.facebookLogin);
        pass = (EditText) findViewById(R.id.editpass);
        login = (Button) findViewById(R.id.loginbtn);
        register = (TextView) findViewById(R.id.register);
        register.setTypeface(head);
        forgotpass = (TextView) findViewById(R.id.forgot);
        forgotpass.setTypeface(head);
        login.setTypeface(head);
        callbackManager = CallbackManager.Factory.create();
        LoginManager.getInstance().registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                AccessToken accessToken = loginResult.getAccessToken();
                Profile profile = Profile.getCurrentProfile();

                // Facebook Email address
                GraphRequest request = GraphRequest.newMeRequest(
                        loginResult.getAccessToken(),
                        new GraphRequest.GraphJSONObjectCallback() {
                            @Override
                            public void onCompleted(
                                    JSONObject object,
                                    GraphResponse response) {
                                Log.v("LoginActivity Response ", response.toString());

                                try {
                                    Fname = object.getString("name");

                                    Fmail = object.getString("email");
                                    Log.v("Email = ", " " + Fmail);

                                    Toast.makeText(getApplicationContext(), Fname + " " + Fmail, Toast.LENGTH_LONG).show();
                                    //start fb service
                                    startfb();

                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                        });
                Bundle parameters = new Bundle();
                parameters.putString("fields", "id,name,email,gender");
                request.setParameters(parameters);
                request.executeAsync();


            }

            @Override
            public void onCancel() {

            }

            @Override
            public void onError(FacebookException error) {

            }
        });

        //google
        GoogleSignInOptions siginoption = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).requestEmail().build();
        googleapiclient = new GoogleApiClient.Builder(this).enableAutoManage(this, this).addApi(Auth.GOOGLE_SIGN_IN_API, siginoption).build();

        popAlert = new MaterialDialog.Builder(this)
                .title("Errors Found!")
                .titleColor(getResources().getColor(R.color.appcolor))
                .content("Please check your data!")
                .positiveText("Ok").build();

        progDialog = new MaterialDialog.Builder(this)
                .title("Logging In")
                .titleColor(getResources().getColor(R.color.appcolor))
                .content("...")
                .progress(true, 0).build();


        boolean wrapInScrollView = true;
        forgot = new MaterialDialog.Builder(this)
                .title("Forgot Password?")
                .titleColor(getResources().getColor(R.color.appcolor))
                .customView(R.layout.forgot_dialog, wrapInScrollView)
                .positiveText("Submit")
                .negativeText("Cancel")
                .negativeColor(getResources().getColor(R.color.appcolor))
                .positiveColor(getResources().getColor(R.color.appcolor))
                .typeface("Roboto_Medium.ttf", "Roboto_Light.ttf")
                .callback(new MaterialDialog.ButtonCallback() {
                    @Override
                    public void onPositive(MaterialDialog dialog) {
                        EditText mobile = (EditText) dialog.findViewById(R.id.dmobile);

                        String mobilePattern = "^([7-8-9]{1}[0-9]{9})+$";

                        String nriMobile = "^\\+[1-9]{1}[0-9]{7,11}$";

                        String user = String.valueOf(mobile.getText());
                        if (user.trim().length() == 0) {
                            session.storeVal("err", "Mobile number Cannot be empty");

                        } else if (!user.matches(mobilePattern)) {


                            //  Toast.makeText(Login.this,"Invalid Mobile number",Toast.LENGTH_LONG).show();
                            session.storeVal("err", "Invalid Mobile Number ");


                        } else {
                            forgotPassword(user);

                        }


                    }

                    @Override
                    public void onNegative(MaterialDialog dialog) {
                        dialog.dismiss();
                    }
                }).build();

        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                boolean res = validate();
                if (res) {

                    //start a webservice
                    /*Intent i=new Intent(Login.this,MainActivity.class);
                    startActivity(i);*/
                     if(session.haveNetworkConnection())
                     {
                    if (methodRequiresPermission(perms, 111)) {
                        Map<String, String> params = new LinkedHashMap<String, String>();
                        params.put("login", "true");
                        params.put("username", num.getText().toString());
                        params.put("password", pass.getText().toString());


                        new webService().execute(params);
                    } else {
                        Toast.makeText(Login.this, "Please Grant required app permissions!!", Toast.LENGTH_SHORT).show();
                    }

                }

                    else{
                        Toast.makeText(Login.this, "Please make sure that you are connected to an Active Internet connection!", Toast.LENGTH_SHORT).show();
                    }

                }



                }
                // Toast.makeText(Login.this,num.getText().toString()+" " + pass.getText().toString()+ " ", Toast.LENGTH_SHORT).show();
            });

        //Register via name and pass
        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(Login.this, Register.class);
                startActivity(i);
            }
        });


        //google login
        GoogleLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                signIn();
            }
        });

        //FacebookLogin
        FacebookLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                LoginManager.getInstance().logInWithReadPermissions(Login.this, Arrays.asList("public_profile", "email"));

            }
        });

        forgotpass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                forgot.show();
            }
        });
    }

    public void startfb() {

        if(session.haveNetworkConnection()) {
            Map<String, String> params = new LinkedHashMap<String, String>();

            params.put("socialLogin", "true");
            params.put("email", Fmail);

            if (methodRequiresPermission(perms, 444)) {

                new webService().execute(params);
            } else {
                Toast.makeText(Login.this, "Please Grant required app permissions!!", Toast.LENGTH_SHORT).show();
            }
        }

        else{
            Toast.makeText(this, "Please make sure that you are connected to an Active Internet connection!", Toast.LENGTH_SHORT).show();
        }
    }

    //google signin
    private void signIn() {
        Intent intent = Auth.GoogleSignInApi.getSignInIntent(googleapiclient);
        startActivityForResult(intent, REQ_CODE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQ_CODE) {
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            handleResult(result);
        }

        callbackManager.onActivityResult(requestCode, resultCode, data);
    }

    private void handleResult(GoogleSignInResult result) {
        if (result.isSuccess()) {
            GoogleSignInAccount signin = result.getSignInAccount();
            Gname = signin.getDisplayName();
            Gmail = signin.getEmail();

            Map<String, String> params = new LinkedHashMap<String, String>();

            params.put("socialLogin", "true");
            params.put("email", Gmail);

              /*  String deviceParams = "Device:"+ Build.DEVICE;
                deviceParams+=" - Display:"+Build.DISPLAY;
                deviceParams+=" - Manufacturer:"+Build.MANUFACTURER;
                deviceParams += " - Model:" + Build.MODEL;

                params.put("deviceinfo",deviceParams);*/
            if (session.haveNetworkConnection()) {
                if (methodRequiresPermission(perms, 333)) {
                    new webService().execute(params);
                } else {
                    Toast.makeText(Login.this, "Please Grant required app permissions!!", Toast.LENGTH_SHORT).show();

                }
                Toast.makeText(this, Gmail + " " + Gname + " ", Toast.LENGTH_LONG).show();
            }
            else{
                Toast.makeText(this, "Please make sure that you are connected to an Active Internet connection!", Toast.LENGTH_SHORT).show();
            }
        }
    }

    //username and password validation
    boolean validate() {
        int error = 0;
        String errorTxt = "error";
        String mobilePattern = "^([7-8-9]{1}[0-9]{9})+$";
        if (error == 0) {
            if (num.getText().toString().trim().length() == 0) {
                errorTxt = "Mobile number Cannot be empty";
                error++;
            } else if (num.getText().toString().trim().length() < 10 || (!num.getText().toString().matches(mobilePattern))) {
                errorTxt = "Enter valid 10 digit mobile number";
                error++;
            }
        }


        if (error == 0) {
            if (pass.getText().toString().trim().length() == 0) {
                errorTxt = "Password Cannot be empty";
                error++;
            } else if (pass.getText().toString().trim().length() < 6 || pass.getText().toString().trim().length() > 30) {
                errorTxt = "Invalid Password";
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

    //webservice for forgot password
    public void forgotPassword(String mobile) {

         mymobile=mobile;

        if(session.haveNetworkConnection()) {
            if (methodRequiresPermission(perms, 222)) {

                progDialog.setTitle("Verifying your Mobile");
                forgot.dismiss();

                Map<String, String> params = new LinkedHashMap<String, String>();
                params.put("forgotPassword", "true");
                params.put("mobile", mobile);

                new webService().execute(params);

            } else {

                Toast.makeText(Login.this, "Please Grant required app permissions!!", Toast.LENGTH_SHORT).show();
            }
        }
        else{
            Toast.makeText(this, "Please make sure that you are connected to an Active Internet connection!", Toast.LENGTH_SHORT).show();
        }
    }

    //googlelogin overriden method
    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {

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

                    if (result.has("login")) {


                        session.createLoginSession(result);

                        Intent intent = new Intent(Login.this, FooterMain.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(intent);
                        finish();
                    } else if (result.has("forgotPassword")) {


                        new MaterialDialog.Builder(Login.this)
                                .title("New Password")
                                .titleColor(getResources().getColor(R.color.appcolor))
                                .content("Your Password is now reset. We have sent your new password to your registered mobile number")
                                .contentColor(getResources().getColor(R.color.appcolor))
                                .positiveText("Ok").show();

                    } else if (result.has("socialLogin")) {

                        if (result.has("otp")) {
                            session.storeVal("otp", String.valueOf(result.getString("otp")));
                            session.storeVal("email", String.valueOf(result.getString("email")));
                            session.storeVal("tmpmobile", String.valueOf(result.getString("mobile")));

                            // session.createLoginSession(result);
                            Intent intent = new Intent(Login.this, Otp.class);
                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            startActivity(intent);
                            finish();
                        }

                    }
                } else if (result.has("socialLogin") && result.has("error")) {

                    Toast.makeText(Login.this, "User not register kindly register to login", Toast.LENGTH_LONG).show();
                    Intent intent = new Intent(Login.this, Register.class);
                    startActivity(intent);
                } else {
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


    public boolean methodRequiresPermission(String[] perms, int permission) {

        if (EasyPermissions.hasPermissions(Login.this, perms)) {
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

    @Override
    public void onPermissionsGranted(int requestCode, List<String> perms) {
        if(requestCode==222)
        {
            progDialog.setTitle("Verifying your Mobile");
            forgot.dismiss();

            Map<String, String> params = new LinkedHashMap<String, String>();
            params.put("forgotPassword", "true");
            params.put("mobile", mymobile);

            new webService().execute(params);
        }

        if(requestCode==111)
        {
            Map<String, String> params = new LinkedHashMap<String, String>();
            params.put("login", "true");
            params.put("username", num.getText().toString());
            params.put("password", pass.getText().toString());


            new webService().execute(params);
        }

        if(requestCode==333)
        {
            Map<String, String> params = new LinkedHashMap<String, String>();

            params.put("socialLogin", "true");
            params.put("email", Gmail);
            new webService().execute(params);

        }

        if(requestCode==444)
        {
            Map<String, String> params = new LinkedHashMap<String, String>();

            params.put("socialLogin", "true");
            params.put("email", Fmail);

            new webService().execute(params);

        }

    }

    @Override
    public void onPermissionsDenied(int requestCode, List<String> perms) {
    }
}