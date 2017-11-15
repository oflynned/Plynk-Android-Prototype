package com.syzible.plynk.network;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import org.json.JSONArray;
import org.json.JSONException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringWriter;
import java.io.Writer;

/**
 * Created by ed on 16/12/2016
 */

public class GetImage extends GetRequest<Bitmap> {
    public GetImage(NetworkCallback<Bitmap> networkCallback, String url) {
        super(networkCallback, url);
    }

    @Override
    public Bitmap transferData() {
        try {
            switch (getConnection().getResponseCode()) {
                case 200:
                case 304:
                    InputStream is = getConnection().getInputStream();
                    Bitmap bitmap = BitmapFactory.decodeStream(is);
                    if (bitmap != null)
                        return bitmap;
                case 404:
                case 500:
                    break;
            }
            return null;
        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }
}
