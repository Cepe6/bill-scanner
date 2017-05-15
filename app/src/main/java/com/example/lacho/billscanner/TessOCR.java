package com.example.lacho.billscanner;

import android.graphics.Bitmap;
import android.net.Uri;
import android.util.Log;

import com.googlecode.tesseract.android.TessBaseAPI;

import java.io.File;

/**
 * Created by lacho on 5/1/17.
 */

class TessOCR {
    private TessBaseAPI mTess;

    public TessOCR(File filesDir) {
        this.mTess = new TessBaseAPI();

        String language = "eng";
        String datapath;
        Uri path = Uri.parse("file:///android_assets/");
        datapath = path.toString();
        try {
            mTess.init(datapath, language);
        } catch (Exception e) {
            Log.e("Tesseract Init", e.toString());
        }
    }



    public String getResult(Bitmap bitmap) {
        mTess.setImage(bitmap);
        String result = mTess.getUTF8Text();

        return result;
    }

    public void onDestroy() {
        if (mTess != null) {
            mTess.end();
        }
    }
}