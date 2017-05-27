package com.example.lacho.billscanner;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.example.lacho.billscanner.accounts.LoginActivity;
import com.kinvey.android.AsyncAppData;
import com.kinvey.android.Client;
import com.kinvey.android.callback.KinveyListCallback;

import java.util.Map;

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

                billsOutput += "\n}";
                printBills(billsOutput);
            }

            @Override
            public void onFailure(Throwable throwable) {
                Log.v("Failure", "failed to recieve");
            }
        });

        Button home_btn = (Button) findViewById(R.id.home_btn);
        home_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(AccountActivity.this, MainActivity.class);
                startActivity(intent);
            }
        });
    }

    public void printBills(String bills) {
        TextView billsArea = (TextView) findViewById(R.id.bills);

        billsArea.setMovementMethod(new ScrollingMovementMethod());
        billsArea.setText(bills);
    }

    public void logout(View view) {
        MainActivity.mKinveyClient.user().logout().execute();
        startActivity(new Intent(this, LoginActivity.class));
    }
}
