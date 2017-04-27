package com.pris.citizenapp.payments;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.afollestad.materialdialogs.MaterialDialog;
import com.pris.citizenapp.R;
import com.pris.citizenapp.common.SessionManager;
import com.pris.citizenapp.entrolab.FooterMain;

/**
 * Created by manav on 26/4/17.
 */

public class PaymentFailure  extends AppCompatActivity {
    private MaterialDialog progDialog;
    private SessionManager session;
    public MaterialDialog popAlert;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment_failure);


        session  = new SessionManager(this);

        Typeface head = Typeface.createFromAsset(getAssets(), "fonts/Roboto_Light.ttf");
        Typeface desc = Typeface.createFromAsset(getAssets(), "fonts/Roboto-Thin.ttf");

        setTitle("Payment Failed");

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


        order_id.setText(session.getStrVal("mer_txn"));
        bank_transaction.setText(session.getStrVal("banktxn"));
        amount.setText(session.getStrVal("pay_amount"));
        name.setText(session.getStrVal("pay_name"));
        email.setText(session.getStrVal("pay_email"));
        mobile.setText(session.getStrVal("pay_mobile"));
        purpose.setText(session.getStrVal("pay_purpose"));

        success_msg.setText("Your Payment Failed!\n Payment Reference ID: #"+session.getStrVal("mer_txn"));
    }
}
