package com.pris.citizenapp.payments;

import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.afollestad.materialdialogs.MaterialDialog;
import com.pris.citizenapp.R;
import com.pris.citizenapp.common.SessionManager;
import com.pris.citizenapp.entrolab.FooterMain;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by manav on 26/4/17.
 */

public class PaymentFailure  extends AppCompatActivity {
    private MaterialDialog progDialog;
    private SessionManager session;
    public MaterialDialog popAlert;
    LinearLayout due_list;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment_failure);

        due_list=(LinearLayout)findViewById(R.id.duelist);
        session  = new SessionManager(this);

        Typeface head = Typeface.createFromAsset(getAssets(), "fonts/Roboto_Light.ttf");
        Typeface desc = Typeface.createFromAsset(getAssets(), "fonts/Roboto-Thin.ttf");

        setTitle("Payment Failed");


        LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View v;

        try {
            JSONObject jsonObject=new JSONObject(session.getStrVal("dues_selected"));
            JSONArray jsonArray=new JSONArray(session.getStrVal("jsonArray"));

            for(int i=0;i<jsonArray.length();i++)
            {
                if(jsonObject.has(jsonArray.getString(i)))
                {

                    View row_view = (View) inflater.inflate(R.layout.linflate_payment_success,null);


                    TextView due_year = (TextView) row_view.findViewById(R.id.due_year);
                    TextView due_amount = (TextView) row_view.findViewById(R.id.due_amount);
                    due_amount.setTypeface(head);
                    due_year.setText(jsonArray.getString(i));
                    due_year.setTypeface(head);
                    due_amount.setText(jsonObject.getString(jsonArray.getString(i)));
                    due_list.addView(row_view);
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }



        TextView success_msg = (TextView) findViewById(R.id.success_message);
        success_msg.setTypeface(head);
        TextView order_id = (TextView) findViewById(R.id.transaction);
        order_id.setTypeface(head);
        TextView bank_transaction = (TextView) findViewById(R.id.bank_transaction);
        bank_transaction.setTypeface(head);
        TextView amount = (TextView) findViewById(R.id.amount);
        amount.setTypeface(head);
        TextView name = (TextView) findViewById(R.id.billing_name);
        name.setTypeface(head);
        TextView email = (TextView) findViewById(R.id.billing_email);
        email.setTypeface(head);
        TextView mobile = (TextView) findViewById(R.id.billing_mobile);
        mobile.setTypeface(head);
        TextView purpose = (TextView) findViewById(R.id.billing_purpose);
        purpose.setTypeface(head);


        Button gohome = (Button) findViewById(R.id.button_back);
        gohome.setTypeface(head);

        gohome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(PaymentFailure.this, FooterMain.class);
                startActivity(intent);
                finish();


            }
        });


        order_id.setText(session.getStrVal("MerchantTransactionIdentifier"));
        bank_transaction.setText(session.getStrVal("BankReferenceIdentifier"));
        amount.setText(session.getStrVal("pay_amount"));
        name.setText(session.getStrVal("pay_name"));
        email.setText(session.getStrVal("payment_mail"));
        mobile.setText(session.getStrVal("pay_mobile"));
        purpose.setText(session.getStrVal("pay_purpose"));
        success_msg.setText("Your Payment Failed!\n Payment Reference ID: #"+session.getStrVal("MerchantTransactionIdentifier"));
    }
}
