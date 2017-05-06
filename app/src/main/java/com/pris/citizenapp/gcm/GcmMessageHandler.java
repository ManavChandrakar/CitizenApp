package com.pris.citizenapp.gcm;

import android.app.IntentService;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.NotificationCompat;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.pris.citizenapp.Fragments.Notification;
import com.pris.citizenapp.R;
import com.pris.citizenapp.common.SessionManager;
import com.pris.citizenapp.entrolab.Application.Grevancedetails;
import com.pris.citizenapp.entrolab.Application.Grievance;
import com.pris.citizenapp.entrolab.Application.PrivateTapApply;
import com.pris.citizenapp.entrolab.Application.RTI;
import com.pris.citizenapp.entrolab.PrivateTap;
import com.pris.citizenapp.entrolab.representative.News;

import java.text.SimpleDateFormat;

/**
 * Created by manav on 1/5/17.
 */

public class GcmMessageHandler extends IntentService {

    String mes;
    private Handler handler;

    private SessionManager session;
    public GcmMessageHandler() {
        super("GcmMessageHandler");
    }

    @Override
    public void onCreate() {
        // TODO Auto-generated method stub
        super.onCreate();
        handler = new Handler();

        session = new SessionManager(getApplicationContext());
    }
    @Override
    protected void onHandleIntent(Intent intent) {
        Bundle extras = intent.getExtras();

        GoogleCloudMessaging gcm = GoogleCloudMessaging.getInstance(this);
        // The getMessageType() intent parameter must be the intent you received
        // in your BroadcastReceiver.
        String messageType = gcm.getMessageType(intent);

        mes = extras.getString("title")+" --- "+extras.getString("message");


        Log.d("GCM",mes);
        Toast.makeText(this,mes.toString(),Toast.LENGTH_LONG).show();
      // Intent nintent = new Intent(getApplicationContext(), Notifications.class);
        Intent nintent = new Intent(getApplicationContext(), Notification.class);
        String check=extras.getString("tag");
        String[] temp=new String[2];
        try {
            temp = check.trim().split("#");

        } catch (Exception ex){
            ex.printStackTrace();
        }

       try {
            if (temp[0].equals("grievance")) {
                nintent.putExtra("flagid",temp[1]);
                nintent = new Intent(getApplicationContext(), Grievance.class);

            }
            else if (temp[0].equals("rti_application")) {
                nintent.putExtra("flagid",temp[1]);
                nintent = new Intent(getApplicationContext(), RTI.class);

            }
            else if (temp[0].equals("panchayat_news")) {
                nintent = new Intent(getApplicationContext(), News.class);

            }
            else if (temp[0].equals("private_tap")) {
                nintent.putExtra("flagid",temp[1]);
                nintent = new Intent(getApplicationContext(), PrivateTapApply.class);

            }
     //       else if (extras.getString("message").equals("")) {
     //           nintent = new Intent(getApplicationContext(), Inbox.class);

        }
        catch (NullPointerException e){
            e.printStackTrace();
        }


        PendingIntent pIntent = PendingIntent.getActivity(this, 0, nintent, 0);



        String longText = extras.getString("title");
        String smallText = longText;


        assert longText != null;
        if(longText.length() > 60){

            smallText = smallText.substring(0,58)+"..";

        }

        long timestand = 1;
        if (extras.getString("timestamp").length() > 0) {
            timestand = (Integer.parseInt(extras.getString("timestamp"))) * 1000L;
        }

        String time =  new SimpleDateFormat("MM dd, yyyy h:ma").format(timestand);

// build notification
// the addAction re-use the same intent to keep the example short

        //Define sound URI
        Uri soundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        Bitmap largeIcon = BitmapFactory.decodeResource(getResources(), R.drawable.ic_panchayatlogo);

        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(getApplicationContext());
        mBuilder.setLargeIcon(largeIcon);
        mBuilder.setSmallIcon(R.drawable.ic_panchayatlogo);
        mBuilder.setContentTitle(extras.getString("title"));
        mBuilder.setContentText("posted on "+time);
        mBuilder.setContentIntent(pIntent).setAutoCancel(true).setGroup("com.pris.citizenapp").setSound(soundUri);


        NotificationManager notificationManager =
                (NotificationManager) getSystemService(NOTIFICATION_SERVICE);

        notificationManager.notify(0, mBuilder.build());



        GcmBroadcastReceiver.completeWakefulIntent(intent);

    }

    public void showToast(){
        handler.post(new Runnable() {
            public void run() {
                Toast.makeText(getApplicationContext(), "You have a new Notification!", Toast.LENGTH_LONG).show();
            }
        });

    }
}
