package com.pris.citizenapp.adapters;

import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.facebook.drawee.backends.pipeline.Fresco;
import com.pris.citizenapp.R;
import com.pris.citizenapp.common.SessionManager;

import java.util.List;

/**
 * Created by manav on 9/4/17.
 */

public class YellowPageAdapter extends RecyclerView.Adapter<YellowPageAdapter.MyHolder> {

    private List<MemberModel> contactList;
    private Context context;
    private SessionManager session;


    public YellowPageAdapter(List<MemberModel> contactList, Context fm)
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
    public YellowPageAdapter.MyHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Fresco.initialize(parent.getContext());



        View itemView = LayoutInflater.
                from(parent.getContext()).
                inflate(R.layout.member_yellowpage, parent, false);


        return new YellowPageAdapter.MyHolder(itemView);
    }

    @Override
    public void onBindViewHolder(YellowPageAdapter.MyHolder holder, int position) {
        final MemberModel i= contactList.get(position);

        Typeface head = Typeface.createFromAsset(context.getAssets(), "fonts/Roboto_Light.ttf");

        if(!(TextUtils.isEmpty(i.name)))
            holder.name.setText(i.name);
        else
            holder.name.setText("N/A");

        if(!(TextUtils.isEmpty(i.category)))
            holder.category.setText(i.category);
        else
            holder.category.setText("N/A");

        if(!(TextUtils.isEmpty(i.subcategory)))
            holder.subcategory.setText(i.subcategory);
        else
            holder.subcategory.setText("N/A");

        if(!(TextUtils.isEmpty(i.contact)))
            holder.phone.setText(i.contact);
        else
            holder.phone.setText("N/A");

        if(!(TextUtils.isEmpty(i.email)))
            holder.mail.setText(i.email);
        else
            holder.mail.setText("N/A");

        if(!(TextUtils.isEmpty(i.address)))
            holder.location.setText(i.address);
        else
            holder.location.setText("N/A");

        holder.name.setTypeface(head);
        holder.category.setTypeface(head);
        holder.subcategory.setTypeface(head);
        holder.mail.setTypeface(head);
        holder.phone.setTypeface(head);
        holder.location.setTypeface(head);


        //glide
        if(!(TextUtils.isEmpty(i.image))) {
            Glide.with(context)
                    .load(i.image)
                    .into(holder.photo);
        }
        else
        {
            holder.photo.setImageResource(R.drawable.ic_avtaar);
        }

        final Uri buri = Uri.parse(i.image);

        holder.photo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!(TextUtils.isEmpty(i.image)))
                {
                    Intent intent = new Intent();
                    intent.setAction(Intent.ACTION_VIEW);
                    intent.setDataAndType(buri, "image/*");
                    context.startActivity(intent);
                }
            }
        });

        //clicklistner
        holder.imgcall.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_DIAL);
                intent.setData(Uri.parse("tel:"+ i.contact.toString()));
                context.startActivity(intent);
            }
        });

        holder.imgmail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_SEND);
                intent.setType("plain/text");
                intent.putExtra(Intent.EXTRA_EMAIL, new String[] { i.email });
                intent.putExtra(Intent.EXTRA_SUBJECT, "");
                intent.putExtra(Intent.EXTRA_TEXT, "");
                context.startActivity(Intent.createChooser(intent, ""));
            }
        });

        holder.imglocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent searchAddress = new  Intent(Intent.ACTION_VIEW,Uri.parse("geo:0,0?q="+i.address));
                context.startActivity(searchAddress);
            }
        });


    }

    @Override
    public int getItemCount() {
        return contactList.size();
    }

    public static class MyHolder extends RecyclerView.ViewHolder
    {
        TextView name,phone,location,category,subcategory,mail;
        ImageView photo;
        ImageButton imgcall,imgmail,imglocation;
        public MyHolder(View itemView) {
            super(itemView);
            name=(TextView)itemView.findViewById(R.id.name);
            phone=(TextView)itemView.findViewById(R.id.phone);
            location=(TextView)itemView.findViewById(R.id.location);
            category=(TextView)itemView.findViewById(R.id.cat);
            subcategory=(TextView)itemView.findViewById(R.id.subcat);
            mail=(TextView)itemView.findViewById(R.id.mail);
            photo=(ImageView)itemView.findViewById(R.id.image);
            imgcall=(ImageButton)itemView.findViewById(R.id.imgcall);
            imgmail=(ImageButton)itemView.findViewById(R.id.imgmail);
            imglocation=(ImageButton)itemView.findViewById(R.id.imglocation);
        }
    }
}
