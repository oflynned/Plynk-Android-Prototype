package com.syzible.plynk.objects;

import com.stfalcon.chatkit.commons.models.IMessage;
import com.stfalcon.chatkit.commons.models.IUser;
import com.syzible.plynk.utils.EncodingUtils;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Date;

/**
 * Created by ed on 14/11/2017.
 */

public class Message implements IMessage {
    private User user;
    private String id, text, messageType;
    private long time;
    private double transactionAmount;
    private boolean wasSeen;

    public Message(User user, JSONObject o) {
        this.user = user;
        try {
            this.id = o.getJSONObject("_id").getString("$oid");

            if (o.has("preview_message"))
                o = o.getJSONObject("preview_message");

            this.time = o.getLong("time");

            this.messageType = o.getString("type");
            switch (messageType) {
                case "new_contact":
                    this.text = "New contact!";
                    break;
                case "user_message":
                case "user_transaction":
                    this.text = EncodingUtils.decodeText(o.getString("message"));
                    this.wasSeen = o.getBoolean("was_seen");
                    break;
            }

            if (o.has("amount")) {
                this.transactionAmount = o.getDouble("amount");
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public Message(User user, String id, String text, long time) {
        this.user = user;
        this.id = id;
        this.text = text;
        this.time = time;
    }

    @Override
    public String getId() {
        return id;
    }

    @Override
    public String getText() {
        return text;
    }

    @Override
    public IUser getUser() {
        return user;
    }

    @Override
    public Date getCreatedAt() {
        return new Date(time);
    }

    public double getTransactionAmount() {
        return transactionAmount;
    }

    public String getMessageType() {
        return messageType;
    }

    public long getTime() {
        return time;
    }

    public boolean isWasSeen() {
        return wasSeen;
    }
}
