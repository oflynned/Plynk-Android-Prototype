package com.syzible.plynk.fragments;

import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.loopj.android.http.BaseJsonHttpResponseHandler;
import com.syzible.plynk.R;
import com.syzible.plynk.network.Endpoints;
import com.syzible.plynk.network.RestClient;
import com.syzible.plynk.objects.Transaction;
import com.syzible.plynk.persistence.LocalPrefs;
import com.syzible.plynk.ui.ActionBarUtils;
import com.syzible.plynk.utils.JSONUtils;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Random;

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

        view.findViewById(R.id.make_plynk_card_purchase).setOnClickListener(v -> {
            JSONObject payload = JSONUtils.generateExpense(getActivity());

            RestClient.post(getActivity(), Endpoints.CARD_PAYMENT, payload, new BaseJsonHttpResponseHandler<JSONObject>() {
                @Override
                public void onSuccess(int statusCode, Header[] headers, String rawJsonResponse, JSONObject response) {
                    System.out.println(response);
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
            JSONObject payload = JSONUtils.generateFundsAddition(PreloadType.preload_android_pay, getActivity());

            RestClient.post(getActivity(), Endpoints.CARD_TOPUP, payload, new BaseJsonHttpResponseHandler<JSONObject>() {
                @Override
                public void onSuccess(int statusCode, Header[] headers, String rawJsonResponse, JSONObject response) {
                    System.out.println(response);
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

        view.findViewById(R.id.preload_via_bank_card).setOnClickListener(v -> {
            JSONObject payload = JSONUtils.generateFundsAddition(PreloadType.preload_card, getActivity());

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
        });

        view.findViewById(R.id.move_funds_to_bank_account).setOnClickListener(v -> {
            JSONObject payload = JSONUtils.generateFundsWithdrawal(getActivity(), 10);
        });

        return view;
    }
}
