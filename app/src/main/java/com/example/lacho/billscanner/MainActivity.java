package com.example.lacho.billscanner;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.googlecode.tesseract.android.TessBaseAPI;
import com.kinvey.android.AsyncAppData;
import com.kinvey.android.Client;
import com.kinvey.android.callback.KinveyPingCallback;
import com.kinvey.java.core.KinveyClientCallback;

import org.json.JSONException;
import org.json.JSONObject;

public class MainActivity extends AppCompatActivity {

    Bitmap image;
    private TessBaseAPI mTess;
    String datapath = "";
    Button button;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        //WIP -> create client and then check connectivity
        final Client mKinveyClient = new Client.Builder(getString(R.string.app_key),
                getString(R.string.app_secret),
                this.getApplicationContext()).build();


        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mKinveyClient.ping(new KinveyPingCallback() {
            public void onFailure(Throwable t) {
                Log.e("", "Kinvey Ping Failed", t);
            }
            public void onSuccess(Boolean b) {
                Log.i("", "Kinvey Ping Success");
            }
        });


        //END OF WIP
        Bills bills = new Bills();
        bills.setTotalPrice(4.4D);
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject = new JSONObject("{\"phonetype\":\"N95\",\"cat\":\"WP\"}");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        bills.setBill(jsonObject);

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

        button = (Button) findViewById(R.id.button);

        String language = "eng";
        datapath = getFilesDir()+ "/tesseract/";
        mTess = new TessBaseAPI();

        mTess.init(datapath, language);

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(intent, 0);
            }
        });


    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        image = (Bitmap) data.getExtras().get("data");
        String OCRresult = null;

        mTess.setImage(image);
        OCRresult = mTess.getUTF8Text();

        button.setText(OCRresult);

    }
}
