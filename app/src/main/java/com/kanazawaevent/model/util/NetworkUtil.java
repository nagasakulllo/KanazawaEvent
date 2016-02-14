package com.kanazawaevent.model.util;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

/**
 * Created by nagai on 2016/02/04.
 */
public class NetworkUtil {
    private NetworkUtil() {

    }

    /**
     * GET
     *
     * @param url URL
     * @return 結果
     */
    public static NetworkResult get(final String url, Context context) {
        if (!checkConnection(context)) {
            return null;
        }

        ExecutorService exeServ = Executors.newSingleThreadExecutor();
        Future<NetworkResult> future = exeServ.submit(new Callable<NetworkResult>() {

            @Override
            public NetworkResult call() throws Exception {
                final Request request = new Request.Builder()
                        .url(url)
                        .get()
                        .build();

                OkHttpClient client = new OkHttpClient();
                Response response = client.newCall(request).execute();
                return new NetworkResult(response);
            }

        });

        NetworkResult result = null;
        try {
            result = future.get();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        } finally {
            exeServ.shutdown();
        }

        return result;
    }

    public static boolean checkConnection(Context context) {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo info = cm.getActiveNetworkInfo();
        if (info == null) {
            return false;
        }

        return info.isConnected();
    }
}
