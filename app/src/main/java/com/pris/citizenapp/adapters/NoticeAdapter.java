package com.pris.citizenapp.adapters;

import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.afollestad.materialdialogs.MaterialDialog;
import com.facebook.drawee.backends.pipeline.Fresco;
import com.pris.citizenapp.R;
import com.pris.citizenapp.common.SessionManager;
import com.thefinestartist.finestwebview.FinestWebView;

import java.text.SimpleDateFormat;
import java.util.List;

/**
 * Created by manav on 21/4/17.
 */

public class NoticeAdapter extends RecyclerView.Adapter<NoticeAdapter.FeedViewHolder> {

    private List<Notice> contactList;
    private Context context;
    private SessionManager session;
    public NoticeAdapter(List<Notice> contactList, Context fm)
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
    public void onBindViewHolder(final FeedViewHolder feedViewHolder, int i) {


        Notice ci = contactList.get(i);

        //   feedViewHolder.uid.setText(ci.uid);
        feedViewHolder.title.setText(ci.title);

        String longText = ci.description;
        String smallText = longText;

        assert longText != null;
        if(longText.length() > 160){

            smallText = smallText.substring(0,158)+"..";

        }

        feedViewHolder.descrtiption.setText(smallText);


        long timestand = 1;
        if (ci.timestamp.length() > 0) {
            timestand = (Integer.parseInt(ci.timestamp)) * 1000L;
        }

        //  String time = String.valueOf(DateUtils.getRelativeTimeSpanString(timestand, System.currentTimeMillis(), DateUtils.SECOND_IN_MILLIS));

        String time = new SimpleDateFormat("MMM dd, yyyy h:ma").format(timestand);


        feedViewHolder.timestamp.setText(time);


        Typeface head = Typeface.createFromAsset(context.getAssets(), "fonts/Roboto_Light.ttf");
        Typeface desc = Typeface.createFromAsset(context.getAssets(), "fonts/Roboto-Thin.ttf");

        feedViewHolder.title.setTypeface(head);
        feedViewHolder.descrtiption.setTypeface(head);
        feedViewHolder.timestamp.setTypeface(desc);

        feedViewHolder.url.setTypeface(desc);
        feedViewHolder.download.setTypeface(desc);

        Log.d("Notice",ci.link+" - "+ci.download);


        if(ci.link.length() > 0 && ci.link != null){

            final String link = ci.link;

            feedViewHolder.url.setVisibility(View.VISIBLE);
            feedViewHolder.url.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    try {
                        new FinestWebView.Builder(context).show(link);
                    }catch (Exception e){
                        Toast.makeText(context,"Cannot Open Link",Toast.LENGTH_SHORT).show();
                    }
                }
            });


        }


        if(ci.download.length() > 0 && ci.download != null){

            final String download = ci.download;
            feedViewHolder.download.setVisibility(View.VISIBLE);
            feedViewHolder.download.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(download));
                    context.startActivity(intent);
                }
            });


        }


        //check for any images from the api and set it on image view using glide

        final Notice dat = ci;


        feedViewHolder.descrtiption.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openDialog(dat);

            }
        });


    }

    protected   void openDialog(Notice ci){

        boolean wrapInScrollView = true;


        MaterialDialog dialog = new MaterialDialog.Builder(context)
                .customView(R.layout.notice_dialog, wrapInScrollView)
                .show();



        View v = dialog.getCustomView();
        v.setFitsSystemWindows(true);



        if(v != null) {
            TextView title = (TextView) v.findViewById(R.id.title);

            TextView description = (TextView) v.findViewById(R.id.description);

            TextView timestamp = (TextView) v.findViewById(R.id.timestamp);
            TextView url = (TextView) v.findViewById(R.id.link);
            final TextView download = (TextView) v.findViewById(R.id.download);

            title.setText(ci.title);

            // description.setText(ci.description);

            description.setText(ci.description);

            long timestand = 1;
            if (ci.timestamp.length() > 0) {
                timestand = (Integer.parseInt(ci.timestamp)) * 1000L;
            }

            String time = new SimpleDateFormat("MMM dd, yyyy h:ma").format(timestand);

            timestamp.setText(time);



            Typeface head = Typeface.createFromAsset(context.getAssets(), "fonts/Roboto_Light.ttf");
            Typeface desc = Typeface.createFromAsset(context.getAssets(), "fonts/Roboto-Thin.ttf");

            title.setTypeface(head);

            timestamp.setTypeface(desc);

            url.setTypeface(desc);
            download.setTypeface(desc);

            Log.d("Notice",ci.link+" - "+ci.download);
            if(ci.link.length() > 0 && ci.link != null){


                url.setVisibility(View.VISIBLE);
                final String link = ci.link;
                url.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        new FinestWebView.Builder(context).show(link);
                    }
                });


            }


            if(ci.download.length() > 0 && ci.download != null){

                download.setVisibility(View.VISIBLE);
                final String downloadfile = ci.download;
                download.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        new FinestWebView.Builder(context).show(downloadfile);
                    }
                });


            }

        }



    }



    @Override
    public FeedViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {

        Fresco.initialize(viewGroup.getContext());



        View itemView = LayoutInflater.
                from(viewGroup.getContext()).
                inflate(R.layout.notice_card, viewGroup, false);


        return new FeedViewHolder(itemView);
    }

    public static class FeedViewHolder extends RecyclerView.ViewHolder {

        //   protected TextView uid;
        protected TextView title;
        protected TextView descrtiption;
        protected TextView timestamp;
        protected TextView url;
        protected TextView download;
        protected ImageView imagview;
        protected RelativeLayout fullView;


        public FeedViewHolder(View v) {
            super(v);

            //     uid = (TextView)  v.findViewById(R.id.uid);
            title = (TextView)  v.findViewById(R.id.title);
            descrtiption = (TextView)  v.findViewById(R.id.description);
            timestamp = (TextView)  v.findViewById(R.id.timestamp);
            fullView = (RelativeLayout) v.findViewById(R.id.contact_view);
            url = (TextView)  v.findViewById(R.id.link);
            download = (TextView)  v.findViewById(R.id.download);
            imagview=(ImageView) v.findViewById(R.id.image_notification);

        }
    }
}
