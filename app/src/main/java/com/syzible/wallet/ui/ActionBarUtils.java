package com.syzible.wallet.ui;

import android.app.Activity;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;

import com.syzible.wallet.R;

/**
 * Created by ed on 16/11/2017.
 */

public class ActionBarUtils {
    public static void setToolbar(Activity activity, String title, String subtitle) {
        ActionBar actionBar = ((AppCompatActivity) activity).getSupportActionBar();
        if (actionBar != null) {
            actionBar.setTitle(title);
            actionBar.setSubtitle(subtitle);
        }
    }

    public static void setToolbar(Activity activity, String title) {
        setToolbar(activity, title, null);
    }

    public static void resetToolbar(Activity activity) {
        setToolbar(activity, activity.getResources().getString(R.string.app_name), null);
    }
}
