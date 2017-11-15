package com.syzible.plynk.objects;

import com.stfalcon.chatkit.commons.models.IDialog;
import com.stfalcon.chatkit.commons.models.IMessage;
import com.stfalcon.chatkit.commons.models.IUser;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by ed on 14/11/2017.
 */

public class Conversation implements IDialog<Message> {
    private List<User> users = new ArrayList<>();
    private String id, profilePic, name;
    private Message lastMessage;
    private int unreadCount;

    public Conversation(User user, Message lastMessage, int unreadCount) {
        this.users.add(user);
        this.id = user.getId();
        this.profilePic = user.getAvatar();
        this.name = user.getFullName();
        this.lastMessage = lastMessage;
        this.unreadCount = unreadCount;
    }

    @Override
    public String getId() {
        return id;
    }

    @Override
    public String getDialogPhoto() {
        return profilePic;
    }

    @Override
    public String getDialogName() {
        return name;
    }

    @Override
    public List<? extends IUser> getUsers() {
        return users;
    }

    @Override
    public Message getLastMessage() {
        return lastMessage;
    }

    @Override
    public void setLastMessage(Message message) {
        this.lastMessage = message;
    }

    public Conversation setUnreadCount(int unreadCount) {
        this.unreadCount = unreadCount;
        return this;
    }

    @Override
    public int getUnreadCount() {
        return unreadCount;
    }
}
