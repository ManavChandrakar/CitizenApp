package com.pris.citizenapp.Fragments;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.pris.citizenapp.R;
import com.pris.citizenapp.common.SessionManager;
import com.pris.citizenapp.entrolab.representative.PaymentHistory;
import com.pris.citizenapp.login.ChangePassword;
import com.pris.citizenapp.login.EditProfile;
import com.pris.citizenapp.login.LinkedProperty;
import com.pris.citizenapp.login.Login;
import com.pris.citizenapp.login.Register;

/**
 * Created by manav on 16/4/17.
 */

public class Profile extends Fragment {

    Toolbar toolbar;
    TextView update,history,changepassword,logout,linkedppt;
    SessionManager session;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
       View v=inflater.inflate(R.layout.profile,container,false);
        toolbar=(Toolbar)v.findViewById(R.id.toolbar);
        ((AppCompatActivity)getActivity()).setSupportActionBar(toolbar);
        getActivity().setTitle("Account");
        return v;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        session = new SessionManager(view.getContext());

        Typeface head = Typeface.createFromAsset(view.getContext().getAssets(), "fonts/Roboto_Light.ttf");

        update=(TextView)view.findViewById(R.id.update);
        update.setTypeface(head);
        history=(TextView)view.findViewById(R.id.history);
        history.setTypeface(head);
        changepassword=(TextView)view.findViewById(R.id.changepass);
        changepassword.setTypeface(head);
        logout=(TextView)view.findViewById(R.id.logout);
        logout.setTypeface(head);
        linkedppt=(TextView)view.findViewById(R.id.linkedppt);
        linkedppt.setTypeface(head);

        linkedppt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(view.getContext(), LinkedProperty.class);
                startActivity(intent);
            }
        });

        update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(view.getContext(),EditProfile.class);
                startActivity(intent);
            }
        });

        history.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(view.getContext(),PaymentHistory.class);
                startActivity(intent);
            }
        });

        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                session.logoutUser();
                Intent intent = new Intent(view.getContext(), Login.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
            }
        });

        changepassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(view.getContext(), ChangePassword.class);
                startActivity(intent);
            }
        });
    }
}
