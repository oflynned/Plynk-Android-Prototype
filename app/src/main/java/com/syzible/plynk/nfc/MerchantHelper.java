package com.syzible.plynk.nfc;

import java.util.Random;

/**
 * Created by ed on 16/11/2017.
 */

public class MerchantHelper {

    public static String getPurchase() {
        return getMerchant() + "/" + getPrice() + "/" + getTime();
    }

    private static String getMerchant() {
        String[] merchants = {
                "Spar", "Centra", "Londis", "Tesco", "SuperValu", "Lidl", "Aldi"
        };

        return merchants[new Random().nextInt(merchants.length)];
    }

    private static long getTime() {
        return System.currentTimeMillis();
    }

    private static double getPrice() {
        return ((double) new Random().nextInt(1000)) / 100;
    }
}
