package com.syzible.plynk.utils;

import android.content.Context;

import com.syzible.plynk.fragments.ManageExternalMoneyFragment;
import com.syzible.plynk.objects.Card;
import com.syzible.plynk.objects.Transaction;
import com.syzible.plynk.persistence.LocalPrefs;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Random;

/**
 * Created by ed on 15/11/2017.
 */

public class JSONUtils {

    public static JSONObject getId(Context context) {
        JSONObject o = new JSONObject();
        try {
            o.put("user_id", LocalPrefs.getID(context));
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return o;
    }

    public static JSONObject getBankCardPayload(Context context, Card card) {
        JSONObject o = new JSONObject();
        try {
            o.put("user_id", LocalPrefs.getID(context));
            o.put("card_number", card.getNumber());
            o.put("card_cvv", card.getCvv());
            o.put("card_expiry", card.getExpiry());
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return o;
    }

    public static JSONObject generateExpense(Transaction transaction, Context context) {
        JSONObject o = new JSONObject();

        try {
            o.put("user_id", LocalPrefs.getID(context));
            o.put("merchant_id", transaction.getRecipient().getVendorName());
            o.put("amount", transaction.getAmount());
            o.put("description", "generated expense of €" + transaction.getAmount());
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return o;
    }

    public static JSONObject generateExpense(Context context) {
        JSONObject o = new JSONObject();

        double expense = ((double) new Random().nextInt(1000)) / 100;
        try {
            o.put("user_id", LocalPrefs.getID(context));
            o.put("merchant_id", "Spar");
            o.put("amount", expense);
            o.put("description", "generated expense of €" + expense);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return o;
    }

    public static JSONObject generateFundsWithdrawal(Context context, double amount) {
        JSONObject o = new JSONObject();

        try {
            o.put("user_id", LocalPrefs.getID(context));
            o.put("amount", amount);
            o.put("description", ManageExternalMoneyFragment.WithdrawalType.withdrawal_to_bank.name());
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return o;
    }

    public static JSONObject generateFundsAddition(ManageExternalMoneyFragment.PreloadType preloadType, Context context) {
        JSONObject o = new JSONObject();
        try {
            o.put("bank_card_id", preloadType == ManageExternalMoneyFragment.PreloadType.preload_android_pay ? "-1" : "-2");
            o.put("user_id", LocalPrefs.getID(context));
            o.put("amount", new Random().nextInt(20));
            o.put("description", preloadType == ManageExternalMoneyFragment.PreloadType.preload_android_pay ? "Android Pay" : "Bank Account");
            o.put("preload_type", preloadType.name());
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return o;
    }
}
