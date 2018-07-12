package com.androidstuff.fenaapp;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

/**
 * Created by Flash_Netcomm on 7/7/2018.
 */

public class NetworkUtils {
    public static boolean isConnected(Context mContext) {
        if (mContext != null) {
            ConnectivityManager cm = (ConnectivityManager) mContext.getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo netInfo = cm.getActiveNetworkInfo();
            return netInfo != null && netInfo.isConnectedOrConnecting();
        } else
            return false;

    }
}
