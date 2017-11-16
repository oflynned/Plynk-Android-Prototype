package com.syzible.plynk.objects;

import android.content.Context;

import com.stfalcon.chatkit.commons.models.IUser;
import com.syzible.plynk.persistence.LocalPrefs;
import com.syzible.plynk.utils.EncodingUtils;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by ed on 13/11/2017.
 */

public class User implements IUser {
    private String id, forename, surname, profilePic;

    public User(JSONObject o) {
        try {
            this.id = o.getString("user_id");
            this.forename = o.getString("forename");
            this.surname = o.getString("surname");
            this.profilePic = o.getString("profile_pic");
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public User(String id, String forename, String surname, String profileUrl) {
        this.id = id;
        this.forename = forename;
        this.surname = surname;
        this.profilePic = profileUrl;
    }

    public static User getMe(Context context) {
        return new User(LocalPrefs.getID(context),
                LocalPrefs.getStringPref(LocalPrefs.Pref.forename, context),
                LocalPrefs.getStringPref(LocalPrefs.Pref.surname, context),
                LocalPrefs.getStringPref(LocalPrefs.Pref.profile_pic, context));
    }

    @Override
    public String getId() {
        return id;
    }

    @Override
    public String getName() {
        return getFullName();
    }

    public String getForename() {
        return EncodingUtils.decodeText(forename);
    }

    public String getSurname() {
        return EncodingUtils.decodeText(surname);
    }

    public String getFullName() {
        return getForename() + " " + getSurname();
    }

    @Override
    public String getAvatar() {
        return profilePic;
    }
}
