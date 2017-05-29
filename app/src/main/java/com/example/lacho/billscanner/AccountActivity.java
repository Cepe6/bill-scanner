package com.example.lacho.billscanner;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.example.lacho.billscanner.accounts.LoginActivity;
import com.kinvey.android.AsyncAppData;
import com.kinvey.android.Client;
import com.kinvey.android.callback.KinveyListCallback;
import com.kinvey.java.Query;

import java.sql.Array;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class AccountActivity extends AppCompatActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.i("Tag", "created");
        setContentView(R.layout.activity_account);

        TextView usernameArea = (TextView) findViewById(R.id.username);
        usernameArea.setText(MainActivity.mKinveyClient.user().getUsername());

        Log.i("Check", MainActivity.mKinveyClient.user().toString());

        AsyncAppData<Bill> events = MainActivity.mKinveyClient.appData("bills", Bill.class);
        events.get(new KinveyListCallback<Bill>() {
            @Override
            public void onSuccess(Bill[] bills) {
                /*
                String billsOutput = "{\n ";
                for (Bill bill : bills) {
                    if(bill.getOwnerID().equals(MainActivity.mKinveyClient.user().getId())) {
                        billsOutput += "\tbill {\n\t\tname: " + bill.getBill() + ", \n\t\tproducts: [";
                        for (Map.Entry<String,String[]> stringEntry : bill.getProducts().entrySet()) {
                            billsOutput += "\n\t\t\t" + stringEntry.getKey() + " {\n\t\t\t\tamount: " + stringEntry.getValue()[0] + "\n\t\t\t\tprice: " + stringEntry.getValue()[1] + "\n\t\t\t}";
                        }
                        billsOutput += "\n\t\t], \n\t\tprice: " +  bill.getTotalPrice() + "\n\t},";
                    }
                }
                billsOutput = billsOutput.substring(0, billsOutput.length() - 1);
                billsOutput += "\n}";
                printBills(billsOutput);
                */
                List<String> products = new ArrayList<>();
                String billString = "";
                for (Bill bill : bills) {
                    if(bill.getOwnerID().equals(MainActivity.mKinveyClient.user().getId())) {
                        billString += "Bill name: " + bill.getBill() + " (total: " + bill.getTotalPrice() + " leva)"
                                + "\nProduct name: " + bill.getProduct()
                                + "\nProduct amount: " + bill.getProductAmount()
                                + "\nProduct price: " + bill.getProductPrice()
                                + "\nTotal: " + bill.getTotalPrice();

                        Pattern p = Pattern.compile("([0-9]+-[0-9]+-[0-9]+).([0-9]+:[0-9]+)");
                        Matcher m = p.matcher(bill.getMeta().get("ect").toString());
Log.i("date", bill.getMeta().get("ect").toString());
Log.i("date", Arrays.toString(bill.getMeta().get("ect").toString().split("([0-9]+-[0-9]+-[0-9]+).([0-9]+:[0-9]+)")));
                        while (m.find()) {
                            billString += "\nAdded on: " + m.group(1) + " " + m.group(2) + "\n";
                        }

                        products.add(billString);
                    }
                }
                printBills(products);
            }

            @Override
            public void onFailure(Throwable throwable) {
                Log.v("Failure", "failed to recieve");
            }
        });

        monthPrice();

        Button home_btn = (Button) findViewById(R.id.home_btn);
        home_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(AccountActivity.this, MainActivity.class);
                startActivity(intent);
            }
        });
    }

    public void monthPrice() {
        AsyncAppData<Bill> events = MainActivity.mKinveyClient.appData("bills", Bill.class);
        final DateFormat dateFormat = new SimpleDateFormat("MM");
        final Date date = new Date();

        events.get(new KinveyListCallback<Bill>() {
            @Override
            public void onSuccess(Bill[] bills) {
                double monthMoney = 0;
                for (Bill bill : bills) {
                    if (bill.getMeta().get("ect").toString().contains("2017-" + dateFormat.format(date) + "-")) {
                        if (bill.getOwnerID().equals(MainActivity.mKinveyClient.user().getId())) {
                            monthMoney += bill.getTotalPrice();
                        }
                    }
                }
                monthMoney = Math.round(monthMoney * 100);
                monthMoney /= 100;
                printMonthMoney(monthMoney);
            }

            @Override
            public void onFailure(Throwable throwable) {
                Log.v("Failure", "failed to recieve");
            }
        });
    }

    public void printMonthMoney(double monthMoney) {
        Log.i("Month", Double.toString(monthMoney));
        TextView total = (TextView) findViewById(R.id.totalMoney);
        total.setText("Spend money for month: " + monthMoney);
    }

    public void printBills(List<String> bills) {
        ArrayAdapter<String> adapter;

            ListView listview=(ListView) findViewById(R.id.bills);

            adapter = new ArrayAdapter<>(this, R.layout.bill_item, R.id.textView1, bills);
            listview.setAdapter(adapter);


        //TextView billsArea = (TextView) findViewById(R.id.bills);

        //billsArea.setMovementMethod(new ScrollingMovementMethod());
        //billsArea.setText(bills);
    }

    public void logout(View view) {
        MainActivity.mKinveyClient.user().logout().execute();
        startActivity(new Intent(this, LoginActivity.class));
    }


}
