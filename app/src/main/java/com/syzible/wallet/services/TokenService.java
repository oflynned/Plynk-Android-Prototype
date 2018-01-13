package com.syzible.wallet.services;

import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;
import com.loopj.android.http.BaseJsonHttpResponseHandler;
import com.syzible.wallet.network.Endpoints;
import com.syzible.wallet.network.RestClient;
import com.syzible.wallet.persistence.LocalPrefs;

import org.json.JSONException;
import org.json.JSONObject;

import cz.msebera.android.httpclient.Header;

/**
 * Created by ed on 15/11/2017.
 */

public class TokenService extends FirebaseInstanceIdService {
    @Override
    public void onTokenRefresh() {
        String refreshedToken = FirebaseInstanceId.getInstance().getToken();
        sendRegistrationToOwnServer(refreshedToken);
    }

    private void sendRegistrationToOwnServer(String token) {
        if (LocalPrefs.isLoggedIn(getApplicationContext())) {
            JSONObject payload = new JSONObject();
            try {
                payload.put("fb_id", LocalPrefs.getID(getApplicationContext()));
                payload.put("fcm_token", token);
            } catch (JSONException e) {
                e.printStackTrace();
            }

            RestClient.post(getApplicationContext(), Endpoints.EDIT_USER_FCM, payload, new BaseJsonHttpResponseHandler<JSONObject>() {
                @Override
                public void onSuccess(int statusCode, Header[] headers, String rawJsonResponse, JSONObject response) {

                }

                @Override
                public void onFailure(int statusCode, Header[] headers, Throwable throwable, String rawJsonData, JSONObject errorResponse) {

                }

                @Override
                protected JSONObject parseResponse(String rawJsonData, boolean isFailure) throws Throwable {
                    return new JSONObject(rawJsonData);
                }
            });
        }
    }
}
