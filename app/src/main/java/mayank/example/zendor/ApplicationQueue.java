package mayank.example.zendor;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.content.DialogInterface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.RetryPolicy;
import com.android.volley.toolbox.Volley;

/**
 * Created by mayank on 12/1/2017.
 */

public class ApplicationQueue extends Application {

    private static ApplicationQueue mInstance;
    private RequestQueue mRequestQueue;
    private static Context mCtx;

    private ApplicationQueue(Context context) {
        mCtx = context;
        mRequestQueue = getRequestQueue();
    }

    public static synchronized ApplicationQueue getInstance(Context context) {
        if (mInstance == null) {
            mInstance = new ApplicationQueue(context);
        }
        return mInstance;
    }

    public RequestQueue getRequestQueue() {
        if (mRequestQueue == null) {
            mRequestQueue = Volley.newRequestQueue(mCtx.getApplicationContext());
        }
        return mRequestQueue;
    }

    public <T> void addToRequestQueue(Request<T> req) {

        if (isNetworkAvailable()) {
            req.setRetryPolicy(new DefaultRetryPolicy(5000, 10, 1));
            req.setShouldCache(false);
            getRequestQueue().add(req);
        }
    }

    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) mCtx.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = null;
        if (connectivityManager != null)
            activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    private void showDialog() {
        Toast.makeText(mCtx, "gdhg", Toast.LENGTH_LONG).show();
        AlertDialog.Builder builder;
        builder = new AlertDialog.Builder(mCtx);
        builder.setCancelable(false);
        builder.setMessage("No Internet !!")
                .setIcon(android.R.drawable.ic_dialog_alert)
                .show();
    }

}