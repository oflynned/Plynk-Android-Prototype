package com.syzible.plynk.fragments;

import android.annotation.SuppressLint;
import android.app.Fragment;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.stfalcon.chatkit.commons.ImageLoader;
import com.stfalcon.chatkit.dialogs.DialogsList;
import com.stfalcon.chatkit.dialogs.DialogsListAdapter;
import com.syzible.plynk.R;
import com.syzible.plynk.network.GetImage;
import com.syzible.plynk.network.NetworkCallback;
import com.syzible.plynk.objects.Conversation;
import com.syzible.plynk.objects.Message;
import com.syzible.plynk.objects.User;
import com.syzible.plynk.ui.ActionBarUtils;
import com.syzible.plynk.utils.BitmapUtils;
import com.syzible.plynk.utils.CachingUtils;
import com.syzible.plynk.utils.EmojiUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Random;

/**
 * Created by ed on 13/11/2017.
 */

public class ChatFragment extends Fragment {
    private ArrayList<Conversation> conversations = new ArrayList<>();

    private static final String SHANE_PIC = "https://www.plynk.me/assets/images/team/shane.jpg";
    private static final String CHRIS_PIC = "https://www.plynk.me/assets/images/team/chris.jpg";
    private static final String JOSE_PIC = "https://www.plynk.me/assets/images/team/jose.jpg";

    @SuppressLint("NewApi")
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_chat, container, false);

        User user1 = new User("1", "Shane", "Devane", SHANE_PIC);
        Message message1 = new Message(user1, "1", "Here's that €5.50 I owe you", getReportedTime());
        conversations.add(new Conversation(user1, message1, 1));

        User user2 = new User("2", "Chris", "La Pat", CHRIS_PIC);
        Message message2 = new Message(user2, "2", "Thanks for paying for that taxi " + EmojiUtils.getEmoji(EmojiUtils.HAPPY), getReportedTime());
        conversations.add(new Conversation(user2, message2, 0));

        User user3 = new User("3", "Jose", "Alfonso Mora Lores", JOSE_PIC);
        Message message3 = new Message(user3, "3", "Where the fuck is my money", getReportedTime());
        conversations.add(new Conversation(user3, message3, 3));

        conversations.sort((Conversation c1, Conversation c2) -> c2.getLastMessage().getCreatedAt().compareTo(c1.getLastMessage().getCreatedAt()));

        DialogsList dialogsList = view.findViewById(R.id.conversations_list);
        DialogsListAdapter<Conversation> dialogsListAdapter = new DialogsListAdapter<>(loadImage());
        dialogsListAdapter.setItems(conversations);
        dialogsList.setAdapter(dialogsListAdapter);
        dialogsList.scrollToPosition(conversations.size() - 1);

        ActionBarUtils.resetToolbar(getActivity());

        return view;
    }

    private static long getReportedTime() {
        final long TIME_PERIOD = 1000 * 60 * 60 * 6;
        return System.currentTimeMillis() - (new Random().nextLong() % TIME_PERIOD);
    }

    private ImageLoader loadImage() {
        return (imageView, url) -> {
            String[] urlParts = url.split("/");
            String id = urlParts[urlParts.length - 1];

            if (!CachingUtils.doesImageExist(getActivity(), id)) {
                new GetImage(new NetworkCallback<Bitmap>() {
                    @Override
                    public void onResponse(Bitmap response) {
                        Bitmap scaledAvatar = BitmapUtils.generateMetUserAvatar(response);
                        imageView.setImageBitmap(scaledAvatar);
                        CachingUtils.cacheImage(id, scaledAvatar);
                    }

                    @Override
                    public void onFailure() {
                        System.out.println("dl failure on chat pic");
                    }
                }, url).execute();
            } else {
                Bitmap bitmap = CachingUtils.getCachedImage(id);
                imageView.setImageBitmap(bitmap);
            }
        };
    }
}
