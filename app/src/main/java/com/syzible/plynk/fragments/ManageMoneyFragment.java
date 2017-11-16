package com.syzible.plynk.fragments;

import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.loopj.android.http.BaseJsonHttpResponseHandler;
import com.syzible.plynk.R;
import com.syzible.plynk.helpers.FragmentHelper;
import com.syzible.plynk.network.Endpoints;
import com.syzible.plynk.network.RestClient;
import com.syzible.plynk.objects.Card;
import com.syzible.plynk.objects.Transaction;
import com.syzible.plynk.objects.User;
import com.syzible.plynk.persistence.LocalPrefs;
import com.syzible.plynk.ui.ActionBarUtils;
import com.syzible.plynk.ui.TransactionItemDecoration;
import com.syzible.plynk.utils.EncodingUtils;
import com.syzible.plynk.utils.JSONUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

import cz.msebera.android.httpclient.Header;

/**
 * Created by ed on 13/11/2017.
 */

public class ManageMoneyFragment extends Fragment {
    private View view;
    private TextView cardNumber, cardName, cardCvv, cardExpiry;

    private ArrayList<Transaction> transactions = new ArrayList<>();
    private TransactionsAdapter adapter;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_manage_money, container, false);
        view.findViewById(R.id.plynk_card).setOnClickListener(v -> {
            //FragmentHelper.setFragmentBackstack(getFragmentManager(), new PastTransactionsFragment(), R.id.fragment_holder);
        });

        prepareTransactionData();

        cardNumber = view.findViewById(R.id.card_number);
        cardName = view.findViewById(R.id.card_name);
        cardCvv = view.findViewById(R.id.card_cvv);
        cardExpiry = view.findViewById(R.id.card_expiry);

        getUserBalance();
        getCardData();

        view.findViewById(R.id.manager_money_external_money).setOnClickListener(v -> {
            FragmentHelper.setFragmentBackstack(getFragmentManager(), new ManageExternalMoneyFragment());
        });

        return view;
    }

    private void getCardData() {
        RestClient.post(getActivity(), Endpoints.GET_PLYNK_CARD_DATA, JSONUtils.getId(getActivity()), new BaseJsonHttpResponseHandler<JSONObject>() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, String rawJsonResponse, JSONObject response) {
                Card card = new Card(response);

                cardName.setText(card.getUser().getFullName());
                cardCvv.setText(card.getCvv());
                cardNumber.setText(card.getFormattedCard());
                cardExpiry.setText(card.getExpiry());
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

    private void getUserBalance() {
        RestClient.post(getActivity(), Endpoints.GET_BALANCE, JSONUtils.getId(getActivity()), new BaseJsonHttpResponseHandler<JSONObject>() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, String rawJsonResponse, JSONObject response) {
                try {
                    float balance = (float) response.getDouble("balance");
                    boolean isNegative = balance < 0;
                    String balanceTitle = (isNegative ? "- " : " ") + EncodingUtils.getEncodedCurrency(Math.abs(balance));
                    ActionBarUtils.setToolbar(getActivity(), balanceTitle);
                } catch (JSONException e) {
                    e.printStackTrace();
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
    }

    private void prepareTransactionData() {
        RestClient.post(getActivity(), Endpoints.GET_PAST_TRANSACTIONS, JSONUtils.getId(getActivity()), new BaseJsonHttpResponseHandler<JSONArray>() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, String rawJsonResponse, JSONArray response) {
                transactions = new ArrayList<>();
                for (int i = 0; i < response.length(); i++) {
                    try {
                        JSONObject o = response.getJSONObject(i);
                        Transaction transaction = new Transaction(o);
                        transactions.add(transaction);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }

                Collections.sort(transactions);

                RecyclerView recyclerView = view.findViewById(R.id.transactions_recycler_view);
                adapter = new TransactionsAdapter(transactions);

                RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getActivity());
                recyclerView.setLayoutManager(layoutManager);
                recyclerView.setItemAnimator(new DefaultItemAnimator());
                recyclerView.addItemDecoration(new TransactionItemDecoration(getActivity(), 16));
                recyclerView.setAdapter(adapter);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, String rawJsonData, JSONArray errorResponse) {

            }

            @Override
            protected JSONArray parseResponse(String rawJsonData, boolean isFailure) throws Throwable {
                return new JSONArray(rawJsonData);
            }
        });
    }

    private class TransactionsAdapter extends RecyclerView.Adapter<TransactionsAdapter.ViewHolder> {
        private List<Transaction> transactionList;

        class ViewHolder extends RecyclerView.ViewHolder {
            TextView title, amount, time;

            ViewHolder(View v) {
                super(v);
                title = v.findViewById(R.id.transaction_title);
                amount = v.findViewById(R.id.transaction_amount);
                time = v.findViewById(R.id.transaction_time);
            }
        }

        TransactionsAdapter(List<Transaction> transactionList) {
            this.transactionList = transactionList;
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.transaction_item, parent, false);
            return new ViewHolder(itemView);
        }

        @Override
        public void onBindViewHolder(ViewHolder holder, int position) {
            Transaction transaction = transactionList.get(position);

            holder.time.setText(EncodingUtils.getEncodedDate(transaction.getTime()));
            holder.title.setText(transaction.isPositive(getActivity()) ?
                    transaction.getPaidFromUser().getFullName() :
                    transaction.getPaidToUser().getFullName());

            String paymentAmount = EncodingUtils.getEncodedCurrency(transaction.getAmount());
            int textColour = transaction.isPositive(getActivity()) ?
                    getActivity().getResources().getColor(R.color.colorAccent) :
                    getActivity().getResources().getColor(R.color.colorPrimary);

            holder.amount.setTextColor(textColour);
            holder.amount.setText(paymentAmount);
        }

        @Override
        public int getItemCount() {
            return transactionList.size();
        }
    }
}
