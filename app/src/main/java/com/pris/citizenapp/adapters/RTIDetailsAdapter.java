package com.pris.citizenapp.adapters;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.facebook.drawee.backends.pipeline.Fresco;
import com.pris.citizenapp.R;
import com.pris.citizenapp.common.SessionManager;
import com.thefinestartist.finestwebview.FinestWebView;

import java.text.SimpleDateFormat;
import java.util.List;

/**
 * Created by manav on 14/4/17.
 */

public class RTIDetailsAdapter extends RecyclerView.Adapter<RTIDetailsAdapter.FeedViewHolder>{

    private List<Replies> contactList;
    private Context context;

    private SessionManager session;


    public RTIDetailsAdapter(List<Replies> contactList, Context fm)
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
    public RTIDetailsAdapter.FeedViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Fresco.initialize(parent.getContext());



        View itemView = LayoutInflater.
                from(parent.getContext()).
                inflate(R.layout.grevience_details_row, parent, false);


        return new RTIDetailsAdapter.FeedViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(RTIDetailsAdapter.FeedViewHolder holder, int position) {

        Replies ci=contactList.get(position);

        holder.reply.setText(ci.reply);
        holder.repname.setText(ci.username);

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
        }
        holder.repstatus.setText("stat");

        long timestand = 1;
        if (ci.timestamp.length() > 0) {
            timestand = (Integer.parseInt(ci.timestamp)) * 1000L;
        }

        String time = new SimpleDateFormat("MMM dd, yyyy h:ma").format(timestand);


        holder.repdate.setText(time);

        if(ci.file.length()==0)
        {
            holder.imagebtn.setVisibility(View.GONE);
        }

        else
        {
            holder.imagebtn.setVisibility(View.VISIBLE);
        }

        holder.imagebtn.setVisibility(View.VISIBLE);

        holder.imagebtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //write logic for downloading file
               /* Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("http://www.androidbegin.com/wp-content/uploads/2013/07/HD-Logo.gif"));
                context.startActivity(intent); */
            }
        });

    }

    @Override
    public int getItemCount() {
        return contactList.size();
    }

    public static class FeedViewHolder extends RecyclerView.ViewHolder {

        protected TextView reply;
        protected TextView repname;
        protected TextView repdate;
        protected TextView repstatus;
        protected TextView imagebtn;

        public FeedViewHolder(View v) {
            super(v);

            reply=(TextView)v.findViewById(R.id.reply);
            repname=(TextView)v.findViewById(R.id.repname);
            repdate=(TextView)v.findViewById(R.id.repDate);
            repstatus=(TextView)v.findViewById(R.id.repstatus);
            imagebtn=(TextView) v.findViewById(R.id.imgbtn);
        }
    }
}
