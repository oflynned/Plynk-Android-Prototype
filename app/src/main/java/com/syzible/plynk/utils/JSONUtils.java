package com.syzible.plynk.utils;

import android.content.Context;

import com.syzible.plynk.objects.Card;
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

    public static JSONObject getBankCardPayload(Context context, Card card) {
        JSONObject o = new JSONObject();
        try {
            o.put("user_id", LocalPrefs.getID(context));
            o.put("card_number", card.getNumber());
            o.put("card_cvv", card.getCvv());
            o.put("card_expiry", card.getExpiry());
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return o;
    }
}
