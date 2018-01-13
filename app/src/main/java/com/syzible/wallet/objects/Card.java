package com.syzible.wallet.objects;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by ed on 16/11/2017.
 */

public class Card {
    private String cvv, expiry, number;
    private User user;

    public Card(JSONObject o) {
        try {
            this.user = new User(o.getJSONObject("user"));
            this.cvv = o.getString("card_cvv");
            this.expiry = o.getString("card_expiry");
            this.number = o.getString("card_number");
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public Card(String cvv, String expiry, String number) {
        this.cvv = cvv;
        this.expiry = expiry;
        this.number = number;
    }

    public User getUser() {
        return user;
    }

    public String getCvv() {
        return cvv;
    }

    public String getExpiry() {
        return expiry;
    }

    public String getNumber() {
        return number;
    }

    public String getFormattedCard() {
        String output = "";
        String[] cardData = number.split("");
        for (int i = 0; i < cardData.length; i++) {
            output += cardData[i];
            if (i > 0 && i < cardData.length - 1 && i % 4 == 0) {
                output += " ";
            }
        }

        return output;
    }
}
