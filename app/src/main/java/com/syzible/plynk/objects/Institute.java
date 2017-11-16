package com.syzible.plynk.objects;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by ed on 16/11/2017.
 */

public class Institute extends Vendor {
    public Institute(JSONObject o) throws JSONException {
        this(o.getString("user_id"), o.getString("name"), o.getString("picture_url"));
    }

    public Institute(String id, String name, String picture) {
        super(id, name, picture);
    }
}
