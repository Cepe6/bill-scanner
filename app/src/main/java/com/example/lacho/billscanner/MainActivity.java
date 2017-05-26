package com.example.lacho.billscanner;

import android.content.Intent;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.media.Image;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.googlecode.tesseract.android.TessBaseAPI;
import com.kinvey.android.AsyncAppData;
import com.kinvey.android.Client;
import com.kinvey.android.callback.KinveyPingCallback;
import com.kinvey.java.core.KinveyClientCallback;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

public class MainActivity extends AppCompatActivity {

    Bitmap image;
    TextView textView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Button ok = (Button) findViewById(R.id.ok);
        ok.setVisibility(View.INVISIBLE);
        //WIP -> create client and then check connectivity
        final Client mKinveyClient = new Client.Builder(getString(R.string.app_key),
                getString(R.string.app_secret),
                this.getApplicationContext()).build();

        mKinveyClient.ping(new KinveyPingCallback() {
            public void onFailure(Throwable t) {
                Log.e("", "Kinvey Ping Failed", t);
            }
            public void onSuccess(Boolean b) {
                Log.i("", "Kinvey Ping Success");
            }
        });
        //WIP -> end

        RecordData recordData = new RecordData(mKinveyClient);

        textView = (TextView) findViewById(R.id.textArea);
        textView.setText("Default text");

        Button button = (Button) findViewById(R.id.button);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(intent, 0);
            }
        });
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        image = (Bitmap) data.getExtras().get("data");

        String ocrResult;
        TessOCR tessOCR = new TessOCR(getDirPath());
        ocrResult = tessOCR.getResult(image);

        if (ocrResult == null) {
            textView.setText("Result is null");
        } else {
            textView.setText(ocrResult);
        }

        tessOCR.onDestroy();

        Button ok = (Button) findViewById(R.id.ok);
        ok.setVisibility(View.VISIBLE);
        //selectCropImage();
    }
//
//    private void selectCropImage() {
//        final CropImageView cropImageView = (CropImageView) findViewById(R.id.cropImageView);
//        cropImageView.setImageBitmap(image);
//
//        final Button ok = (Button) findViewById(R.id.ok);
//        ok.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                ImageView imageView = (ImageView) findViewById(R.id.picture);
//                imageView.setImageBitmap(cropImageView.getCroppedImage());
//                ok.setVisibility(View.GONE);
//            }
//        });
//    }

    private String getDirPath() {
        File f = new File(getCacheDir() + "/tesseract/");
        if (!f.exists()) try {

            InputStream is = getAssets().open("tesseract/tessdata/eng.traineddata");
            int size = is.available();
            byte[] buffer = new byte[size];
            is.read(buffer);
            is.close();

            FileOutputStream fos = new FileOutputStream(f);
            fos.write(buffer);
            fos.close();
        } catch (Exception e) { Log.e("error", e.toString()); }

        return f.getPath();
    }
}
