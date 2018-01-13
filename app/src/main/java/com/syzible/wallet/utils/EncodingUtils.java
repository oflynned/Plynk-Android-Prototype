package com.syzible.wallet.utils;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * Created by ed on 15/11/2017.
 */

public class EncodingUtils {
    public static String encodeText(String text) {
        try {
            return URLEncoder.encode(text, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        return text;
    }

    public static String decodeText(String encodedText) {
        try {
            return URLDecoder.decode(encodedText, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        return encodedText;
    }

    public static String getEncodedDate(long time) {
        SimpleDateFormat format = new SimpleDateFormat("HH:mm dd/MM/yyyy", Locale.getDefault());
        Date date = new Date(time);
        return format.format(date);
    }

    public static String getEncodedCurrency(float amount) {
        NumberFormat formatter = NumberFormat.getCurrencyInstance();
        String value = formatter.format(amount);
        value = "€" + value.substring(1, value.length());
        return value;
    }
}
