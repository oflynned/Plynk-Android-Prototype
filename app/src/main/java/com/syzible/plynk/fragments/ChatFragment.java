package com.syzible.plynk.fragments;

import android.app.Fragment;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.loopj.android.http.BaseJsonHttpResponseHandler;
import com.stfalcon.chatkit.commons.ImageLoader;
import com.stfalcon.chatkit.messages.MessageInput;
import com.stfalcon.chatkit.messages.MessagesList;
import com.stfalcon.chatkit.messages.MessagesListAdapter;
import com.syzible.plynk.R;
import com.syzible.plynk.network.BroadcastFilters;
import com.syzible.plynk.network.Endpoints;
import com.syzible.plynk.network.GetImage;
import com.syzible.plynk.network.NetworkCallback;
import com.syzible.plynk.network.RestClient;
import com.syzible.plynk.objects.Message;
import com.syzible.plynk.objects.User;
import com.syzible.plynk.persistence.LocalPrefs;
import com.syzible.plynk.services.NetworkAvailableService;
import com.syzible.plynk.services.NotificationUtils;
import com.syzible.plynk.ui.ActionBarUtils;
import com.syzible.plynk.utils.BitmapUtils;
import com.syzible.plynk.utils.CachingUtils;
import com.syzible.plynk.utils.EncodingUtils;
import com.syzible.plynk.utils.JSONUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Objects;

import cz.msebera.android.httpclient.Header;

/**
 * Created by ed on 16/11/2017.
 */

public class ChatFragment extends Fragment {

    private View view;
    private Context context;
    private User partner;
    private ArrayList<Message> messages = new ArrayList<>();

    private MessagesListAdapter<Message> adapter;
    private BroadcastReceiver newPartnerMessageReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (Objects.equals(intent.getAction(), BroadcastFilters.on_new_message_received.toString())) {
                String partnerId = intent.getStringExtra("partner_id");

                if (partnerId.equals(partner.getId())) {
                    JSONObject payload = JSONUtils.getMessageInteractionPayload(getActivity(), partnerId);
                    RestClient.post(getActivity(), Endpoints.GET_MESSAGES, payload,
                            new BaseJsonHttpResponseHandler<JSONArray>() {
                                @Override
                                public void onSuccess(int statusCode, Header[] headers, String rawJsonResponse, JSONArray response) {
                                    try {
                                        JSONObject latestPayload = response.getJSONObject(response.length() - 1);
                                        User sender = new User(latestPayload.getJSONObject("user"));
                                        Message message = new Message(sender, latestPayload.getJSONObject("message"));
                                        adapter.addToStart(message, true);

                                        //markSeen();
                                        NotificationUtils.dismissNotification(getActivity(), partner);
                                        partner.setLastActive(sender.getLastActive());
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }
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
            }
        }
    };

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_chat, container, false);
        this.context = ChatFragment.this.getActivity();

        ActionBarUtils.setToolbar(getActivity(), partner.getFullName(), "Money Sent | Money Received");
        loadMessages();
        setupAdapter(view);

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    private void setupAdapter(View view) {
        adapter = new MessagesListAdapter<>(LocalPrefs.getID(context), loadImage());

        setMessageInputListener(adapter);

        MessagesList messagesList = view.findViewById(R.id.messages_list);
        messagesList.setAdapter(adapter);
    }

    private void loadMessages() {
        setupAdapter(view);
        RestClient.post(context, Endpoints.GET_MESSAGES,
                JSONUtils.getPartnerInteractionPayload(context, partner),
                new BaseJsonHttpResponseHandler<JSONArray>() {
                    @Override
                    public void onSuccess(int statusCode, Header[] headers, String rawJsonResponse, JSONArray response) {
                        messages.clear();
                        for (int i = 0; i < response.length(); i++) {
                            try {
                                JSONObject data = response.getJSONObject(i);
                                JSONObject dataMessage = data.getJSONObject("message");
                                User sender = new User(data.getJSONObject("user"));
                                Message message = new Message(sender, dataMessage);
                                messages.add(message);
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }

                        adapter.addToEnd(messages, true);
                    }

                    @Override
                    public void onFailure(int statusCode, Header[] headers, Throwable throwable, String rawJsonData, JSONArray errorResponse) {
                        System.out.println("failed?");
                    }

                    @Override
                    protected JSONArray parseResponse(String rawJsonData, boolean isFailure) throws Throwable {
                        return new JSONArray(rawJsonData);
                    }
                });

        // markSeen();
    }

    private void markSeen() {
        RestClient.post(context, Endpoints.MARK_MESSAGE_SEEN,
                JSONUtils.getMessageInteractionPayload(context, partner),
                new BaseJsonHttpResponseHandler<JSONObject>() {
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
    }

    private void setMessageInputListener(final MessagesListAdapter<Message> adapter) {
        MessageInput messageInput = view.findViewById(R.id.message_input);
        messageInput.setInputListener(input -> {
            final String messageContent = input.toString().trim();
            final User me = User.getMe(getActivity());

            try {
                Message message = new Message(me, LocalPrefs.getID(context), messageContent, System.currentTimeMillis());
                adapter.addToStart(message, true);

                JSONObject messagePayload = JSONUtils.getMessageInteractionPayload(context, partner);
                messagePayload.put("message", EncodingUtils.encodeText(message.getText().trim()));
                messagePayload.put("type", "user_message");

                RestClient.post(context, Endpoints.SEND_MESSAGE, messagePayload, new BaseJsonHttpResponseHandler<JSONObject>() {
                    @Override
                    public void onSuccess(int statusCode, Header[] headers, String rawJsonResponse, JSONObject response) {
                        System.out.println(response);
                    }

                    @Override
                    public void onFailure(int statusCode, Header[] headers, Throwable throwable, String rawJsonData, JSONObject errorResponse) {
                        System.out.println(rawJsonData);
                    }

                    @Override
                    protected JSONObject parseResponse(String rawJsonData, boolean isFailure) throws Throwable {
                        return new JSONObject(rawJsonData);
                    }
                });
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return true;
        });
    }

    public ChatFragment setPartner(User partner) {
        this.partner = partner;
        return this;
    }

    private ImageLoader loadImage() {
        return (imageView, url) -> {
            // can only use Facebook to sign up so use the embedded id in the url
            final String id = url.split("/")[3];

            if (!CachingUtils.doesImageExist(context, id)) {
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
}
