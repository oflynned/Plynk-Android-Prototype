package com.syzible.plynk.utils;

import android.content.Context;

import com.syzible.plynk.persistence.LocalPrefs;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by ed on 15/11/2017.
 */

public class JSONUtils {

    public static JSONObject getId(Context context) {
        JSONObject o = new JSONObject();
        try {
            o.put("user_id", LocalPrefs.getID(context));
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return o;
    }
}
