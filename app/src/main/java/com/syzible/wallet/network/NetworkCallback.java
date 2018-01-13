package com.syzible.wallet.network;

/**
 * Created by ed on 16/12/2016
 */
public interface NetworkCallback<T> {
    void onResponse(T response);
    void onFailure();
}
