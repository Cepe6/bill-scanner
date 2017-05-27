package com.example.lacho.billscanner;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.kinvey.android.AsyncAppData;
import com.kinvey.android.Client;
import com.kinvey.android.callback.KinveyListCallback;

public class AccountActivity extends AppCompatActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.i("Tag", "created");
        setContentView(R.layout.activity_account);
        Client mKinveyClient = new Client.Builder(getString(R.string.app_key),
                getString(R.string.app_secret),
                this.getApplicationContext()).build();

        AsyncAppData<Bill> events = mKinveyClient.appData("bills", Bill.class);
        events.get(new KinveyListCallback<Bill>() {
            @Override
            public void onSuccess(Bill[] bills) {
                Log.v("Success", "received "+ bills.length + " events");
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
}
