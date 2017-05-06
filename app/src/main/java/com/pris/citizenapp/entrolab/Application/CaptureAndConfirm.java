package com.pris.citizenapp.entrolab.Application;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.location.Address;
import android.location.Geocoder;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.StatFs;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.util.Patterns;
import android.view.Display;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.drawee.view.SimpleDraweeView;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.pris.citizenapp.R;
import com.pris.citizenapp.common.ImageLoadingUtils;
import com.pris.citizenapp.common.SessionManager;
import com.pris.citizenapp.entrolab.FooterMain;
import com.pris.citizenapp.github.kevinsawicki.http.HttpRequest;
import com.pris.citizenapp.login.Login;
import com.pris.citizenapp.login.Register;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Random;

import static com.pris.citizenapp.common.SessionManager.USER_EMAIL;
import static com.pris.citizenapp.common.SessionManager.USER_FULL_NAME;
import static com.pris.citizenapp.common.SessionManager.USER_MOBILE;

/**
 * Created by manav on 12/4/17.
 */

public class CaptureAndConfirm extends AppCompatActivity implements OnMapReadyCallback {


    private GoogleMap mMap; // Might be null if Google Play services APK is not available.
    private SessionManager session;
    static final int REQUEST_TAKE_PHOTO = 1;
    private double latitude_v;
    private double longitude_v;
    private static final int TAKE_PICTURE = 100;

    private String file_name = "file";
    private String appended_image="";
    private Uri mUri;
    TextView ClickCat,ClickSubcat;


    private ImageLoadingUtils utils;

    MaterialDialog progress;
    MaterialDialog dialog;

    public String tag;
    private static final String ALLOWED_CHARACTERS = "0123456789qwertyuiopasdfghjklzxcvbnm";
    TextView loc;
    TextView gpstxt;
    EditText myname,mobile,mail,cat,subcat,details,landmark;

    ArrayList<String> category;
    HashMap<String,String> mapcategory ;

    ArrayList<String> subcategory;
    HashMap<String,String> mapsubcat ;

   // private Realm realm;
    SimpleDraweeView capimg;
    MaterialDialog popAlert;
    LinearLayout lln;
    View vie;
    TextView tv1,tv2,tv3,tv4,tv5;
    private SimpleDateFormat dateFormatter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Fresco.initialize(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.capture);
        setTitle("Grevience Form");
        Typeface head = Typeface.createFromAsset(getAssets(), "fonts/Roboto_Light.ttf");


        //   Realm.init(this);

      //  realm = Realm.getDefaultInstance();
      //  setTitle("Grevience");
        session = new SessionManager(this);
        tv1=(TextView)findViewById(R.id.tv1);
        tv1.setTypeface(head);
        tv2=(TextView)findViewById(R.id.tv2);
        tv2.setTypeface(head);
        tv3=(TextView)findViewById(R.id.tv3);
        tv3.setTypeface(head);
        tv4=(TextView)findViewById(R.id.tv4);
        tv4.setTypeface(head);
        tv5=(TextView)findViewById(R.id.tv5);
        tv5.setTypeface(head);
        myname=(EditText)findViewById(R.id.name);
        myname.setText(session.getStrVal(USER_FULL_NAME));
        mail=(EditText)findViewById(R.id.mail);
        mail.setText(session.getStrVal(USER_EMAIL));
        mobile=(EditText)findViewById(R.id.mobile);
        cat=(EditText)findViewById(R.id.category);
        mobile.setText(session.getStrVal(USER_MOBILE));
        subcat=(EditText)findViewById(R.id.subcat);
        details=(EditText)findViewById(R.id.editdetails);
        landmark=(EditText)findViewById(R.id.landmark);
        ClickCat=(TextView) findViewById(R.id.clickcat);
        ClickCat.setTypeface(head);
        ClickSubcat=(TextView) findViewById(R.id.clicksubcat);
        ClickSubcat.setTypeface(head);
        category=new ArrayList<String>();
        subcategory=new ArrayList<String>();
        vie=(View)findViewById(R.id.vw);

        lln=(LinearLayout)findViewById(R.id.myll);

        ClickCat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new MaterialDialog.Builder(CaptureAndConfirm.this)
                        .title("Categories")
                        .items(category)
                        .positiveColor(getResources().getColor(R.color.appcolor))
                        .itemsCallbackSingleChoice(-1, new MaterialDialog.ListCallbackSingleChoice() {
                            @Override
                            public boolean onSelection(MaterialDialog dialog, View view, int which, CharSequence text) {

                                Log.d("Selected", String.valueOf(text) + " - " + which);
                                String selected = String.valueOf(text);
                                cat.setText(String.valueOf(text));
                                String uid = mapcategory.get(selected);
                                session.storeVal("category", uid);

                                if (selected.equals("null")) {
                                    Toast.makeText(CaptureAndConfirm.this, "select a valid field", Toast.LENGTH_LONG).show();
                                } else {
                                    Map<String, String> params1 = new LinkedHashMap<String, String>();
                                    params1.put("getSubCategories", "true");
                                    params1.put("category", uid);
                                    new webService().execute(params1);
                                }

                                dialog.hide();
                                return true;
                            }
                        })
                        .positiveText("Ok").show();
            }
        });


     ClickSubcat.setOnClickListener(new View.OnClickListener() {
         @Override
         public void onClick(View view) {

             if (!(cat.getText().toString().equals("null"))) {

                 new MaterialDialog.Builder(CaptureAndConfirm.this)
                         .title("SubCategories")
                         .items(subcategory)
                         .positiveColor(getResources().getColor(R.color.appcolor))
                         .itemsCallbackSingleChoice(-1, new MaterialDialog.ListCallbackSingleChoice() {
                             @Override
                             public boolean onSelection(MaterialDialog dialog, View view, int which, CharSequence text) {

                                 Log.d("Selected", String.valueOf(text) + " - " + which);
                                 String selected = String.valueOf(text);
                                 subcat.setText(String.valueOf(text));
                                 String uid = mapsubcat.get(selected);
                                 session.storeVal("subcategory", uid);


                                 if (session.haveNetworkConnection()) {
                                     if (selected.equals("null")) {

                                         Toast.makeText(CaptureAndConfirm.this, "select a valid field", Toast.LENGTH_LONG).show();
                                     }
                                 }
                                 dialog.hide();
                                 return true;
                             }
                         })
                         .positiveText("Ok").show();
             } else {
                 Toast.makeText(CaptureAndConfirm.this, "selct above fields", Toast.LENGTH_LONG).show();

             }

         }
     });


        utils = new ImageLoadingUtils(CaptureAndConfirm.this);

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

        loc = (TextView) findViewById(R.id.location);
        gpstxt = (TextView) findViewById(R.id.gps);




      /*  if (session.hasVal("assessment")) {
           // assessment.setText(session.getStrVal("assessment"));
          //  session.removeVal("assessment");
        }*/
        if (session.haveNetworkConnection()) {

                Map<String, String> params = new LinkedHashMap<String, String>();
                params.put("getCategories", "true");
                new webService().execute(params);

        }




        if (session.hasVal("clat") && session.hasVal("clongd")) {

            latitude_v = Double.parseDouble(session.getStrVal("clat"));
            longitude_v = Double.parseDouble(session.getStrVal("clongd"));

            gpstxt.setText(String.valueOf(latitude_v) + "," + String.valueOf(longitude_v));
            loc.setText(getAddress(latitude_v, longitude_v));
            Log.d("MapsActivity", "Loc: " + latitude_v + " - " + longitude_v);
        } else {

            Toast.makeText(this, "Confirm Location First!", Toast.LENGTH_SHORT).show();
            finish();
        }


       setUpMapIfNeeded();


        Button capture = (Button) findViewById(R.id.capture);
        capture.setTypeface(head);

        capture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (getAvailableSpaceInMB() >= 10) {

                    Intent icapture = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

                    File mediaStorageDir = new File(Environment.getExternalStorageDirectory()
                            + "/citizenapp");
                    if (!mediaStorageDir.exists()) {
                        if (!mediaStorageDir.mkdirs()) {

                            Log.d("Report", "Dirs not made");
                        }
                    }
                    tag = session.getStrVal("division") + getRandomString(5);

                    File f = new File(Environment.getExternalStorageDirectory() + "/citizenapp", tag + "P.jpg");
                    file_name = tag + "P.jpg";

                    Log.d("StartCapture", "File: " + Environment.getExternalStorageDirectory() + "/citizenapp" + tag + "P.jpg");


                    icapture.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(f));

                    mUri = Uri.fromFile(f);
                    startActivityForResult(icapture, TAKE_PICTURE);
                }

                 else {
                    Toast.makeText(CaptureAndConfirm.this,"Memory full kindly empty some space",Toast.LENGTH_LONG).show();
                }

            }
        });


        Button save = (Button) findViewById(R.id.save);
        save.setTypeface(head);

        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String mystrres=imageLogic();
                boolean res = validate();

                if (res) {


                    if (session.haveNetworkConnection()) {
                        Map<String, String> params = new LinkedHashMap<String, String>();

                        params.put("newGrievance", "true");

                        String deviceParams = "Device:" + Build.DEVICE;
                        deviceParams += " - Display:" + Build.DISPLAY;
                        deviceParams += " - Manufacturer:" + Build.MANUFACTURER;
                        deviceParams += " - Model:" + Build.MODEL;


                        params.put("deviceinfo", deviceParams);
                        //
                        params.put("category", session.getStrVal("category"));
                        params.put("subcategory", session.getStrVal("subcategory"));
                        params.put("details", details.getText().toString());
                        params.put("location", loc.getText().toString());
                        params.put("lat", String.valueOf(latitude_v));
                        params.put("lng", String.valueOf(longitude_v));
                        params.put("landmark", landmark.getText().toString());
                        params.put("panchayat", session.getStrVal("panchayat"));
                        params.put("name", myname.getText().toString());
                        params.put("mobile", mobile.getText().toString());
                        params.put("email", mail.getText().toString());
                        params.put("aadhar", session.getStrVal("myaadhar"));
                        //params.put("aadhar", "412539307900");
                        params.put("images", file_name);
                        params.put("username", session.getStrVal("username"));

                        //


                      /*  params.put("gps_location", String.valueOf(loc.getText()));
                        params.put("latitude", String.valueOf(latitude_v));
                        params.put("longitude", String.valueOf(longitude_v));
                        params.put("assessment", String.valueOf(assessment.getText()));
                        params.put("block", String.valueOf(block.getText()));
                        params.put("image", file_name);

                        params.put("tag", tag);
                        params.put("username", session.getStrVal(session.USER_NAME));
                        params.put("panchayat", session.getStrVal("panchayat"));*/

                        new webServiceaddgrevience().execute(params);

                        //Toast.makeText(CaptureAndConfirm.this, "House Tag Added Successfully!", Toast.LENGTH_LONG).show();

                       /* session.removeVal("clat");
                        session.removeVal("clongd");
                        String destPath = Environment.getExternalStorageDirectory() + "/citizenapp/" + file_name;
                        session.storeVal("imagepath", destPath);*/
               /* Intent i = new Intent(CaptureAndConfirm.this, GrievanceForm.class);
                i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(i);*/
                       // finish();
                    }
                }
            }
        });

    }

    private String imageLogic() {
        String newimag=appended_image.trim();
        newimag = newimag.replace(' ', ',');
        return newimag;

    }

    private boolean validate() {
        int error = 0;
        String errorTxt ="error";


        if(details.getText().toString().length()==0)
        {
            errorTxt = "Enter Details of Grevience";
            error++;
        }

        if(subcat.getText().toString().length()==0 || subcat.getText().toString().equals("null"))
        {
            errorTxt = "Select Subcategory";
            error++;
        }

        if(cat.getText().toString().length()==0 || cat.getText().toString().equals("null"))
        {
            errorTxt = "Select Category";
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
            if (myname.getText().toString().trim().length() == 0) {
                errorTxt = "Full Name Mandatory";
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

    private static String getRandomString(final int sizeOfRandomString) {
        final Random random = new Random();
        final StringBuilder sb = new StringBuilder(sizeOfRandomString);
        for (int i = 0; i < sizeOfRandomString; ++i)
            sb.append(ALLOWED_CHARACTERS.charAt(random.nextInt(ALLOWED_CHARACTERS.length())));
        return sb.toString();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == Activity.RESULT_OK) {

            this.getContentResolver().notifyChange(mUri, null);

            ContentResolver cr = this.getContentResolver();

            Log.d("ActivityResult", "Result data: " + file_name + ", Image saved to:" + mUri);

            new ImageCompressionAsyncTask(true).execute(mUri.toString());


        } else {
            // User cancelled the image capture

           Toast.makeText(CaptureAndConfirm.this, "Image Capture Cancelled ", Toast.LENGTH_SHORT).show();
        }

    }


    @Override
    protected void onResume() {
        super.onResume();
        setUpMapIfNeeded();
    }

    private void setUpMapIfNeeded() {
        // Do a null check to confirm that we have not already instantiated the map.
        if (mMap == null) {
            // Try to obtain the map from the SupportMapFragment.
            //mMap = ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map));
            SupportMapFragment supportMapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
            supportMapFragment.getMapAsync(this);
        }
    }


    private void setUpMap() {
        //  mMap.addMarker(new MarkerOptions().position(new LatLng(0, 0)).title("Marker"));

        // mMap.setMyLocationEnabled(true);
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(latitude_v, longitude_v), 16));

        mMap.setPadding(0, 0, 20, 20);


        MarkerOptions markerOptions = new MarkerOptions().position(new LatLng(latitude_v, longitude_v)).title(
                "GPS Tag #" + tag).draggable(false);
        mMap.addMarker(markerOptions);

    }

    private String getAddress(Double lat, Double longt) {
        String address = "na";


        if (session.haveNetworkConnection()) {
            Geocoder geo = new Geocoder(CaptureAndConfirm.this);
            try {
                List<Address> addresses = geo.getFromLocation(lat, longt, 1);

                if (addresses != null && !addresses.isEmpty()) {
                    Log.d("Address", "Address: " + addresses.get(0));

                    address = "";
                    Address ad = addresses.get(0);
                    for (int i = 0; i < ad.getMaxAddressLineIndex(); i++) {
                        address += ad.getAddressLine(i);
                    }

                }
            } catch (IOException e) {
                e.printStackTrace();
            }

        }

        return address;
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap=googleMap;
        mMap.getUiSettings().setScrollGesturesEnabled(false);
        if (mMap != null) {
            setUpMap();
        }

    }

/*
    private class webService extends AsyncTask<Map, Integer, String>

    {


        @Override
        protected String doInBackground(Map... params) {

            return postData(params[0]);
        }

        @Override
        protected void onPreExecute() {


            progress.setContent("Sending to Server...");
            progress.show();
            super.onPreExecute();


        }

        protected void onPostExecute(String response) {

            progress.dismiss();

            JSONObject result = null;

            Log.d("webService", "HTTP Request Result: " + response);

            try {

                result = new JSONObject(response);

                String res = result.getString("result");

                //Log.d("HTTP result filesync",response.toString());

                if (res.trim().equals("success")) {


                    if (result.has("addTag")) {

                        Toast.makeText(CaptureAndConfirm.this, "House Tag Added Successfully!", Toast.LENGTH_LONG).show();

                        session.removeVal("clat");
                        session.removeVal("clongd");

                        Intent i = new Intent(CaptureAndConfirm.this, GrievanceForm.class);
                        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(i);
                        finish();


                    }


                    //Log.d("DB Sync","Updating Sync status:"+ret);
                } else {
                    dialog.setTitle("Oops! Error");
                    dialog.setContent(result.getString("error"));
                    dialog.show();
                }


            } catch (JSONException e) {
                e.printStackTrace();
            }


        }

        protected void onProgressUpdate(Integer... progress) {
        }

        public String postData(Map data) {

            String response = "{\"result\":\"failed\"}";

            try {

                response = HttpRequest.post("http://130.211.128.117/mobile/house_tag/mobile.php").form(data).body();

            } catch (Exception e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }

            return response;


        }


    }*/

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


                    if (result.has("categories")) {

                        JSONArray array = result.getJSONArray("categories");
                        mapcategory = new HashMap<String, String>();
                        for (int i = 0; i < array.length(); i++) {
                            JSONObject obj = array.getJSONObject(i);
                            String d = obj.getString("category");
                            String uid = obj.getString("uid");
                            category.add(d);
                            mapcategory.put(d, uid);
                        }
                        Log.d("ARRAYLIST", String.valueOf(mapcategory));

                    }

                    if (result.has("subcategories")) {

                        JSONArray array = result.getJSONArray("subcategories");
                        mapsubcat = new HashMap<String, String>();
                        for (int i = 0; i < array.length(); i++) {
                            JSONObject obj = array.getJSONObject(i);
                            String d = obj.getString("subcategory");
                            String uid = obj.getString("uid");
                            subcategory.add(d);
                            mapsubcat.put(d, uid);
                        }
                        Log.d("ARRAYLIST", String.valueOf(mapsubcat));

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




    class ImageCompressionAsyncTask extends AsyncTask<String, Void, String> {
        private boolean fromGallery;

        public ImageCompressionAsyncTask(boolean fromGallery) {
            this.fromGallery = fromGallery;
        }

        @Override
        protected String doInBackground(String... params) {
            String filePath = compressImage(mUri.toString());
            return filePath;
        }

        public String compressImage(String imageUri) {

            Log.d("ImageResizeTask", "Compress img: " + imageUri + ", Real:" + getRealPathFromURI(imageUri));
//            String filePath = getRealPathFromURI(imageUri);
            String filePath = (imageUri);

            Bitmap scaledBitmap = null;

            File imagefile = new File(imageUri);
// Get the stream


            BitmapFactory.Options options = new BitmapFactory.Options();

            options.inJustDecodeBounds = true;
            Bitmap bmp = BitmapFactory.decodeFile(mUri.getPath(), options);

            Log.d("ImageResizeTask", "Image params: .. height:" + options.outHeight + ", width: " + options.outWidth);

            int actualHeight = options.outHeight;
            int actualWidth = options.outWidth;
            float maxHeight = 1080.0f;
            float maxWidth = 1920.0f;
            float imgRatio = actualWidth / actualHeight;
            float maxRatio = maxWidth / maxHeight;

            if (actualHeight > maxHeight || actualWidth > maxWidth) {
                if (imgRatio < maxRatio) {
                    imgRatio = maxHeight / actualHeight;
                    actualWidth = (int) (imgRatio * actualWidth);
                    actualHeight = (int) maxHeight;
                } else if (imgRatio > maxRatio) {
                    imgRatio = maxWidth / actualWidth;
                    actualHeight = (int) (imgRatio * actualHeight);
                    actualWidth = (int) maxWidth;
                } else {
                    actualHeight = (int) maxHeight;
                    actualWidth = (int) maxWidth;

                }
            }

            try {

                options.inSampleSize = utils.calculateInSampleSize(options, actualWidth, actualHeight);
                options.inJustDecodeBounds = false;
                options.inDither = false;
                options.inPurgeable = true;
                options.inInputShareable = true;
                options.inTempStorage = new byte[16 * 1024];

            } catch (NullPointerException e) {
                e.printStackTrace();
            }

            try {

                bmp = BitmapFactory.decodeFile(mUri.getPath(), options);
            } catch (OutOfMemoryError exception) {
                exception.printStackTrace();

            }
            try {
                scaledBitmap = Bitmap.createBitmap(actualWidth, actualHeight, Bitmap.Config.ARGB_8888);
            } catch (OutOfMemoryError exception) {
                exception.printStackTrace();
            }

            float ratioX = actualWidth / (float) options.outWidth;
            float ratioY = actualHeight / (float) options.outHeight;
            float middleX = actualWidth / 2.0f;
            float middleY = actualHeight / 2.0f;

            Matrix scaleMatrix = new Matrix();
            scaleMatrix.setScale(ratioX, ratioY, middleX, middleY);


            try {
                Canvas canvas = new Canvas(scaledBitmap);
                canvas.setMatrix(scaleMatrix);


                canvas.drawBitmap(bmp, middleX - bmp.getWidth() / 2, middleY - bmp.getHeight() / 2, new Paint(Paint.FILTER_BITMAP_FLAG));
            } catch (NullPointerException e) {
                e.printStackTrace();
            }

            ExifInterface exif;
            try {
                exif = new ExifInterface(filePath);

                int orientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, 0);
                Log.d("EXIF", "Exif: " + orientation);
                Matrix matrix = new Matrix();
                if (orientation == 6) {
                    matrix.postRotate(90);
                    Log.d("EXIF", "Exif: " + orientation);
                } else if (orientation == 3) {
                    matrix.postRotate(180);
                    Log.d("EXIF", "Exif: " + orientation);
                } else if (orientation == 8) {
                    matrix.postRotate(270);
                    Log.d("EXIF", "Exif: " + orientation);
                }
                scaledBitmap = Bitmap.createBitmap(scaledBitmap, 0, 0, scaledBitmap.getWidth(), scaledBitmap.getHeight(), matrix, true);
            } catch (IOException e) {
                e.printStackTrace();
            }
            FileOutputStream out = null;
            String filename = mUri.getPath();
            try {
                out = new FileOutputStream(filename);
                scaledBitmap.compress(Bitmap.CompressFormat.JPEG, 100, out);

            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }

            return filename;

        }

        private String getRealPathFromURI(String contentURI) {
            Uri contentUri = Uri.parse(contentURI);
            Cursor cursor = getContentResolver().query(contentUri, null, null, null, null);
            if (cursor == null) {
                return contentUri.getPath();
            } else {
                cursor.moveToFirst();
                int idx = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA);
                return cursor.getString(idx);
            }
        }

        public String getFilename() {
            File file = new File(Environment.getExternalStorageDirectory().getPath(), "citizenapp");
            if (!file.exists()) {
                file.mkdirs();
            }
            String uriSting = (file.getAbsolutePath() + "/" + System.currentTimeMillis() + ".jpg");
            return uriSting;

        }


        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            Log.d("ImageResizeTask", "Image compressed " + result);
          /*  capimg.setVisibility(View.VISIBLE);

            Uri uri = Uri.fromFile(new File(result));
            capimg.setImageURI(uri);*/
            ImageView image=new ImageView(CaptureAndConfirm.this);
            View view=new View(CaptureAndConfirm.this);
            int hei=(int)((getResources().getDisplayMetrics().heightPixels)/5);
            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, hei);
            image.setLayoutParams(layoutParams);
            image.setScaleType(ImageView.ScaleType.CENTER_CROP);
            Uri uri = Uri.fromFile(new File(result));
            image.setImageURI(uri);
            LinearLayout.LayoutParams arams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 20);
            view.setLayoutParams(arams);
            lln.addView(image);
            lln.addView(view);


            //uploading to server

            if (session.haveNetworkConnection()) {
                Map<String, String> params = new LinkedHashMap<String, String>();

                params.put("uploadFile", "true");

                params.put("tag", tag);
                params.put("username", session.getStrVal("username"));

                new uploadService().execute(params);
            }

        }

    }


    private class uploadService extends AsyncTask<Map, Integer, String>

    {


        @Override
        protected String doInBackground(Map... params) {

            return postData(params[0]);
        }

        @Override
        protected void onPreExecute() {

            progress.setTitle("Uploading Image to Server");
            progress.show();

            super.onPreExecute();


        }

        protected void onPostExecute(String response) {


            progress.dismiss();
            JSONObject result = null;

            Log.d("webService", "HTTP Request Result: " + response);

            try {

                result = new JSONObject(response);

                String res = result.getString("result");

                //Log.d("HTTP result filesync",response.toString());

                if (res.trim().equals("success")) {


                    session.storeVal("image", result.getString("filename"));
                    appended_image+=result.getString("filename")+" ";
                    Toast.makeText(CaptureAndConfirm.this, "Image Uploaded!", Toast.LENGTH_SHORT).show();

                    Log.d("ImageUpload", "image uploaded, calling java.");


                    /*capimg.setVisibility(View.VISIBLE);
                    Uri uri = Uri.parse(result.getString("filename"));
                    capimg.setImageURI(uri);

                    capimg.refreshDrawableState();*/


                    ImageView image=new ImageView(CaptureAndConfirm.this);
                    Uri uri = Uri.parse(result.getString("filename"));
                    image.setImageURI(uri);
                    lln.addView(image);

                   /* final Uri viewuri = uri;
                    capimg.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            Intent intent = new Intent();
                            intent.setAction(android.content.Intent.ACTION_VIEW);

                            intent.setDataAndType(viewuri, "image*//*");
                            startActivity(intent);
                        }
                    });*/

                    //Log.d("DB Sync","Updating Sync status:"+ret);


                    //Log.d("DB Sync","Updating Sync status:"+ret);
                }


            } catch (JSONException e) {
                e.printStackTrace();
            }


        }

        protected void onProgressUpdate(Integer... progress) {
        }

        public String postData(Map data) {

            String response = "{\"result\":\"failed\"}";

            try {

                String destPath = Environment.getExternalStorageDirectory() + "/citizenapp/" + file_name;

                HttpRequest request = HttpRequest.post("http://130.211.128.117/mobile/house_tag/mobile.php");
                request.part("filename", file_name);
                request.part("tag", tag);
                request.part("uploadFile", "uploadfile");
                request.part("image", file_name, new File(destPath));
                if (request.ok())
                    System.out.println("Status was updated");

                response = request.body();

            } catch (Exception e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }

            return response;

        }

    }


/*
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.


        if (session.isLoggedIn()) {
            getMenuInflater().inflate(R.menu.login_menu, menu);


        }


        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if (item.getItemId() == R.id.sign_out) {

          //  session.logoutUser();
            Intent intent = new Intent(CaptureAndConfirm.this, VerifyAadhar.class);
            startActivity(intent);

            finish();

        }


        return super.onOptionsItemSelected(item);
    }*/

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


                    if (result.has("grievance_id")) {

                   /*     popAlert.setTitle("Grevience id");
                        popAlert.setContent(result.getString("message"));
                        popAlert.show();*/

                        new MaterialDialog.Builder(CaptureAndConfirm.this)
                                .title("Grevience Id")
                                .titleColor(getResources().getColor(R.color.appcolor))
                                .content(result.getString("message"))
                                .contentColor(getResources().getColor(R.color.appcolor))
                                .positiveColor(getResources().getColor(R.color.appcolor))
                                 .onPositive(new MaterialDialog.SingleButtonCallback() {
                                     @Override
                                     public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                                         Intent intent=new Intent(CaptureAndConfirm.this, FooterMain.class);
                                         intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                         startActivity(intent);
                                     }
                                 })
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

    public static long getAvailableSpaceInMB(){
        final long SIZE_KB = 1024L;
        final long SIZE_MB = SIZE_KB * SIZE_KB;
        long availableSpace = -1L;
        StatFs stat = new StatFs(Environment.getExternalStorageDirectory().getPath());
        availableSpace = (long) stat.getAvailableBlocks() * (long) stat.getBlockSize();
        return availableSpace/SIZE_MB;
    }

}

