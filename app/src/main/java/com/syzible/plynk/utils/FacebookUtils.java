package com.syzible.plynk.utils;

import android.content.Context;
import android.content.Intent;

import com.facebook.login.LoginManager;
import com.syzible.plynk.persistence.LocalPrefs;
import com.syzible.plynk.services.TokenService;

/**
 * Created by ed on 15/11/2017.
 */

public class FacebookUtils {
    public static void saveToken(String token, Context context) {
        LocalPrefs.setStringPref(LocalPrefs.Pref.fb_access_token, token, context);
    }

    public static String getToken(Context context) {
        return LocalPrefs.getStringPref(LocalPrefs.Pref.fb_access_token, context);
    }

    public static boolean hasExistingToken(Context context) {
        return !getToken(context).equals("");
    }

    private static void clearToken(Context context) {
        LocalPrefs.purgePref(LocalPrefs.Pref.fb_access_token, context);
    }

    public static void getStoredPrefs(Context context) {
        for (LocalPrefs.Pref pref : LocalPrefs.Pref.values())
            System.out.println(pref.name() + ": " + LocalPrefs.getStringPref(pref, context));
    }

    public static void deleteToken(Context context) {
        // stop updating the FCM token to the server
        Intent fcmTokenService = new Intent(context, TokenService.class);
        context.stopService(fcmTokenService);

        // now log out and clear tokens
        clearToken(context);
        LoginManager.getInstance().logOut();
    }
}
