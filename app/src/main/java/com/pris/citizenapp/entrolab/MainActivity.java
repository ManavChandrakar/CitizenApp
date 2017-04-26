package com.pris.citizenapp.entrolab;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.TypedValue;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.pris.citizenapp.R;
import com.pris.citizenapp.common.SessionManager;
import com.pris.citizenapp.entrolab.Application.Grievance;
import com.pris.citizenapp.entrolab.Application.GrivencelocationImage;
import com.pris.citizenapp.entrolab.Application.PrivateTapApply;
import com.pris.citizenapp.entrolab.Application.RTI;
import com.pris.citizenapp.entrolab.Application.RTIForm;
import com.pris.citizenapp.entrolab.Application.VerifyAadhar;
import com.pris.citizenapp.entrolab.representative.ElectedWing;
import com.pris.citizenapp.entrolab.representative.News;
import com.pris.citizenapp.entrolab.representative.PublicServants;
import com.pris.citizenapp.entrolab.representative.YellowPage;
import com.pris.citizenapp.payments.CheckoutActivity;

public class MainActivity extends AppCompatActivity {

    ImageView news,Elected,publicservent,yellowPage,tax1,privatetap,kolagaaram,advertisement,trade,auction;
    ImageView grievance,rti,ptapsapply;
    Toolbar toolbar;
    TextView name,panchaayatname;
    LinearLayout layout;
    SessionManager session;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        session=new SessionManager(this);
        setContentView(R.layout.citizen_main);
        toolbar=(Toolbar)findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        Elected=(ImageView)findViewById(R.id.elected);
        news=(ImageView)findViewById(R.id.news);
        publicservent=(ImageView)findViewById(R.id.publicservent);
        yellowPage=(ImageView)findViewById(R.id.yellow);
        layout=(LinearLayout)findViewById(R.id.ll);
        layout.setVisibility(View.VISIBLE);
        name=(TextView) findViewById(R.id.tooltext);
        panchaayatname=(TextView)findViewById(R.id.panchaayatname);
        //set panchayat name
        panchaayatname.setText(session.getStrVal("panchayat_name"));
        panchaayatname.setTextSize(TypedValue.COMPLEX_UNIT_SP, 17);

        name.setText(session.getStrVal("name"));

        tax1=(ImageView)findViewById(R.id.tax1);
        privatetap=(ImageView)findViewById(R.id.privatetap);
        kolagaaram=(ImageView)findViewById(R.id.kolagaraam);
        advertisement=(ImageView)findViewById(R.id.advertisement);
        trade=(ImageView)findViewById(R.id.tradetax);
        auction=(ImageView)findViewById(R.id.auction);

        grievance=(ImageView)findViewById(R.id.grievance);
        rti=(ImageView)findViewById(R.id.rti);
        ptapsapply=(ImageView)findViewById(R.id.pvttaps);


        Elected.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i =new Intent(MainActivity.this, ElectedWing.class);
                startActivity(i);
            }
        });


        news.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i =new Intent(MainActivity.this, News.class);
                startActivity(i);
            }
        });

        publicservent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i =new Intent(MainActivity.this, PublicServants.class);
                startActivity(i);
            }
        });

        yellowPage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i =new Intent(MainActivity.this, YellowPage.class);
                startActivity(i);
            }
        });

        tax1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               Intent intent=new Intent(MainActivity.this,PropertyTaxesParent.class);
                startActivity(intent);
            }
        });

        privatetap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(MainActivity.this,PrivateTap.class);
                startActivity(intent);
            }
        });

        kolagaaram.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(MainActivity.this,Kolagaaram.class);
                startActivity(intent);
            }
        });

        advertisement.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(MainActivity.this,AdvertisementTax.class);
                startActivity(intent);
            }
        });

        trade.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(MainActivity.this,TradeLicenceTax.class);
                startActivity(intent);
            }
        });

        auction.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(MainActivity.this,Auction.class);
                startActivity(intent);
            }
        });

        grievance.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               Intent intent =new Intent(MainActivity.this, VerifyAadhar.class);
                intent.putExtra("flag","G");
                //Intent intent =new Intent(MainActivity.this, GrivencelocationImage.class);
                startActivity(intent);
            }
        });

        rti.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent =new Intent(MainActivity.this, VerifyAadhar.class);
                intent.putExtra("flag","R");
                startActivity(intent);
            }
        });

        ptapsapply.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent =new Intent(MainActivity.this, CheckoutActivity.class);
                intent.putExtra("flag","P");
                startActivity(intent);
            }
        });

    }

}
