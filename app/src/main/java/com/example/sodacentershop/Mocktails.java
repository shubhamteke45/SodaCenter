package com.example.sodacentershop;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.sql.Time;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class Mocktails extends AppCompatActivity {

    ImageView mojito, blackCurrent, ElectricShock, blueLagoon, RasberryMint, caranberryLime;
    AlertDialog.Builder builder;
    String TAG ="main";
    final int UPI_PAYMENT = 0;


    FirebaseFirestore firebaseFirestore;

    String mojito_soda = "MOJITO";
    String mojito_price = "60";

    String black_current_soda = "Black Current";
    String black_current_price = "70";

    String electric_shock_soda = "Electric Shock";
    String electric_shock_price = "80";

    String blue_lagoon_soda = "Blue Lagoon";
    String blue_lagoon_price = "90";

    String raspberry_mint_soda = "Raspberry Mint";
    String raspberry_mint_price = "100";

    String caranberry_lime_soda = "Caranberry Lime";
    String caranberry_lime_price = "120";

    String selected_soda = "";
    String selected_price = "";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mocktails);
        mojito = findViewById(R.id.mojito);
        blackCurrent = findViewById(R.id.black);
        ElectricShock = findViewById(R.id.shock);
        blueLagoon = findViewById(R.id.blue);
        RasberryMint = findViewById(R.id.mint);
        caranberryLime = findViewById(R.id.lime);

        builder = new AlertDialog.Builder(this);

        mojito.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selected_soda = mojito_soda;
                selected_price = mojito_price;
                confirmation(selected_soda, selected_price);
            }
        });

        blackCurrent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selected_soda = black_current_soda;
                selected_price = black_current_price;
                confirmation(selected_soda, selected_price);
            }
        });

        ElectricShock.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selected_soda = electric_shock_soda;
                selected_price = electric_shock_price;
                confirmation(selected_soda, selected_price);
            }
        });

        blueLagoon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selected_soda = blue_lagoon_soda;
                selected_price = blue_lagoon_price;
                confirmation(selected_soda, selected_price);
            }
        });

        RasberryMint.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selected_soda = raspberry_mint_soda;
                selected_price = raspberry_mint_price;
                confirmation(selected_soda, selected_price);
            }
        });

        caranberryLime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selected_soda = caranberry_lime_soda;
                selected_price = caranberry_lime_price;
                confirmation(selected_soda, selected_price);
            }
        });


    }

    private void confirmation(String soda, String price) {

        //Setting message manually and performing action on button click
        String message = soda +" ₹"+ price;
        builder.setMessage(message)
                .setCancelable(false)
                .setPositiveButton("CONFIRM", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        payUsingUpi(soda, price);

                    }
                })
                .setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        //  Action for 'NO' Button
                        dialog.cancel();
                        Toast.makeText(getApplicationContext(),"Cancel",
                                Toast.LENGTH_SHORT).show();
                    }
                });
        //Creating dialog box
        AlertDialog alert = builder.create();
        //Setting the title manually
        alert.setTitle("MAKE PAYMENT");
        alert.show();
    }

    void payUsingUpi(  String note, String amount) {
                Uri uri = Uri.parse("upi://pay").buildUpon()
                .appendQueryParameter("pa", "skbendale02@oksbi")
                .appendQueryParameter("pn", "Sahil Bendale")
                //.appendQueryParameter("mc", "")
                //.appendQueryParameter("tid", "02125412")
                //.appendQueryParameter("tr", "25584584")
                .appendQueryParameter("tn", note)
                .appendQueryParameter("am", amount)
                .appendQueryParameter("cu", "INR")
                //.appendQueryParameter("refUrl", "blueapp")
                .build();
        Intent upiPayIntent = new Intent(Intent.ACTION_VIEW);
        upiPayIntent.setData(uri);
        // will always show a dialog to user to choose an app
        Intent chooser = Intent.createChooser(upiPayIntent, "Pay with");
        // check if intent resolves
        if(null != chooser.resolveActivity(getPackageManager())) {
            startActivityForResult(chooser, UPI_PAYMENT);
        } else {
            Toast.makeText(Mocktails.this,"No UPI app found, please install one to continue",Toast.LENGTH_SHORT).show();
        }
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.e("main ", "response "+resultCode );
        /*
       E/main: response -1
       E/UPI: onActivityResult: txnId=AXI4a3428ee58654a938811812c72c0df45&responseCode=00&Status=SUCCESS&txnRef=922118921612
       E/UPIPAY: upiPaymentDataOperation: txnId=AXI4a3428ee58654a938811812c72c0df45&responseCode=00&Status=SUCCESS&txnRef=922118921612
       E/UPI: payment successfull: 922118921612
         */
        switch (requestCode) {
            case UPI_PAYMENT:
                if ((RESULT_OK == resultCode) || (resultCode == 11)) {
                    if (data != null) {
                        String trxt = data.getStringExtra("response");
                        Log.e("UPI", "onActivityResult: " + trxt);
                        ArrayList<String> dataList = new ArrayList<>();
                        dataList.add(trxt);
                        upiPaymentDataOperation(dataList);
                    } else{
                        Log.e("UPI", "onActivityResult: " + "Return data is null");
                        ArrayList<String> dataList = new ArrayList<>();
                        dataList.add("nothing");
                        upiPaymentDataOperation(dataList);
                    }
                } else {
                    //when user simply back without payment
                    Log.e("UPI", "onActivityResult: " + "Return data is null");
                    ArrayList<String> dataList = new ArrayList<>();
                    dataList.add("nothing");
                    upiPaymentDataOperation(dataList);
                }
                break;
        }
    }

    private void upiPaymentDataOperation(ArrayList<String> data) {
        if (isConnectionAvailable(Mocktails.this)) {
            String str = data.get(0);
            Log.e("UPIPAY", "upiPaymentDataOperation: "+str);
            String paymentCancel = "";
            if(str == null) str = "discard";
            String status = "";
            String approvalRefNo = "";
            String response[] = str.split("&");

            for (int i = 0; i < response.length; i++) {
                String equalStr[] = response[i].split("=");
                if(equalStr.length >= 2) {
                    if (equalStr[0].toLowerCase().equals("Status".toLowerCase())) {
                        status = equalStr[1].toLowerCase();
                    }
                    else if (equalStr[0].toLowerCase().equals("ApprovalRefNo".toLowerCase()) || equalStr[0].toLowerCase().equals("txnRef".toLowerCase())) {
                        approvalRefNo = equalStr[1];
                    }
                }
                else {
                    paymentCancel = "Payment cancelled by user.";
                }
            }
            if (status.equals("success")) {
                //Code to handle successful transaction here.

                Calendar calendar = Calendar.getInstance();
                SimpleDateFormat mdformat = new SimpleDateFormat("HH:mm:ss");
                String strDate = mdformat.format(calendar.getTime());

                String date = DateFormat.format("dd/MM/yyyy", calendar).toString();

                firebaseFirestore = FirebaseFirestore.getInstance();
                DocumentReference df = firebaseFirestore.collection("SodaOrders").document();

                Map<String, Object> user = new HashMap<>();
                user.put("category", selected_soda);
                user.put("price", selected_price);
                user.put("time", strDate);
                user.put("date", date);

                df.set(user);

                Toast.makeText(Mocktails.this, "Transaction successful.", Toast.LENGTH_SHORT).show();
            }
            else if("Payment cancelled by user.".equals(paymentCancel)) {



                Toast.makeText(Mocktails.this, "Payment cancelled by user.", Toast.LENGTH_SHORT).show();

            }
            else {
                Toast.makeText(Mocktails.this, "Transaction failed.Please try again", Toast.LENGTH_SHORT).show();

            }
        } else {
            Log.e("UPI", "Internet issue: ");
            Toast.makeText(Mocktails.this, "Internet connection is not available. Please check and try again", Toast.LENGTH_SHORT).show();
        }
    }
    public static boolean isConnectionAvailable(Context context) {
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivityManager != null) {
            NetworkInfo netInfo = connectivityManager.getActiveNetworkInfo();
            if (netInfo != null && netInfo.isConnected()
                    && netInfo.isConnectedOrConnecting()
                    && netInfo.isAvailable()) {
                return true;
            }
        }
        return false;
    }


}