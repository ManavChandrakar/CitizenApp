package com.pris.citizenapp.entrolab.representative;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.text.Spanned;
import android.webkit.WebView;
import android.widget.TextView;

import com.pris.citizenapp.R;
import com.pris.citizenapp.common.SessionManager;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;

/**
 * Created by manav on 9/4/17.
 */

public class NewsDetails extends AppCompatActivity {

    TextView title,time;
    WebView web;
    SessionManager session;
    Toolbar toolbar;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        session=new SessionManager(this);
        setTitle("News Detail");
        setContentView(R.layout.news_details);
        web= (WebView) findViewById(R.id.tvweb);
        /*title=(TextView)findViewById(R.id.titlehead);
        time=(TextView)findViewById(R.id.time);*/
        String htmltext = session.getStrVal("description");

       /* try {
            String afterDecode = URLDecoder.decode(session.getStrVal("sharetitle"), "UTF-8");
            title.setText(afterDecode);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }*/
        //time.setText(session.getStrVal("time"));
        web.loadUrl(htmltext);
    }
}
