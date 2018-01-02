package mayank.example.zendor;

import android.content.Context;
import android.net.Uri;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import mayank.example.zendor.ApplicationQueue;

/**
 * Created by mayank on 12/14/2017.
 */

public class frequentlyUsedClass {

    private static final String userName = "2000148140";
    private static final String password = "hvDjCw";


    public static void sendOTP(final String phone, final String message, Context mContext) {

        Log.e("asd", "yes");
        Uri.Builder builder = new Uri.Builder();
        builder.scheme("http");
        builder.authority("enterprise.smsgupshup.com");
        builder.appendPath("GatewayAPI");
        builder.appendPath("rest");
        builder.appendQueryParameter("method", "sendMessage");
        builder.appendQueryParameter("msg", message);
        builder.appendQueryParameter("v", "1.1");
        builder.appendQueryParameter("userid", userName);
        builder.appendQueryParameter("password", password);
        builder.appendQueryParameter("send_to", "91" + phone);
        builder.appendQueryParameter("msg_type", "Unicode_text");
        builder.appendQueryParameter("format", "JSON");
        builder.appendQueryParameter("auth_scheme", "Plain");
        builder.appendQueryParameter("mask", "ZENDOR");
        String url = builder.build().toString();

        StringRequest stringRequest = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.e("otp response", response);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        });
        ApplicationQueue.getInstance(mContext).addToRequestQueue(stringRequest);
    }

}
