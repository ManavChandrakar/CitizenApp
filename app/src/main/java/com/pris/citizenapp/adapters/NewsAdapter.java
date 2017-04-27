package com.pris.citizenapp.adapters;

import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.net.Uri;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.text.Spanned;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.afollestad.materialdialogs.MaterialDialog;
import com.bumptech.glide.Glide;
import com.facebook.drawee.backends.pipeline.Fresco;
import com.pris.citizenapp.R;
import com.pris.citizenapp.common.SessionManager;
import com.pris.citizenapp.entrolab.representative.NewsDetails;
import com.thefinestartist.finestwebview.FinestWebView;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.text.SimpleDateFormat;
import java.util.List;

import static com.thefinestartist.utils.content.ContextUtil.startActivity;

/**
 * Created by manav on 9/4/17.
 */

public class NewsAdapter extends RecyclerView.Adapter<NewsAdapter.MyHolder> {

    private List<NewsModel> contactList;
    private Context context;
    private SessionManager session;
    String time="";
    String afterDecode="";

    public NewsAdapter(List<NewsModel> contactList, Context fm)
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
    public NewsAdapter.MyHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Fresco.initialize(parent.getContext());



        View itemView = LayoutInflater.
                from(parent.getContext()).
                inflate(R.layout.news_row, parent, false);


        return new NewsAdapter.MyHolder(itemView);
    }

    @Override
    public void onBindViewHolder(NewsAdapter.MyHolder holder, int position) {
        Typeface head = Typeface.createFromAsset(context.getAssets(), "fonts/Roboto_Light.ttf");

        final NewsModel i= contactList.get(position);

        if(!(TextUtils.isEmpty(i.title))) {
           /* Spanned sp = Html.fromHtml(i.title);
            holder.title.setText(sp);*/
            try {
                 afterDecode = URLDecoder.decode(i.title, "UTF-8");
                holder.title.setText(afterDecode);
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
        }


        if(!(TextUtils.isEmpty(i.timestamp)))
        {

            long timestamp = 1;
            if (i.timestamp.length() > 0) {
                timestamp = (Integer.parseInt(i.timestamp)) * 1000L;
            }

            //  String time = String.valueOf(DateUtils.getRelativeTimeSpanString(timestand, System.currentTimeMillis(), DateUtils.SECOND_IN_MILLIS));

            time = new SimpleDateFormat("MMM dd, yyyy h:ma").format(timestamp);
            holder.timestamp.setText("Reported on "+time);
        }

        else
            holder.timestamp.setText("N/A");



        /*if(!(TextUtils.isEmpty(i.description))) {
            String longText = i.description;
            String smallText = longText;

            assert longText != null;
            if (longText.length() > 160) {

                smallText = smallText.substring(0, 158) + "..";

            }
            Spanned sp = Html.fromHtml(smallText);
            holder.description.setText(sp);
        }

        else
            holder.description.setText("N/A");*/

        final String finalTime = time;
        holder.llclick.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                 session.storeVal("description",i.description);
                 session.storeVal("sharetitle",afterDecode);
                 session.storeVal("time", time);
                 Intent intent=new Intent(context, NewsDetails.class);
                 context.startActivity(intent);
            }
        });

        holder.title.setTypeface(head);
        holder.timestamp.setTypeface(head);

    }

    @Override
    public int getItemCount() {
        return contactList.size();
    }

    public static class MyHolder extends RecyclerView.ViewHolder
    {
        TextView timestamp,description,title;
         LinearLayout llclick;
        ImageView photo;
        public MyHolder(View itemView) {
            super(itemView);
            title=(TextView)itemView.findViewById(R.id.title);
            timestamp=(TextView)itemView.findViewById(R.id.timestamp);
           // description=(TextView)itemView.findViewById(R.id.description);
            llclick=(LinearLayout)itemView.findViewById(R.id.llclick);
        }
    }
}
