package com.example.lacho.billscanner;

import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.util.Log;

import com.googlecode.tesseract.android.TessBaseAPI;

import java.io.File;
import java.io.IOException;

/**
 * Created by lacho on 5/1/17.
 */

class TessOCR {
    private TessBaseAPI mTess;

    public TessOCR(String fileDir) {
        this.mTess = new TessBaseAPI();
        String language = "eng";

        try {
            mTess.init(fileDir, language);
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