package com.syzible.plynk.activities;

import android.content.Intent;
import android.graphics.Bitmap;
import android.nfc.NdefMessage;
import android.nfc.NfcAdapter;
import android.os.Bundle;
import android.os.Parcelable;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.loopj.android.http.BaseJsonHttpResponseHandler;
import com.syzible.plynk.R;
import com.syzible.plynk.fragments.ChatFragment;
import com.syzible.plynk.fragments.ChatListFragment;
import com.syzible.plynk.fragments.ManageMoneyFragment;
import com.syzible.plynk.fragments.MyDetailsFragment;
import com.syzible.plynk.helpers.FragmentHelper;
import com.syzible.plynk.network.Endpoints;
import com.syzible.plynk.network.GetImage;
import com.syzible.plynk.network.NetworkCallback;
import com.syzible.plynk.network.RestClient;
import com.syzible.plynk.nfc.MerchantHelper;
import com.syzible.plynk.objects.Merchant;
import com.syzible.plynk.objects.Transaction;
import com.syzible.plynk.objects.User;
import com.syzible.plynk.persistence.LocalPrefs;
import com.syzible.plynk.utils.BitmapUtils;
import com.syzible.plynk.utils.CachingUtils;
import com.syzible.plynk.utils.EncodingUtils;
import com.syzible.plynk.utils.FacebookUtils;
import com.syzible.plynk.utils.JSONUtils;

import org.json.JSONException;
import org.json.JSONObject;

import cz.msebera.android.httpclient.Header;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private TextView userBalance;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close) {
            @Override
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);

                RestClient.post(MainActivity.this, Endpoints.GET_BALANCE, JSONUtils.getId(MainActivity.this),
                        new BaseJsonHttpResponseHandler<JSONObject>() {
                            @Override
                            public void onSuccess(int statusCode, Header[] headers, String rawJsonResponse, JSONObject response) {
                                try {
                                    double balance = response.getDouble("balance");
                                    userBalance.setText(EncodingUtils.getEncodedCurrency((float) balance));
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
        };

        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        navigationView.getMenu().getItem(0).setChecked(true);

        View view = navigationView.getHeaderView(0);
        ImageView profilePic = view.findViewById(R.id.card_user_pic);
        userBalance = view.findViewById(R.id.card_user_balance);
        TextView userName = view.findViewById(R.id.card_user_name);
        userName.setText(LocalPrefs.getFullName(this));

        if (!CachingUtils.doesImageExist(this, LocalPrefs.getID(this))) {
            new GetImage(new NetworkCallback<Bitmap>() {
                @Override
                public void onResponse(Bitmap response) {
                    Bitmap croppedImage = BitmapUtils.getCroppedCircle(response);
                    profilePic.setImageBitmap(croppedImage);
                    CachingUtils.cacheImage(LocalPrefs.getID(MainActivity.this), croppedImage);
                }

                @Override
                public void onFailure() {

                }
            }, LocalPrefs.getStringPref(LocalPrefs.Pref.profile_pic, this)).execute();
        } else {
            String id = LocalPrefs.getID(this);
            Bitmap bitmap = CachingUtils.getCachedImage(id);
            profilePic.setImageBitmap(bitmap);
        }

        FragmentHelper.setFragmentWithoutBackstack(getFragmentManager(), new ChatListFragment());
    }

    @Override
    protected void onResume() {
        super.onResume();
        Intent intent = getIntent();
        if (NfcAdapter.ACTION_NDEF_DISCOVERED.equals(intent.getAction())) {
            Parcelable[] rawMessages = intent.getParcelableArrayExtra(
                    NfcAdapter.EXTRA_NDEF_MESSAGES);

            NdefMessage message = (NdefMessage) rawMessages[0];
            String dataTransferred = new String(message.getRecords()[0].getPayload());

            String vendorName = dataTransferred.split("/")[0];
            double vendorExpense = Double.parseDouble(dataTransferred.split("/")[1]);
            long vendorSaleTime = Long.parseLong(dataTransferred.split("/")[2]);

            Intent androidPayIntent = new Intent(this, AndroidPayActivity.class);
            androidPayIntent.putExtra("vendor_name", vendorName);
            androidPayIntent.putExtra("vendor_expense", String.valueOf(vendorExpense));
            androidPayIntent.putExtra("vendor_sale_time", String.valueOf(vendorSaleTime));
            startActivity(androidPayIntent);
        }
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_settings) {
            return true;
        } else if (id == R.id.action_nfc_merchant) {
            double amount = MerchantHelper.getPrice();
            Intent intent = new Intent(this, AndroidPayActivity.class);
            intent.putExtra("transaction_amount", String.valueOf(amount));
            startActivity(intent);
            return true;
        } else if (id == R.id.action_log_out) {
            FacebookUtils.deleteToken(this);
            startActivity(new Intent(this, AuthenticationActivity.class));
            this.finish();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.nav_chats) {
            FragmentHelper.setFragmentWithoutBackstack(getFragmentManager(), new ChatListFragment());
        } else if (id == R.id.nav_manage_money) {
            FragmentHelper.setFragmentWithoutBackstack(getFragmentManager(), new ManageMoneyFragment());
        } else if (id == R.id.nav_my_details) {
            FragmentHelper.setFragmentWithoutBackstack(getFragmentManager(), new MyDetailsFragment());
        } else if (id == R.id.nav_invite_friends) {

        } else if (id == R.id.nav_help) {

        }

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}
