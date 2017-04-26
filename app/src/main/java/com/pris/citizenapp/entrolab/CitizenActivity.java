package com.pris.citizenapp.entrolab;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.Toolbar;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.pris.citizenapp.Certificates.BirthCft;
import com.pris.citizenapp.Certificates.DeathCft;
import com.pris.citizenapp.Certificates.MarriageCft;
import com.pris.citizenapp.Certificates.PropertyCerificate;
import com.pris.citizenapp.Certificates.TradeLicenceCft;
import com.pris.citizenapp.R;
import com.pris.citizenapp.adapters.CustomSwipeAdapter;
import com.pris.citizenapp.common.SessionManager;
import com.pris.citizenapp.entrolab.AdvertisementTax;
import com.pris.citizenapp.entrolab.Application.Grievance;
import com.pris.citizenapp.entrolab.Application.PrivateTapApply;
import com.pris.citizenapp.entrolab.Application.RTI;
import com.pris.citizenapp.entrolab.Application.VerifyAadhar;
import com.pris.citizenapp.entrolab.Auction;
import com.pris.citizenapp.entrolab.Kolagaaram;
import com.pris.citizenapp.entrolab.MainActivity;
import com.pris.citizenapp.entrolab.PrivateTap;
import com.pris.citizenapp.entrolab.PropertyTaxesParent;
import com.pris.citizenapp.entrolab.TradeLicenceTax;
import com.pris.citizenapp.entrolab.representative.ElectedWing;
import com.pris.citizenapp.entrolab.representative.News;
import com.pris.citizenapp.entrolab.representative.PublicServants;
import com.pris.citizenapp.entrolab.representative.YellowPage;
import com.pris.citizenapp.payments.CheckoutActivity;

import java.util.ArrayList;

import static com.pris.citizenapp.common.SessionManager.USER_FULL_NAME;

/**
 * Created by manav on 16/4/17.
 */

public class CitizenActivity extends Fragment {

    ImageView news,Elected,publicservent,yellowPage,tax1,privatetap,kolagaaram,advertisement,trade,auction;
    ImageView grievance,rti,ptapsapply,property_certificate,marriage_certificate,birthcertificate,death_certificate,trade_certificate;
    Toolbar toolbar;
    TextView name,panchaayatname;
    LinearLayout layout;
    SessionManager session;
    ViewPager viewPager;
    CustomSwipeAdapter customSwipeAdapter;
    ArrayList<String>all;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.citizen_main,container,false);
    }

    @Override
    public void onViewCreated(View view, @Nullable final Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        session=new SessionManager(view.getContext());
        toolbar=(Toolbar)view.findViewById(R.id.toolbar);


        //just for testing
        all=new ArrayList<String>();
        all.add("http://pris.ap.gov.in/mobile/house_tag/files/11nhvhP.jpg");
        all.add("http://pris.ap.gov.in/mobile/house_tag/files/11nhvhP.jpg");
        all.add("http://pris.ap.gov.in/mobile/house_tag/files/11nhvhP.jpg");
        all.add("http://pris.ap.gov.in/mobile/house_tag/files/11nhvhP.jpg");
        //


        viewPager=(ViewPager)view.findViewById(R.id.pager);
        customSwipeAdapter=new CustomSwipeAdapter(view.getContext(),all);
        viewPager.setAdapter(customSwipeAdapter);

        TabLayout tabLayout = (TabLayout) view.findViewById(R.id.tab_layout);
        tabLayout.setupWithViewPager(viewPager, true);
        //setSupportActionBar(toolbar);
        //getSupportActionBar().setDisplayShowTitleEnabled(false);*/
        Elected=(ImageView)view.findViewById(R.id.elected);
        news=(ImageView)view.findViewById(R.id.news);
        publicservent=(ImageView)view.findViewById(R.id.publicservent);
        yellowPage=(ImageView)view.findViewById(R.id.yellow);
        layout=(LinearLayout)view.findViewById(R.id.ll);
        layout.setVisibility(View.VISIBLE);
        name=(TextView) view.findViewById(R.id.tooltext);
        panchaayatname=(TextView)view.findViewById(R.id.panchaayatname);
        //set panchayat name
        panchaayatname.setText(session.getStrVal("panchayat_name"));
        panchaayatname.setTextSize(TypedValue.COMPLEX_UNIT_SP, 17);

        name.setText(session.getStrVal(USER_FULL_NAME));

        tax1=(ImageView)view.findViewById(R.id.tax1);
        privatetap=(ImageView)view.findViewById(R.id.privatetap);
        kolagaaram=(ImageView)view.findViewById(R.id.kolagaraam);
        advertisement=(ImageView)view.findViewById(R.id.advertisement);
        trade=(ImageView)view.findViewById(R.id.tradetax);
        auction=(ImageView)view.findViewById(R.id.auction);

        grievance=(ImageView)view.findViewById(R.id.grievance);
        rti=(ImageView)view.findViewById(R.id.rti);
        ptapsapply=(ImageView)view.findViewById(R.id.pvttaps);
        property_certificate=(ImageView)view.findViewById(R.id.propert_certificate);
        marriage_certificate=(ImageView)view.findViewById(R.id.marriagecft);
        birthcertificate=(ImageView)view.findViewById(R.id.birthcft);
        death_certificate=(ImageView)view.findViewById(R.id.deathcft);
        trade_certificate=(ImageView)view.findViewById(R.id.trade_licencectf);


        Elected.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i =new Intent(view.getContext(), ElectedWing.class);
                startActivity(i);
            }
        });


        news.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i =new Intent(view.getContext(), News.class);
                startActivity(i);
            }
        });

        publicservent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i =new Intent(view.getContext(), PublicServants.class);
                startActivity(i);
            }
        });

        yellowPage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i =new Intent(view.getContext(), YellowPage.class);
                startActivity(i);
            }
        });

        tax1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(view.getContext(),PropertyTaxesParent.class);
                startActivity(intent);
            }
        });

        privatetap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(view.getContext(),PrivateTap.class);
                startActivity(intent);
            }
        });

        kolagaaram.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(view.getContext(),Kolagaaram.class);
                startActivity(intent);
            }
        });

        advertisement.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(view.getContext(),AdvertisementTax.class);
                startActivity(intent);
            }
        });

        trade.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(view.getContext(),TradeLicenceTax.class);
                startActivity(intent);
            }
        });

        auction.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(view.getContext(),Auction.class);
                startActivity(intent);
            }
        });

        grievance.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent =new Intent(view.getContext(), Grievance.class);
                //Intent intent =new Intent(MainActivity.this, GrivencelocationImage.class);
                startActivity(intent);
            }
        });

        rti.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent =new Intent(view.getContext(), RTI.class);
                startActivity(intent);
            }
        });

        ptapsapply.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent =new Intent(view.getContext(), PrivateTapApply.class);
                startActivity(intent);
            }
        });

        property_certificate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(view.getContext(), PropertyCerificate.class);
                startActivity(intent);
            }
        });

        birthcertificate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(view.getContext(),BirthCft.class);
                 startActivity(intent);
            }
        });

        death_certificate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(view.getContext(),DeathCft.class);
                startActivity(intent);
            }
        });

        marriage_certificate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(view.getContext(),MarriageCft.class);
                startActivity(intent);
            }
        });

        trade_certificate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(view.getContext(),TradeLicenceCft.class);
                startActivity(intent);
            }
        });
    }
}

