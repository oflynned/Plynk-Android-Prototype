package com.syzible.wallet.objects;

import android.content.Context;
import android.support.annotation.NonNull;

import com.syzible.wallet.persistence.LocalPrefs;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by ed on 16/11/2017.
 */

public class Transaction implements Comparable<Transaction> {
    private float amount;
    private Vendor recipient, sender;
    private long time;

    enum TransactionType {
        individual_transaction,
        preload_android_pay, preload_card,
        good_service_payment, withdrawal_to_bank
    }

    public Transaction(JSONObject o) {
        try {
            this.amount = (float) o.getDouble("amount");

            System.out.println(o);
            TransactionType type = TransactionType.valueOf(o.getString("transaction_type"));
            if (type.equals(TransactionType.individual_transaction)) {
                this.recipient = new User(o.getJSONObject("paid_to"));
                this.sender = new User(o.getJSONObject("paid_from"));
            } else if (type.equals(TransactionType.good_service_payment)) {
                this.recipient = new Merchant(o.getJSONObject("paid_to"));
                this.sender = new User(o.getJSONObject("paid_from"));
            } else if (type.equals(TransactionType.preload_android_pay)) {
                this.recipient = new User(o.getJSONObject("paid_to"));
                this.sender = new Institute(o.getJSONObject("paid_from"));
            } else if (type.equals(TransactionType.preload_card)) {
                this.recipient = new User(o.getJSONObject("paid_to"));
                this.sender = new Institute(o.getJSONObject("paid_from"));
            } else if (type.equals(TransactionType.withdrawal_to_bank)) {
                this.recipient = new Institute(o.getJSONObject("paid_to"));
                this.sender = new User(o.getJSONObject("paid_from"));
            }

            this.time = o.getLong("time");
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public Transaction(float amount, Vendor paidToUser, Vendor paidFromUser, long time) {
        this.amount = amount;
        this.recipient = paidToUser;
        this.sender = paidFromUser;
        this.time = time;
    }

    public float getAmount() {
        return amount;
    }

    public Vendor getRecipient() {
        return recipient;
    }

    public Vendor getSender() {
        return sender;
    }

    public long getTime() {
        return time;
    }

    public boolean isPositive(Context context) {
        return recipient.getId().equals(LocalPrefs.getID(context));
    }

    @Override
    public int compareTo(@NonNull Transaction transaction) {
        return this.getTime() < transaction.getTime() ? 1 : -1;
    }
}
