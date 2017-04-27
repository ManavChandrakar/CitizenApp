package com.pris.citizenapp.adapters;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.support.v4.view.PagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.bumptech.glide.Glide;
import com.pris.citizenapp.R;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;

/**
 * Created by manav on 19/4/17.
 */

public class CustomSwipeAdapter extends PagerAdapter{

/*
    private int [] image_resource={R.mipmap.ic_launcher,R.mipmap.ic_launcher,R.mipmap.ic_launcher,R.mipmap.ic_launcher,R.mipmap.ic_launcher,R.mipmap.ic_launcher};
*/
    private Context ctx;
    private LayoutInflater inflater;
    ArrayList<String> al;

    public CustomSwipeAdapter(Context ctx,ArrayList<String> al)
    {
        this.ctx=ctx;
        this.al=al;
    }

    @Override
    public int getCount() {
       // return image_resource.length;
        return al.size();
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return (view==(LinearLayout)object);
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {

        inflater=(LayoutInflater) ctx.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View itemview=inflater.inflate(R.layout.swiper_layout,container,false);
        ImageView imageView=(ImageView)itemview.findViewById(R.id.imageswipe);
       // imageView.setImageURI(Uri.pars);
       // imageView.setImageResource(R.drawable.ic_avtaar);
        /*Bitmap bitmap = null;
        try {
            bitmap = BitmapFactory.decodeStream((InputStream)new URL(al.get(position)).getContent());
        } catch (Exception e) {
            e.printStackTrace();
        }
        imageView.setImageBitmap(bitmap);*/
        Glide.with(ctx)
                .load(Uri.parse(al.get(position)))
                .fitCenter()
                .into(imageView);

        container.addView(itemview);
        return itemview;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((LinearLayout)object);
    }
}
