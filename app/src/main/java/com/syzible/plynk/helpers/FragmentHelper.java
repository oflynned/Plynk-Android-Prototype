package com.syzible.plynk.helpers;

import android.app.Fragment;
import android.app.FragmentManager;

import com.syzible.plynk.R;

/**
 * Created by ed on 13/11/2017.
 */

public class FragmentHelper {
    public static void setFragmentWithoutBackstack(FragmentManager fm, Fragment f, int layoutHolder) {
        if (fm != null)
            fm.beginTransaction()
                    .replace(layoutHolder, f)
                    .commit();
    }

    public static void setFragmentBackstack(FragmentManager fm, Fragment f, int layoutHolder) {
        if (fm != null)
            fm.beginTransaction()
                    .replace(layoutHolder, f)
                    .addToBackStack(f.getClass().getName())
                    .commit();
    }

    public static void removeTopFragment(FragmentManager fragmentManager) {
        if (fragmentManager != null)
            fragmentManager.popBackStack();
    }

    public static void clearBackstack(FragmentManager fragmentManager) {
        if (fragmentManager != null)
            fragmentManager.popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
    }
}
