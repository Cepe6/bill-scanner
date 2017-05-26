package com.example.lacho.billscanner;

import android.graphics.Bitmap;
import android.util.Log;
import com.googlecode.tesseract.android.TessBaseAPI;


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
        return mTess.getUTF8Text();
    }

    public void onDestroy() {
        if (mTess != null) {
            mTess.end();
        }
    }
}