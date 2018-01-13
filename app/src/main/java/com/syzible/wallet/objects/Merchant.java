package com.syzible.wallet.objects;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by ed on 16/11/2017.
 */

public class Merchant extends Vendor {
    public Merchant(JSONObject o) throws JSONException {
        this(o.getString("user_id"), o.getString("name"), o.getString("picture_url"));
    }

    public Merchant(String id, String name, String picture) {
        super(id, name, picture);
    }
}
