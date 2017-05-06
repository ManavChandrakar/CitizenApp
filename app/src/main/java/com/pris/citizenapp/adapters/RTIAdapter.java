package com.pris.citizenapp.adapters;

import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.facebook.drawee.backends.pipeline.Fresco;
import com.pris.citizenapp.R;
import com.pris.citizenapp.common.SessionManager;
import com.pris.citizenapp.entrolab.Application.RTIDetails;

import java.text.SimpleDateFormat;
import java.util.List;

/**
 * Created by manav on 14/4/17.
 */

public class RTIAdapter extends RecyclerView.Adapter<RTIAdapter.FeedViewHolder> {

    private List<RTIDataModel> contactList;
    private Context context;

    private SessionManager session;


    public RTIAdapter(List<RTIDataModel> contactList, Context fm)
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
    public void onBindViewHolder(final RTIAdapter.FeedViewHolder feedViewHolder, int i) {

        Typeface head = Typeface.createFromAsset(context.getAssets(), "fonts/Roboto_Light.ttf");

        final RTIDataModel ci = contactList.get(i);

        feedViewHolder.gid.setText(ci.rti_id);
        feedViewHolder.reportedby.setText(ci.name);
      /*  String stat="";

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


        feedViewHolder.reportedon.setText(time);

        feedViewHolder.llcomplete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i=new Intent(context,RTIDetails.class);
                Bundle bundle = new Bundle();
                bundle.putParcelableArrayList("arraydetailsrti", ci.replies);
                i.putExtras(bundle);
                context.startActivity(i);
            }
        });


        feedViewHolder.tv1.setTypeface(head);
        feedViewHolder.tv2.setTypeface(head);
        feedViewHolder.tv3.setTypeface(head);
        feedViewHolder.tv4.setTypeface(head);
    }



    @Override
    public RTIAdapter.FeedViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {

        Fresco.initialize(viewGroup.getContext());



        View itemView = LayoutInflater.
                from(viewGroup.getContext()).
                inflate(R.layout.rti_adapter, viewGroup, false);


        return new RTIAdapter.FeedViewHolder(itemView);
    }

    public static class FeedViewHolder extends RecyclerView.ViewHolder {

        //   protected TextView uid;
        protected TextView gid;
        protected TextView reportedby;
        protected TextView reportedon;
        TextView tv1,tv2,tv3,tv4;

        protected TextView status;


        protected LinearLayout llcomplete;


        public FeedViewHolder(View v) {
            super(v);

            //     uid = (TextView)  v.findViewById(R.id.uid);
          /*  issue_id = (TextView)  v.findViewById(R.id.issue_id);
            title = (TextView)  v.findViewById(R.id.title);
            category = (TextView)  v.findViewById(R.id.category);
            subcategory = (TextView)  v.findViewById(R.id.sub_category);
            location = (TextView)  v.findViewById(R.id.location);

            current_status = (TextView)  v.findViewById(R.id.status);
            details = (TextView)  v.findViewById(R.id.details);

            update = (TextView)  v.findViewById(R.id.update);
            timestamp = (TextView)  v.findViewById(R.id.timestamp);
            map = (TextView) v.findViewById(R.id.map);

*/
            gid=(TextView)v.findViewById(R.id.gid);
            reportedby=(TextView)v.findViewById(R.id.reported);
            reportedon=(TextView)v.findViewById(R.id.reportedon);;
            status=(TextView)v.findViewById(R.id.status);
            llcomplete=(LinearLayout)v.findViewById(R.id.llcomplete);
            tv1=(TextView)v.findViewById(R.id.tv1);
            tv2=(TextView)v.findViewById(R.id.tv2);
            tv3=(TextView)v.findViewById(R.id.tv3);
            tv4=(TextView)v.findViewById(R.id.tv4);

        }
    }


}