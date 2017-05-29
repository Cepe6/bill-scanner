package com.example.lacho.billscanner;

import android.content.Intent;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Created by lacho on 5/28/2017.
 */

public class CustomBillActivity extends AppCompatActivity implements View.OnClickListener {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_custom_bill);
        Button upload = (Button) findViewById(R.id.upload);
        upload.setOnClickListener(this);
    }

    public void upload(String billName, String productName, int productAmount, double productPrice) {
        RecordData recordData = new RecordData(MainActivity.mKinveyClient);

        recordData.makeRecord(billName,
                productName,
                productAmount,
                productPrice,
                productPrice * productAmount,
                MainActivity.mKinveyClient.user().getId());
    }

    @Override
    public void onClick(View v) {
        EditText billNameEdit = (EditText)findViewById(R.id.bill_name);
        EditText productNameEdit = (EditText)findViewById(R.id.product_name);
        EditText productAmountEdit = (EditText)findViewById(R.id.product_amount);
        EditText productPriceEdit = (EditText)findViewById(R.id.product_price);
        if(billNameEdit == null || productNameEdit == null || productAmountEdit == null || productPriceEdit == null) {
            CharSequence text = "Enter all information please!";
            Toast.makeText(getApplicationContext(), text, Toast.LENGTH_SHORT).show();
        } else {
            String billName = billNameEdit.getText().toString();
            String productName = productNameEdit.getText().toString();
            int productAmount = Integer.parseInt(productAmountEdit.getText().toString());
            double productPrice = Double.parseDouble(productPriceEdit.getText().toString());
            upload(billName, productName, productAmount, productPrice);
        }

        billNameEdit.setText("");
        productNameEdit.setText("");
        productAmountEdit.setText("");
        productPriceEdit.setText("");
    }
}
