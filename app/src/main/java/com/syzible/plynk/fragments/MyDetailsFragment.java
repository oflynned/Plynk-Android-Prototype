package com.syzible.plynk.fragments;

import android.app.Fragment;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.syzible.plynk.R;
import com.syzible.plynk.network.GetImage;
import com.syzible.plynk.network.NetworkCallback;
import com.syzible.plynk.persistence.LocalPrefs;
import com.syzible.plynk.ui.ActionBarUtils;
import com.syzible.plynk.utils.BitmapUtils;
import com.syzible.plynk.utils.CachingUtils;

/**
 * Created by ed on 13/11/2017.
 */

public class MyDetailsFragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_my_details, container, false);

        ImageView profilePic = view.findViewById(R.id.my_profile_pic);
        String id = LocalPrefs.getID(getActivity());

        if (!CachingUtils.doesImageExist(getActivity(), id)) {
            new GetImage(new NetworkCallback<Bitmap>() {
                @Override
                public void onResponse(Bitmap response) {
                    Bitmap croppedImage = BitmapUtils.getCroppedCircle(response);
                    profilePic.setImageBitmap(croppedImage);
                    CachingUtils.cacheImage(id, croppedImage);
                }

                @Override
                public void onFailure() {

                }
            }, id).execute();
        } else {
            Bitmap bitmap = CachingUtils.getCachedImage(id);
            profilePic.setImageBitmap(bitmap);
        }

        ActionBarUtils.resetToolbar(getActivity());

        return view;
    }
}
