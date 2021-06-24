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
import android.text.format.DateFormat;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

public class Home extends AppCompatActivity {

    ImageView about, cocktail, dk1, dk2, dk3, dk4, dk5,dk6, dk7;

    AlertDialog.Builder builder;
    String TAG ="main";
    final int UPI_PAYMENT = 0;

    String selected_soda = "";
    String selected_price = "";

    FirebaseFirestore firebaseFirestore;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        about = findViewById(R.id.about);
        cocktail = findViewById(R.id.cocktail);
        dk1 = findViewById(R.id.dk1);
        dk2 = findViewById(R.id.dk2);
        dk3 = findViewById(R.id.dk3);
        dk4 = findViewById(R.id.dk4);
        dk5 = findViewById(R.id.dk5);
        dk6 = findViewById(R.id.dk6);
        dk7 = findViewById(R.id.dk7);

        builder = new AlertDialog.Builder(this);


        about.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(Home.this, "About", Toast.LENGTH_SHORT).show();
            }
        });

        cocktail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), Mocktails.class));
            }
        });

        dk1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selected_soda = "COLA";
                selected_price = "20";
                confirmation(selected_soda, selected_price);
            }
        });

        dk2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selected_soda = "JIRA";
                selected_price = "20";
                confirmation(selected_soda, selected_price);
            }
        });

        dk3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selected_soda = "MINT";
                selected_price = "20";
                confirmation(selected_soda, selected_price);
            }
        });

        dk4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selected_soda = "PUDINA";
                selected_price = "20";
                confirmation(selected_soda, selected_price);
            }
        });

        dk5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selected_soda = "FRUIT BEER";
                selected_price = "20";
                confirmation(selected_soda, selected_price);
            }
        });
        dk6.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selected_soda = "SPRITE";
                selected_price = "20";
                confirmation(selected_soda, selected_price);
            }
        });

        dk7.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selected_soda = "APPY FIZZ";
                selected_price = "20";
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
            Toast.makeText(Home.this,"No UPI app found, please install one to continue",Toast.LENGTH_SHORT).show();
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
        if (isConnectionAvailable(Home.this)) {
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


                Toast.makeText(Home.this, "Transaction successful.", Toast.LENGTH_SHORT).show();
            }
            else if("Payment cancelled by user.".equals(paymentCancel)) {


                Toast.makeText(Home.this, "Payment cancelled by user.", Toast.LENGTH_SHORT).show();
            }
            else {
                Toast.makeText(Home.this, "Transaction failed.Please try again", Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(Home.this, "Internet connection is not available. Please check and try again", Toast.LENGTH_SHORT).show();
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