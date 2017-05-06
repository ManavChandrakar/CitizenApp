package com.pris.citizenapp.adapters;

import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.facebook.drawee.backends.pipeline.Fresco;
import com.pris.citizenapp.R;
import com.pris.citizenapp.common.SessionManager;
import com.pris.citizenapp.entrolab.Application.Grevancedetails;
import com.pris.citizenapp.entrolab.Application.TapDetails;
import com.pris.citizenapp.payments.CheckoutActivity;

import java.text.SimpleDateFormat;
import java.util.List;

/**
 * Created by manav on 17/4/17.
 */

public class PrivateTapAdapter extends RecyclerView.Adapter<PrivateTapAdapter.FeedViewHolder> {

    private List<PrivateTapModel> contactList;
    private Context context;

    private SessionManager session;


    public PrivateTapAdapter(List<PrivateTapModel> contactList, Context fm)
    {

        this.contactList = contactList;
        session = new SessionManager(fm);
        try {
            context = fm;
        }
        catch (NullPointerException e){
            e.printStackTrace();
        }
    }


    @Override
    public int getItemCount() {
        return contactList.size();
    }

    @Override
    public void onBindViewHolder(final PrivateTapAdapter.FeedViewHolder feedViewHolder, int i) {

        Typeface head = Typeface.createFromAsset(context.getAssets(), "fonts/Roboto_Light.ttf");

        final PrivateTapModel ci = contactList.get(i);

        feedViewHolder.applicationid.setText(ci.application_id);
        feedViewHolder.appliedby.setText(ci.name);
        feedViewHolder.connections.setText(ci.connections);
        feedViewHolder.connection_fee.setText(ci.connection_fee);
        feedViewHolder.assessmentno.setText(ci.assessment_no);
        feedViewHolder.doorno.setText(ci.door_no);
        feedViewHolder.tid.setText(ci.transaction_id);


        long ttimr = 1;
        if (ci.transaction_timestamp.length() > 0) {
            ttimr= (Integer.parseInt(ci.timestamp)) * 1000L;
        }

        String ttime = new SimpleDateFormat("MMM dd, yyyy h:ma").format(ttimr);
        feedViewHolder.ttime.setText(ttime);
/*

        String stat="";

        if(ci.status.equals("1"))
        {
            stat="New";
        }
        else if(ci.status.equals("2"))
        {
            stat="Accepted";
        }

        else if(ci.status.equals("3"))
        {
            stat="Processing";
        }

        else if(ci.status.equals("4"))
        {
            stat="Rejected";
        }*/

        feedViewHolder.status.setText(ci.status);


        long timestand = 1;
        if (ci.timestamp.length() > 0) {
            timestand = (Integer.parseInt(ci.timestamp)) * 1000L;
        }

        String time = new SimpleDateFormat("MMM dd, yyyy h:ma").format(timestand);


        feedViewHolder.appliedon.setText(time);

        feedViewHolder.llcomplete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i=new Intent(context,TapDetails.class);
                Bundle bundle = new Bundle();
                bundle.putParcelableArrayList("arraydetailstap", ci.replies);
                i.putExtras(bundle);
                context.startActivity(i);
            }
        });


        int myNum = 0;

        try {
            myNum = Integer.parseInt(ci.connection_fee);
        } catch(NumberFormatException nfe) {
            System.out.println("Could not parse " + nfe);
        }

        if(ci.connection_fee_status.equals("0") && myNum > 0)
        {
            feedViewHolder.button.setVisibility(View.VISIBLE);
            final int finalMyNum = myNum;
            feedViewHolder.button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent=new Intent(context, CheckoutActivity.class);
                    intent.putExtra("fees", finalMyNum);
                    context.startActivity(intent);
                }
            });

        }
        else
        {
            feedViewHolder.button.setVisibility(View.GONE);
        }

        feedViewHolder.tv1.setTypeface(head);
        feedViewHolder.tv2.setTypeface(head);
        feedViewHolder.tv3.setTypeface(head);
        feedViewHolder.tv4.setTypeface(head);
        feedViewHolder.tv5.setTypeface(head);
        feedViewHolder.tv6.setTypeface(head);
        feedViewHolder.tv7.setTypeface(head);
        feedViewHolder.tv8.setTypeface(head);
        feedViewHolder.tv9.setTypeface(head);
        feedViewHolder.tv10.setTypeface(head);

    }



    @Override
    public PrivateTapAdapter.FeedViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {

        Fresco.initialize(viewGroup.getContext());



        View itemView = LayoutInflater.
                from(viewGroup.getContext()).
                inflate(R.layout.private_tap_adapter, viewGroup, false);


        return new PrivateTapAdapter.FeedViewHolder(itemView);
    }

    public static class FeedViewHolder extends RecyclerView.ViewHolder {

        //   protected TextView uid;
        protected TextView applicationid;
        protected TextView appliedby;
        protected TextView appliedon;

        protected TextView assessmentno;
        protected TextView doorno;
        protected TextView connections;
        protected TextView connection_fee;

        protected TextView tid;
        protected TextView ttime;

        protected TextView status,tv1,tv2,tv3,tv4,tv5,tv6,tv7,tv8,tv9,tv10;
        protected Button button;


        protected LinearLayout llcomplete;


        public FeedViewHolder(View v) {
            super(v);

            applicationid=(TextView)v.findViewById(R.id.aid);
            appliedby=(TextView)v.findViewById(R.id.reported);
            appliedon=(TextView)v.findViewById(R.id.reportedon);
            assessmentno=(TextView)v.findViewById(R.id.assessment_no);
            doorno=(TextView)v.findViewById(R.id.dno);
            connections=(TextView)v.findViewById(R.id.connections);
            connection_fee=(TextView)v.findViewById(R.id.connection_fee);
            tid=(TextView)v.findViewById(R.id.t_id);
            ttime=(TextView)v.findViewById(R.id.t_date);
            status=(TextView)v.findViewById(R.id.status);
            button=(Button)v.findViewById(R.id.btnpay);
            llcomplete=(LinearLayout)v.findViewById(R.id.llcomplete);
            tv1=(TextView)v.findViewById(R.id.tv1);
            tv2=(TextView)v.findViewById(R.id.tv2);
            tv3=(TextView)v.findViewById(R.id.tv3);
            tv4=(TextView)v.findViewById(R.id.tv4);
            tv5=(TextView)v.findViewById(R.id.tv5);
            tv6=(TextView)v.findViewById(R.id.tv6);
            tv7=(TextView)v.findViewById(R.id.tv7);
            tv8=(TextView)v.findViewById(R.id.tv8);
            tv8=(TextView)v.findViewById(R.id.tv9);
            tv10=(TextView)v.findViewById(R.id.tv10);

        }
    }


}
