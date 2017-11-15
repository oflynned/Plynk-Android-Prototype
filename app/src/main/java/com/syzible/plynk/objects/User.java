package com.syzible.plynk.objects;

import android.graphics.Bitmap;

import com.stfalcon.chatkit.commons.models.IUser;

import org.json.JSONObject;

/**
 * Created by ed on 13/11/2017.
 */

public class User implements IUser {
    private String id, forename, surname, profileUrl;

    public User(JSONObject object) {

    }

    public User(String id, String forename, String surname, String profileUrl) {
        this.id = id;
        this.forename = forename;
        this.surname = surname;
        this.profileUrl = profileUrl;
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
        return forename;
    }

    public String getSurname() {
        return surname;
    }

    public String getFullName() {
        return forename + " " + surname;
    }

    @Override
    public String getAvatar() {
        return profileUrl;
    }

    public String getProfileUrl() {
        return profileUrl;
    }
}
