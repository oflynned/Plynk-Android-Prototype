package com.syzible.wallet.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Vibrator;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;
import com.loopj.android.http.BaseJsonHttpResponseHandler;
import com.syzible.wallet.R;
import com.syzible.wallet.network.Endpoints;
import com.syzible.wallet.network.RestClient;
import com.syzible.wallet.objects.Card;
import com.syzible.wallet.objects.Merchant;
import com.syzible.wallet.objects.Transaction;
import com.syzible.wallet.objects.User;
import com.syzible.wallet.utils.EncodingUtils;
import com.syzible.wallet.utils.JSONUtils;

import org.json.JSONException;
import org.json.JSONObject;

import cz.msebera.android.httpclient.Header;

/**
 * Created by ed on 17/11/2017.
 */

public class AndroidPayActivity extends AppCompatActivity {
    private TextView cardName, cardNumber, cardCvv, cardExpiry;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_android_pay);
        makeFullscreen();

        cardName = findViewById(R.id.ap_card_name);
        cardCvv = findViewById(R.id.ap_card_cvv);
        cardExpiry = findViewById(R.id.ap_card_expiry);
        cardNumber = findViewById(R.id.ap_card_number);

        boolean isExpenditure = getIntent().getStringExtra("vendor_action").equals("expense");
        String vendorName = getIntent().getStringExtra("vendor_name");
        float vendorExpense = Float.parseFloat(getIntent().getStringExtra("vendor_expense"));
        long vendorSaleTime = Long.parseLong(getIntent().getStringExtra("vendor_sale_time"));
        String lastActiveFragment = getIntent().getStringExtra("last_fragment_active");

        String vendorTransaction = EncodingUtils.getEncodedCurrency(vendorExpense);
        ((TextView) findViewById(R.id.android_pay_amount)).setText(vendorTransaction);

        getCardData();
        animateCard();
        if (isExpenditure)
            makeTransaction(vendorName, vendorExpense, vendorSaleTime);
        else
            makeDesposit(vendorName, vendorExpense, vendorSaleTime);

        animatePayLogo();
        killActivity(lastActiveFragment);
    }

    private void makeFullscreen() {
        View decorView = getWindow().getDecorView();
        int uiOptions = View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_FULLSCREEN;
        decorView.setSystemUiVisibility(uiOptions);
    }

    private void makeDesposit(String vendorName, float vendorExpense, long vendorSaleTime) {
        JSONObject payload = JSONUtils.generateAndroidPayFundsAddition(vendorExpense, this);
        RestClient.post(this, Endpoints.CARD_TOPUP, payload, new BaseJsonHttpResponseHandler<JSONObject>() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, String rawJsonResponse, JSONObject response) {
                Toast.makeText(AndroidPayActivity.this, "Topup approved", Toast.LENGTH_SHORT).show();
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

    private void makeTransaction(String vendorName, float vendorExpense, long vendorSaleTime) {
        Merchant merchant = new Merchant(vendorName, vendorName, "");
        User me = User.getMe(this);

        Transaction transaction = new Transaction(vendorExpense, merchant, me, vendorSaleTime);
        RestClient.post(this, Endpoints.CARD_PAYMENT, JSONUtils.generateExpense(transaction, this), new BaseJsonHttpResponseHandler<JSONObject>() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, String rawJsonResponse, JSONObject response) {
                try {
                    boolean isSuccessful = response.getBoolean("success");
                    if (!isSuccessful) {
                        String reason = response.getString("reason");
                        Toast.makeText(AndroidPayActivity.this, reason, Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(AndroidPayActivity.this, "Payment approved to " + transaction.getRecipient().getVendorName(), Toast.LENGTH_SHORT).show();
                    }
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

    private void getCardData() {
        RestClient.post(this, Endpoints.GET_DIGITAL_CARD_DATA, JSONUtils.getId(this), new BaseJsonHttpResponseHandler<JSONObject>() {
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

    private void animateCard() {
        YoYo.with(Techniques.FadeIn).duration(500).playOn(cardName);
        YoYo.with(Techniques.FadeIn).duration(500).playOn(cardNumber);
        YoYo.with(Techniques.FadeIn).duration(500).playOn(cardCvv);
        YoYo.with(Techniques.FadeIn).duration(500).playOn(cardExpiry);
    }

    private void animatePayLogo() {
        Vibrator v = (Vibrator) this.getSystemService(Context.VIBRATOR_SERVICE);
        final Runnable runnable = () -> {
            if (v != null)
                v.vibrate(500);
        };

        new Handler().postDelayed(runnable, 2000);

        ImageView imageView = findViewById(R.id.android_pay_logo);
        YoYo.with(Techniques.FadeIn).duration(700).playOn(imageView);
        YoYo.with(Techniques.Bounce).delay(700).duration(1300).playOn(imageView);
    }

    private void killActivity(String lastFragmentActive) {
        final Runnable run = () -> {
            Intent intent = new Intent(AndroidPayActivity.this, MainActivity.class);
            intent.putExtra("invocation", "android_pay");
            intent.putExtra("last_fragment_active", lastFragmentActive);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
        };

        new Handler().postDelayed(run, 3000);
    }
}
