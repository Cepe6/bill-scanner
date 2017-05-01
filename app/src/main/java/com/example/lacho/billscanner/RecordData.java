package com.example.lacho.billscanner;

import android.content.Context;
import android.util.Log;

import com.kinvey.android.AsyncAppData;
import com.kinvey.android.Client;
import com.kinvey.java.core.KinveyClientCallback;

/**
 * Created by lacho on 5/1/17.
 */

public class RecordData {
    private Client mKinveyClient;

    public RecordData(Client mKinveyClient) {
        this.mKinveyClient = mKinveyClient;
    }

    public void makeRecord(String bill, Double totalPrice) {
        Bills bills = new Bills();
        bills.setBill(bill);
        bills.setTotalPrice(totalPrice);

        AsyncAppData<Bills> myevents = mKinveyClient.appData("bills", Bills.class);
        myevents.save(bills, new KinveyClientCallback<Bills>() {
            @Override
            public void onFailure(Throwable e) {
                Log.e("TAG", "failed to save event data", e);
            }

            @Override
            public void onSuccess(Bills b) {
                Log.d("TAG", "saved data for entity " + b.toString());
            }
        });
        mKinveyClient.query();
    }
}
