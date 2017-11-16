package com.syzible.plynk.objects;

import android.content.Context;
import android.support.annotation.NonNull;

import com.syzible.plynk.persistence.LocalPrefs;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by ed on 16/11/2017.
 */

public class Transaction implements Comparable<Transaction>{
    private float amount;
    private User paidToUser, paidFromUser;
    private long time;

    public Transaction(JSONObject o) {
        try {
            this.amount = (float) o.getDouble("amount");
            this.paidToUser = new User(o.getJSONObject("user_paid_to"));
            this.paidFromUser = new User(o.getJSONObject("user_paid_from"));
            this.time = o.getLong("time");
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public Transaction(float amount, User paidToUser, User paidFromUser, long time) {
        this.amount = amount;
        this.paidToUser = paidToUser;
        this.paidFromUser = paidFromUser;
        this.time = time;
    }

    public float getAmount() {
        return amount;
    }

    public User getPaidToUser() {
        return paidToUser;
    }

    public User getPaidFromUser() {
        return paidFromUser;
    }

    public long getTime() {
        return time;
    }

    public boolean isPositive(Context context) {
        return paidToUser.getId().equals(LocalPrefs.getID(context));
    }

    @Override
    public int compareTo(@NonNull Transaction transaction) {
        return this.getTime() < transaction.getTime() ? 1 : -1;
    }
}
