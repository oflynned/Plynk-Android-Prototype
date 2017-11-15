package com.syzible.plynk.network;

/**
 * Created by ed on 17/05/2017.
 */

public class Endpoints {
    private static final int API_VERSION = 1;
    private static final String LOCAL_ENDPOINT = "http://10.0.2.2:3000";
    private static final String API_URL = LOCAL_ENDPOINT + "/api/v" + API_VERSION;

    // users
    public static final String CREATE_USER = API_URL + "/user/create";
    public static final String GET_USER = API_URL + "/user/get";
    public static final String DELETE_USER = API_URL + "/user/delete";
    public static final String EDIT_USER_FCM = API_URL + "/user/edit-fcm";

    // transactions
    public static final String GET_BALANCE = API_URL + "/transaction/balance";
}
