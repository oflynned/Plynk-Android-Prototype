package com.syzible.wallet.objects;

import com.syzible.wallet.utils.EncodingUtils;

/**
 * Created by ed on 16/11/2017.
 */

public abstract class Vendor {
    private String id, name, picture;
    private float expenseAmount;

    public Vendor(String id, String name, String picture) {
        this.id = id;
        this.name = name;
        this.picture = picture;
    }

    public String getId() {
        return id;
    }

    public String getVendorName() {
        return EncodingUtils.decodeText(name);
    }

    public String getPicture() {
        return picture;
    }

    public float getExpenseAmount() {
        return expenseAmount;
    }

    public void setExpenseAmount(float expenseAmount) {
        this.expenseAmount = expenseAmount;
    }
}
