package com.pris.citizenapp.entrolab;

/**
 * Created by manav on 5/4/17.
 */

import android.content.Intent;
import android.content.pm.PackageInstaller;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.pris.citizenapp.R;
import com.pris.citizenapp.common.SessionManager;
import com.pris.citizenapp.login.Login;

public class SplashScreen extends AppCompatActivity {


    // Splash screen timer
    private static int SPLASH_TIME_OUT = 2000;
    //  ProgressBar progress;
    private SessionManager session;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splashscreen);


        session  = new SessionManager(this);



        new Handler().postDelayed(new Runnable() {

            /*
             * Showing splash screen with a timer. This will be useful when you
             * want to show case your app logo / company
             */

            @Override
            public void run() {
                // This method will be executed once the timer is over
                // Start your app main activity



                if(session.isLoggedIn()) {

                    Intent i = new Intent(SplashScreen.this, FooterMain.class);

                    startActivity(i);

                }
                else{
                    Intent i = new Intent(SplashScreen.this, Login.class);

                    startActivity(i);
                }


                // close this activity
                finish();

            }
        }, SPLASH_TIME_OUT);

    }


    @Override
    public void onBackPressed() {
        //  super.onBackPressed();

    }

}
