package com.pris.citizenapp.adapters;

/**
 * Created by manav on 10/4/17.
 */

import android.content.Context;
import android.graphics.Typeface;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.facebook.drawee.backends.pipeline.Fresco;
import com.pris.citizenapp.R;
import com.pris.citizenapp.common.SessionManager;

import java.text.SimpleDateFormat;
import java.util.List;

public class PaymentAdapter extends RecyclerView.Adapter<PaymentAdapter.FeedViewHolder> {

    private List<PayHistory> contactList;
    private Context context;

    private SessionManager session;


    public PaymentAdapter(List<PayHistory> contactList, Context fm)
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


        PayHistory ci = contactList.get(i);

        //   feedViewHolder.uid.setText(ci.uid);

        Typeface head = Typeface.createFromAsset(context.getAssets(), "fonts/Roboto_Light.ttf");
        Typeface desc = Typeface.createFromAsset(context.getAssets(), "fonts/Roboto-Thin.ttf");

        feedViewHolder.order_id.setTypeface(head);

        feedViewHolder.title.setTypeface(desc);
        feedViewHolder.assessment.setTypeface(desc);
        feedViewHolder.taxtype.setTypeface(desc);
        feedViewHolder.amount.setTypeface(head);

        feedViewHolder.year.setTypeface(desc);
        feedViewHolder.timestamp.setTypeface(head);



        feedViewHolder.order_id.setText(ci.order_id);
        feedViewHolder.assessment.setText(ci.assessment);
        feedViewHolder.title.setText(ci.title);
        feedViewHolder.taxtype.setText(ci.taxtype);
        feedViewHolder.amount.setText(ci.amount+"/-");
        feedViewHolder.year.setText(ci.dueyear);


        long timestand = 1;
        if (ci.timestamp.length() > 0) {
            timestand = (Integer.parseInt(ci.timestamp)) * 1000L;
        }

        //  String time = String.valueOf(DateUtils.getRelativeTimeSpanString(timestand, System.currentTimeMillis(), DateUtils.SECOND_IN_MILLIS));

        String time = new SimpleDateFormat("MMM dd, yyyy h:ma").format(timestand);


        feedViewHolder.timestamp.setText("Paid On "+time);






    }

    public String getSubCode(String code, String[] subcodes, String [] subs) {
        int i = -1;
        for (String cc: subs) {
            i++;
            if (cc.equals(code))
                break;
        }
        return subcodes[i];
    }

    @Override
    public FeedViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {

        Fresco.initialize(viewGroup.getContext());



        View itemView = LayoutInflater.
                from(viewGroup.getContext()).
                inflate(R.layout.history_card, viewGroup, false);


        return new FeedViewHolder(itemView);
    }

    public static class FeedViewHolder extends RecyclerView.ViewHolder {

        //   protected TextView uid;
        protected TextView order_id;
        protected TextView title;
        protected TextView assessment;

        protected TextView taxtype;

        protected TextView amount;
        protected TextView year;

        protected TextView timestamp;

        protected RelativeLayout fullView;


        public FeedViewHolder(View v) {
            super(v);

            //     uid = (TextView)  v.findViewById(R.id.uid);
            order_id = (TextView)  v.findViewById(R.id.issue_id);
            title = (TextView)  v.findViewById(R.id.title);
            assessment = (TextView)  v.findViewById(R.id.category);
            taxtype = (TextView)  v.findViewById(R.id.sub_category);
            amount = (TextView)  v.findViewById(R.id.due_amount);
            year = (TextView)  v.findViewById(R.id.due_year);
            timestamp = (TextView)  v.findViewById(R.id.timestamp);

        }
    }


}
