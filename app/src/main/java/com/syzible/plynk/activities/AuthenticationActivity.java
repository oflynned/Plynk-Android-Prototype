package com.syzible.plynk.activities;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Base64;

import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.GraphRequest;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.loopj.android.http.BaseJsonHttpResponseHandler;
import com.syzible.plynk.R;
import com.syzible.plynk.network.Endpoints;
import com.syzible.plynk.network.RestClient;
import com.syzible.plynk.persistence.LocalPrefs;
import com.syzible.plynk.services.TokenService;
import com.syzible.plynk.utils.EncodingUtils;
import com.syzible.plynk.utils.FacebookUtils;

import org.json.JSONException;
import org.json.JSONObject;

import java.security.MessageDigest;
import java.util.Arrays;

import cz.msebera.android.httpclient.Header;
import mehdi.sakout.fancybuttons.FancyButton;


/**
 * Created by ed on 15/11/2017.
 */

public class AuthenticationActivity extends AppCompatActivity {

    private CallbackManager callbackManager;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_authentication);

        checkPermissions();

        if (FacebookUtils.hasExistingToken(this)) {
            startMain();
        } else {
            callbackManager = CallbackManager.Factory.create();

            FancyButton facebookLoginButton = findViewById(R.id.login_fb_login_button);
            facebookLoginButton.setOnClickListener(view -> LoginManager.getInstance().logInWithReadPermissions(
                    AuthenticationActivity.this,
                    Arrays.asList("user_friends", "public_profile")
            ));

            registerFacebookCallback();
        }

        // printHashKey();
    }

    public void printHashKey() {
        try {
            PackageInfo info = getPackageManager().getPackageInfo(getPackageName(), PackageManager.GET_SIGNATURES);
            for (Signature signature : info.signatures) {
                MessageDigest md = MessageDigest.getInstance("SHA");
                md.update(signature.toByteArray());
                String hashKey = new String(Base64.encode(md.digest(), 0));
                System.out.println(hashKey);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        callbackManager.onActivityResult(requestCode, resultCode, data);
    }

    public void checkPermissions() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED ||
                ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{
                            Manifest.permission.READ_EXTERNAL_STORAGE,
                            Manifest.permission.WRITE_EXTERNAL_STORAGE
                    }, 123);
        }
    }

    private void startMain() {
        AuthenticationActivity.this.finish();
        startActivity(new Intent(AuthenticationActivity.this, MainActivity.class));
    }

    private void registerFacebookCallback() {
        LoginManager.getInstance().registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                String accessToken = loginResult.getAccessToken().getToken();
                FacebookUtils.saveToken(accessToken, AuthenticationActivity.this);

                GraphRequest request = GraphRequest.newMeRequest(loginResult.getAccessToken(),
                        (o, response) -> {
                            try {
                                System.out.println(o);
                                String id = o.getString("id");
                                String forename = o.getString("first_name");
                                String surname = o.getString("last_name");
                                String gender = o.getString("gender");
                                String pic = "https://graph.facebook.com/" + id + "/picture?type=large";

                                JSONObject postData = new JSONObject();
                                postData.put("user_id", id);
                                postData.put("forename", EncodingUtils.encodeText(forename));
                                postData.put("surname", EncodingUtils.encodeText(surname));
                                postData.put("gender", gender);
                                postData.put("profile_pic", pic);

                                Context context = AuthenticationActivity.this;
                                LocalPrefs.setStringPref(LocalPrefs.Pref.user_id, id, context);
                                LocalPrefs.setStringPref(LocalPrefs.Pref.forename, forename, context);
                                LocalPrefs.setStringPref(LocalPrefs.Pref.surname, surname, context);
                                LocalPrefs.setStringPref(LocalPrefs.Pref.gender, gender, context);
                                LocalPrefs.setStringPref(LocalPrefs.Pref.profile_pic, pic, context);

                                Intent startFCMTokenService = new Intent(context, TokenService.class);
                                AuthenticationActivity.this.startService(startFCMTokenService);

                                RestClient.post(AuthenticationActivity.this, Endpoints.CREATE_USER, postData, new BaseJsonHttpResponseHandler<JSONObject>() {
                                    @Override
                                    public void onSuccess(int statusCode, Header[] headers, String rawJsonResponse, JSONObject response) {
                                        startMain();
                                    }

                                    @Override
                                    public void onFailure(int statusCode, Header[] headers, Throwable throwable, String rawJsonData, JSONObject errorResponse) {
                                        System.out.println("login failed?");
                                    }

                                    @Override
                                    protected JSONObject parseResponse(String rawJsonData, boolean isFailure) throws Throwable {
                                        return new JSONObject(rawJsonData);
                                    }
                                });

                            } catch (JSONException e) {
                                e.printStackTrace();
                            }

                        });

                Bundle parameters = new Bundle();
                parameters.putString("fields", "id,first_name,last_name,gender");
                request.setParameters(parameters);
                request.executeAsync();
            }

            @Override
            public void onCancel() {
                FacebookUtils.deleteToken(AuthenticationActivity.this);
            }

            @Override
            public void onError(FacebookException e) {
                FacebookUtils.deleteToken(AuthenticationActivity.this);
                e.printStackTrace();
            }
        });
    }

}
