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
import com.kinvey.android.Client;
import com.kinvey.android.callback.KinveyPingCallback;

public class MainActivity extends AppCompatActivity {

    Bitmap image;
    private TessBaseAPI mTess;
    String datapath = "";
    Button button;
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        //WIP -> create client and then check connectivity
        final Client mKinveyClient = new Client.Builder("", "", this.getApplicationContext()).build();

        mKinveyClient.ping(new KinveyPingCallback() {
            public void onFailure(Throwable t) {
                Log.e("", "Kinvey Ping Failed", t);
            }
            public void onSuccess(Boolean b) {
                Log.d("", "Kinvey Ping Success");
            }
        });
        //END OF WIP

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

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
