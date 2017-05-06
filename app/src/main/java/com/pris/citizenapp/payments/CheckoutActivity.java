package com.pris.citizenapp.payments;

import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.afollestad.materialdialogs.MaterialDialog;
import com.paynimo.android.payment.PaymentActivity;
import com.paynimo.android.payment.PaymentModesActivity;
import com.paynimo.android.payment.model.Checkout;
import com.pris.citizenapp.R;
import com.pris.citizenapp.common.SessionManager;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Random;

import static com.pris.citizenapp.common.SessionManager.USER_EMAIL;
import static com.pris.citizenapp.common.SessionManager.USER_FULL_NAME;

/**
 * Created by manav on 14/4/17.
 */

public class CheckoutActivity extends AppCompatActivity {

    protected TextView hid;
    protected TextView hid_txt;

    public MaterialDialog popAlert;

    protected TextView assessment;
    protected TextView assessment_txt;

    protected TextView citizen;
    protected TextView father;
    protected TextView aadhar;
    protected TextView aadhar_txt;
    protected TextView total,header;
    protected TextView total_txt,mailtxt,mobiletxt,nametxt,addresstxt;
    EditText mail,mobile,name,address;
    protected LinearLayout duelist;
    protected TextView paynow;
    protected int final_total;
    Toolbar toolbar;
    SessionManager session;


    protected RelativeLayout fullView;

    TextView PayNow;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.checkout_page);
      /*  toolbar=(Toolbar)findViewById(R.id.toolbar);
        toolbar.setTitle("Proceed to Payment");
        setSupportActionBar(toolbar);*/
        setTitle("Proceed to Payment");
        Typeface head = Typeface.createFromAsset(getAssets(), "fonts/Roboto_Light.ttf");

        aadhar=(TextView)findViewById(R.id.aadhar);
        session=new SessionManager(this);
        hid = (TextView)findViewById(R.id.hid);
        hid_txt = (TextView)findViewById(R.id.hid_txt);
        hid.setTypeface(head);
        assessment = (TextView) findViewById(R.id.assessment);
        assessment_txt = (TextView) findViewById(R.id.assessment_txt);
        citizen = (TextView) findViewById(R.id.citizen);
        citizen.setTypeface(head);
        mail=(EditText)findViewById(R.id.mail);
        mobile=(EditText)findViewById(R.id.mobile);
        mailtxt=(TextView) findViewById(R.id.emailtxt);
        mailtxt.setTypeface(head);
        mobiletxt=(TextView) findViewById(R.id.mobiletxt);
        nametxt=(TextView) findViewById(R.id.nametxt);
        nametxt.setTypeface(head);
        addresstxt=(TextView)findViewById(R.id.addresstxt);
        addresstxt.setTypeface(head);
        mobiletxt.setTypeface(head);

        father = (TextView) findViewById(R.id.father_name);
        father.setTypeface(head);
        aadhar = (TextView) findViewById(R.id.aadhar);
        aadhar_txt = (TextView)  findViewById(R.id.aadhar_txt);
        aadhar_txt.setTypeface(head);
        total = (TextView)  findViewById(R.id.total);
        total_txt = (TextView)  findViewById(R.id.total_txt);
        total_txt.setTypeface(head);
        header=(TextView)findViewById(R.id.taxtype);
        name=(EditText)findViewById(R.id.name);
        address=(EditText)findViewById(R.id.address);

        duelist = (LinearLayout)  findViewById(R.id.duelist);
        paynow = (TextView)  findViewById(R.id.pay_now);
        paynow.setTypeface(head);


        popAlert = new MaterialDialog.Builder(this)
                .title("Errors Found!")
                .content("Please check your data!")
                .positiveText("Ok").build();


        hid.setText(session.getStrVal("pay_hid"));
        aadhar.setText(session.getStrVal("pay_email"));
        father.setText(session.getStrVal("father"));
        total.setText(session.getStrVal("pay_amount"));
        mail.setText(session.getStrVal(USER_EMAIL));

        citizen.setText(session.getStrVal("pay_name"));
        mobile.setText(session.getStrVal("pay_mobile"));
        assessment.setText(session.getStrVal("pay_assessment"));
        header.setText(session.getStrVal("pay_purpose"));
        name.setText(session.getStrVal(USER_FULL_NAME));
        LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View v;

        try {
            JSONObject jsonObject=new JSONObject(session.getStrVal("dues_selected"));
            JSONArray jsonArray=new JSONArray(session.getStrVal("jsonArray"));

            for(int i=0;i<jsonArray.length();i++)
            {
                if(jsonObject.has(jsonArray.getString(i)))
                {

                    View row_view = (View) inflater.inflate(R.layout.dues_layout,null);


                    CheckBox due_year = (CheckBox) row_view.findViewById(R.id.due_year);
                    due_year.setChecked(true);
                    due_year.setClickable(false);
                    TextView due_amount = (TextView) row_view.findViewById(R.id.due_amount);
                    due_amount.setTypeface(head);


                    due_year.setText(jsonArray.getString(i));
                    due_year.setTypeface(head);
                    due_amount.setText(jsonObject.getString(jsonArray.getString(i)));
                    duelist.addView(row_view);
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }


       paynow.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View view) {
               boolean res=validate();
               if(res)
               {
                   Checkout checkout = new Checkout();
                   checkout.setMerchantIdentifier("T139772");//where T1234 is the MERCHANT CODE, update it with Merchant Code provided by TPSL
                   String mres=gen();
                   checkout.setTransactionIdentifier("TXN"+mres); //where TXN001 is the Merchant Transaction Identifier, it should be different for each transaction (alphanumeric value, no special character allowed)
                   checkout.setTransactionReference ("ORD"+mres); //where ORD0001 is the Merchant Transaction Reference number
                   checkout.setTransactionType ("Sale"); //Transaction Type
                   checkout.setTransactionSubType ("Debit"); //Transaction Subtype
                   checkout.setTransactionCurrency ("INR"); //Currency Type
                   checkout.setTransactionAmount (session.getStrVal("pay_amount"));//Transaction Amount

                   String todayAsString = new SimpleDateFormat("ddMMyyyy").format(new Date());

                   checkout.setTransactionDateTime (todayAsString); //Transaction Date
                   // setting Consumer fields values
                   checkout.setConsumerIdentifier (""); //Consumer Identifier, default value "", set this value as application user name if you want Instrument Vaulting, SI on Cards. Consumer ID should be alpha-numeric value with no space
                   checkout.setConsumerEmailID (mail.getText().toString().trim());//Consumer Email ID
                   checkout.setConsumerMobileNumber (session.getStrVal("pay_mobile")); //Consumer Mobile Number
                   session.storeVal("payment_mail",mail.getText().toString().trim());
                   checkout.setConsumerAccountNo ("");

                   try {
                       JSONObject jsonObject=new JSONObject(session.getStrVal("dues_selected"));
                       JSONArray jsonArray=new JSONArray(session.getStrVal("jsonArray"));

                       for(int i=0;i<jsonArray.length();i++)
                       {
                           if(jsonObject.has(jsonArray.getString(i)))
                           {
                               checkout.addCartItem("test",jsonObject.getString(jsonArray.getString(i)),"0.0","0.0","",jsonArray.getString(i),session.getStrVal("pay_purpose"),"www.pris.gov.in");

                           }
                       }
                   } catch (JSONException e) {
                       e.printStackTrace();
                   }

                   Intent intent=new Intent(CheckoutActivity.this,PaymentModesActivity.class);
                   intent.putExtra(PaymentActivity.EXTRA_REQUESTED_PAYMENT_MODE,PaymentActivity.PAYMENT_METHOD_DEFAULT);
                   intent.putExtra(PaymentActivity.ARGUMENT_DATA_CHECKOUT,checkout);
                   intent.putExtra(PaymentActivity.EXTRA_PUBLIC_KEY,"6636259131GPLFAX");
                   startActivityForResult(intent, PaymentActivity.REQUEST_CODE);
               }


           }
       });







        //Testing the push
  /*      checkout.setTransactionMerchantInitiated("Y");
        checkout.setPaymentInstrumentIdentifier("4012001037141112");
        checkout.setPaymentInstrumentExpiryMonth("12");
        checkout.setPaymentInstrumentExpiryYear("2017");
        checkout.setPaymentInstrumentVerificationCode("123");
        checkout.setPaymentInstrumentHolderName("Any Name");
        checkout.setTransactionIsRegistration("Y");*/

      /*  Intent intent=new Intent(CheckoutActivity.this,PaymentModesActivity.class);
        intent.putExtra(PaymentActivity.EXTRA_REQUESTED_PAYMENT_MODE,PaymentActivity.PAYMENT_METHOD_DEFAULT);
        intent.putExtra(PaymentActivity.ARGUMENT_DATA_CHECKOUT,checkout);
        intent.putExtra(PaymentActivity.EXTRA_PUBLIC_KEY,"6636259131GPLFAX");
        startActivityForResult(intent, PaymentActivity.REQUEST_CODE);*/
    }

    public String gen() {
        Random r = new Random(System.currentTimeMillis() );
        return 10000 + r.nextInt(20000) + "";
    }


    private boolean validate() {
        int error=0;
        String errorTxt="";
        if(mail.getText().toString().length() > 0 && error == 0){

            if(!Patterns.EMAIL_ADDRESS.matcher(mail.getText().toString()).matches()) {

                errorTxt = "Invalid Email";
                error++;
            }
        }

        if(name.getText().toString().trim().length()==0 && error==0)
        {

                errorTxt = "Name field cannot be empty";
                error++;
        }

        String mobilePattern = "^([7-8-9]{1}[0-9]{9})+$";
        if (error == 0) {
            if (mobile.getText().toString().trim().length() == 0 ) {
                errorTxt = "Mobile number Cannot be empty";
                error++;
            }
            else if (mobile.getText().toString().trim().length()< 10 || (!mobile.getText().toString().matches(mobilePattern))) {
                errorTxt = "Enter valid 10 digit mobile number";
                error++;
            }
        }

        if (error > 0) {
            error = 0;

            popAlert.setTitle("Oops! Errors Found");
            popAlert.setContent(errorTxt);

            popAlert.show();

            return false;

        }

        return true;
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // Check which request we're responding to
        if (requestCode == PaymentActivity.REQUEST_CODE) {
            // Make sure the request was successful
            if (resultCode == PaymentActivity.RESULT_OK) {
                Log.d("TAG", "Result Code :" + RESULT_OK);
                if (data != null) {

                    try {
                        Checkout checkout_res = (Checkout) data
                                .getSerializableExtra(PaymentActivity.ARGUMENT_DATA_CHECKOUT);
                        Log.d("Checkout Response Obj", checkout_res
                                .getMerchantResponsePayload().toString());

                        String transactionType = checkout_res.
                                getMerchantRequestPayload().getTransaction().getType();
                        String transactionSubType = checkout_res.
                                getMerchantRequestPayload().getTransaction().getSubType();
                        if (transactionType != null && transactionType						   .equalsIgnoreCase(PaymentActivity.TRANSACTION_TYPE_PREAUTH)
                                && transactionSubType != null && transactionSubType
                                .equalsIgnoreCase(PaymentActivity.TRANSACTION_SUBTYPE_RESERVE)){
                            // Transaction Completed and Got SUCCESS
                            if (checkout_res.getMerchantResponsePayload()
                                    .getPaymentMethod().getPaymentTransaction()
                                    .getStatusCode().equalsIgnoreCase(PaymentActivity.TRANSACTION_STATUS_PREAUTH_RESERVE_SUCCESS)) {
                                Toast.makeText(getApplicationContext(), "Transaction Status - Success", Toast.LENGTH_SHORT).show();
                                Log.v("TRANSACTION STATUS=>", "SUCCESS");

                                /**
                                 * TRANSACTION STATUS - SUCCESS (status code
                                 * 0200 means success), NOW MERCHANT CAN PERFORM
                                 * ANY OPERATION OVER SUCCESS RESULT
                                 */

                                if (checkout_res.getMerchantResponsePayload()
                                        .getPaymentMethod().getPaymentTransaction()
                                        .getInstruction().getId() != null && !checkout_res.getMerchantResponsePayload()
                                        .getPaymentMethod().getPaymentTransaction()
                                        .getInstruction().getId().isEmpty()) {

                                    /**
                                     * SI TRANSACTION STATUS - SUCCESS (received
                                     * Mandate ID means success)
                                     */
                                    Log.v("TRANSACTION SI STATUS=>", "SUCCESS");
                                }

                            } // Transaction Completed and Got FAILURE

                            else {
                                // some error from bank side
                                Log.v("TRANSACTION STATUS=>", "FAILURE");
                                Toast.makeText(getApplicationContext(),
                                        "Transaction Status - Failure",
                                        Toast.LENGTH_SHORT).show();
                            }

                        } else {

                            // Transaction Completed and Got SUCCESS
                            if (checkout_res.getMerchantResponsePayload().getPaymentMethod()					.getPaymentTransaction().getStatusCode().equalsIgnoreCase(
                                    PaymentActivity.TRANSACTION_STATUS_SALES_DEBIT_SUCCESS)) {
                                Toast.makeText(getApplicationContext(), "Transaction Status - Success", Toast.LENGTH_SHORT).show();
                                Log.v("TRANSACTION STATUS=>", "SUCCESS");

                                /**
                                 * TRANSACTION STATUS - SUCCESS (status code
                                 * 0300 means success), NOW MERCHANT CAN PERFORM
                                 * ANY OPERATION OVER SUCCESS RESULT
                                 */

                                Intent intent=new Intent(CheckoutActivity.this,PaymentSuccess.class);
                                startActivity(intent);

                                if (checkout_res
                                        .getMerchantResponsePayload()
                                        .getPaymentMethod().getPaymentTransaction()
                                        .getInstruction().getId() != null && !checkout_res.getMerchantResponsePayload()
                                        .getPaymentMethod().getPaymentTransaction()
                                        .getInstruction().getId().isEmpty()) {

                                    /**
                                     * SI TRANSACTION STATUS - SUCCESS (received
                                     * Mandate ID means success)
                                     */
                                    Log.v("TRANSACTION SI STATUS=>", "SUCCESS");
                                }

                            } // Transaction Completed and Got FAILURE
                            else {
                                // some error from bank side
                                Log.v("TRANSACTION STATUS=>", "FAILURE");
                                Toast.makeText(getApplicationContext(),
                                        "Transaction Status - Failure",
                                        Toast.LENGTH_SHORT).show();

                                Intent intent=new Intent(CheckoutActivity.this,PaymentFailure.class);
                                startActivity(intent);
                            }

                        }
                        String result = "StatusCode : " + checkout_res
                                .getMerchantResponsePayload().getPaymentMethod()
                                .getPaymentTransaction().getStatusCode()
                                + "\nStatusMessage : " + checkout_res
                                .getMerchantResponsePayload().getPaymentMethod()
                                .getPaymentTransaction().getStatusMessage()
                                + "\nErrorMessage : "+ checkout_res
                                .getMerchantResponsePayload().getPaymentMethod()
                                .getPaymentTransaction().getErrorMessage()
                                + "\nAmount : " + checkout_res
                                .getMerchantResponsePayload().getPaymentMethod().getPaymentTransaction().getAmount()
                                + "\nDateTime : " + checkout_res.
                                getMerchantResponsePayload().getPaymentMethod()
                                .getPaymentTransaction().getDateTime()
                                + "\nMerchantTransactionIdentifier : "
                                + checkout_res.getMerchantResponsePayload().getMerchantTransactionIdentifier()
                                + "\nIdentifier : " + checkout_res
                                .getMerchantResponsePayload().getPaymentMethod()
                                .getPaymentTransaction().getIdentifier()
                                + "\nBankSelectionCode : " + checkout_res
                                .getMerchantResponsePayload().getPaymentMethod()
                                .getBankSelectionCode()
                                + "\nBankReferenceIdentifier : " + checkout_res
                                .getMerchantResponsePayload().getPaymentMethod()
                                .getPaymentTransaction().getBankReferenceIdentifier()
                                + "\nRefundIdentifier : " + checkout_res
                                .getMerchantResponsePayload().getPaymentMethod()
                                .getPaymentTransaction().getRefundIdentifier()
                                + "\nBalanceAmount : " + checkout_res
                                .getMerchantResponsePayload().getPaymentMethod()
                                .getPaymentTransaction().getBalanceAmount()
                                + "\nInstrumentAliasName : " + checkout_res
                                .getMerchantResponsePayload().getPaymentMethod()
                                .getInstrumentAliasName()
                                + "\nSI Mandate Id : " + checkout_res
                                .getMerchantResponsePayload().getPaymentMethod()
                                .getPaymentTransaction().getInstruction().getId()
                                + "\nSI Mandate Status : " + checkout_res
                                .getMerchantResponsePayload().getPaymentMethod()
                                .getPaymentTransaction().getInstruction().getStatusCode()
                                + "\nSI Mandate Error Code : " + checkout_res
                                .getMerchantResponsePayload().getPaymentMethod()
                                .getPaymentTransaction().getInstruction().getErrorcode()
                                + "\nSI MerchantAdditionalDetails : " + checkout_res
                                .getMerchantResponsePayload()
                                .getMerchantAdditionalDetails();

                        session.storeVal("MerchantTransactionIdentifier",checkout_res.getMerchantResponsePayload().getMerchantTransactionIdentifier());
                        session.storeVal("BankReferenceIdentifier",checkout_res.getMerchantResponsePayload().getPaymentMethod().getPaymentTransaction().getBankReferenceIdentifier());
                        session.storeVal("BankSelectionCode",checkout_res.getMerchantResponsePayload().getPaymentMethod().getBankSelectionCode());
                        session.storeVal("RefundIdentifier",checkout_res.getMerchantResponsePayload().getPaymentMethod().getPaymentTransaction().getRefundIdentifier());
                        session.storeVal("pay_date",checkout_res.getMerchantResponsePayload().getPaymentMethod().getPaymentTransaction().getDateTime());
                        session.storeVal("InstrumentAliasName",checkout_res.getMerchantResponsePayload().getPaymentMethod().getInstrumentAliasName());

                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                }
            }
            else if (resultCode == PaymentActivity.RESULT_ERROR) {
                Log.d("TAG", "got an error");

                if (data.hasExtra(PaymentActivity.RETURN_ERROR_CODE) &&
                        data.hasExtra(PaymentActivity.RETURN_ERROR_DESCRIPTION)) {
                    String error_code = (String) data
                            .getStringExtra(PaymentActivity.RETURN_ERROR_CODE);
                    String error_desc = (String) data
                            .getStringExtra(PaymentActivity.RETURN_ERROR_DESCRIPTION);

                    Toast.makeText(getApplicationContext(), " Got error :"
                            + error_code + "--- " + error_desc, Toast.LENGTH_SHORT)
                            .show();
                    Log.d("TAG" + " Code=>", error_code);
                    Log.d("TAG" + " Desc=>", error_desc);

                }

            }
            else if (resultCode == PaymentActivity.RESULT_CANCELED) {
                Toast.makeText(getApplicationContext(), "Transaction Aborted by User",
                        Toast.LENGTH_SHORT).show();
                Log.d("TAG", "User pressed back button");

            }
        }
    }

}
