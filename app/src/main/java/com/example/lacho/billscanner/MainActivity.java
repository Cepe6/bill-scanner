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
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
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

    private static final String DEFAULT_LANGUAGE = "eng";

    private Bitmap image;
    private TextView textView;
    private Client mKinveyClient;
    private String datapath = "";
    private String language = DEFAULT_LANGUAGE;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mKinveyClient = new Client.Builder(getString(R.string.app_key),
                getString(R.string.app_secret),
                this.getApplicationContext()).build();

        Log.i("CHECKING", "IS USER LOGGED IN: " + mKinveyClient.user().isUserLoggedIn());
        if(mKinveyClient.user().isUserLoggedIn()) {
                Log.i("YEP", "User is " + mKinveyClient.user().getUsername());
            if(mKinveyClient.user().getUsername() == null) {
                Log.i("WRONG THO", "User is " + mKinveyClient.user().getId());
                mKinveyClient.user().logout();
                Intent login = new Intent(this, LoginActivity.class);
                startActivity(login);
            }
        } else {
            Intent login = new Intent(this, LoginActivity.class);
            startActivity(login);
        }

        mKinveyClient.ping(new KinveyPingCallback() {
            public void onFailure(Throwable t) {
                Log.e("", "Kinvey Ping Failed", t);
            }
            public void onSuccess(Boolean b) {
                Log.i("", "Kinvey Ping Success");
            }
        });




        textView = (TextView) findViewById(R.id.textArea);

        Button button = (Button) findViewById(R.id.button);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(intent, 0);
            }
        });
    }

    public void onRadioButtonClicked(View view) {
        // Is the button now checked?
        boolean checked = ((RadioButton)view).isChecked();

        // Check which radio button was clicked
        switch(view.getId()) {
            case R.id.radio_eng:
                if (checked) {
                    language = "eng";
                }
                break;
            case R.id.radio_bul:
                if (checked) {
                    language = "bul";
                }
                break;
        }
    }
    public void changeToAcc(View v) {
        Intent intent = new Intent(this, AccountActivity.class);
        startActivity(intent);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        findViewById(R.id.ok).setVisibility(View.VISIBLE);
        image = (Bitmap) data.getExtras().get("data");

        //selectCropImage();

        setAndOutputTess();
        //findViewById(R.id.cropImageView).setVisibility(View.GONE);
    }

    private void setAndOutputTess() {
        String ocrResult;
        TessBaseAPI tessOCR = new TessBaseAPI();

        datapath  = getFilesDir() + "/tesseract/";
        checkFile(new File(datapath + "tessdata/"));

        tessOCR.init(datapath, language);
        tessOCR.setImage(image);
        ocrResult = tessOCR.getUTF8Text();

        if (ocrResult == null) {
            textView.setText("Houston, we have a problem!");
        } else {
            textView.setText(ocrResult);
        }

        tessOCR.clear();
    }

    private void selectCropImage() {
        final CropImageView cropImageView = (CropImageView) findViewById(R.id.cropImageView);
        cropImageView.setVisibility(View.VISIBLE);
        cropImageView.setImageBitmap(image);
        cropImageView.setAutoZoomEnabled(false);

        final Button ok = (Button) findViewById(R.id.ok);
        ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                image = cropImageView.getCroppedImage();
                ok.setVisibility(View.INVISIBLE);
            }
        });
    }

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

    public void addBill(View view) {
        RecordData recordData = new RecordData(mKinveyClient);

        Map<String, String[]> products = new LinkedHashMap<>();
        products.put("Apple", new String[] {"2", "3.50"});
        products.put("Milk", new String[] {"1", "1.00"});
        products.put("Mayo", new String[] {"5", "10.00"});
        recordData.makeRecord("Billa", products, 14.50, mKinveyClient.user().getId());
    }
}
