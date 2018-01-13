package com.syzible.wallet.network;

import android.os.AsyncTask;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by ed on 16/12/2016
 */

abstract class GetRequest<T> extends AsyncTask<Object, Void, T> {
    private NetworkCallback<T> networkCallback;
    private String url;
    private HttpURLConnection connection;

    GetRequest(NetworkCallback<T> networkCallback, String url) {
        this.networkCallback = networkCallback;
        this.url = url;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }

    @Override
    protected void onProgressUpdate(Void... progress) {
        super.onProgressUpdate(progress);
    }

    @Override
    protected T doInBackground(Object... objects) {
        try {
            connection = (HttpURLConnection) new URL(url).openConnection();
            connection.setReadTimeout(10000);
            connection.setConnectTimeout(15000);
            connection.setRequestMethod("GET");
            connection.setInstanceFollowRedirects(true);
            connection.setUseCaches(false);
            connection.setAllowUserInteraction(false);
            connection.connect();

            return transferData();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (connection != null) {
                connection.disconnect();
            }
        }

        return null;
    }

    @Override
    protected void onPostExecute(T o) {
        super.onPostExecute(o);
        assert networkCallback != null;
        if (o != null)
            networkCallback.onResponse(o);
        else
            networkCallback.onFailure();
    }

    HttpURLConnection getConnection() {
        return connection;
    }

    public abstract T transferData();
}
