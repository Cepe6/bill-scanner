package com.example.lacho.billscanner;

import android.util.Log;

import com.kinvey.android.AsyncAppData;
import com.kinvey.android.Client;
import com.kinvey.java.core.KinveyClientCallback;

import java.util.Map;


public class RecordData {
    private Client mKinveyClient;

    public RecordData(Client mKinveyClient) {
        this.mKinveyClient = mKinveyClient;
    }

    public void makeRecord(String bill, Map<String, String[]> products, Double totalPrice) {
        Bill billObj = new Bill();
        billObj.setBill(bill);
        billObj.setProducts(products);
        billObj.setTotalPrice(totalPrice);

        AsyncAppData<Bill> myevents = mKinveyClient.appData("bills", Bill.class);
        myevents.save(billObj, new KinveyClientCallback<Bill>() {
            @Override
            public void onFailure(Throwable e) {
                Log.e("TAG", "failed to save event data", e);
            }

            @Override
            public void onSuccess(Bill b) {
                Log.d("TAG", "saved data for entity " + b.toString());
            }
        });
        mKinveyClient.query();
    }
}
