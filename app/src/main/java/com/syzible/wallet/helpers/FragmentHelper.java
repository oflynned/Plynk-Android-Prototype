package com.syzible.wallet.helpers;

import android.app.Fragment;
import android.app.FragmentManager;

import com.syzible.wallet.R;

/**
 * Created by ed on 13/11/2017.
 */

public class FragmentHelper {
    public static void setFragmentWithoutBackstack(FragmentManager fm, Fragment f) {
        if (fm != null)
            fm.beginTransaction()
                    .replace(R.id.fragment_holder, f)
                    .commit();
    }

    public static void setFragmentBackstack(FragmentManager fm, Fragment f) {
        if (fm != null)
            fm.beginTransaction()
                    .replace(R.id.fragment_holder, f)
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
