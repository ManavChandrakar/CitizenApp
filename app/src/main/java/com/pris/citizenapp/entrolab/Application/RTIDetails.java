package com.pris.citizenapp.entrolab.Application;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;

import com.afollestad.materialdialogs.MaterialDialog;
import com.facebook.drawee.backends.pipeline.Fresco;
import com.pris.citizenapp.R;
import com.pris.citizenapp.adapters.RTIDetailsAdapter;
import com.pris.citizenapp.adapters.Replies;
import com.pris.citizenapp.common.SessionManager;

import java.util.ArrayList;

/**
 * Created by manav on 14/4/17.
 */

public class RTIDetails extends AppCompatActivity {

    RecyclerView recList;
    LinearLayoutManager llm;
    private SessionManager session;
    public int LIST_SIZE;
    Toolbar toolbar;
    MaterialDialog popalert;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Fresco.initialize(this);
        setContentView(R.layout.member_recycle);
        toolbar=(Toolbar)findViewById(R.id.toolbar);
        toolbar.setTitle("RTI Details");
        setSupportActionBar(toolbar);
        session = new SessionManager(this);

        ArrayList<Replies> challenge = this.getIntent().getExtras().getParcelableArrayList("arraydetailsrti");
        recList = (RecyclerView)  findViewById(R.id.myrecyclerView);
        recList.setHasFixedSize(true);

        llm = new LinearLayoutManager(this);
        llm.setOrientation(LinearLayoutManager.VERTICAL);
        recList.setLayoutManager(llm);

        popalert = new MaterialDialog.Builder(this)
                .title("Errors Found!")
                .titleColor(getResources().getColor(R.color.appcolor))
                .content("No Replies Found")
                .positiveText("Ok").build();

        if(challenge.size()==0)
        {
            popalert.show();
        }

        RTIDetailsAdapter ca = new RTIDetailsAdapter(challenge,this);
        recList.setAdapter(ca);
        ca.notifyDataSetChanged();
    }

}
