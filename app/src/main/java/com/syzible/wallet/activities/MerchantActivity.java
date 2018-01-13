package com.syzible.wallet.activities;

import android.nfc.NdefMessage;
import android.nfc.NdefRecord;
import android.nfc.NfcAdapter;
import android.nfc.NfcEvent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.syzible.wallet.R;
import com.syzible.wallet.nfc.MerchantHelper;

/**
 * Created by ed on 16/11/2017.
 */

public class MerchantActivity extends AppCompatActivity implements NfcAdapter.CreateNdefMessageCallback {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nfc_merchant);

        NfcAdapter adapter = NfcAdapter.getDefaultAdapter(this);
        adapter.setNdefPushMessageCallback(this, this);
    }

    @Override
    public NdefMessage createNdefMessage(NfcEvent nfcEvent) {
        String message = MerchantHelper.getPurchase();
        NdefRecord ndefRecord = NdefRecord.createMime("text/plain", message.getBytes());
        return new NdefMessage(ndefRecord);
    }
}