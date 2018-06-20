package com.androidstuff.fenaapp.ClassUtills;

import com.android.volley.VolleyError;

import org.json.JSONObject;

/**
 * Created by Flash_Netcomm on 6/18/2018.
 */

public interface ResponseListener {
    void onSuccess(JSONObject object, int Tag);

    void onError(VolleyError error, int Tag);
}
