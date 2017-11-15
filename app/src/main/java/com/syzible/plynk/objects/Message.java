package com.syzible.plynk.objects;

import com.stfalcon.chatkit.commons.models.IMessage;
import com.stfalcon.chatkit.commons.models.IUser;

import java.util.Date;

/**
 * Created by ed on 14/11/2017.
 */

public class Message implements IMessage {
    private User user;
    private String id, text;
    private long time;

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
}
