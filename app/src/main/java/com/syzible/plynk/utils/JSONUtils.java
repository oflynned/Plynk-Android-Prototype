package com.syzible.plynk.utils;

import android.content.Context;

import com.syzible.plynk.fragments.ManageExternalMoneyFragment;
import com.syzible.plynk.objects.Card;
import com.syzible.plynk.objects.Transaction;
import com.syzible.plynk.objects.User;
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

    public static JSONObject getMessageInteractionPayload(Context context, User partner) {
        return getMessageInteractionPayload(context, partner.getId());
    }

    public static JSONObject getMessageInteractionPayload(Context context, String id) {
        JSONObject o = new JSONObject();
        try {
            o.put("from_id", LocalPrefs.getID(context));
            o.put("to_id", id);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return o;
    }

    public static JSONObject getPartnerInteractionPayload(Context context, User partner) {
        return getPartnerInteractionPayload(context, partner.getId(), null);
    }

    public static JSONObject getPartnerInteractionPayload(Context context, User partner, String type) {
        return getPartnerInteractionPayload(context, partner.getId(), type);
    }

    public static JSONObject getPartnerInteractionPayload(Context context, String id, String type) {
        JSONObject o = new JSONObject();
        try {
            o.put("my_id", LocalPrefs.getID(context));
            o.put("partner_id", id);

            if (type != null)
                o.put("type", type);
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

    public static JSONObject generateCardFundsAddition(JSONObject cardData, float amount, Context context) {
        JSONObject o = new JSONObject();
        try {
            o.put("bank_card_id", cardData.getString("card_number"));
            o.put("user_id", LocalPrefs.getID(context));
            o.put("amount", amount);
            o.put("description", "Bank Account");
            o.put("preload_type", ManageExternalMoneyFragment.PreloadType.preload_card.name());
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return o;
    }

    public static JSONObject generateAndroidPayFundsAddition(float amount, Context context) {
        JSONObject o = new JSONObject();
        try {
            o.put("bank_card_id", "-1");
            o.put("user_id", LocalPrefs.getID(context));
            o.put("amount", amount);
            o.put("description", "Android Pay");
            o.put("preload_type", ManageExternalMoneyFragment.PreloadType.preload_android_pay.name());
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return o;
    }
}
