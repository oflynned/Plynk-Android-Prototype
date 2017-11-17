package com.syzible.plynk.fragments;

import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.loopj.android.http.BaseJsonHttpResponseHandler;
import com.syzible.plynk.R;
import com.syzible.plynk.activities.AndroidPayActivity;
import com.syzible.plynk.network.Endpoints;
import com.syzible.plynk.network.RestClient;
import com.syzible.plynk.ui.ActionBarUtils;
import com.syzible.plynk.utils.JSONUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import cz.msebera.android.httpclient.Header;

/**
 * Created by ed on 16/11/2017.
 */

public class ManageExternalMoneyFragment extends Fragment {

    public enum PreloadType {
        preload_android_pay, preload_card
    }

    public enum WithdrawalType {
        plynk_good_service_payment, withdrawal_to_bank
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_manage_external_money, container, false);
        ActionBarUtils.setToolbar(getActivity(), "Manage External Funds");

        view.findViewById(R.id.add_bank_card).setOnClickListener(v -> {
            RestClient.post(getActivity(), Endpoints.ADD_BANK_CARD, JSONUtils.getId(getActivity()), new BaseJsonHttpResponseHandler<JSONObject>() {
                @Override
                public void onSuccess(int statusCode, Header[] headers, String rawJsonResponse, JSONObject response) {
                    Toast.makeText(getActivity(), rawJsonResponse, Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onFailure(int statusCode, Header[] headers, Throwable throwable, String rawJsonData, JSONObject errorResponse) {

                }

                @Override
                protected JSONObject parseResponse(String rawJsonData, boolean isFailure) throws Throwable {
                    return new JSONObject(rawJsonData);
                }
            });
        });

        view.findViewById(R.id.view_bank_cards).setOnClickListener(v -> {
            RestClient.post(getActivity(), Endpoints.GET_BANK_CARD, JSONUtils.getId(getActivity()), new BaseJsonHttpResponseHandler<JSONObject>() {
                @Override
                public void onSuccess(int statusCode, Header[] headers, String rawJsonResponse, JSONObject response) {
                    if (response.has("success")) {
                        Toast.makeText(getActivity(), "No bank card added", Toast.LENGTH_SHORT).show();
                    } else {
                        try {
                            Toast.makeText(getActivity(), response.getString("card_number"), Toast.LENGTH_SHORT).show();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }

                @Override
                public void onFailure(int statusCode, Header[] headers, Throwable throwable, String rawJsonData, JSONObject errorResponse) {

                }

                @Override
                protected JSONObject parseResponse(String rawJsonData, boolean isFailure) throws Throwable {
                    return new JSONObject(rawJsonData);
                }
            });
        });

        view.findViewById(R.id.preload_via_android_pay).setOnClickListener(v -> {
            Intent androidPayIntent = new Intent(getActivity(), AndroidPayActivity.class);
            androidPayIntent.putExtra("vendor_action", "deposit");
            androidPayIntent.putExtra("vendor_name", "-1");
            androidPayIntent.putExtra("vendor_expense", String.valueOf(10));
            androidPayIntent.putExtra("vendor_sale_time", String.valueOf(System.currentTimeMillis()));

            Fragment currentFragment = getFragmentManager().findFragmentById(R.id.fragment_holder);
            androidPayIntent.putExtra("last_fragment_active", currentFragment.getClass().getName());
            startActivity(androidPayIntent);
        });

        view.findViewById(R.id.preload_via_bank_card).setOnClickListener(v -> {
            RestClient.post(getActivity(), Endpoints.GET_BANK_CARD, JSONUtils.getId(getActivity()), new BaseJsonHttpResponseHandler<JSONObject>() {
                @Override
                public void onSuccess(int statusCode, Header[] headers, String rawJsonResponse, JSONObject response) {
                    if (response.has("success")) {
                        Toast.makeText(getActivity(), "No bank card added", Toast.LENGTH_SHORT).show();
                    } else {
                        JSONObject payload = JSONUtils.generateFundsAddition(PreloadType.preload_card, response, 10, getActivity());
                        RestClient.post(getActivity(), Endpoints.CARD_TOPUP, payload, new BaseJsonHttpResponseHandler<JSONObject>() {
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

                @Override
                public void onFailure(int statusCode, Header[] headers, Throwable throwable, String rawJsonData, JSONObject errorResponse) {

                }

                @Override
                protected JSONObject parseResponse(String rawJsonData, boolean isFailure) throws Throwable {
                    return new JSONObject(rawJsonData);
                }
            });
        });

        view.findViewById(R.id.move_funds_to_bank_account).setOnClickListener(v -> {
            JSONObject payload = JSONUtils.generateFundsWithdrawal(getActivity(), 10);
            RestClient.post(getActivity(), Endpoints.WITHDRAW_TO_BANK, payload, new BaseJsonHttpResponseHandler<JSONObject>() {
                @Override
                public void onSuccess(int statusCode, Header[] headers, String rawJsonResponse, JSONObject response) {
                    Toast.makeText(getActivity(), rawJsonResponse, Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onFailure(int statusCode, Header[] headers, Throwable throwable, String rawJsonData, JSONObject errorResponse) {

                }

                @Override
                protected JSONObject parseResponse(String rawJsonData, boolean isFailure) throws Throwable {
                    return new JSONObject(rawJsonData);
                }
            });
        });
        return view;
    }
}
