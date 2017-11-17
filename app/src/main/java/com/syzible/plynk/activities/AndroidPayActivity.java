package com.syzible.plynk.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;
import com.loopj.android.http.BaseJsonHttpResponseHandler;
import com.syzible.plynk.R;
import com.syzible.plynk.fragments.ManageMoneyFragment;
import com.syzible.plynk.helpers.FragmentHelper;
import com.syzible.plynk.network.Endpoints;
import com.syzible.plynk.network.RestClient;
import com.syzible.plynk.objects.Merchant;
import com.syzible.plynk.objects.Transaction;
import com.syzible.plynk.objects.User;
import com.syzible.plynk.utils.EncodingUtils;
import com.syzible.plynk.utils.JSONUtils;

import org.json.JSONException;
import org.json.JSONObject;

import cz.msebera.android.httpclient.Header;

/**
 * Created by ed on 17/11/2017.
 */

public class AndroidPayActivity extends AppCompatActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_android_pay);

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        String vendorName = getIntent().getStringExtra("vendor_name");
        float vendorExpense = Float.parseFloat(getIntent().getStringExtra("vendor_expense"));
        long vendorSaleTime = Long.parseLong(getIntent().getStringExtra("vendor_sale_time"));

        String vendorTransaction = EncodingUtils.getEncodedCurrency(vendorExpense);
        ((TextView) findViewById(R.id.android_pay_amount)).setText(vendorTransaction);

        Merchant merchant = new Merchant(vendorName, vendorName, "");
        User me = User.getMe(this);

        Transaction transaction = new Transaction(vendorExpense, merchant, me, vendorSaleTime);

        /*
        RestClient.post(this, Endpoints.CARD_PAYMENT, JSONUtils.generateExpense(transaction, this), new BaseJsonHttpResponseHandler<JSONObject>() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, String rawJsonResponse, JSONObject response) {
                try {
                    boolean isSuccessful = response.getBoolean("success");
                    if (!isSuccessful) {
                        String reason = response.getString("reason");
                        Toast.makeText(MainActivity.this, reason, Toast.LENGTH_SHORT).show();

                        FragmentHelper.setFragmentWithoutBackstack(getFragmentManager(), new ManageMoneyFragment());
                    } else {
                        Toast.makeText(MainActivity.this, "Payment approved to " + transaction.getRecipient().getVendorName(), Toast.LENGTH_SHORT).show();
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
         */

        animatePayLogo();
        killActivity();
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

    private void killActivity() {
        final Runnable run = () -> {
            Intent intent = new Intent(AndroidPayActivity.this, MainActivity.class);
            intent.putExtra("invocation", "android_pay");
            startActivity(intent);
            AndroidPayActivity.this.finish();
        };

        new Handler().postDelayed(run, 3000);
    }
}
