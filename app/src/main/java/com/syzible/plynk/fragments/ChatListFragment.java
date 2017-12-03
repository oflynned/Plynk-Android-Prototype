package com.syzible.plynk.fragments;

import android.app.AlertDialog;
import android.app.Fragment;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.MainThread;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.loopj.android.http.BaseJsonHttpResponseHandler;
import com.stfalcon.chatkit.commons.ImageLoader;
import com.stfalcon.chatkit.dialogs.DialogsList;
import com.stfalcon.chatkit.dialogs.DialogsListAdapter;
import com.syzible.plynk.R;
import com.syzible.plynk.activities.MainActivity;
import com.syzible.plynk.helpers.FragmentHelper;
import com.syzible.plynk.network.Endpoints;
import com.syzible.plynk.network.GetImage;
import com.syzible.plynk.network.NetworkCallback;
import com.syzible.plynk.network.RestClient;
import com.syzible.plynk.objects.Conversation;
import com.syzible.plynk.objects.Message;
import com.syzible.plynk.objects.Transaction;
import com.syzible.plynk.objects.User;
import com.syzible.plynk.ui.ActionBarUtils;
import com.syzible.plynk.utils.BitmapUtils;
import com.syzible.plynk.utils.CachingUtils;
import com.syzible.plynk.utils.EmojiUtils;
import com.syzible.plynk.utils.JSONUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;

import cz.msebera.android.httpclient.Header;

/**
 * Created by ed on 13/11/2017.
 */

public class ChatListFragment extends Fragment implements DialogsListAdapter.OnDialogClickListener<Conversation>, DialogsListAdapter.OnDialogLongClickListener<Conversation> {
    private ArrayList<Conversation> conversations = new ArrayList<>();
    private View view;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_chat_list, container, false);

        ActionBarUtils.resetToolbar(getActivity());
        loadUsers();

        return view;
    }

    private void loadUsers() {
        RestClient.post(getActivity(), Endpoints.GET_MESSAGE_PREVIEWS, JSONUtils.getId(getActivity()),
                new BaseJsonHttpResponseHandler<JSONArray>() {
                    @Override
                    public void onSuccess(int statusCode, Header[] headers, String rawJsonResponse, JSONArray response) {
                        conversations = new ArrayList<>();

                        for (int i = 0; i < response.length(); i++) {
                            try {
                                JSONObject entity = response.getJSONObject(i);
                                System.out.println(entity);
                                User user = new User(entity);
                                Message message = new Message(user, entity);
                                Conversation conversation = new Conversation(user, message, entity.getInt("unread_count"));
                                conversations.add(conversation);
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }

                        Collections.sort(conversations, (c1, c2) ->
                                c2.getLastMessage().getCreatedAt().compareTo(c1.getLastMessage().getCreatedAt()));

                        DialogsList dialogsList = view.findViewById(R.id.conversations_list);
                        DialogsListAdapter<Conversation> dialogsListAdapter = new DialogsListAdapter<>(loadImage());
                        dialogsListAdapter.setItems(conversations);
                        dialogsListAdapter.setOnDialogClickListener(ChatListFragment.this);
                        dialogsList.setAdapter(dialogsListAdapter);
                        dialogsList.scrollToPosition(conversations.size() - 1);
                    }

                    @Override
                    public void onFailure(int statusCode, Header[] headers, Throwable throwable, String rawJsonData, JSONArray errorResponse) {

                    }

                    @Override
                    protected JSONArray parseResponse(String rawJsonData, boolean isFailure) throws Throwable {
                        return new JSONArray(rawJsonData);
                    }
                });
    }

    private ImageLoader loadImage() {
        return (imageView, url) -> {
            // can only use Facebook to sign up so use the embedded id in the url
            final String id = url.split("/")[3];

            if (!CachingUtils.doesImageExist(getActivity(), id)) {
                new GetImage(new NetworkCallback<Bitmap>() {
                    @Override
                    public void onResponse(Bitmap response) {
                        Bitmap croppedImage = BitmapUtils.getCroppedCircle(response);
                        final Bitmap scaledAvatar = BitmapUtils.scaleBitmap(croppedImage, BitmapUtils.BITMAP_SIZE_SMALL);
                        CachingUtils.cacheImage(id, scaledAvatar);
                        imageView.setImageBitmap(scaledAvatar);
                    }

                    @Override
                    public void onFailure() {
                        System.out.println("dl failure on chat pic");
                    }
                }, url).execute();
            } else {
                final Bitmap cachedImage = CachingUtils.getCachedImage(id);
                imageView.setImageBitmap(cachedImage);
            }
        };
    }

    @Override
    public void onDialogClick(Conversation conversation) {
        for (int i = 0; i < conversations.size(); i++)
            if (conversations.get(i).getId().equals(conversation.getId()))
                conversations.get(i).setUnreadCount(0);

        User partner = (User) conversation.getUsers().get(0);
        Fragment f = new ChatFragment().setPartner(partner);
        FragmentHelper.setFragmentBackstack(getFragmentManager(), f);
    }

    @Override
    public void onDialogLongClick(Conversation conversation) {
        User partner = (User) conversation.getUsers().get(0);
        new AlertDialog.Builder(getActivity())
                .setTitle("Send money to " + partner.getForename() + "?")
                .setMessage("Click okay to send €4.20 to " + partner.getFullName() + " " + EmojiUtils.getEmoji(EmojiUtils.HAPPY))
                .setPositiveButton("Send", (dialogInterface, i) -> {
                    Transaction t = new Transaction(4.20f, partner, User.getMe(getActivity()), System.currentTimeMillis());
                    JSONObject o = JSONUtils.generateExpense(t, getActivity());
                    RestClient.post(getActivity(), Endpoints.USER_TRANSACTION, o, new BaseJsonHttpResponseHandler<JSONObject>() {
                        @Override
                        public void onSuccess(int statusCode, Header[] headers, String rawJsonResponse, JSONObject response) {

                        }

                        @Override
                        public void onFailure(int statusCode, Header[] headers, Throwable throwable, String rawJsonData, JSONObject errorResponse) {

                        }

                        @Override
                        protected JSONObject parseResponse(String rawJsonData, boolean isFailure) throws Throwable {
                            return new JSONObject(rawJsonData);
                        }
                    });
                })
                .setNegativeButton("Cancel", null)
                .create()
                .show();
    }
}
