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

import com.example.lacho.billscanner.accounts.LoginActivity;
import com.googlecode.tesseract.android.TessBaseAPI;
import com.kinvey.android.AsyncAppData;
import com.kinvey.android.Client;
import com.kinvey.android.callback.KinveyPingCallback;
import com.kinvey.android.callback.KinveyUserCallback;
import com.kinvey.java.User;
import com.kinvey.java.auth.Credential;
import com.kinvey.java.core.KinveyClientCallback;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.LinkedHashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    Bitmap image;
    TextView textView;
    Client mKinveyClient;
    String datapath = "";
    String language = "eng";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Button ok = (Button) findViewById(R.id.ok);
        ok.setVisibility(View.INVISIBLE);

        mKinveyClient = new Client.Builder(getString(R.string.app_key),
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


        Intent login = new Intent(this, LoginActivity.class);
        startActivity(login);

        RecordData recordData = new RecordData(mKinveyClient);

        Map<String, String[]> products = new LinkedHashMap<>();
        products.put("Apple", new String[] {"2", "3.50"});
        products.put("Milk", new String[] {"1", "1.00"});
        products.put("Mayo", new String[] {"5", "10.00"});
        recordData.makeRecord("Billa", products, 14.50);

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

    public Client getmKinveyClient() {
        return mKinveyClient;
    }

    public void changeToAcc(View v) {
        Intent intent = new Intent(this, AccountActivity.class);
        startActivity(intent);
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        image = (Bitmap) data.getExtras().get("data");
        datapath  = getFilesDir() + "/tesseract/";
        String ocrResult;

        checkFile(new File(datapath + "tessdata/"));

        TessOCR tessOCR = new TessOCR(datapath, language);
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

//    private void selectCropImage() {
//        final CropImageView cropImageView = (CropImageView) findViewById(R.id.cropImageView);
//        cropImageView.setImageBitmap(image);
//
//        final Button ok = (Button) findViewById(R.id.ok);
//        ok.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                ImageView imageView = (ImageView) findViewById(R.id.imageview);
//                imageView.setImageBitmap(cropImageView.getCroppedImage());
//                ok.setVisibility(View.GONE);
//            }
//        });
//    }

    private void copyFiles() {
        try {
            //location we want the file to be at
            String filepath = datapath + "/tessdata/" + language + ".traineddata";

            //get access to AssetManager
            AssetManager assetManager = getAssets();

            //open byte streams for reading/writing
            InputStream instream = assetManager.open("tessdata/" + language + ".traineddata");
            OutputStream outstream = new FileOutputStream(filepath);

            //copy the file to the location specified by filepath
            byte[] buffer = new byte[1024];
            int read;
            while ((read = instream.read(buffer)) != -1) {
                outstream.write(buffer, 0, read);
            }
            outstream.flush();
            outstream.close();
            instream.close();

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void checkFile(File dir) {
        //directory does not exist, but we can successfully create it
        if (!dir.exists()&& dir.mkdirs()){
            copyFiles();
        }
        //The directory exists, but there is no data file in it
        if(dir.exists()) {
            String datafilepath = datapath + "/tessdata/" + language + ".traineddata";
            File datafile = new File(datafilepath);
            if (!datafile.exists()) {
                copyFiles();
            }
        }
    }
}
