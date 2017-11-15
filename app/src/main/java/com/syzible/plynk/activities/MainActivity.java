package com.syzible.plynk.activities;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.media.Image;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
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
import com.syzible.plynk.fragments.ManageMoneyFragment;
import com.syzible.plynk.fragments.MyProfileFragment;
import com.syzible.plynk.helpers.FragmentHelper;
import com.syzible.plynk.network.Endpoints;
import com.syzible.plynk.network.GetImage;
import com.syzible.plynk.network.NetworkCallback;
import com.syzible.plynk.network.RestClient;
import com.syzible.plynk.persistence.LocalPrefs;
import com.syzible.plynk.utils.BitmapUtils;
import com.syzible.plynk.utils.CachingUtils;
import com.syzible.plynk.utils.FacebookUtils;
import com.syzible.plynk.utils.JSONUtils;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.DecimalFormat;

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
                                    DecimalFormat decimalFormat = new DecimalFormat("#.##");
                                    double balance = response.getDouble("balance");
                                    float formattedBalance = Float.valueOf(decimalFormat.format(balance));
                                    userBalance.setText(String.valueOf(formattedBalance));
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

        FragmentHelper.setFragmentWithoutBackstack(getFragmentManager(), new ChatFragment(), R.id.fragment_holder);
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
        } else if (id == R.id.action_about) {
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
            FragmentHelper.setFragmentWithoutBackstack(getFragmentManager(), new ChatFragment(), R.id.fragment_holder);
        } else if (id == R.id.nav_manage_money) {
            FragmentHelper.setFragmentWithoutBackstack(getFragmentManager(), new ManageMoneyFragment(), R.id.fragment_holder);
        } else if (id == R.id.nav_my_profile) {
            FragmentHelper.setFragmentWithoutBackstack(getFragmentManager(), new MyProfileFragment(), R.id.fragment_holder);
        } else if (id == R.id.nav_invite_friends) {

        } else if (id == R.id.nav_help) {

        }

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}
