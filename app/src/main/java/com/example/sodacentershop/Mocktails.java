package com.example.sodacentershop;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import java.util.ArrayList;

public class Mocktails extends AppCompatActivity {

    ImageView mojito, blackCurrent, ElectricShock, blueLagoon, RasberryMint, caranberryLime;
    AlertDialog.Builder builder;
    String TAG ="main";
    final int UPI_PAYMENT = 0;


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
                confirmation("MOJITO", "60");
            }
        });

        blackCurrent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                confirmation("Black Current", "70");
            }
        });

        ElectricShock.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                confirmation("Electric Shock" , "80");
            }
        });

        blueLagoon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                confirmation("Blue Lagoon", "90");
            }
        });

        RasberryMint.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                confirmation("Raspberry Mint", "100");
            }
        });

        caranberryLime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                confirmation("Cranberry Lime", "120");
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
                .appendQueryParameter("pa", "shubham.teke30@okhdfcbank")
                .appendQueryParameter("pn", "Shubham Teke")
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